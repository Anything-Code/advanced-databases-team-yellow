import { GraphDatabase } from 'neo4j';
import { MongoClient } from 'mongodb';
import { createClient } from 'redis';

(async () => {
    const neo4jClient = new GraphDatabase('http://neo4j:h6UrzYQiRBEY95@localhost:7474');
    const mongoClient = await MongoClient.connect('mongodb://localhost:27017');
    const redisClient = createClient();

    redisClient.on('error', (err) => console.log('Redis Client Error', err));

    await redisClient.connect();

    await redisClient.set('key', 'value');
    const value = await redisClient.get('key');

    console.log('Value found in Redis:', value);

    await mongoClient.close();
    await redisClient.disconnect();
})();
