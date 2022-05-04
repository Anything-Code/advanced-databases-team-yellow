use redis::Connection;
use std::error::Error;

pub fn connect() -> Result<Connection, Box<dyn Error>> {
    let redis_client = redis::Client::open("redis://127.0.0.1")?;
    let con = redis_client
        .get_connection()
        .expect("Connection failed. [Redis]");

    return Ok(con);
}

pub fn test() -> Result<(), Box<dyn Error>> {
    let mut connection = connect().unwrap();

    let (k1, k2): (i32, i32) = redis::pipe()
        .atomic()
        .set::<&str, i32>("key_1", 42)
        .ignore()
        .set::<&str, i32>("key_2", 43)
        .ignore()
        .get("key_1")
        .get("key_2")
        .query(&mut connection)?;

    println!("{:#?}", k1);
    println!("{:#?}", k2);

    return Ok(());
}
