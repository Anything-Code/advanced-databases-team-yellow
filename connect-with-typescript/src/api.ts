import express from 'express';
import { Session } from 'neo4j-driver';
import {
    calcDistanceFromEmergencyToOperator,
    calcDistanceFromOperatorToEmergency,
    deleteEmergencyWithRelations,
    deleteOperatorWithRelations,
    retreaveEmergenciesWithOperators,
    retreaveEmergencyWithOperators,
    retreaveOperatorsWithEmergencies,
    retreaveOperatorWithEmergencies,
} from './queries';

export class API {
    static from(session: Session) {
        const app = express();

        app.get('/', async (req, res) => {
            res.send('Pong!');
        });

        app.get('/operators', async (req, res) => {
            const o = await retreaveOperatorsWithEmergencies(session);

            res.send({ operators: o.toArray() });
        });
        app.get('/emergencies', async (req, res) => {
            const o = await retreaveEmergenciesWithOperators(session);

            res.send({ emergencies: o.toArray() });
        });
        app.get('/operator/:guid', async (req, res) => {
            const o = await retreaveOperatorWithEmergencies(session, req.params.guid);

            res.send({ operator: o });
        });
        app.get('/emergency/:guid', async (req, res) => {
            const o = await retreaveEmergencyWithOperators(session, req.params.guid);

            res.send({ emergency: o });
        });
        app.delete('/operator/:guid', async (req, res) => {
            await deleteOperatorWithRelations(session, req.params.guid);

            res.send();
        });
        app.delete('/emergency/:guid', async (req, res) => {
            await deleteEmergencyWithRelations(session, req.params.guid);

            res.send();
        });
        app.get('/operator-to-emergency/:guid', async (req, res) => {
            const o = await calcDistanceFromOperatorToEmergency(session, req.params.guid);

            res.send({ distance: o });
        });
        app.get('/emergency-to-operator/:guid', async (req, res) => {
            const o = await calcDistanceFromEmergencyToOperator(session, req.params.guid);

            res.send({ distance: o });
        });

        return app;
    }
}
