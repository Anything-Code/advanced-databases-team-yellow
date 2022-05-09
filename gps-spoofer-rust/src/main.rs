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

    let thread1 = Car::new(
        "Police",
        "BWL A 1",
        1111.1,
        true,
        tx.clone(),
        heidelberg_weststadt,
    );
    let thread2 = Car::new(
        "Ambulance",
        "BWL A 2",
        1111.1,
        true,
        tx.clone(),
        heidelberg_bergheim,
    );
    let thread3 = Car::new(
        "Firetruck",
        "BWL A 3",
        1111.1,
        true,
        tx.clone(),
        heidelberg_neunheim,
    );

    // Mby impl trait for nicenesssss!!!
    drop(tx);

    for payload in rx {
        //
        // Regex to extract the data:
        // /(?'type'\w+) (?'license_plate'\(\w+ \w+ \w\)).*Lat: (?'Lat'\d+\.\d+), Lon: (?'Lon'\d*.\d*)gm/
        //
        let message = format!(
            "{} ({}) [{:#?}% ({:#?}m of {:#?}m) traveled in {:#?}s] Lat: {:#?}, Lon: {:#?}",
            payload.car_type,
            payload.nr,
            payload.progress as u64,
            payload.traveled_distance as u64,
            payload.length as u64,
            payload.time_diff_in_s as u64,
            payload.coords.0,
            payload.coords.1
        );

        println!("{}", message);

        redis::cmd("PUBLISH")
            .arg("test")
            .arg(message)
            .query::<()>(&mut r_con)
            .unwrap();
    }

    thread1.join().unwrap();
    thread2.join().unwrap();
    thread3.join().unwrap();
}
