use std::{error::Error, sync::mpsc::SyncSender};

use either::Either;
use redis::{ControlFlow, PubSubCommands};

use crate::{car::Car, connect_redis};

pub fn subscribe(
    channel: String,
    car_sender: SyncSender<Either<Car, String>>,
) -> Result<(), Box<dyn Error>> {
    let _ = tokio::spawn(async move {
        let mut conn = connect_redis::connect().unwrap();

        let _: () = conn
            .subscribe(&[channel], |msg| {
                let received: String = msg.get_payload().unwrap();

                car_sender.send(Either::Right(received)).unwrap();

                return ControlFlow::Continue;
            })
            .unwrap();
    });

    Ok(())
}
