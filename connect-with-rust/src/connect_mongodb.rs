use mongodb::{bson::doc, bson::Document, options::ClientOptions, Collection};
use std::error::Error;

async fn connect() -> Result<mongodb::Client, Box<dyn Error>> {
    let options = ClientOptions::parse("mongodb://localhost:27017").await?;
    let mongo_client = mongodb::Client::with_options(options)?;

    return Ok(mongo_client);
}

pub async fn test() -> Result<(), Box<dyn Error>> {
    let client = connect().await?;
    let theaters: Collection<Document> = client.database("test").collection("theaters");
    let res = theaters
        .insert_one(
            doc! {
                "name": "Test"
            },
            None,
        )
        .await
        .expect("Insertion (MongoDB) not possible!");
    println!("Theater: {:#?}", res);

    return Ok(());
}
