import { seedOperators } from './util';
import neo4j from 'neo4j-driver';

(async () => {
    console.log('\nSeeding started...\n');

    const driver = neo4j.driver('neo4j://localhost', neo4j.auth.basic('neo4j', 'password'));
    const session = driver.session({
        database: 'neo4j',
        defaultAccessMode: neo4j.session.WRITE,
    });

    const _fakes = await seedOperators(session, 100);

    console.log('\nSeeding done!\n');

    await session.close();
    await driver.close();
})();
