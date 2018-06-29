package com.kairos.planning.domain;

import org.apache.commons.lang3.builder.CompareToBuilder;

import java.util.Comparator;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class TaskOrEmployeeComparator implements Comparator<TaskOrEmployee>{
		 @Override
		    public int compare(TaskOrEmployee a, TaskOrEmployee b) {
		        return new CompareToBuilder()
						.append(a.getClass().toString(), b.getClass().toString())
						.append(a.getId(), b.getId())
		                .toComparison();
		        //return ThreadLocalRandom.current().nextInt(-1,1);
		    }
	}
