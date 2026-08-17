// Executed by the official MongoDB image when its data volume is initialized.
// Manual usage: mongosh mongodb://localhost:27017/smartcare database/mongodb/create_prescriptions.js

const database = db.getSiblingDB("smartcare");

if (!database.getCollectionNames().includes("prescriptions")) {
    database.createCollection("prescriptions", {
        validator: {
            $jsonSchema: {
                bsonType: "object",
                required: ["patientName", "appointmentId", "medication"],
                properties: {
                    patientName: { bsonType: "string", minLength: 3, maxLength: 100 },
                    appointmentId: { bsonType: "long" },
                    medication: { bsonType: "string", minLength: 3, maxLength: 100 },
                    doctorNotes: { bsonType: ["string", "null"], maxLength: 200 },
                    pharmacyName: { bsonType: ["string", "null"], maxLength: 255 }
                }
            }
        },
        validationLevel: "strict",
        validationAction: "error"
    });
}

database.prescriptions.createIndex(
    { appointmentId: 1 },
    { name: "idx_prescriptions_appointment" }
);
