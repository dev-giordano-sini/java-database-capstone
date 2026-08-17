// Development-only reset. This permanently deletes all prescription documents.
db.getSiblingDB("smartcare").prescriptions.drop();
