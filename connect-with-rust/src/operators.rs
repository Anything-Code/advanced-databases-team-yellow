use crate::paths::{calc_current_coords, progress_percent, traveled_distance};
use geo::{prelude::HaversineLength, LineString};
use std::{
    ops::Sub,
    sync::mpsc::{Sender, SyncSender},
    thread::{self, JoinHandle},
    time::Instant,
};

pub struct PoliceCar {
    pub nr: &'static str,
    pub progress: f64,
    pub traveled_distance: f64,
    pub length: f64,
    pub time_diff_in_s: f64,
    pub coords: (f64, f64),
}

impl PoliceCar {
    pub fn new(
        nr: &'static str,
        speed: f64, // In m/s
        looping: bool,
        tx: SyncSender<PoliceCar>,
        path: LineString<f64>,
    ) -> JoinHandle<()> {
        let handle = thread::spawn(move || {
            let length = path.haversine_length().round();
            let mut init_time = Instant::now();

            loop {
                let time_diff_in_s = Instant::now().sub(init_time).as_secs_f64(); // init_time.elapsed....
                let td = traveled_distance(speed, time_diff_in_s);
                let progress = progress_percent(length, td);
                let new_coords = calc_current_coords(&path, length, time_diff_in_s, speed);

                match new_coords {
                    Ok(coords) => {
                        let payload = Self {
                            nr,
                            progress,
                            traveled_distance: td,
                            length,
                            time_diff_in_s,
                            coords,
                        };

                        tx.send(payload).unwrap();
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
