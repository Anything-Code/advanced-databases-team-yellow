use mongodb::bson::Uuid;
use neo4rs::{query, Graph};
use std::sync::Arc;

async fn connect() -> Arc<Graph> {
    let uri = "127.0.0.1:7687";
    let user = "neo4j";
    let pass = "h6UrzYQiRBEY95";

    let neo4j_client = Arc::new(Graph::new(uri, user, pass).await.unwrap());

    return neo4j_client;
}

pub async fn test() {
    let client = connect().await;
    let id = Uuid::new().clone().to_string();

    let result = client
        .run(query("CREATE (p:Person {id: $id})").param("id", id))
        .await
        .expect("Insertion (Neo4J) not possible!");

    println!("{:#?}", result);
}
