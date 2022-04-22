use mongodb::{bson::doc, bson::Document, options::ClientOptions, Collection};
use neo4rs::{query, Graph};
use std::{error::Error, sync::Arc};
use uuid::Uuid;

#[tokio::main]
async fn main() -> Result<(), Box<dyn Error>> {
    println!("starting...");

    let options = ClientOptions::parse("mongodb://localhost:27017").await?;
    let mongo_client = mongodb::Client::with_options(options)?;
    let theaters: Collection<Document> =
        mongo_client.database("sample_mflix").collection("theaters");
    let res = theaters
        .insert_one(
            doc! {
                "name": "Test"
            },
            None,
        )
        .await
        .expect("Insertion not possible!");
    println!("Theater: {:#?}", res);

    let redis_client = redis::Client::open("redis://127.0.0.1/")?;
    let mut con = redis_client.get_connection()?;

    let (k1, k2): (i32, i32) = redis::pipe()
        .atomic()
        .set::<&str, i32>("key_1", 42)
        .ignore()
        .set::<&str, i32>("key_2", 43)
        .ignore()
        .get("key_1")
        .get("key_2")
        .query(&mut con)?;

    println!("{:#?}", k1);
    println!("{:#?}", k2);

    let uri = "127.0.0.1:7687";
    let user = "neo4j";
    let pass = "h6UrzYQiRBEY95";
    let id = Uuid::new_v4().clone();

    let graph = Arc::new(Graph::new(&uri, user, pass).await.unwrap());
    let result = graph
        .run(query("CREATE (p:Person {id: $id})").param("id", 2))
        .await
        .unwrap();

    println!("{:#?}", result);

    println!("Done!");
    Ok(())
}
