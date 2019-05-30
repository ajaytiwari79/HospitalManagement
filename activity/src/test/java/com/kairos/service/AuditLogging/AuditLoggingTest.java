package com.kairos.service.AuditLogging;

import com.kairos.commons.IgnoreLogging;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.shift.ShiftActivity;
import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.node.DiffNode;
import de.danielbechler.diff.node.Visit;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.kairos.commons.utils.DateUtils.asDate;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.CommonConstants.PACKAGE_NAME;

/**
 * pradeep
 * 8/5/19
 */
public class AuditLoggingTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditLoggingTest.class);
    private Set<String> strings = newHashSet("int","long");

    @Test
    public void diffChecker(){
        List<ShiftActivity> shiftActivityList = newArrayList(new ShiftActivity("dsada",asDate(LocalDate.now()),asDate(LocalDate.now().plusDays(1)),new BigInteger("12")));
        List<ShiftActivity> shiftActivity2 = newArrayList(new ShiftActivity("sadasdsasdas",asDate(LocalDate.of(2019,12,15)),asDate(LocalDate.of(2019,12,16)),new BigInteger("125")));
        Shift shift = new Shift(asDate(LocalDate.now()),asDate(LocalDate.now().plusDays(1)), 153l,shiftActivityList);
        Shift shift2 = new Shift(asDate(LocalDate.of(2019,12,15)),asDate(LocalDate.of(2019,12,16)),153l,shiftActivity2);
        ObjectDifferBuilder builder = ObjectDifferBuilder.startBuilding();
        DiffNode diff = builder.build().compare(shift, shift2);
        final Map<String , Object> result = new HashMap<String, Object>();
        diff.visit(new DiffNode.Visitor()
        {
            @Override
            public void node(DiffNode arg0, Visit arg1) {
                final Object oldValue = arg0.canonicalGet(shift2);
                final Object newValue = arg0.canonicalGet(shift);
                if(!isIgnoreLogging(arg0)) {
                    updateMap(arg0, oldValue, newValue, arg0.getPropertyName(), result);
                }
            }

        });
        LOGGER.info("test {}",result);
    }

    private boolean isIgnoreLogging(DiffNode arg0) {
        LOGGER.info("property Name {}",arg0.getPropertyName());
        if(isIgnoredField(arg0)){
            return true;
        }
        else if(isIgnoredMethod(arg0)){
            return true;
        }
        else if(isNotNull(arg0.getParentNode()) && isIgnoredClass(arg0.getParentNode().getValueType())) {
            return true;
        }
        return false;
    }

    private boolean isIgnoredMethod(DiffNode arg0) {
        return arg0.getPropertyAnnotation(IgnoreLogging.class) != null;
    }

    private boolean isIgnoredClass(Class className) {
        return className.getAnnotation(IgnoreLogging.class) != null;
    }

    private boolean isIgnoredField(DiffNode arg0) {
        return arg0.getFieldAnnotation(IgnoreLogging.class) != null;
    }

    private void updateMap(DiffNode arg0, Object oldValue, Object newValue, String properteyName, Map<String, Object> result) {
        if(isArgumentValid(arg0, properteyName)) {
            if(isPropertyValid(arg0, properteyName)) {
                result.put("new_" + properteyName, newValue);
                result.put("old_" + properteyName, oldValue);
            }
        }
    }

    private boolean isArgumentValid(DiffNode arg0, String properteyName) {
        return (isNotNull(properteyName) && isValid(arg0)) || isValidPa(arg0);
    }

    private boolean isPropertyValid(DiffNode arg0, String properteyName) {
        return arg0.isChanged() && !properteyName.toUpperCase().contains("UPDATEDATE") && !properteyName.equals("/");
    }

    boolean isValid(DiffNode arg0){

        return strings.contains(arg0.getValueType().getName()) && isNotNull(arg0.getParentNode()) && arg0.getParentNode().getValueType().getPackage().getName().contains(PACKAGE_NAME);
    }

    boolean isValidPa(DiffNode arg0){
        LOGGER.debug("property {}",arg0.getPropertyName());
        return isNotNull(arg0.getParentNode()) && arg0.getParentNode().getValueType().getPackage().getName().contains(PACKAGE_NAME);
    }
}
