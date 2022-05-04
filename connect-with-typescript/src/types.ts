export type EmergencyType = 'Assault' | 'Fire' | 'Car-Accident';

export interface Operator {
    id: string;
    type: 'Ambulance' | 'Firefighter' | 'Police';
    lat: number;
    lon: number;
}
