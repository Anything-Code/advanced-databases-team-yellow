const patientForm = document.getElementById('patient-form');
const patientAddress= document.getElementById('patient-address');
const volunteerId = document.getElementById('volunteer-id');
const volunteerAddress= document.getElementById('volunteer-address');

// GET to API 
async function findVolunteer(e){
    e.preventDefault();

    if(patientAddress.value===''){
        alert('Please fill in fields');
        return 0;
    }

    const sendBody={
        addr: patientAddress.value
    }

    const res =await fetch('/api/v1/resolve',{

        'method':'POST',
        'headers':{
            'Content-Type':'application/json'
        },
        'body': JSON.stringify(sendBody)
    });
    var loc = await res.json();
    var volunteers = await getVolunteers();

    var lat1 = loc.coordinates[0];
    var long1 = loc.coordinates[1];
    for(var vol of volunteers)
    {
        var lat2 =vol.coordinates[0];
        var long2=vol.coordinates[1];
        var dist = calcCrow(lat1, long1, lat2 , long2);
        vol.Distance = dist;        
    }

    volunteers.sort((vol1, vol2) => (vol1.Distance > vol2.Distance) ? 1 : -1);
    var parent = document.getElementById('Results');
    var html = '';
    for(var vol of volunteers)
    {
        html+= 'Distance: ' + vol.Distance + ', Coord: [' + vol.coordinates[0] + '-' + vol.coordinates[1] + ']<br>';
    }
    parent.innerHTML = html;
}

async function getVolunteers(){
    const res = await fetch('/api/v1/volunteers');
    const data = await res.json();

    // console.log(data);
    return data.data.map(volunteer =>{
        return {'coordinates': [volunteer.location.coordinates[0], volunteer.location.coordinates[1]]}
    });

    //findVolunteer();
}

document.getElementById('findbutton').addEventListener('click', findVolunteer, false);
// patientForm.addEventListener('find',findVolunteer)

function calcCrow(lat1, lon1, lat2, lon2) {

        var R = 6371; // km
        var dLat = toRad(lat2-lat1);
        var dLon = toRad(lon2-lon1);
        var lat1 = toRad(lat1);
        var lat2 = toRad(lat2);

        var a = Math.sin(dLat/2) * Math.sin(dLat/2) +Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(lat1) * Math.cos(lat2); 
        var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
        var d = R * c;
    return d;
}

    // Converts numeric degrees to radians
function toRad(Value) {
        return Value * Math.PI / 180;
}


function getNearest(sortedVolunteers, volunteerId, searchResult) {
    const lats = [
        sortedVolunteers.features[volunteerId].geometry.coordinates[1],
        searchResult.coordinates[1]
    ];
    const lons = [
        sortedVolunteers.features[volunteerId].geometry.coordinates[0],
        searchResult.coordinates[0]
    ];
    const sortedLons = lons.sort((a, b) => {
        if (a > b) {
            return 1;
        }
        if (a.distance < b.distance) {
            return -1;
        }
        return 0;
    });
    const sortedLats = lats.sort((a, b) => {
        if (a > b) {
            return 1;
        }
        if (a.distance < b.distance) {
            return -1;
        }
        return 0;
    });
    return [
        [sortedLons[0], sortedLats[0]],
        [sortedLons[1], sortedLats[1]]
    ];
}


geocoder.on('result', (event) => {
    const searchResult = event.result.geometry;
    const options = { units: 'miles' };
    for (const volunteer of volunteer.features) {
        volunteer.properties.distance = turf.distance(
            searchResult,
            volunteer.geometry,
            options
        );
    }
    volunteer.features.sort((a, b) => {
        if (a.properties.distance > b.properties.distance) {
            return 1;
        }
        if (a.properties.distance < b.properties.distance) {
            return -1;
        }
        return 0; // a must be equal to b
    });
    const listings = document.getElementById('listings');
    while (listings.firstChild) {
        listings.removeChild(listings.firstChild);
    }
    buildLocationList(volunteers);
    
});