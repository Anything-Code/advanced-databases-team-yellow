# Advanced databases - Team yellow

## Brainstorming

Codename R.E.T.A.R.D or  
Codename P.E.S.S  
Realtime.Emergency.Transmission.Appropriating.Response.Data or  
Prepared.Emergency.Security.System  

![Brainstorming](./img/night-crawler-brainstorming.png)

## Use-cases

### 1. Diagram

![Use case diagram](./img/use-case-diagram.png)

### 2. Table

| Use-case                                                          | Database | Reason                                                              |
| :---------------------------------------------------------------- | :------- | :------------------------------------------------------------------ |
| Report emergency                                                  | Redis    | Real-time transmission without delay                                |
| Read location and distance to user                                | Redis    | Pub-sub emergency channel with GPS-data                             |
| Read location and distance to helpers                             | Redis    | Pub-sub emergency channel with GPS-data                             |
| Store current areas of emergencies as GeoJson                     | MongoDB  | Mongo works well with GeoJson and supports geospacial algorithms    |
| Store dangerous areas to be arround at night (as GeoJson polygon) | MongoDB  | Mongo works well with GeoJson and supports geospacial algorithms    |
| Compute distance from user to helpers (and vice-verca)            | Neo4J    | Neo4J supports path-tracing algorithms with GPS-data out of the box |

