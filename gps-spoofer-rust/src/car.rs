use crate::paths::{calc_current_coords, progress_percent, traveled_distance};
use either::Either;
use geo::{coord, prelude::HaversineLength, Coordinate, LineString};
use std::{
    sync::mpsc::SyncSender,
    thread::{self, JoinHandle},
    time::Instant,
};

#[derive(Copy, Clone)]
pub struct Emergency {
    pub uuid: &'static str,
    address: &'static str,
    lat: f64,
    lon: f64,
}

#[derive(Copy, Clone)]
pub struct Car {
    pub car_type: &'static str,
    pub license_plate: &'static str,
    pub speed: f64, // In m/s
    pub infinite: bool,
    pub driving_since: f64,
    pub progress: f64,
    pub traveled_distance: f64,
    pub path_length: f64,
    pub lap_clock: f64,
    pub coords: (f64, f64),
    pub channel: Option<Emergency>,
    stopped: bool,
    stop_driving: bool,
}

impl Car {
    pub fn new(
        car_type: &'static str,
        license_plate: &'static str,
        speed: f64, // In m/s
        path: LineString<f64>,
    ) -> Car {
        let path_length = path.haversine_length().round();
        let path_as_coords: Vec<Coordinate<f64>> = path.into_iter().collect();
        let first_coord = path_as_coords.first().unwrap();

        return Self {
            car_type,
            license_plate,
            speed,
            infinite: false,
            driving_since: 0.0,
            progress: 0.0,
            traveled_distance: 0.0,
            path_length,
            lap_clock: 0.0,
            coords: (first_coord.x, first_coord.y),
            channel: None,
            stopped: false,
            stop_driving: false,
        };
    }

    pub fn drive(
        mut self,
        path: LineString<f64>,
        car_sender: SyncSender<Either<Self, String>>,
    ) -> JoinHandle<()> {
        let handle = thread::spawn(move || {
            let length = path.haversine_length().round();
            let init_full_time = Instant::now();
            let mut init_clock = Instant::now();

            loop {
                self.driving_since = init_full_time.elapsed().as_secs_f64();
                if self.stop_driving {
                    break;
                }
                if self.stopped {
                    car_sender.send(either::Left(self.clone())).unwrap();
                    continue;
                }

                self.lap_clock = init_clock.elapsed().as_secs_f64();
                self.traveled_distance = traveled_distance(self.speed.clone(), self.lap_clock);
                self.progress = progress_percent(length, self.traveled_distance);

                let speed = self.speed;
                let path = path.clone();
                let new_coords = calc_current_coords(path, length, self.lap_clock, speed);

                match new_coords {
                    Ok(coords) => {
                        self.coords = coords;

                        car_sender.send(either::Left(self.clone())).unwrap();
                    }
                    Err(e) => {
                        // println!("\n{}", e);
                        if self.infinite {
                            init_clock = Instant::now();
                        } else {
                            break;
                        };
                    }
                }
            }

            if !self.infinite {
                println!("\n{}: Destination reached", self.license_plate);
            }
        });

        return handle;
    }

    fn stop_driving(mut self) {
        self.stop_driving = true;
    }

    pub fn join_channel(mut self, emergency: Emergency) -> LineString<f64> {
        self.channel = Some(emergency);
        self.infinite = false;

        let path = LineString::from(vec![
            coord! {x: self.coords.0, y: self.coords.1},
            coord! {x:emergency.lat, y: emergency.lon},
        ]);

        return path;
    }

    pub fn leave_channel(mut self) {
        self.channel = None
    }

    pub fn toggle_start_stop(mut self) {
        self.stopped = !self.stopped
    }
}
