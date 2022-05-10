// mod connect_mongodb;
// mod connect_neo4j;
mod car;
mod connect_redis;
mod paths;

use car::Car;
use std::sync::mpsc;

// #[tokio::main]
fn main() {
    // println!("Starting...\n");
    // connect_mongodb::test().await?;
    // connect_neo4j::test().await;
    // connect_redis::test()?;
    let mut r_con = connect_redis::connect().unwrap();
    let (tx, rx) = mpsc::sync_channel::<Car>(1000);

    let heidelberg_weststadt = paths::read_kml("heidelberg-weststadt.kml");
    let heidelberg_bergheim = paths::read_kml("heidelberg-bergheim.kml");
    let heidelberg_neunheim = paths::read_kml("heidelberg-neunheim.kml");

    let police_car = Car::new("Police", "BWL A 1", 1111.1, heidelberg_weststadt.clone());
    let ambulance = Car::new("Ambulance", "BWL A 2", 1111.1, heidelberg_bergheim.clone());
    let firetruck = Car::new("Firetruck", "BWL A 3", 1111.1, heidelberg_neunheim.clone());

    let t1 = police_car.drive(heidelberg_weststadt, true, tx.clone());
    let t2 = ambulance.drive(heidelberg_bergheim, true, tx.clone());
    let t3 = firetruck.drive(heidelberg_neunheim, true, tx.clone());

    // Mby impl trait for nicenesssss!!!
    drop(tx);

    for car in rx {
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
            .arg("test")
            .arg(message)
            .query::<()>(&mut r_con)
            .unwrap();
    }

    t1.join().unwrap();
    t2.join().unwrap();
    t3.join().unwrap();
}
