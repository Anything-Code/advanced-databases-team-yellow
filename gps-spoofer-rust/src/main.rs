mod car;
mod connect_redis;
mod paths;

use car::Car;
use std::{
    sync::{mpsc::sync_channel, Arc, Mutex},
    thread::JoinHandle,
};

fn main() {
    let mut r_con = connect_redis::connect().unwrap();
    let (car_sender, car_receiver) = sync_channel::<Car>(1000);

    let heidelberg_weststadt = paths::read_kml("heidelberg-weststadt.kml");
    let heidelberg_bergheim = paths::read_kml("heidelberg-bergheim.kml");
    let heidelberg_neunheim = paths::read_kml("heidelberg-neunheim.kml");

    let cars = (0..3)
        .map(|i| match i {
            0 => Car::new(
                "Police",
                "BWL A 1",
                1111.1,
                Arc::new(Mutex::new(heidelberg_weststadt.clone())),
            ),
            1 => Car::new(
                "Ambulance",
                "BWL A 1",
                1111.1,
                Arc::new(Mutex::new(heidelberg_bergheim.clone())),
            ),
            2 => Car::new(
                "Firetruck",
                "BWL A 1",
                1111.1,
                Arc::new(Mutex::new(heidelberg_neunheim.clone())),
            ),
            _ => Car::new(
                "Police",
                "BWL A 1",
                1111.1,
                Arc::new(Mutex::new(heidelberg_weststadt.clone())),
            ),
        })
        .collect::<Vec<Car>>();

    let threads = cars
        .into_iter()
        .enumerate()
        .map(|(i, car)| match i {
            0 => car.drive(car_sender.clone()),
            1 => car.drive(car_sender.clone()),
            2 => car.drive(car_sender.clone()),
            _ => car.drive(car_sender.clone()),
        })
        .collect::<Vec<JoinHandle<()>>>();

    // Mby impl trait for nicenesssss!!!
    drop(car_sender);

    for car in car_receiver {
        //
        // Regex to extract the data:
        // /(?'type'\w+) (?'license_plate'\(\w+ \w+ \w\)).*Lat: (?'Lat'\d+\.\d+), Lon: (?'Lon'\d*.\d*)gm/
        //
        let message = format!(
            "{} ({}) [{:#?}% ({:#?}m of {:#?}m) traveled in {:#?}s] Lat: {:#?}, Lon: {:#?}",
            car.car_type,
            car.license_plate,
            car.progress as u64,
            car.traveled_distance as u64,
            car.path_length as u64,
            car.lap_clock as u64,
            car.coords.0,
            car.coords.1
        );

        println!("{}", message);

        redis::cmd("PUBLISH")
            .arg("All")
            .arg(message.clone())
            .query::<()>(&mut r_con)
            .unwrap();
        redis::cmd("PUBLISH")
            .arg(car.car_type)
            .arg(message.clone())
            .query::<()>(&mut r_con)
            .unwrap();
        match car.channel {
            Some(emergency) => redis::cmd("PUBLISH")
                .arg(emergency.uuid)
                .arg(message)
                .query::<()>(&mut r_con)
                .unwrap(),
            None => (),
        }
    }

    let (r_sender, r_receiver) = sync_channel::<Car>(1000);

    threads.into_iter().for_each(|t| t.join().unwrap());
}
