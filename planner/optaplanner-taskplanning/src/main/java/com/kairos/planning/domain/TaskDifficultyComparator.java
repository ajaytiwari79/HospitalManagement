package com.kairos.planning.domain;

import org.apache.commons.lang3.builder.CompareToBuilder;

import java.util.Comparator;

public class TaskDifficultyComparator implements Comparator<Task>{
		 @Override
		    public int compare(Task a, Task b) {
		        return new CompareToBuilder()
						.append(a.getInitialStartTime1(), b.getInitialStartTime1())
		                .append(a.getPriority(), b.getPriority())
		                //.append(a.getTaskType().getRequiredSkillList().size(), b.getTaskType().getRequiredSkillList().size())

		                .toComparison();
		    }
	}
