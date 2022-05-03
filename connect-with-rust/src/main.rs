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
    let (tx, rx) = mpsc::channel::<String>();

    let heidelberg_weststadt = paths::read_kml("heidelberg-weststadt.kml");

    let thread1 = PoliceCar::new(
        "BWL A 1".to_string(),
        11.1,
        false,
        tx.clone(),
        heidelberg_weststadt.clone(),
    );
    let thread2 = PoliceCar::new(
        "BWL A 2".to_string(),
        111.1,
        false,
        tx.clone(),
        heidelberg_weststadt.clone(),
    );
    let thread3 = PoliceCar::new(
        "BWL A 3".to_string(),
        1111.1,
        false,
        tx.clone(),
        heidelberg_weststadt.clone(),
    );

    let mut iter = rx.try_iter();
    loop {
        let message = iter.next();
        match message {
            Some(message) => {
                println!("{}", message);
                // redis::cmd("PUBLISH")
                //     .arg("test")
                //     .arg(message)
                //     .query::<()>(&mut r_con)
                //     .unwrap();
            }
            None => break,
        }
    }

    thread1.join().unwrap();
    thread2.join().unwrap();
    thread3.join().unwrap();
}
