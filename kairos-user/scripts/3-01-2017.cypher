MATCH (s:Skill{isEnabled:true})
 where not  (s)-[:HAS_CATEGORY]->(:SkillCategory{isEnabled:true})
detach delete s;