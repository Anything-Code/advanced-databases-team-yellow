// mod connect_mongodb;
// mod connect_neo4j;
mod connect_redis;

use std::error::Error;

// #[tokio::main]
fn main() -> Result<(), Box<dyn Error>> {
    println!("Starting...");

    // connect_mongodb::test().await?;
    // connect_neo4j::test().await;
    connect_redis::test()?;

    println!("Done!");
    Ok(())
}
