db.wtaBaseRuleTemplate.updateMany({"wtaTemplateType":  { $in : ["TIME_BANK","WTA_FOR_BREAKS_IN_SHIFT"]}},{$set : {"checkRuleFromView":true}})
