import neo4j from 'neo4j-driver';
import { API } from './api';

(async () => {
    const driver = neo4j.driver('bolt://localhost:11003', neo4j.auth.basic('neo4j', 'password'));
    const session = driver.session({
        database: 'neo4j',
        defaultAccessMode: neo4j.session.WRITE,
    });
    console.log('\nConnected...\n');

    const port = 3000;
    const api = API.from(session);

    api.listen(port, () => {
        console.log(`Server started @http://localhost:${port}`);
    });

    // driver.close();
    // session.close();
    // console.log('\nConnections dropped!\n');

    // console.log('\nDistance from operator: ' + distanceFromOperator + 'm' + '!\n');
    // console.log('\nDistance from emergency: ' + distanceFromEmergency + 'm' + '!\n');
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
