CREATE CONSTRAINT ON (staff:Staff) ASSERT staff.externalId IS UNIQUE;
// Second Script
match(n:Staff)-[:BELONGS_TO]-(u:Employment)-[:HAS_EMPLOYMENTS]-(o:Organization)
with n,o,count(n.externalId) as r where r>1
set n.externalId=121
return r,n.externalId ,n,o