import { List } from 'immutable';
import { Session } from 'neo4j-driver';
import { EmergencyWithOperators, OperatorWithEmergency } from './types';

export async function calcDistanceFromOperatorToEmergency(session: Session, operatorId: string): Promise<number> {
    const res = await session.run(`MATCH (o:Operator {id: $operatorId})-[:IS_ASSIGNED_TO]->(e:Emergency) RETURN o, e`, {
        operatorId,
    });

    if (res.records.length === 0) {
        throw 'Nothing like that was found...';
    }

    const operatorProps = res.records[0].get('o').properties;
    const emergencyProps = res.records[0].get('e').properties;

    const distRes = await session.run(
        `WITH 
            point({latitude: $operatorLat, longitude: $operatorLon}) AS p1, 
            point({latitude: $emergencyLat, longitude: $emergencyLon}) AS p2 
            RETURN point.distance(p1, p2) / 1000 AS km`,
        {
            operatorLat: operatorProps.lat,
            operatorLon: operatorProps.lon,
            emergencyLat: emergencyProps.lat,
            emergencyLon: emergencyProps.lon,
        }
    );

    const distanceAsKm: number = distRes.records[0].get('km');

    return distanceAsKm;
}

export async function calcDistanceFromEmergencyToOperator(session: Session, emergencyId: string): Promise<number> {
    const res = await session.run(
        `MATCH (e:Emergency {id: $emergencyId})<-[:IS_ASSIGNED_TO]-(o:Operator) RETURN e, o`,
        {
            emergencyId,
        }
    );

    if (res.records.length === 0) {
        throw 'Nothing like that was found...';
    }

    const emergencyProps = res.records[0].get('e').properties;
    const operatorProps = res.records[0].get('o').properties;

    const distRes = await session.run(
        `WITH 
            point({latitude: $emergencyLat, longitude: $emergencyLon}) AS p1, 
            point({latitude: $operatorLat, longitude: $operatorLon}) AS p2 
            RETURN point.distance(p1, p2) / 1000 AS km`,
        {
            emergencyLat: emergencyProps.lat,
            emergencyLon: emergencyProps.lon,
            operatorLat: operatorProps.lat,
            operatorLon: operatorProps.lon,
        }
    );

    const distanceAsKm: number = distRes.records[0].get('km');

    return distanceAsKm;
}

export async function retreaveOperatorsWithEmergencies(session: Session): Promise<List<OperatorWithEmergency>> {
    const res = await session.run(`MATCH (o:Operator)-[:IS_ASSIGNED_TO]->(e:Emergency) RETURN o, e`);

    if (res.records.length === 0) {
        throw 'Nothing like that was found...';
    }

    const operatorsWithEmergencies = res.records.map((item) => ({
        ...item.get('o').properties,
        emergency: item.get('e').properties,
    }));

    return List(operatorsWithEmergencies);
}

export async function retreaveEmergenciesWithOperators(session: Session): Promise<List<EmergencyWithOperators>> {
    const res = await session.run(`MATCH (e:Emergency)<-[:IS_ASSIGNED_TO]-(o:Operator) RETURN o, e`);

    if (res.records.length === 0) {
        throw 'Nothing like that was found...';
    }

    const emergenciesWithOperators = res.records.map((item) => ({
        ...item.get('e').properties,
        operators: [item.get('o').properties],
    }));

    return List(emergenciesWithOperators);
}

export async function retreaveOperatorWithEmergencies(session: Session, guid: string): Promise<OperatorWithEmergency> {
    const res = await session.run(
        `MATCH (o:Operator)-[:IS_ASSIGNED_TO]->(e:Emergency) WHERE o.id = $guid RETURN o, e`,
        {
            guid,
        }
    );

    if (res.records.length === 0) {
        throw 'Nothing like that was found...';
    }

    const operatorsWithEmergencies = {
        ...res.records[0].get('o').properties,
        emergency: res.records[0].get('e').properties,
    };

    return operatorsWithEmergencies;
}

export async function retreaveEmergencyWithOperators(session: Session, guid: string): Promise<EmergencyWithOperators> {
    const res = await session.run(
        `MATCH (e:Emergency)<-[:IS_ASSIGNED_TO]-(o:Operator) WHERE e.id = $guid RETURN o, e`,
        { guid }
    );

    if (res.records.length === 0) {
        throw 'Nothing like that was found...';
    }

    const emergenciesWithOperators = {
        ...res.records[0].get('e').properties,
        operators: [res.records[0].get('o').properties],
    };

    return emergenciesWithOperators;
}

export async function deleteOperatorWithRelations(session: Session, guid: string): Promise<void> {
    await session.run(`MATCH (o:Operator) WHERE o.id = $guid DETACH DELETE o`, {
        guid,
    });
}

export async function deleteEmergencyWithRelations(session: Session, guid: string): Promise<void> {
    await session.run(`MATCH (e:Emergency) WHERE e.id = $guid DETACH DELETE e`, {
        guid,
    });
}
