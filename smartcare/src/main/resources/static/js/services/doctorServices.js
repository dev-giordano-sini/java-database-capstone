import { API_BASE_URL } from "../config/config.js";
const DOCTOR_API = API_BASE_URL + '/doctor';

async function getDoctors() {
    const request = await fetch(DOCTOR_API, {
        method: 'GET',
        headers: { 'Content-Type': 'application/json' }        
    }).then(response => {
        return JSON.parse(response);
    })
    .catch(error => { [] });
}

async function deleteDoctor(id, token) {
    const request = await fetch(DOCTOR_API 
        + '?id=' + id 
        + '&token='+ token, {
        method: 'DELETE',
        headers: { 'Content-Type': 'application/json' }        
    }).then(response => {
        return JSON.parse(response);
    })
    .catch(error => { [] });
}

async function saveDoctor(doctor, token) {
    const request = await fetch(DOCTOR_API 
        + '?&token='+ token, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(doctor)
    }).then(response => {
        showMessage("success");
    })
    .catch(error => { alert(error.message) });
}

async function filterDoctors(name, time, specialty, token) {
    const request = await fetch(DOCTOR_API 
        + '?name=' + name 
        + '&time=' + time
        + '&specialty=' + specialty
        +  '&token='+ token, {
        method: 'GET',
        headers: { 'Content-Type': 'application/json' }        
    }).then(response => {
        return JSON.parse(response);
    })
    .catch(error => { [] });
}
