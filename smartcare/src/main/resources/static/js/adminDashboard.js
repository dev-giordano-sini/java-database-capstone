import { openModal } from "../components/modals.js";
import { getDoctors, filterDoctors, saveDoctor } from "./services/doctorServices.js";
import { createDoctorCard } from "./components/doctorCard.js";

document.getElementById('addDocBtn').addEventListener('click', () => {
    openModal('addDoctor');
});


document.addEventListener("DOMContentLoaded", () => {
    loadDoctorCards();
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

const contentDiv = document.getElementById("content");
contentDiv.innerHTML = "";
/**
 * Appends each card to the contentDiv.
 */
contentDiv.innerHTML = "";


document.getElementById("searchBar").addEventListener("input", filterDoctorsOnChange);
document.getElementById("filterTime").addEventListener("change", filterDoctorsOnChange);
document.getElementById("filterSpecialty").addEventListener("change", filterDoctorsOnChange);


function filterDoctorsOnChange() {
    // fare logica
    filterDoctors();
}

function renderDoctorCards(doctors) {

}

/**
 * When the "Add Doctor" button is clicked:
openModal() opens the modal
The modal form is populated with input fields for:
Name, specialty, email, password, mobile no., availability time.
Collects any checkbox values for doctor availability
On form submission:
Use adminAddDoctor() to collect data.
Verifies that a valid login token exists (to authenticate the admin).
Send a POST request using saveDoctor() from './services/doctorServices.js'
If successful, closes the modal, reloads the page or doctor list, and shows a success message and refresh the doctor list.
If failed, alerts the user with an error message.
 */