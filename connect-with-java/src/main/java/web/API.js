var express = require("express");
var app = express();
const MongoClient = require('mongodb').MongoClient;
const url = "mongodb://localhost:27017/";
const client = new MongoClient(url);

const path = require('path');

const port = process.env.PORT || 8080;

// sendFile will go here
app.get('/', function(req, res) {
  res.sendFile('/map.html', {root: __dirname});
});

app.listen(port);
console.log('Server started at http://localhost:' + port);

app.get("/url", async (req, res, next) => {

    var result = await getMongoZones();

    res.json(result);
   });


async function getMongoZones(){
    await client.connect();

    const db = client.db("EmergencyApp");
    const collection = db.collection("EmergencyZone");

    var response = await collection.find({}).toArray();
    var allZones = [];
    var i = 0;
    
    while(i < response.length){
        const zone ={
            [response[i].MapId] : {
                center: { lat: response[i].location.coordinates[1], lng: response[i].location.coordinates[0] },
                population: response[i].Radius,
                color: response[i].Color,
                },};
        allZones[i] = zone;
        i++;
    }

    


    //const zone2 = { lat: response[0].location.coordinates[0], lng: response[0].location.coordinates[1] };
    return allZones;
}