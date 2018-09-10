package com.kairos.utils.validator;

import com.kairos.persistence.model.staffing_level.StaffingLevel;
import com.kairos.persistence.repository.staffing_level.StaffingLevelMongoRepository;
import com.kairos.utils.user_context.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

@Component
public class ExistingStaffingLevelValidator implements ConstraintValidator<ExistingStaffingLevel,Date> {

	@Autowired
	StaffingLevelMongoRepository staffingLevelMongoRepository;
	@Autowired
	HttpServletRequest request;


	
	public void initialize(ExistingStaffingLevel constraintAnnotation) { }

	public boolean isValid(Date selectedDate, ConstraintValidatorContext context){
		Assert.notNull(selectedDate,"can not be null");
		Assert.notNull(UserContext.getUnitId(),"can not be null");

			final Map<String, String> pathVariables = (Map<String, String>) request
					.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

			        String staffingLevelId=pathVariables.get("staffingLevelId");
			        boolean valid=false;
			        //check for update
			       if(!StringUtils.isEmpty(staffingLevelId)){
					   BigInteger id=new BigInteger(staffingLevelId);
					   StaffingLevel staffingLevel=staffingLevelMongoRepository.findByUnitIdAndCurrentDateAndDeletedFalse(UserContext.getUnitId(),selectedDate);
					     if(staffingLevel!=null && staffingLevel.getId().equals(id)){
					     	//same object date is not modified
							 valid=true;
						 }else if(staffingLevel!=null && (!staffingLevel.getId().equals(id))){
					     	//other staffing  level exist for selected date
							 valid=false;

						 }else{
						 	//no staffing level exist
							 valid=true;
						 }

				   }else{

					   valid=staffingLevelMongoRepository.existsByUnitIdAndCurrentDateAndDeletedFalse(UserContext.getUnitId(),selectedDate)?false:true;
				   }
				return valid;
	}
}
;