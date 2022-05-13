use crate::paths::{calc_current_coords, progress_percent, traveled_distance};
use either::Either;
use geo::{prelude::HaversineLength, Coordinate, LineString};
use std::{error::Error, time::Instant};
use tokio::sync::mpsc;
use Either::Left;

#[derive(Debug, Copy, Clone)]
pub struct Emergency {
    pub uuid: &'static str,
    pub address: &'static str,
    pub lat: f64,
    pub lon: f64,
}

#[derive(Debug, Copy, Clone)]
pub struct Car {
    pub car_type: &'static str,
    pub license_plate: &'static str,
    pub speed: f64, // In m/s
    pub driving_since: f64,
    pub progress: f64,
    pub traveled_distance: f64,
    pub path_length: f64,
    pub lap_clock: f64,
    pub coords: Coordinate<f64>,
    pub emergency: Option<Emergency>,
    stopped: bool,
    pub cancel_driving: bool,
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
            driving_since: 0.0,
            progress: 0.0,
            traveled_distance: 0.0,
            path_length,
            lap_clock: 0.0,
            coords: *first_coord,
            emergency: None,
            stopped: false,
            cancel_driving: false,
        };
    }

    pub fn update(
        self: Self,
        car_type: Option<&'static str>,
        license_plate: Option<&'static str>,
        speed: Option<f64>, // In m/s
        driving_since: Option<f64>,
        progress: Option<f64>,
        traveled_distance: Option<f64>,
        path_length: Option<f64>,
        lap_clock: Option<f64>,
        coords: Option<Coordinate<f64>>,
        emergency: Option<Emergency>,
        stopped: Option<bool>,
        cancel_driving: Option<bool>,
    ) -> Car {
        Car {
            car_type: match car_type {
                Some(car_type) => car_type,
                None => self.car_type,
            },
            license_plate: match license_plate {
                Some(license_plate) => license_plate,
                None => self.license_plate,
            },
            speed: match speed {
                Some(speed) => speed,
                None => self.speed,
            },
            driving_since: match driving_since {
                Some(driving_since) => driving_since,
                None => self.driving_since,
            },
            progress: match progress {
                Some(progress) => progress,
                None => self.progress,
            },
            traveled_distance: match traveled_distance {
                Some(traveled_distance) => traveled_distance,
                None => self.traveled_distance,
            },
            path_length: match path_length {
                Some(path_length) => path_length,
                None => self.path_length,
            },
            lap_clock: match lap_clock {
                Some(lap_clock) => lap_clock,
                None => self.lap_clock,
            },
            coords: match coords {
                Some(coords) => coords,
                None => self.coords,
            },
            emergency: match emergency {
                Some(emergency) => Some(emergency),
                None => self.emergency,
            },
            stopped: match stopped {
                Some(stopped) => stopped,
                None => self.stopped,
            },
            cancel_driving: match cancel_driving {
                Some(cancel_driving) => cancel_driving,
                None => self.cancel_driving,
            },
        }
    }
}

pub async fn drive(
    mut car: Car,
    infinite: bool,
    path: LineString<f64>,
    car_sender: mpsc::Sender<Either<Car, String>>,
) -> Result<tokio::task::JoinHandle<()>, Box<dyn Error>> {
    Ok(tokio::spawn(async move {
        let length = path.haversine_length().round();
        // let init_full_time = Instant::now();
        let mut init_clock = Instant::now();

        loop {
            // let total_time_passed = init_full_time.elapsed().as_secs_f64();
            let lap_clock = init_clock.elapsed().as_secs_f64();
            let traveled_distance = traveled_distance(car.speed.clone(), lap_clock);
            let progress = progress_percent(length, traveled_distance);
            let new_coords =
                calc_current_coords(path.clone(), length, lap_clock, car.speed.clone());

            match new_coords {
                Ok(coords) => {
                    let new_car = car.update(
                        None,
                        None,
                        Some(car.speed),
                        None,
                        Some(progress),
                        Some(traveled_distance),
                        Some(length),
                        Some(lap_clock),
                        Some(coords),
                        None,
                        None,
                        None,
                    );
                    car_sender.send(Left(new_car)).await.unwrap();
                    car = new_car;
                }
                Err(e) => {
                    if infinite {
                        println!("\n{}", e);
                        init_clock = Instant::now();
                    } else {
                        car_sender.send(Left(car)).await.unwrap();
                        // break;
                    };
                }
            }
        }

        drop(car_sender);

        if !infinite {
            println!("\n{}: Destination reached", car.license_plate);
        }
    }))
}
