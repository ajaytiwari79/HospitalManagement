

var db = require('gdpr');
var MongoClient = mongo.MongoClient;
var url = 'mongodb://localhost:27017/testdb';
MongoClient.connect(url, (err, db) => {
    
assert.equal(null, err);
  
var collection = db.collection('filterGroup');

var clauseQuery={"accessModule.moduleId":"module_17","countryId":4,"active":true};
var assetModule={"accessModule.moduleId":"module_16","countryId":4,"active":true};
var masterProcessingModule={"accessModule.moduleId":"tab_224","countryId":4,"active":true};
var query=[];
var queryClause={
    "countryId" : NumberLong(4),
    "accessModule" : [ 
        {
            "name" : "Clauses",
            "moduleId" : "module_17",
            "isModuleId" : true,
            "active" : true
        }
    ],
    "filterTypes" : [ 
        "ORGANIZATION_TYPES", 
        "ORGANIZATION_SUB_TYPES", 
        "ORGANIZATION_SERVICES", 
        "ORGANIZATION_SUB_SERVICES"
    ]
};
var queryMasterAsset{
    
    "countryId" : NumberLong(4),
    "accessModule" : [ 
        {
            "name" : "Asset List",
            "moduleId" : "module_16",
            "isModuleId" : true,
            "active" : true
        }
    ],
    "filterTypes" : [ 
        "ORGANIZATION_TYPES", 
        "ORGANIZATION_SUB_TYPES", 
        "ORGANIZATION_SERVICES", 
        "ORGANIZATION_SUB_SERVICES"
    ]
}
var queryMasterProcessingActivity={
    "countryId" : NumberLong(4),
    "accessModule" : [ 
        {
            "name" : "Processing Activity List",
            "moduleId" : "tab_224",
            "isModuleId" : true,
            "active" : true
        }
    ],
    "filterTypes" : [ 
        "ORGANIZATION_TYPES", 
        "ORGANIZATION_SUB_TYPES", 
        "ORGANIZATION_SERVICES", 
        "ORGANIZATION_SUB_SERVICES"
    ]
}

collection.findOne((clauseQuery), (err, doc) => {
        
        if (err) {
            query.push(queryClause);
            console.log(err);
        } else {
            
            console.log(doc);
        }

    }); 

collection.findOne((assetModule), (err, doc) => {
        
        if (err) {
             query.push(assetModule);
            console.log(err);
        } else {
            
            console.log(doc);
        }

    }); 

collection.findOne((masterProcessingModule), (err, doc) => {
        
        if (err) {
             query.push(masterProcessingModule);
            console.log(err);
        } else {
            
            console.log(doc);
        }

    }); 





var insertDocument = (db, callback) => {
    
    var collection = db.collection('cars');
      
    collection.insertMany(query, (err, result) => {
        
        assert.equal(err, null);
        assert.equal(1, result.result.n);
        console.log("A document was inserted into the collection");
        
        callback(result);
    });
}






