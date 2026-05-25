| name                  | about                                                                       | title                             | labels       | assignees |
|-----------------------|------------------------------------------------------------------------------|-----------------------------------|--------------|-----------|
| Database Schema Design | Defines relational and document-based schema for Smart Clinic architecture | "[DB] Design schema architecture" | schema-design |           |


### **Table: patient**

| Column Name | Data Type    | Constraints                 |
|-------------|--------------|-----------------------------|
| id          | INT          | PK, AUTO_INCREMENT          |
| name        | VARCHAR(100) | NOT NULL                    |
| email       | VARCHAR(100) | NOT NULL, UNIQUE            |
| password    | VARCHAR(80)  | NOT NULL                    |
| address     | VARCHAR(100) | NOT NULL                    |
| phone       | VARCHAR(15)  | NOT NULL                    |
| birthdate   | DATE         |                             |
| alias       | VARCHAR(80)  | CHECK (gender IN ('M','F')) |

---

### **Table: doctor**


| Column Name              | Data Type    | Constraints                 |
|--------------------------|--------------|-----------------------------|
| id                       | INT          | PK, AUTO_INCREMENT          |
| name                     | VARCHAR(100) | NOT NULL                    |
| email                    | VARCHAR(100) | NOT NULL, UNIQUE            |
| password                 | VARCHAR(80)  | NOT NULL                    |
| phone                    | VARCHAR(15)  | NOT NULL                    |
| specialty                | VARCHAR(50)  | NOT NULL                    |
| rating                   | INT DEFAULT 0|                             |

---

### **Table: doctor_available_times**


| Column Name              | Data Type    | Constraints               |
|--------------------------|--------------|---------------------------|
| doctor_id                | INT          | FK → doctor(id), NOT NULL |
| available_times          | VARCHAR(15)  | NOT NULL                  |

---


### **Table: appointment**


| Column Name                         | Data Type | Constraints                |
|-------------------------------------|-----------|----------------------------|
| id                                  | INT       | PK, AUTO_INCREMENT         |
| appointment_time                    | DATE      | NOT NULL                   | 
| status                              | INT       | NOT NULL                   | 
| doctor_id                           | INT       | FK → doctor(id), NOT NULL  | 
| patient_id                          | INT       | FK → patient(id), NOT NULL | 

---


## **Table: admin**


| Column Name       | Data Type   | Constraints                |
|-------------------|-------------|----------------------------|
| id                | INT         | PK, AUTO_INCREMENT         |
| username          | VARCHAR(80) | NOT NULL                   |
| password          | VARCHAR(30) | NOT NULL                   |

---



## **MongoDB Collection Design**

## **Table: prescription**

```json
{
  "_id": ObjectId("6807dd712725f013281e7201"),
  "patientName": "John Smith",
  "appointmentId": 51,
  "medication": "Paracetamol",
  "dosage": "500mg",
  "doctorNotes": "Take 1 tablet every 6 hours.",
  "_class": "com.project.back_end.models.Prescription"
}


````