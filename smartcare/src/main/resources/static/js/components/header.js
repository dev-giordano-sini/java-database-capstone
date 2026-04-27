if (window.location.pathname.endsWith("/")) {
    localStorage.removeItem("userRole");
    localStorage.removeItem("token");
}

const role = localStorage.getItem("userRole");
const token = localStorage.getItem("token");

if ((role === "loggedPatient" || role === "admin" || role === "doctor") && !token) {
    localStorage.removeItem("userRole");
    alert("Session expired or invalid login. Please log in again.");
    window.location.href = "/";
    return;
}



if (role === "admin") {
    headerContent += `
      <button id="addDocBtn" class="adminBtn" onclick="openModal('addDoctor')">Add Doctor</button>
      <a href="#" onclick="logout()">Logout</a>`;
}

if (role === "doctor") {
    headerContent += `    
    <button id="homeBtn" class="homeBtn" onclick="goToHome()">Home</button>
    <a href="#" onclick="logout()">Logout</a>`;
}


if (role === "patient") {    
    headerContent += `
    <button id="loginBtn" class="loginBtn" onclick="login()">Login</button>
    <button id="signupBtn" class="signupBtn" onclick="signup()">Sign Up</button>
    `;
}

if (role === "loggedPatient ") {
    headerContent += `
    <a href="#home">Home</a>
    <a href="#appointments">Appointments</a>      
    <a href="#" onclick="logout()">Logout</a>`;
}

headerDiv.innerHTML = headerContent;
attachHeaderButtonListeners();

function attachHeaderButtonListeners() {
    //add all listeners here
    //document.getElementById("demo").addEventListener("click", myFunction);
}


/**
 * Start with an empty string: headerContent = ""
If role is admin  
  Add HTML string for "Add Doctor" button and Logout link
If role is doctor  
  Add "Home" button and Logout
If role is patient  
  Add Login and Signup buttons
If role is loggedPatient  
  Add Home, Appointments, and Logout

*/

/*
After rendering the header  
Find buttons by ID  
Attach 'click' event listeners (e.g. to open a modal or clear storage)
*/

/**
 * Create a function called logout  
Inside it, remove token and userRole  
Redirect to homepage

Create logoutPatient function  
Remove token  
Set role back to "patient"  
Redirect to patient dashboard
 */