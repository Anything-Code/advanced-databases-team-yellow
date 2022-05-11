use std::{
    error::Error,
    sync::{Arc, Mutex},
};

use redis::{ControlFlow, PubSubCommands};

use crate::{car::Car, connect_redis};

pub fn subscribe(channel: String, cars: Arc<Mutex<Vec<Car>>>) -> Result<(), Box<dyn Error>> {
    let _ = tokio::spawn(async move {
        let mut conn = connect_redis::connect().unwrap();

        let _: () = conn
            .subscribe(&[channel], |msg| {
                let received: String = msg.get_payload().unwrap();

                println!("\n{}", received);

                return ControlFlow::Continue;
            })
            .unwrap();
    });

    Ok(())
}
