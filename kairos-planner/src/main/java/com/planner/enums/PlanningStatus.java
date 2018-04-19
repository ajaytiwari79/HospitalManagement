package com.planner.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PlanningStatus {

	SOLVING("Solving"),UNSOLVED("Unsolved"), NOTSOLVING("Not solving"), SOLVED("Solved");

	private String value;

	private PlanningStatus(String value) {
		this.value = value;
	}

	@JsonValue
	public String toValue() {
		return value;
	}

	public static PlanningStatus getEnumByString(String status) {
		for (PlanningStatus is : PlanningStatus.values()) {
			if (status.equals(is.toValue()))
				return is;
		}
		return null;
	}

}
