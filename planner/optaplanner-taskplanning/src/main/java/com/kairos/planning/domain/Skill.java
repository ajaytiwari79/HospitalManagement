package com.kairos.planning.domain;



import com.kairos.planning.enums.SkillType;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("Skill")
public class Skill {

	private String id;
	private Long externalId;
	private String name;
	private SkillType skillType;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "Skill [name=" + name + "]";
	}

	public Long getExternalId() {
		return externalId;
	}

	public void setExternalId(Long externalId) {
		this.externalId = externalId;
	}

	public SkillType getSkillType() {
		return skillType;
	}

	public void setSkillType(SkillType skillType) {
		this.skillType = skillType;
	}
}
