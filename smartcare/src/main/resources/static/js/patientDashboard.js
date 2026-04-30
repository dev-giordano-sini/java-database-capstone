import { createDoctorCard } from "./components/doctorCard.js";
import { openModal } from "./components/modals.js";
import { getDoctors, filterDoctors } from "./services/doctorServices.js";
import { patientLogin, patientSignup } from "./services/patientServices.js";



document.addEventListener("DOMContentLoaded", () => {
    loadDoctorCards();

    const btn = document.getElementById("patientSignup");
    if (btn) btn.addEventListener("click", () => openModal("patientSignup"));

    const loginBtn = document.getElementById("patientLogin");
    if (loginBtn) loginBtn.addEventListener("click", () => openModal("patientLogin"));
});

function loadDoctorCards() {
    /**
         * Sistemare...
         */

    var doctors = getDoctors();
    doctors.forEach(doctor => {
        createDoctorCard(doctor);
    });

}


document.getElementById("searchBar").addEventListener("input", filterDoctorsOnChange);
document.getElementById("filterTime").addEventListener("change", filterDoctorsOnChange);
document.getElementById("filterSpecialty").addEventListener("change", filterDoctorsOnChange);


function filterDoctorsOnChange() {
    /**
     * da sistemare
     */
    filterDoctors(name, time, specialty).then(response => {
        contentDiv.innerHTML = doctors.length > 0 ? renderedCards : "<p>No doctors found with the given filters.</p>";
    });
}

function renderDoctorCards(doctors) {

}

window.signupPatient = async function () {
    /**
     * he signupPatient() function is triggered on form submission:
    
    Collects user inputs (name, email, password, phone, address).
    
    Sends the data to the backend via patientSignup().
    
    On success:
    
    Shows an alert with a success message.
    Closes the modal and reloads the page.
    On failure: Shows an error message.
     */
};


window.loginPatient = async function () {
    /**
     * The loginPatient() function is triggered on login form submission:

Captures login credentials (email, password).

Calls patientLogin() to authenticate.

On success:

Stores JWT token in localStorage.
Redirects user to loggedPatientDashboard.html.
On failure:

Shows error alert.
     */
};
