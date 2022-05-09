export type OperatorType = 'Police' | 'Firefighter' | 'Ambulance';
export type EmergencyType = 'Assault' | 'Fire' | 'Car-Accident';

export interface Operator {
    id: string;
    type: OperatorType;
    lat: number;
    lon: number;
}
export interface Emergency {
    id: string;
    address: string;
    type: EmergencyType;
    lat: number;
    lon: number;
    reported: string;
}

export interface OperatorWithEmergency extends Operator {
    emergencies: Emergency;
}

export interface EmergencyWithOperators extends Emergency {
    operators: Array<Operator>;
}
