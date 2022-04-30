import { GraphDatabase } from 'neo4j';
import { MongoClient } from 'mongodb';
import { createClient } from 'redis';
import axios from 'axios';

const getLatLong = async (
    streetAndHouseNr: string,
    city?: string,
    county?: string,
    state?: string,
    country?: string,
    postalCode?: string
) => {
    const res = await axios.get(
        // 'https://nominatim.openstreetmap.org/search.php?street=Alfred-Jost-Stra%C3%9Fe+38&format=jsonv2'
        'https://nominatim.openstreetmap.org/search.php?format=jsonv2',
        { params: { street: streetAndHouseNr, city, county, state, country, postalcode: postalCode } }
    );

    // console.log(res);

    if (res.data.length === 0) {
        throw 'No results found!';
    }

    const lat = res.data[0].lat;
    const lon = res.data[0].lon;

    return [parseFloat(lat), parseFloat(lon)];
};

(async () => {
    const res = await getLatLong('Alfred-Jost-Straße 38');

    console.log(res);
    // const neo4jClient = new GraphDatabase('http://neo4j:h6UrzYQiRBEY95@localhost:7474');
    // const mongoClient = await MongoClient.connect('mongodb://localhost:27017');
    // const redisClient = createClient();
    // redisClient.on('error', (err) => console.log('Redis Client Error', err));
    // await redisClient.connect();
    // await redisClient.set('key', 'value');
    // const value = await redisClient.get('key');
    // console.log('Value found in Redis:', value);
    // await mongoClient.close();
    // await redisClient.disconnect();
})();
