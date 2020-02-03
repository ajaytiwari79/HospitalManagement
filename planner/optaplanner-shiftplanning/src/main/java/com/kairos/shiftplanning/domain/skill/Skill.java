package com.kairos.shiftplanning.domain.skill;



import com.kairos.shiftplanning.enums.SkillType;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@Setter
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

	@Override
	public String toString() {
		return "Skill [name=" + name + "]";
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
