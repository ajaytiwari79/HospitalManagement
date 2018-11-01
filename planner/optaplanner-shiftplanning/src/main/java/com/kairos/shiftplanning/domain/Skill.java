package com.kairos.shiftplanning.domain;



import com.kairos.shiftplanning.enums.SkillType;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@XStreamAlias("Skill")
public class Skill {

	private String id;
	private String name;
	private SkillType skillType;
	private int weight;

	public Skill(String id, String name, SkillType skillType) {
		this.id = id;
		this.name = name;
		this.skillType = skillType;
	}

	public Skill() {
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

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

	public SkillType getSkillType() {
		return skillType;
	}

	public void setSkillType(SkillType skillType) {
		this.skillType = skillType;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;

		if (o == null || getClass() != o.getClass()) return false;

		Skill skill = (Skill) o;

		return new EqualsBuilder()
				.append(id, skill.id)
				.append(name, skill.name)
	//			.append(skillType.toString(), skill.skillType.toString())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
				.append(id)
				.append(name)
//				.append(skillType.toString())
				.toHashCode();
	}
}
