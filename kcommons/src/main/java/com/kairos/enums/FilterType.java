package com.kairos.enums;

import org.springframework.data.mongodb.core.query.Criteria;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.kairos.commons.utils.ObjectUtils.*;

/**
 * Created by prerna on 30/4/18.
 * Modified By: mohit.shakya@oodlestechnologies.com on Jun 26th, 2018
 */

public enum FilterType {

    EMPLOYMENT_TYPE("Employment Type") {
        @Override
        public <T> Criteria updateCriteria(Criteria criteria, Set<T> set) {
            return new Criteria();
        }
    }, EXPERTISE("Expertise") {
        @Override
        public <T> Criteria updateCriteria(Criteria criteria, Set<T> set) {
            return new Criteria();
        }
    }, STAFF_STATUS("Staff Status") {
        @Override
        public <T> Criteria updateCriteria(Criteria criteria, Set<T> set) {
            return new Criteria();
        }
    }, GENDER("Gender") {
        @Override
        public <T> Criteria updateCriteria(Criteria criteria, Set<T> set) {
            return new Criteria();
        }
    },
    TIME_TYPE("Time Type") {
        @Override
        public <T> Criteria updateCriteria(Criteria criteria, Set<T> set) {
            return criteria.orOperator(Criteria.where("activities.timeTypeId").in(getBigIntegerSet(set),Criteria.where("activities.childActivities.timeTypeId").in(getBigIntegerSet(set))));
        }
    }, PLANNED_TIME_TYPE("Planned Time Type") {
        @Override
        public <T> Criteria updateCriteria(Criteria criteria, Set<T> set) {
            return criteria.and("activities.plannedTimes.plannedTimeId").in(getBigIntegerSet(set));
        }
    }, ACTIVITY_CATEGORY_TYPE("Category Type") {
        @Override
        public <T> Criteria updateCriteria(Criteria criteria, Set<T> set) {
            return new Criteria();
        }
    }, ORGANIZATION_TYPE("Organization Type") {
        @Override
        public <T> Criteria updateCriteria(Criteria criteria, Set<T> set) {
            return new Criteria();
        }
    },
    STAFF_IDS("Staff") {
        @Override
        public <T> Criteria updateCriteria(Criteria criteria, Set<T> set) {
            return new Criteria();
        }
    }, ACTIVITY_IDS("Activity") {
        @Override
        public <T> Criteria updateCriteria(Criteria criteria, Set<T> set) {
            return criteria.orOperator(Criteria.where("activities.activityId").in(getBigIntegerSet(set),Criteria.where("activities.childActivities.activityId").in(getBigIntegerSet(set))));
        }
    }, UNIT_IDS("Unit") {
        @Override
        public <T> Criteria updateCriteria(Criteria criteria, Set<T> set) {
            return new Criteria();
        }
    }, TIME_INTERVAL("Time Interval") {
        @Override
        public <T> Criteria updateCriteria(Criteria criteria, Set<T> set) {
            return new Criteria();
        }
    }, EMPLOYMENT("Employment") {
        @Override
        public <T> Criteria updateCriteria(Criteria criteria, Set<T> set) {
            return new Criteria();
        }
    }, SELECTED_STAFF_IDS("Selected Staff IDs") {
        @Override
        public <T> Criteria updateCriteria(Criteria criteria, Set<T> set) {
            return new Criteria();
        }
    },
    SKILLS("Skills") {
        @Override
        public <T> Criteria updateCriteria(Criteria criteria, Set<T> set) {
            return new Criteria();
        }
    }, REAL_TIME_STATUS("Real Time Status") {
        @Override
        public <T> Criteria updateCriteria(Criteria criteria, Set<T> set) {
            return criteria.and("activities.timeTypeId").in(getBigIntegerSet(set));
        }
    }, TAGS("Tags") {
        @Override
        public <T> Criteria updateCriteria(Criteria criteria, Set<T> set) {
            return new Criteria();
        }
    }, GROUPS("Groups") {
        @Override
        public <T> Criteria updateCriteria(Criteria criteria, Set<T> set) {
            return criteria.and("staffId").in(getLongSet(set));
        }
    }, NIGHT_WORKERS("Night Workers") {
        @Override
        public <T> Criteria updateCriteria(Criteria criteria, Set<T> set) {
            return new Criteria();
        }
    },
    ACTIVITY_STATUS("Activity Status") {
        @Override
        public <T> Criteria updateCriteria(Criteria criteria, Set<T> set) {
            return criteria.and("activities.status").in(set);
        }
    }, PHASE("Phase") {
        @Override
        public <T> Criteria updateCriteria(Criteria criteria, Set<T> set) {
            return criteria.and("phaseId").in(getBigIntegerSet(set));
        }
    }, DAYS_OF_WEEK("Days Of Week") {
        @Override
        public <T> Criteria updateCriteria(Criteria criteria, Set<T> set) {
            return new Criteria();
        }
    }, DAY_TYPE("Day Type") {
        @Override
        public <T> Criteria updateCriteria(Criteria criteria, Set<T> set) {
            return new Criteria();
        }
    }, TIME_SLOT("Time Slot") {
        @Override
        public <T> Criteria updateCriteria(Criteria criteria, Set<T> set) {
            return new Criteria();
        }
    }, FIBONACCI("Fibonacci") {
        @Override
        public <T> Criteria updateCriteria(Criteria criteria, Set<T> set) {
            return new Criteria();
        }
    }, UNIT_NAME("Unit Name") {
        @Override
        public <T> Criteria updateCriteria(Criteria criteria, Set<T> set) {
            return new Criteria();
        }
    }, ACTIVITY_TIMECALCULATION_TYPE("Activity Timecalculation Type") {
        @Override
        public <T> Criteria updateCriteria(Criteria criteria, Set<T> set) {
            return new Criteria();
        }
    }, TEAM("Team") {
        @Override
        public <T> Criteria updateCriteria(Criteria criteria, Set<T> set) {
            return new Criteria();
        }
    },
    ABSENCE_ACTIVITY("Absence Activity") {
        @Override
        public <T> Criteria updateCriteria(Criteria criteria, Set<T> set) {
            return new Criteria();
        }
    }, FUNCTIONS("Functions") {
        @Override
        public <T> Criteria updateCriteria(Criteria criteria, Set<T> set) {
            Set<LocalDate> dates = getLocalDate(set);
            List<Criteria> criteriaList = new ArrayList<>();
            for (LocalDate date : dates) {
               criteriaList.add(new Criteria().gte(date).lt(date));
            }
            return criteria.orOperator(criteriaList.toArray(new Criteria[criteriaList.size()]));
        }
    }, VALIDATED_BY("Validated By") {
        @Override
        public <T> Criteria updateCriteria(Criteria criteria, Set<T> set) {
            return new Criteria();
        }
    }, CALCULATION_TYPE("Calculation type") {
        @Override
        public <T> Criteria updateCriteria(Criteria criteria, Set<T> set) {
            return new Criteria();
        }
    }, CALCULATION_BASED_ON("Calculation Based On") {
        @Override
        public <T> Criteria updateCriteria(Criteria criteria, Set<T> set) {
            return new Criteria();
        }
    }, CALCULATION_UNIT("Calculation Unit") {
        @Override
        public <T> Criteria updateCriteria(Criteria criteria, Set<T> set) {
            return new Criteria();
        }
    }, PLANNED_BY("Planned By") {
        @Override
        public <T> Criteria updateCriteria(Criteria criteria, Set<T> set) {
            return new Criteria();
        }
    }, REASON_CODE("Reason Code") {
        @Override
        public <T> Criteria updateCriteria(Criteria criteria, Set<T> set) {
            return new Criteria();
        }
    },
    AGE("Age") {
        @Override
        public <T> Criteria updateCriteria(Criteria criteria, Set<T> set) {
            return new Criteria();
        }
    }, ORGANIZATION_EXPERIENCE("Organisation Experience") {
        @Override
        public <T> Criteria updateCriteria(Criteria criteria, Set<T> set) {
            return new Criteria();
        }
    }, ESCALATION_CAUSED_BY("Escalation Caused By") {
        @Override
        public <T> Criteria updateCriteria(Criteria criteria, Set<T> set) {
            return new Criteria();
        }
    }, EMPLOYMENT_SUB_TYPE("Employment Sub Type") {
        @Override
        public <T> Criteria updateCriteria(Criteria criteria, Set<T> set) {
            return new Criteria();
        }
    },
    MAIN_TEAM("Main Team") {
        @Override
        public <T> Criteria updateCriteria(Criteria criteria, Set<T> set) {
            return new Criteria();
        }
    }, SKILL_LEVEL("Skill Level") {
        @Override
        public <T> Criteria updateCriteria(Criteria criteria, Set<T> set) {
            return new Criteria();
        }
    }, ACCESS_GROUPS("Access Groups") {
        @Override
        public <T> Criteria updateCriteria(Criteria criteria, Set<T> set) {
            return new Criteria();
        }
    }, BIRTHDAY("Birthday") {
        @Override
        public <T> Criteria updateCriteria(Criteria criteria, Set<T> set) {
            return new Criteria();
        }
    }, SENIORITY("Seniority") {
        @Override
        public <T> Criteria updateCriteria(Criteria criteria, Set<T> set) {
            return new Criteria();
        }
    }, PAY_GRADE_LEVEL("Pay Grade Level") {
        @Override
        public <T> Criteria updateCriteria(Criteria criteria, Set<T> set) {
            return new Criteria();
        }
    }, CTA_ACCOUNT_TYPE("CTA account type") {
        @Override
        public <T> Criteria updateCriteria(Criteria criteria, Set<T> set) {
            return new Criteria();
        }
    },
    TIME_BANK_BALANCE("Time Bank balance") {
        @Override
        public <T> Criteria updateCriteria(Criteria criteria, Set<T> set) {
            return new Criteria();
        }
    }, EMPLOYED_SINCE("Employed Since") {
        @Override
        public <T> Criteria updateCriteria(Criteria criteria, Set<T> set) {
            return new Criteria();
        }
    }, TEAM_TYPE("Team Type") {
        @Override
        public <T> Criteria updateCriteria(Criteria criteria, Set<T> set) {
            return new Criteria();
        }
    }, ASSIGN_ACTIVITY("Assign Activity") {
        @Override
        public <T> Criteria updateCriteria(Criteria criteria, Set<T> set) {
            return new Criteria();
        }
    }, ASSIGN_TIME_TYPE("Assign Time Type") {
        @Override
        public <T> Criteria updateCriteria(Criteria criteria, Set<T> set) {
            return new Criteria();
        }
    };


    public String value;

    FilterType(String value) {
        this.value = value;
    }

    public abstract <T> Criteria updateCriteria(Criteria criteria, Set<T> set);

}