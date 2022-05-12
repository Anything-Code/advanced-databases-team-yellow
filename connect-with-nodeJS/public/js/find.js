const patientForm = document.getElementById('patient-form');
const patientAddress= document.getElementById('patient-address');
const volunteerId = document.getElementById('volunteer-id');
const volunteerAddress= document.getElementById('volunteer-address');

// GET to API 
async function findVolunteer(e){
    e.preventDefault();

    if(patientAddress.value===''){
        alert('Please fill in fields');
    }

    const res =await fetch('/api/v1/volunteers/resolve',{

        'method':'POST',
        'headers':{
            'Content-Type':'text/plain'
        },
        'body': patientAddress.value
    });

    //console.log(patientAddress.coordinates); 

}

async function getVolunteers(){
    const res = await fetch('/api/v1/volunteers');
    const data = await res.json();

    // console.log(data);
    const volunteers = data.data.map(volunteer =>{
        return {
            type : 'Feature',
            'geometry': {
                        'type': 'Point',
                        'coordinates': [volunteer.location.coordinates[0], volunteer.location.coordinates[1]]
                    },
        }
    });

    findVolunteer();
}


patientForm.addEventListener('find', findVolunteer)

// function calcCrow(lat1, lon1, lat2, lon2) {
//     lat1=patientAddress.value.coordinates.loc[0].longitude
//     lat1=patientAddress.value.coordinates.loc[0].latitude

//         var R = 6371; // km
//         var dLat = toRad(lat2-lat1);
//         var dLon = toRad(lon2-lon1);
//         var lat1 = toRad(lat1);
//         var lat2 = toRad(lat2);

//         var a = Math.sin(dLat/2) * Math.sin(dLat/2) +Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(lat1) * Math.cos(lat2); 
//         var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
//         var d = R * c;
//     return d;
// }

//     // Converts numeric degrees to radians
// function toRad(Value) {
//         return Value * Math.PI / 180;
// }


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