use geo::{
    algorithm::line_interpolate_point::LineInterpolatePoint, GeometryCollection, LineString, Point,
};
use kml::{quick_collection, KmlReader};
use std::{
    path::Path,
    sync::{Arc, Mutex, MutexGuard},
};

pub fn traveled_distance(speed: f64, time_passed: f64) -> f64 {
    return speed * time_passed;
}

fn traveled_distance_ratio(total: f64, time_passed: f64, speed: f64) -> f64 {
    let td = traveled_distance(speed, time_passed);

    return td / total;
}

pub fn progress_percent(total: f64, traveled_distance: f64) -> f64 {
    return traveled_distance / total * 100.0;
}

pub fn calc_current_coords(
    path: Arc<Mutex<geo::LineString<f64>>>,
    length: f64,
    time_passed: f64,
    speed: f64,
) -> Result<(f64, f64), &'static str> {
    let ratio = traveled_distance_ratio(length, time_passed, speed);

    if ratio > 1.0 {
        return Err("Ratio cannot be greater than 1!");
    }

    let coords = path.lock().unwrap().line_interpolate_point(ratio).unwrap();

    return Ok((coords.y(), coords.x()));
}

pub fn read_kml(filename: &str) -> LineString<f64> {
    let kml_path = Path::new(env!("CARGO_MANIFEST_DIR"))
        .join("src")
        .join("static")
        .join(filename);

    let mut kml_reader = KmlReader::<_, f64>::from_path(kml_path).unwrap();
    let kml_data = kml_reader.read().unwrap();
    let geom_coll: GeometryCollection<_> = quick_collection(kml_data).unwrap();

    let without_duplicates_as_points = geom_coll
        .iter()
        .cloned()
        .collect::<Vec<_>>()
        .iter()
        .cloned()
        .enumerate()
        .filter(|&(key, _)| key >= 1 && key < geom_coll.len() - 1)
        .map(|item| item.1)
        .map(|item| Point::try_from(item).unwrap())
        .collect::<Vec<_>>();

    return LineString::from_iter(without_duplicates_as_points.iter().cloned());
}
