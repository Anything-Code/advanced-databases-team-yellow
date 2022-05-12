mod car;
mod connect_redis;
mod paths;
mod pub_sub;

use car::Car;
use either::Either;
use regex::Regex;
use std::{error::Error, sync::mpsc::sync_channel};

fn car_by_plate(cars: Vec<Car>, plate: String) -> Vec<Car>{
    return cars.iter().filter(|i| i.license_plate == plate);
}

#[tokio::main]
async fn main() -> Result<(), Box<dyn Error>> {
    let mut r_pub_con = connect_redis::connect().unwrap();
    let (car_sender, car_receiver) = sync_channel::<Either<Car, String>>(1000);

    let heidelberg_weststadt = paths::read_kml("heidelberg-weststadt.kml");
    let heidelberg_bergheim = paths::read_kml("heidelberg-bergheim.kml");
    let heidelberg_neunheim = paths::read_kml("heidelberg-neunheim.kml");

    let police_car = Car::new("Police", "BWL_A_1", 1111.1, heidelberg_weststadt.clone());
    let ambulance = Car::new("Ambulance", "BWL_A_2", 1111.1, heidelberg_bergheim.clone());
    let firetruck = Car::new("Firetruck", "BWL_A_3", 1111.1, heidelberg_neunheim.clone());

    let mut t1 = police_car.drive(heidelberg_weststadt.clone(), car_sender.clone());
    let mut t2 = ambulance.drive(heidelberg_bergheim.clone(), car_sender.clone());
    let mut t3 = firetruck.drive(heidelberg_neunheim.clone(), car_sender.clone());

    if let Err(error) = pub_sub::subscribe(String::from("Emergencies"), car_sender.clone()) {
        println!("{:?}", error);
        panic!("{:?}", error);
    } else {
        println!("Connected to sub-queue!")
    }

    // Mby impl trait for nicenesssss!!!
    drop(car_sender);

    for res in car_receiver {
        //
        // Regex to extract the data:
        // /(?'type'\w+) (?'license_plate'\(\w+ \w+ \w\)).*Lat: (?'Lat'\d+\.\d+), Lon: (?'Lon'\d*.\d*)gm/
        //
        match res {
            Either::Left(car) => {
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
                // println!("{}", message);

                redis::cmd("PUBLISH")
                    .arg("All")
                    .arg(message.clone())
                    .query::<()>(&mut r_pub_con)
                    .unwrap();
                redis::cmd("PUBLISH")
                    .arg(car.car_type)
                    .arg(message.clone())
                    .query::<()>(&mut r_pub_con)
                    .unwrap();
                match car.channel {
                    Some(emergency) => redis::cmd("PUBLISH")
                        .arg(emergency.uuid)
                        .arg(message)
                        .query::<()>(&mut r_pub_con)
                        .unwrap(),
                    None => (),
                }
            }
            Either::Right(message) => {
                let re = Regex::new(r"(\w+) (\w+) (\w+)").unwrap();

                if re.is_match(message.as_str()) {
                    re.captures_iter(&message).for_each(|m| {
                        let t = &m[1];
                        let emergency = &m[2];
                        let car_plate = &m[3];

                        let mut car;

                        if let car =  {
                            
                        }

                        if police_car.license_plate == car_plate {
                            car = police_car;
                        }
                        if ambulance.license_plate == car_plate {
                            car = ambulance;
                        }
                        if firetruck.license_plate == car_plate {
                            car = firetruck;
                        }

                        if t == "Join" && car.license_plate == car_plate {
                            println!("{}", car.license_plate)
                        }
                    });
                }
            }
        }
    }

    t1.join().unwrap();
    t2.join().unwrap();
    t3.join().unwrap();

    Ok(())
}
