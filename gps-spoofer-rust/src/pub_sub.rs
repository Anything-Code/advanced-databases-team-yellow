use std::error::Error;

use either::Either;
use tokio::{sync::mpsc::Sender, task::JoinHandle};

use crate::{car::Car, connect_redis};

pub async fn subscribe(
    channel: String,
    tx: Sender<Either<Car, String>>,
) -> Result<JoinHandle<()>, Box<dyn Error>> {
    let handle = tokio::spawn(async move {
        let mut conn = connect_redis::connect().unwrap();
        let mut ps = conn.as_pubsub();

        ps.subscribe(channel).unwrap();

        loop {
            let msg = ps.get_message().unwrap();
            let payload: String = msg.get_payload().unwrap();
            // println!("channel '{}': {}", msg.get_channel_name(), payload);
            tx.send(Either::Right(payload)).await.unwrap();
        }
    });

    Ok(handle)
    // let _: () = conn
    //     .subscribe(&[channel], |msg| {
    //         let received: String = msg.get_payload().unwrap();

    //         tx.send(Either::Right(received)).await;

    //         return ControlFlow::Continue;
    //     })
    //     .unwrap();
}
