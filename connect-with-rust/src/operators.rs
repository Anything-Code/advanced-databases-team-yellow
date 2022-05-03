use crate::paths::{calc_current_coords, progress_percent, traveled_distance};
use geo::{prelude::HaversineLength, LineString};
use std::{
    ops::Sub,
    sync::mpsc::Sender,
    thread::{self, JoinHandle},
};
use time::Instant;
pub struct PoliceCar;

impl PoliceCar {
    pub fn new(
        nr: String,
        speed: f64, // In m/s
        looping: bool,
        tx: Sender<String>,
        path: LineString<f64>,
    ) -> JoinHandle<()> {
        let handle = thread::spawn(move || {
            let length = path.haversine_length().round();
            let mut init_time = Instant::now();

            loop {
                let time_diff_in_s = Instant::now().sub(init_time).as_seconds_f64();
                let td = traveled_distance(speed, time_diff_in_s);
                let progress = progress_percent(length, td);
                let new_coords = calc_current_coords(&path, length, time_diff_in_s, speed);

                match new_coords {
                    Ok(v) => {
                        // println!(
                        //     "PoliceCar {:#?} [{:#?}% ({:#?}m of {:#?}m) traveled in {:#?}s] Lat: {:#?}, Lon: {:#?}",
                        //     nr, progress as u64, td as u64, length as u64, time_diff_in_s as u64, v.0, v.1
                        // );
                        tx.send(format!("PoliceCar {:#?}: [{:#?} {:#?}]", nr, v.0, v.1))
                            .unwrap();
                    }
                    Err(e) => {
                        println!("\n{:#?}", e);
                        if looping {
                            init_time = Instant::now();
                        } else {
                            break;
                        };
                    }
                }
            }

            if !looping {
                println!("\n1 lap done!");
            }
        });

        return handle;
    }
}
