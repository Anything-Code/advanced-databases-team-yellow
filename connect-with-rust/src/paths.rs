use geo::{
    algorithm::line_interpolate_point::LineInterpolatePoint, line_locate_point::LineLocatePoint,
    prelude::HaversineLength, GeometryCollection, LineString, Point,
};
use kml::{quick_collection, KmlReader};
use std::path::Path;

pub fn read_kml() {
    let kml_path = Path::new(env!("CARGO_MANIFEST_DIR"))
        .join("src")
        .join("static")
        .join("polygon.kml");

    let mut kml_reader = KmlReader::<_, f64>::from_path(kml_path).unwrap();
    let kml_data = kml_reader.read().unwrap();
    let geom_coll: GeometryCollection<_> = quick_collection(kml_data).unwrap();

    let without_duplicates = geom_coll
        .iter()
        .cloned()
        .collect::<Vec<_>>()
        .iter()
        .cloned()
        .enumerate()
        .filter(|&(key, _)| key >= 1)
        .map(|item| item.1)
        .collect::<Vec<_>>();

    let only_points = without_duplicates
        .iter()
        .cloned()
        .take(11)
        .map(|item| Point::try_from(item).unwrap())
        .collect::<Vec<_>>();

    let path = LineString::from_iter(only_points.iter().cloned());

    (1..100).for_each(|x| {
        let yo = path.line_interpolate_point(x as f64 / 100.0).unwrap();
        println!("x: {:#?}, y: {:#?}", yo.x(), yo.y());
    });
    println!("\nLength of path: {:#?} m", path.haversine_length().round())
}
