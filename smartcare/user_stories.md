| name            | about                                                                   | title                          | labels       | assignees |
|-----------------|-------------------------------------------------------------------------|--------------------------------|--------------|-----------|
| Patient Stories | Functional user stories for the patient role in the Smart Clinic system | "[STORY] Patient User Stories" | user-stories |           |

> [!IMPORTANT]
> Define workflows that streamline scheduling and enhance doctor readiness.
> Support both availability management and patient context awareness.

## **Role: Patient**

**As a** Patient
**I need** to access to some functionalities
**So that** I can receive the right care
# User Story Template


### Patient Story 1 – Patient Exploring

_As a patient, I can view a list of doctors without logging in to explore options before registering_
```gherkin
Given I access to the doctor list
When I have not sign up to system
Then I see the information about the doctors
````

### Patient Story 2 – Patient Sign up

_As a patient, I can sign up using your email and password to book appointments_
```gherkin
Given I am on the sign up page
When I submit valid registration details
Then my account should be created
And I should be redirected to the login page
````

### Patient Story 3 – Patient Log in

_As a patient, I can log into the portal to manage your bookings_
```gherkin
Given I have a registred account
When I ented valid credentials
Then I should redirected to the dashboard
````



### Patient Story 4 – Patient log out

_As a patient, I can log out of the portal to secure your account_
```gherkin
Given I logged as patient
When I have a valid token session
Then I should logged out and redirected to the login page
And my session should be cleared
````


### Patient Story 5 – Patient Book Appointment

_As a patient, I can log in and book an hour-long appointment to consult with a doctor_
```gherkin
Given I have a registred account
When I ented valid credentials
Then I should view my dashboard
And I can book an appointment
````


### Patient Story 6 – Patient Exploring

_As a patient, I can view my upcoming appointments so that I can prepare accordingly_
```gherkin
Given I logged as patient
When I have a valid session
Then I shoud see the list of upcoming appointments
````

| name               | about                                                                 | title                        | labels        | assignees |
|--------------------|-----------------------------------------------------------------------|------------------------------|---------------|-----------|
| Admin User Stories | Functional user stories for the admin role in the Smart Clinic system | "[STORY] Admin User Stories" | admin-stories |           |


## **Role: Admin**

**As an** Admin
**I need** to access to the platform
**So that** I can add doctors, delete doctors and review appointment statistics

### User Story 1 – Admin Login

_As an Admin, I can log into the portal with your username and password to manage the platform securely_
```gherkin
Given I am registered as an admin
When I enter valid credentials on the login page
Then I should be redirected to the admin dashboard
And I should see the doctors list
````

### User Story 2 - Admin logout

_As ab Admin, I can log out of the portal to protect system access_
```gherkin
Given I logged as admin
When I have a valid token session
Then I should logged out and redirected to the login page
And my session should be cleared
````

### User Story 3 - Admin Add Doctors
_As an Admin, I can a doctors to the portal_
```gherkin
Given I logged as admin
When I add a new doctor
Then The doctor profile will be save to PostgreSQL database
And I should see into the doctors list
````

### User Story 4 - Admin Delete Doctors
_As an Admin, I can celete doctor's profile from the portal_
```gherkin
Given I logged as admin
When I delete a specific doctor
Then The record will be removed from PostgreSQL database
And I should not see into the doctors list and all related appointments will be remove
````

### User Story 5 - Admin View Statistics
_As an Admin, I can view the number of appointments per month and track usage statistics_

```gherkin
Given I logged as admin
When I open the appointment statistics
Then I see the number of appointments
````

| name                  | about                                                               | title                         | labels       | assignees |
|-----------------------|----------------------------------------------------------------------|-------------------------------|--------------|-----------|
| Doctor User Stories   | Functional user stories for the doctor role in the Smart Clinic system | "[STORY] Doctor User Stories" | user-stories |           |

> [!IMPORTANT]
> Define workflows that streamline scheduling and enhance doctor readiness.
> Support both availability management and patient context awareness.

## **Role: Doctor**

**As a** doctor
**I need** access to my appointment calendar and patient information
**So that** I can deliver efficient care and manage my availability.

### User Story 1 – Doctor Login

_As a doctor, I want to log into the portal, so that I can manage my appointments._
```gherkin
Given I am registered as a doctor
When I enter valid credentials on the login page
Then I should be redirected to the doctor dashboard
And I should see my upcoming schedule
````

### User Story 2 – Doctor logout

_As a doctor, I want to Log out of the portal to protect my data_
```gherkin
Given I logged as doctor
When I have a valid token session
Then I should logged out and redirected to the login page
And my session should be cleared
````

### User Story 3 - Doctor Appointment

_As a doctor, I want to view my appointment calendar to stay organized_
```gherkin
Given I logged as doctor
When I navigate to the calendar
Then I should see the visual rappresentation
````

### User Story 4 - Doctor Unavailability
_As doctor, I want to mark your unavailability to inform patients only the available slots_
```gherkin
Given I logged as doctor
When I mark a slot as unavailability
Then the system should block the slots from being book by patients
````

### User Story 5 - Doctor Details
_As Doctor, I want to update my profile with specialization and contact information so that patients have up-to-date information_
```gherkin
Given I logged as doctor
When I update my profile
Then the system should show the updated information to the patient
````

### User Story 6 - Doctor's Patient details
_As doctor, I want to view the patient details for upcoming appointments so that I can be prepared_
```gherkin
Given I logged as doctor
When I click on a specific appointment
Then I should see patient's details
````
