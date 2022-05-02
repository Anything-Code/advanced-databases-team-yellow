// mod connect_mongodb;
// mod connect_neo4j;
mod connect_redis;
mod paths;

use crate::paths::{progress_percent, traveled_distance};
use geo::prelude::HaversineLength;
use std::{error::Error, ops::Sub, time::Instant};

// #[tokio::main]
fn main() -> Result<(), Box<dyn Error>> {
    let mut r_con = connect_redis::connect().unwrap();
    // connect_mongodb::test().await?;
    // connect_neo4j::test().await;
    // connect_redis::test()?;
    println!("Starting...\n");

    let path = paths::read_kml("polygon.kml");
    let length = path.haversine_length().round();
    let mut init_time = Instant::now();
    let speed = 11.1; // In meters/second (40km/h)

    loop {
        let time_diff_in_s = Instant::now().sub(init_time).as_secs_f64();
        let td = traveled_distance(speed, time_diff_in_s);
        let progress = progress_percent(length, td);
        let new_coords =
            paths::calc_current_coords(&path, length, time_diff_in_s, speed /* dyn-var */);

        match new_coords {
            Ok(v) => {
                println!(
                    "[{:#?}% ({:#?}m of {:#?}m) traveled in {:#?}s] Lat: {:#?}, Lon: {:#?}",
                    progress as u64, td as u64, length as u64, time_diff_in_s as u64, v.0, v.1
                );
                redis::cmd("PUBLISH")
                    .arg("test")
                    .arg(format!("Lat: {:#?}, Lon: {:#?}", v.0, v.1))
                    .query(&mut r_con)?;
            }
            Err(e) => {
                println!("\n{:#?}", e);
                break;
                // init_time = Instant::now();
            }
        }
    }

    println!("\n1 lap done!");
    Ok(())
}
