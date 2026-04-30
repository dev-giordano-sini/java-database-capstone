import { API_BASE_URL } from "../config/config.js";
const PATIENT_API = API_BASE_URL + '/patient'


export async function patientSignup(data) {
    const request = await fetch(PATIENT_API 
        + '/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    }).then(response => {
        return JSON.parse(response);
    })
    .catch(error => { alert(error.message) });
}

export async function patientLogin(data) {
    const request = await fetch(PATIENT_API +
        'login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    }).then(response => {
        return JSON.parse(response);
    })
    .catch(error => { alert(error.message) });
}


export async function getPatientData(token) {
    const request = await fetch(PATIENT_API
        + 'patient'        
        + '?token=' + token
        , {
        method: 'GET',
        headers: { 'Content-Type': 'application/json' }        
    }).then(response => {
        return JSON.parse(response);
    })
    .catch(error => { alert(error.message) });
}

export async function getPatientAppointments(token, id, user) {
    let role = '';
    if(user == 'patient' || user == 'doctor') {
        role = user;
    }
    const request = await fetch(PATIENT_API
        + 'appointments'
        
        + '&id=' + id        
        + '&role=' + role
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

export async function filterAppointments(token, condition, name) {   
    const request = await fetch(PATIENT_API
        + 'filter_appointment'
        + '?token=' + token
        + '&name=' + name        
        + '&condition=' + condition
        , {
        method: 'GET',
        headers: { 'Content-Type': 'application/json' }        
    }).then(response => {
        return JSON.parse(response);
    })
    .catch(error => { 
        alert(error.message) 
        return []
    });
}