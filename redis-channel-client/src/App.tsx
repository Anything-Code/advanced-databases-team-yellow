import React, { useCallback, useEffect, useState } from "react";
import logo from "./logo.svg";
import "./App.css";
import {
  MapContainer,
  Marker,
  Polyline,
  Popup,
  TileLayer,
} from "react-leaflet";
import layers from "leaflet/dist/images/layers.png";
import layers2x from "leaflet/dist/images/layers-2x.png";
import markerIcon from "leaflet/dist/images/marker-icon.png";
import markerIcon2x from "leaflet/dist/images/marker-icon-2x.png";
import markerShadow from "leaflet/dist/images/marker-shadow.png";
import policeIcon from "./img/police.png";
import ambulanceIcon from "./img/ambulance.png";
import firetruckIcon from "./img/firetruck.png";
import { Icon } from "leaflet";

export function sleep(ms: number) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

function App() {
  const position = { lat: 49.4141772, lng: 8.6508929 };

  const [policePos, setPolicePos] = useState<{
    lat: number;
    lng: number;
  }>(position);
  const [ambulancePos, setAmbulancePos] = useState<{
    lat: number;
    lng: number;
  }>(position);
  const [firetruckPos, setFiretruckPos] = useState<{
    lat: number;
    lng: number;
  }>(position);
  const [manhuntPos, setManhuntPos] = useState<{
    lat: number;
    lng: number;
  }>({ lng: 8.6508929, lat: 49.4141772 });
  const [stabbingPos, setStabbingPos] = useState<{
    lat: number;
    lng: number;
  }>({ lng: 8.657238054886221, lat: 49.3784348 });
  const [firehazardPos, setFirehazardPos] = useState<{
    lat: number;
    lng: number;
  }>({ lng: 8.6839833, lat: 49.41273485 });
  const [socket, setSocket] = useState<WebSocket>(
    new WebSocket("ws://localhost:8080")
  );
  const [carsOccupied, setCarsOccupied] = useState<Record<string, boolean>>({
    BWL_A_1: false,
    BWL_A_2: false,
    BWL_A_3: false,
  });
  const toggleJoin = useCallback(
    (license_plate: string) => {
      switch (license_plate) {
        case "BWL_A_1":
          if (carsOccupied.BWL_A_1) {
            socket.send("Leave 1");
            setCarsOccupied((old) => ({ ...old, BWL_A_1: false }));
          } else {
            socket.send("Join 1");
            setCarsOccupied((old) => ({ ...old, BWL_A_1: true }));
          }
          break;
        case "BWL_A_2":
          if (carsOccupied.BWL_A_2) {
            socket.send("Leave 2");
            setCarsOccupied((old) => ({ ...old, BWL_A_2: false }));
          } else {
            socket.send("Join 2");
            setCarsOccupied((old) => ({ ...old, BWL_A_2: true }));
          }
          break;
        case "BWL_A_3":
          if (carsOccupied.BWL_A_3) {
            socket.send("Leave 3");
            setCarsOccupied((old) => ({ ...old, BWL_A_3: true }));
          } else {
            socket.send("Join 3");
            setCarsOccupied((old) => ({ ...old, BWL_A_3: true }));
          }
          break;

        default:
          break;
      }
    },
    [carsOccupied.BWL_A_1, carsOccupied.BWL_A_2, carsOccupied.BWL_A_3, socket]
  );

  useEffect(() => {
    try {
      socket.addEventListener("open", async (_event) => {
        socket.send("Client connected!");
        // await sleep(5000);
        // socket.send("Join 1");
        // await sleep(5000);
        // socket.send("Leave 1");
        // socket.send("Join 2");
        // await sleep(5000);
        // socket.send("Leave 2");
        // socket.send("Join 3");
        // await sleep(5000);
        // socket.send("Leave 3");
        // Deno.exit();
      });
      socket.addEventListener("message", (event) => {
        const r =
          /(?<channel>\w+): (?<type>\w+) (?<license_plate>\(\w+_\w+_\w\)).*Lat: (?<Lat>\d+\.\d+), Lon: (?<Lon>\d*.\d*)/gm;
        const match = r.exec(event.data);
        if (match != null) {
          const type = match[2];
          const lng = parseFloat(match[4]);
          const lat = parseFloat(match[5]);
          const coords = { lat, lng };
          if (type === "Police") {
            setPolicePos(() => coords);
          }
          if (type === "Ambulance") {
            setAmbulancePos(() => coords);
          }
          if (type === "Firetruck") {
            setFiretruckPos(() => coords);
          }
          console.log(event.data);
        }
      });
    } catch (err) {
      console.error(`Could not connect to WebSocket: '${err}'`);
    }
  }, [socket]);

  return (
    <MapContainer
      style={{ height: "100vh", width: "100vw" }}
      center={position}
      zoom={13}
      scrollWheelZoom={true}
    >
      <TileLayer
        attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
      />
      <Marker
        position={manhuntPos}
        icon={
          new Icon({
            iconUrl: markerIcon,
          })
        }
      >
        <Popup>#Manhunt-1 8.6508929 49.4141772</Popup>
      </Marker>

      <Marker
        position={stabbingPos}
        icon={
          new Icon({
            iconUrl: markerIcon,
          })
        }
      >
        <Popup>#Stabbing-1 8.657238054886221 49.3784348</Popup>
      </Marker>

      <Marker
        position={firehazardPos}
        icon={
          new Icon({
            iconUrl: markerIcon,
          })
        }
      >
        <Popup>#Firehazard-1 8.6839833 49.41273485</Popup>
      </Marker>

      <Polyline positions={[policePos, ambulancePos]} />
      <Polyline positions={[ambulancePos, firetruckPos]} />
      <Polyline positions={[firetruckPos, policePos]} />

      <Marker
        position={policePos}
        icon={
          new Icon({
            iconUrl: policeIcon,
            iconSize: [80, 60],
            iconAnchor: [40, 30],
          })
        }
      >
        <Popup>
          <span style={{ padding: "1rem" }}>BWL_A_1</span>
          {carsOccupied.BWL_A_1 ? (
            <button onClick={() => toggleJoin("BWL_A_1")}>Leave Emergency #Manhunt-1</button>
          ) : (
            <button onClick={() => toggleJoin("BWL_A_1")}>Join Emergency #Manhunt-1</button>
          )}
        </Popup>
      </Marker>

      <Marker
        position={ambulancePos}
        icon={
          new Icon({
            iconUrl: ambulanceIcon,
            iconSize: [80, 80],
            iconAnchor: [40, 40],
          })
        }
      >
        <Popup>
          <span style={{ padding: "1rem" }}>BWL_A_2</span>
          {carsOccupied.BWL_A_2 ? (
            <button onClick={() => toggleJoin("BWL_A_2")}>Leave Emergency #Stabbing-1</button>
          ) : (
            <button onClick={() => toggleJoin("BWL_A_2")}>Join Emergency #Stabbing-1</button>
          )}
        </Popup>
      </Marker>

      <Marker
        position={firetruckPos}
        icon={
          new Icon({
            iconUrl: firetruckIcon,
            iconSize: [80, 80],
            iconAnchor: [40, 40],
          })
        }
      >
        <Popup>
          <span style={{ padding: "1rem" }}>BWL_A_3</span>
          {carsOccupied.BWL_A_3 ? (
            <button onClick={() => toggleJoin("BWL_A_3")}>Leave Emergency #Firehazard-1</button>
          ) : (
            <button onClick={() => toggleJoin("BWL_A_3")}>Join Emergency #Firehazard-1</button>
          )}
        </Popup>
      </Marker>
    </MapContainer>
  );
}

export default App;
