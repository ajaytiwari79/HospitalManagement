package com.kairos.planning.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.ArrayList;
import java.util.List;
@XStreamAlias("TaskType")
public class TaskType {

	private String id;
	private String code;
	private String title;
	private Long baseDuration;
	private Long externalId;
	private List<Skill> requiredSkillList=new ArrayList<Skill>();
	private boolean isForbiddenAllow;

	public TaskType() {

	}
	/*public static TaskType getDummyTaskType(){
		return new TaskType(1000000l,"DUmmY","DUMMy",0l, new ArrayList<>());
	}*/

	public Long getExternalId() {
		return externalId;
	}

	public void setExternalId(Long externalId) {
		this.externalId = externalId;
	}

	public TaskType(String id, String code, String title, Long baseDuration, List<Skill> requiredSkillList) {
		this.id = id;
		this.code = code;
		this.title = title;
		this.baseDuration = baseDuration;
		this.requiredSkillList = requiredSkillList;
	}

	public boolean isForbiddenAllow() {
		return isForbiddenAllow;
	}

	public void setForbiddenAllow(boolean forbiddenAllow) {
		isForbiddenAllow = forbiddenAllow;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Long getBaseDuration() {
		return baseDuration;
	}
	public void setBaseDuration(Long baseDuration) {
		this.baseDuration = baseDuration;
	}
	public List<Skill> getRequiredSkillList() {
		return requiredSkillList;
	}
	public void setRequiredSkillList(List<Skill> requiredSkillList) {
		this.requiredSkillList = requiredSkillList;
	}

}
