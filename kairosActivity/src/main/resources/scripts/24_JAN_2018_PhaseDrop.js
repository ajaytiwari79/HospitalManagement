use 'kairos'
db.phases.drop();
db.shifts.updateMany({},{$set:{isMainShift:true}});
