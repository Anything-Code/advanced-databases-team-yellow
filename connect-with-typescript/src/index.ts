function calcDistanceFromOperatorToEmergency(operatorId: string): Promise<number> {
    throw new Error('Function not implemented.');
}

function calcDistanceFromEmergencyToOperator(emergencyId: string): Promise<number> {
    throw new Error('Function not implemented.');
}

(async () => {
    console.log('\nStarted...\n');

    const operatorId = '526bc011-284b-4666-9192-525fde4d1a37';
    const emergencyId = 'f554c706-5016-40e2-94d0-94bc4d11507c';

    const distanceFromOperator: number = await calcDistanceFromOperatorToEmergency(operatorId);
    const distanceFromEmergency: number = await calcDistanceFromEmergencyToOperator(emergencyId);

    console.log('\nDistance from operator: ' + distanceFromOperator + 'm' + '!\n');
    console.log('\nDistance from emergency: ' + distanceFromEmergency + 'm' + '!\n');
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
