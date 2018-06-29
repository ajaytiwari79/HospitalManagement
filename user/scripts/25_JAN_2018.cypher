//db.phases.find({countryId:NumberLong(53)},{_id:1,name:1}).pretty();


//Sample Response of mongo Phases

//{ "_id" : "244", "name" : "REQUEST" }
//{ "_id" : "245", "name" : "PUZZLE" }
//{ "_id" : "248", "name" : "DRAFT" }
//{ "_id" : "248", "name" : "CONSTRUCTION" }


//Please Change phaseId below from  value of _id above Mongo Query



match wd= (w:WTABaseRuleTemplate)
forEach(n IN nodes(wd)|  
  merge(n)-[:HAS_TEMPLATE_MATRIX]->(t:PhaseTemplateValue{phaseName:"REQUEST",phaseId:1,staffValue:0,managementValue:0,disabled:true,optionalFrequency:0,optional:false})
  merge(n)-[:HAS_TEMPLATE_MATRIX]->(:PhaseTemplateValue{phaseName:"PUZZLE",phaseId:2,staffValue:0,managementValue:0,disabled:true,optionalFrequency:0,optional:false})
  merge(n)-[:HAS_TEMPLATE_MATRIX]->(:PhaseTemplateValue{phaseName:"DRAFT",phaseId:3,staffValue:0,managementValue:0,disabled:true,optionalFrequency:0,optional:false})
  merge(n)-[:HAS_TEMPLATE_MATRIX]->(:PhaseTemplateValue{phaseName:"CONSTRUCTION",phaseId:4,staffValue:0,managementValue:0,disabled:true,optionalFrequency:0,optional:false})
);
