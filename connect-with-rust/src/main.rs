// mod connect_mongodb;
// mod connect_neo4j;
mod connect_redis;
mod paths;

use std::error::Error;

// #[tokio::main]
fn main() -> Result<(), Box<dyn Error>> {
    println!("Starting...\n");

    // connect_mongodb::test().await?;
    // connect_neo4j::test().await;

    // connect_redis::test()?;
    paths::read_kml();

    println!("\nDone!");
    Ok(())
}
