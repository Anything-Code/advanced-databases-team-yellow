import faker from '@faker-js/faker';
import axios from 'axios';
import { randomInt, randomUUID } from 'crypto';
import { List } from 'immutable';
import { GraphDatabase } from 'neo4j';
import { Session } from 'neo4j-driver';
import { EmergencyType, Operator } from './types';

// API-Playground here: https://nominatim.openstreetmap.org/ui/search.html?street=Alfred-Jost-Stra%C3%9Fe+38&postalcode=69124
export async function geocoding(
    streetAndHouseNr: string,
    city?: string,
    county?: string,
    state?: string,
    country?: string,
    postalCode?: string
) {
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
}

export async function reverseGeocoding(coords: [number, number]) {
    const res = await axios.get(
        // 'https://nominatim.openstreetmap.org/search.php?street=Alfred-Jost-Stra%C3%9Fe+38&format=jsonv2'
        'https://nominatim.openstreetmap.org/reverse?format=jsonv2',
        { params: { lat: coords[0], lon: coords[1] } }
    );

    // console.log(res);

    if (res.data.length === 0) {
        throw 'No results found!';
    }

    return res.data.display_name;
}

export function randomOperatorType(): 'Ambulance' | 'Firefighter' | 'Police' {
    const r = randomInt(3);

    switch (r) {
        case 0:
            return 'Ambulance';
        case 1:
            return 'Firefighter';
        case 2:
            return 'Police';
        default:
            throw 'Error in randomOperatorType.';
    }
}

function adequateEmergencyType(operator: Operator): EmergencyType {
    switch (operator.type) {
        case 'Ambulance':
            return 'Car-Accident';
        case 'Firefighter':
            return 'Fire';
        case 'Police':
            return 'Assault';

        default:
            throw 'Error in adequateEmergencyType.';
    }
}

export async function seedOperators(session: Session, target: number, res = List<Operator>()): Promise<List<Operator>> {
    const [lat, lon] = await geocoding('Dantestrasse 1', 'Heidelberg');
    const [nearbyLat, nearbyLon] = faker.address.nearbyGPSCoordinate([lat, lon]);

    const operator: Operator = {
        id: randomUUID(),
        type: randomOperatorType(),
        lat: parseFloat(nearbyLat),
        lon: parseFloat(nearbyLon),
    };

    const [nearbyEmergencyLat, nearbyEmergencyLon] = faker.address.nearbyGPSCoordinate([lat, lon]);
    const address = await reverseGeocoding([parseFloat(nearbyEmergencyLat), parseFloat(nearbyEmergencyLon)]);

    const emergency = {
        id: randomUUID(),
        type: adequateEmergencyType(operator),
        reported: faker.date.past().toISOString(),
        lat: parseFloat(nearbyEmergencyLat),
        lon: parseFloat(nearbyEmergencyLon),
        address,
    };

    const operatorResult = await session.run(
        'CREATE (a:Operator {id: $id, type: $type, lat: $lat, lon: $lon}) RETURN a',
        operator
    );
    const emergencyResult = await session.run(
        'CREATE (a:Emergency {id: $id, type: $type, reported: $reported, lat: $lat, lon: $lon, address: $address}) RETURN a',
        emergency
    );
    const relationshipResult = await session.run(
        `MATCH (a:Operator), (b:Emergency)
            WHERE a.id = '${operator.id}' AND b.id = '${emergency.id}'
            CREATE (a)-[r:IS_ASSIGNED_TO]->(b)
            RETURN type(r)`
    );

    // result.records[0].get('a').properties contains the inserted props without <id>

    console.log(res.size + 1 + ' of ' + target + ' Operator/s created...');

    if (res.size >= target) {
        return res;
    }

    return seedOperators(session, target, List([...res, operator]));
}
