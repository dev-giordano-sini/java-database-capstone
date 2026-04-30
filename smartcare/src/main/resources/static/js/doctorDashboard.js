import {getAllAppointments} from "./services/appointmentRecordService.js";
import {createPatientRow} from "./components/patientRows";

/**
 * Initialize Global Variables:

Define and store:

A reference to the appointment table body where rows will be rendered (#patientTableBody).
selectedDate, initialized to today's date.
token, retrieved from localStorage (used for authentication).
patientName, initialized as null, for search filtering.
Setup Search Bar Functionality:

Add an event listener to the search bar (#searchBar):

On input change, update the patientName variable.
If the search input is empty, default patientName to "null".
Call loadAppointments() to refresh the list with the filtered data.
Bind Event Listeners to Filter Controls:

"Today's Appointments" button (#todayButton):

Resets the selectedDate to today.
Updates the date picker field to reflect today’s date.
Calls loadAppointments().
Date picker (#datePicker):

Updates the selectedDate variable when changed.
Calls loadAppointments() to fetch and display appointments for the selected date.
 */

/**
 * Notes
The getAllAppointments() function is responsible for backend API calls based on the selected date and search term.
The createPatientRow() component is used to dynamically build each row of the appointments table.
All API calls should include the doctor’s token for authentication (retrieved from localStorage).
Always use async/await syntax when working with fetch() to ensure proper flow control and error handling.
Ensure meaningful fallback messages are shown if no appointments are found or if the API fails.
 */

function loadAppointments() {
    //getAllAppointments(selectedDate, patientName, token)
}