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