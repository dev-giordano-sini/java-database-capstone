//export function createDoctorCard(doctor);

export function createDoctorCard(doctor) {

    const card = document.createElement("div");
    card.classList.add("doctor-card");

    const role = localStorage.getItem("userRole");
    /**
     * Read the current user’s role (admin, patient, loggedPatient) from localStorage.
    You'll use this later to decide which buttons to show.
     */

    const infoDiv = document.createElement("div");
    infoDiv.classList.add("doctor-info");

    const name = document.createElement("h3");
    name.textContent = doctor.name;

    /**
     * Create a heading element and set the text to the doctor’s name.
    Repeat similarly for:
    
    specialization
    email
    availability (you can join an array with join(", ") to display multiple times)
     */

    infoDiv.appendChild(name);
    infoDiv.appendChild(specialization);
    infoDiv.appendChild(email);
    infoDiv.appendChild(availability);


    const actionsDiv = document.createElement("div");
    actionsDiv.classList.add("card-actions");


    if (role === "admin") {
        const removeBtn = document.createElement("button");
        removeBtn.textContent = "Delete";
    } else if (role === "patient") {
        const bookNow = document.createElement("button");
        bookNow.textContent = "Book Now";
        bookNow.addEventListener("click", () => {
            alert("Patient needs to login first.");
        });
    }
    else if (role === "loggedPatient") {
        const bookNow = document.createElement("button");
        bookNow.textContent = "Book Now";
        bookNow.addEventListener("click", async (e) => {
            const token = localStorage.getItem("token");
            const patientData = await getPatientData(token);
            showBookingOverlay(e, doctor, patientData);
        });
    }

    removeBtn.addEventListener("click", async () => {
        // 1. Confirm deletion
        // 2. Get token from localStorage
        // 3. Call API to delete
        // 4. On success: remove the card from the DOM
    });

    card.appendChild(infoDiv);
    card.appendChild(actionsDiv);
    return card;
}