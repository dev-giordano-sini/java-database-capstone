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
