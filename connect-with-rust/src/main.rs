use rusted_cypher::GraphClient;
//use mongodb::{Client, options::{ClientOptions, ResolverConfig}};
use std::env;

fn main() {
    println!("starting...");

    let neo4j_connection = GraphClient::connect("neo4j://localhost:7687");
    let mongo_connection = env::var("mongodb://localhost:27017").expect("You must set the MONGODB_URI environment var!");


    println!("Done!");
}
