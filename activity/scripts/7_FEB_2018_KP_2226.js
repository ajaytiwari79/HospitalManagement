db.activities.updateMany({},{$set:{"rulesActivityTab.eligibleForSchedules":[{ "phaseId": 1, "name": "REQUEST", "description": "REQUEST PHASE", "eligibleForStaff": false, "eligibleForManagement": false},
{ "phaseId": 2, "name": "PUZZLE", "description": "PUZZLE PHASE", "eligibleForStaff": false, "eligibleForManagement": false},
{ "phaseId": 3, "name": "CONSTRUCTION", "description": "CONSTRUCTION PHASE", "eligibleForStaff": false, "eligibleForManagement": false},
{ "phaseId": 4, "name": "FINAL", "description": "FINAL PHASE", "eligibleForStaff": false, "eligibleForManagement": false}]}})