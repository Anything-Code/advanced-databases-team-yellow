use crate::paths::{calc_current_coords, progress_percent, traveled_distance};
use geo::{prelude::HaversineLength, Coordinate, LineString, Point};
use std::{
    ops::Sub,
    sync::mpsc::SyncSender,
    thread::{self, JoinHandle},
    time::Instant,
};
#[derive(Copy, Clone)]
pub struct Car {
    pub car_type: &'static str,
    pub license_plate: &'static str,
    pub speed: f64, // In m/s
    pub driving_since: f64,
    pub progress: f64,
    pub traveled_distance: f64,
    pub path_length: f64,
    pub lap_clock: f64,
    pub coords: (f64, f64),
    stopped: bool,
}

impl Car {
    pub fn new(
        car_type: &'static str,
        license_plate: &'static str,
        speed: f64, // In m/s
        path: LineString<f64>,
    ) -> Car {
        let path_length = path.haversine_length().round();
        let path_as_points = path.into_points();
        let first_point = path_as_points.first().unwrap();

        return Self {
            car_type,
            license_plate,
            speed,
            driving_since: 0.0,
            progress: 0.0,
            traveled_distance: 0.0,
            path_length,
            lap_clock: 0.0,
            coords: (first_point.0.x, first_point.0.y),
            stopped: false,
        };
    }

    pub fn drive(
        mut self,
        path: LineString<f64>,
        infinite: bool,
        tx: SyncSender<Self>,
    ) -> JoinHandle<()> {
        let handle = thread::spawn(move || {
            let length = path.haversine_length().round();
            let init_full_time = Instant::now();
            let mut init_clock = Instant::now();

            loop {
                self.driving_since = init_full_time.elapsed().as_secs_f64();
                if self.stopped {
                    tx.send(self).unwrap();
                } else {
                    self.lap_clock = init_clock.elapsed().as_secs_f64();
                    self.traveled_distance = traveled_distance(self.speed, self.lap_clock);
                    self.progress = progress_percent(length, self.traveled_distance);
                    let new_coords = calc_current_coords(&path, length, self.lap_clock, self.speed);

                    match new_coords {
                        Ok(coords) => {
                            self.coords = coords;

                            tx.send(self).unwrap();
                        }
                        Err(e) => {
                            println!("\n{:#?}", e);
                            if infinite {
                                init_clock = Instant::now();
                            } else {
                                break;
                            };
                        }
                    }
                }
            }

            if !infinite {
                println!("\n1 lap done!");
            }
        });

        return handle;
    }

    pub fn toggle(mut self) {
        self.stopped = !self.stopped
    }
}
