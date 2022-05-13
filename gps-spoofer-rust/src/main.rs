mod car;
mod connect_redis;
mod paths;
mod pub_sub;
mod util;

use crate::{car::Emergency, util::string_to_static_str};
use car::{drive, Car};
use either::Either;
use geo::{coord, LineString};
use regex::Regex;
use std::error::Error;
use tokio::sync::mpsc;

#[tokio::main]
async fn main() -> Result<(), Box<dyn Error>> {
    let mut r_pub_con = connect_redis::connect().unwrap();
    let (tx, mut rx) = mpsc::channel(1000);

    let heidelberg_weststadt = paths::read_kml("heidelberg-weststadt.kml");
    let heidelberg_bergheim = paths::read_kml("heidelberg-bergheim.kml");
    let heidelberg_neunheim = paths::read_kml("heidelberg-neunheim.kml");

    let mut police_car = Car::new(
        "Police",
        "BWL_A_1",
        11.1 * 4.0,
        heidelberg_weststadt.clone(),
    );
    let mut ambulance = Car::new(
        "Ambulance",
        "BWL_A_2",
        11.1 * 4.0,
        heidelberg_bergheim.clone(),
    );
    let mut firetruck = Car::new(
        "Firetruck",
        "BWL_A_3",
        11.1 * 4.0,
        heidelberg_neunheim.clone(),
    );

    let mut t1 = drive(police_car, true, heidelberg_weststadt.clone(), tx.clone()).await?;
    let mut t2 = drive(ambulance, true, heidelberg_bergheim.clone(), tx.clone()).await?;
    let mut t3 = drive(firetruck, true, heidelberg_neunheim.clone(), tx.clone()).await?;

    pub_sub::subscribe(String::from("Emergencies"), tx.clone()).await?;

    // This loop terminates once all mpsc::Senders of rx are dropped
    //
    // Regex (EXMAScript-specification) to extract the data:
    // With channel: (?<channel>\w+): (?<type>\w+) (?<license_plate>\(\w+_\w+_\w\)).*Lat: (?<Lat>\d+\.\d+), Lon: (?<Lon>\d*.\d*)
    // Without channel /(?<type>\w+) (?<license_plate>\(\w+_\w+_\w\)).*Lat: (?<Lat>\d+\.\d+), Lon: (?<Lon>\d*.\d*)gm/
    //
    println!("\nmpsc::Channel ready...");
    while let Some(res) = rx.recv().await {
        match res {
            Either::Left(car) => {
                let message = format!(
                    "{} ({}) [{:#?}% ({:#?}m of {:#?}m) traveled in {:#?}s] Lat: {:#?}, Lon: {:#?}",
                    car.car_type,
                    car.license_plate,
                    car.progress as u64,
                    car.traveled_distance as u64,
                    car.path_length as u64,
                    car.lap_clock as u64,
                    car.coords.x,
                    car.coords.y
                );
                // println!("{}", message);

                redis::cmd("PUBLISH")
                    .arg("All")
                    .arg(message.clone())
                    .query::<()>(&mut r_pub_con)
                    .unwrap();
                redis::cmd("PUBLISH")
                    .arg(car.car_type)
                    .arg(message.clone())
                    .query::<()>(&mut r_pub_con)
                    .unwrap();
                match car.emergency {
                    Some(emergency) => redis::cmd("PUBLISH")
                        .arg(emergency.uuid)
                        .arg(message)
                        .query::<()>(&mut r_pub_con)
                        .unwrap(),
                    None => (),
                }
            }
            // Exp: PUBLISH Emergencies "Join stabbing BWL_A_1 49.3784348 8.657238054886221"
            Either::Right(message) => {
                let join_reg = Regex::new(r"(Join) (#\w+-\d+) (\w+) (\d+.\d+) (\d+.\d+)").unwrap();
                let leave_reg = Regex::new(r"(Leave) (\w+)").unwrap();

                if leave_reg.is_match(message.as_str()) {
                    for m in leave_reg.captures_iter(&message) {
                        let command = &m[1];
                        let car_plate = &m[2];

                        println!("\n{} {}", command, car_plate);

                        if car_plate == police_car.license_plate {
                            police_car.emergency = None;
                            t1.abort();
                            t1 = drive(police_car, true, heidelberg_weststadt.clone(), tx.clone())
                                .await?;
                        }

                        if car_plate == ambulance.license_plate {
                            ambulance.emergency = None;
                            t2.abort();
                            t2 = drive(ambulance, true, heidelberg_bergheim.clone(), tx.clone())
                                .await?;
                        }

                        if car_plate == firetruck.license_plate {
                            firetruck.emergency = None;
                            t3.abort();
                            t3 = drive(firetruck, true, heidelberg_neunheim.clone(), tx.clone())
                                .await?;
                        }
                    }
                }

                if join_reg.is_match(message.as_str()) {
                    for m in join_reg.captures_iter(&message) {
                        let command = &m[1];
                        let uuid = string_to_static_str(String::from(&m[2]));
                        let car_plate = &m[3];
                        let lat = m[4].parse::<f64>().unwrap();
                        let lon = m[5].parse::<f64>().unwrap();

                        println!("\n{} {} {} {} {}", command, uuid, car_plate, lat, lon);
                        if car_plate == police_car.license_plate
                            || car_plate == ambulance.license_plate
                            || car_plate == firetruck.license_plate
                        {
                            let path =
                                LineString::from(vec![police_car.coords, coord! {x: lat, y: lon}]);

                            if car_plate == police_car.license_plate {
                                t1.abort();

                                police_car.emergency = Some(Emergency {
                                    uuid,
                                    address: "Ludwig-Guttmann-Straße 6",
                                    lat,
                                    lon,
                                });

                                t1 = drive(police_car, false, path.clone(), tx.clone()).await?;
                            }

                            if car_plate == ambulance.license_plate {
                                t2.abort();

                                ambulance.emergency = Some(Emergency {
                                    uuid,
                                    address: "Alfred-Jost-Straße 38",
                                    lat,
                                    lon,
                                });

                                t2 = drive(ambulance, false, path.clone(), tx.clone()).await?;
                            }

                            if car_plate == firetruck.license_plate {
                                t3.abort();

                                firetruck.emergency = Some(Emergency {
                                    uuid,
                                    address: "Uferstraße 56",
                                    lat,
                                    lon,
                                });

                                t3 = drive(firetruck, false, path.clone(), tx.clone()).await?;
                            }
                        }
                    }
                }
            }
        }
    }
    println!("\nmpsc::Channel closed!");

    // Mby impl trait for nicenesssss!!!
    // drop(tx);
    drop(rx);

    Ok(())
}
