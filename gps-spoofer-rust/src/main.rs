// mod connect_mongodb;
// mod connect_neo4j;
mod connect_redis;
mod operators;
mod paths;

use operators::PoliceCar;
use std::sync::mpsc;

// #[tokio::main]
fn main() {
    // println!("Starting...\n");
    // connect_mongodb::test().await?;
    // connect_neo4j::test().await;
    // connect_redis::test()?;
    let mut r_con = connect_redis::connect().unwrap();
    let (tx, rx) = mpsc::sync_channel::<PoliceCar>(1000);

    let heidelberg_weststadt = paths::read_kml("heidelberg-weststadt.kml");

    let thread1 = PoliceCar::new(
        "BWL A 1",
        11.1,
        false,
        tx.clone(),
        heidelberg_weststadt.clone(),
    );
    let thread2 = PoliceCar::new(
        "BWL A 2",
        111.1,
        true,
        tx.clone(),
        heidelberg_weststadt.clone(),
    );
    let thread3 = PoliceCar::new(
        "BWL A 3",
        1111.1,
        true,
        tx.clone(),
        heidelberg_weststadt.clone(),
    );

    // Mby impl trait for nicenesssss!!!
    drop(tx);

    for payload in rx {
        let message = format!(
            "PoliceCar ({}) [{:#?}% ({:#?}m of {:#?}m) traveled in {:#?}s] Lat: {:#?}, Lon: {:#?}",
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
