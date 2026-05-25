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
