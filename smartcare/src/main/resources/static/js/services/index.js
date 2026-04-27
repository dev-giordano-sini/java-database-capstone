const ADMIN_API = API_BASE_URL + '/admin';
const DOCTOR_API = API_BASE_URL + '/doctor/login'
window.onload = function () {
    const adminBtn = document.getElementById('adminLogin');
    if (adminBtn) {
        adminBtn.addEventListener('click', () => {
            openModal('adminLogin');
        });
    }
    const admin = { username, password };    
    const adminLoginHandler = makeRequest(ADMIN_API, admin);

    const doctor = { username, password };
    const doctorBtn = document.getElementById('doctorLogin');
    if (doctorBtn) {
        doctorBtn.addEventListener('click', () => {
            openModal('doctorLogin');
        });
    }
    
    const doctorLoginHandler = makeRequest(DOCTOR_API, doctor);

    async function makeRequest(API_URL, data) {
        const request = await fetch(API_URL, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        }).then(response => {
            selectRole(response)
        })
        .catch(error => { alert("Invalid credentials!") });
    }

    function selectRole(token) {
        localStorage.setItem("userRole", token);
    }    
}

