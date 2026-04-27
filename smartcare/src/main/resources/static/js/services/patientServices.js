import { API_BASE_URL } from "../config/config.js";
const PATIENT_API = API_BASE_URL + '/patient'


async function patientSignup(data) {
    const request = await fetch(PATIENT_API, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    }).then(response => {
        return JSON.parse(response);
    })
    .catch(error => { alert(error.message) });
}

async function patientLogin(data) {
    const request = await fetch(PATIENT_API, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    }).then(response => {
        return JSON.parse(response);
    })
    .catch(error => { alert(error.message) });
}


async function getPatientData(token) {
    const request = await fetch(PATIENT_API
        + '?token=' + token
        , {
        method: 'GET',
        headers: { 'Content-Type': 'application/json' }        
    }).then(response => {
        return JSON.parse(response);
    })
    .catch(error => { alert(error.message) });
}

async function getPatientAppointments(id, token ,user) {
    let role = '';
    if(user == 'patient' || user == 'doctor') {
        role = user;
    }
    const request = await fetch(PATIENT_API
        + '?id=' + id
        + '&token=' + token
        + '&user=' + role
        , {
        method: 'GET',
        headers: { 'Content-Type': 'application/json' }        
    }).then(response => {
        return JSON.parse(response);
    })
    .catch(error => { 
        alert(error.message) 
        return null
    });
}