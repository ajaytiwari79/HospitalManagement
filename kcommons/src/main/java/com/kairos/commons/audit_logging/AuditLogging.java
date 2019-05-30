package com.kairos.commons.audit_logging;

import com.kairos.commons.IgnoreLogging;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.enums.audit_logging.LoggingType;
import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.node.DiffNode;
import de.danielbechler.diff.node.Visit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.*;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.commons.utils.ObjectUtils.newHashSet;
import static com.kairos.constants.CommonConstants.PACKAGE_NAME;
import static de.danielbechler.diff.node.DiffNode.State.ADDED;
import static de.danielbechler.diff.node.DiffNode.State.CHANGED;

/**
 * pradeep
 * 8/5/19
 */

//@Component
public class AuditLogging {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditLogging.class);
    private static Set<String> primitives = newHashSet("int", "long", "boolean", "short", "byte", "float", "double");

    private static MongoTemplate mongoTemplate;

    /*@Autowired
    public void setMongoTemplate(@Qualifier("AuditLoggingMongoTemplate") MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }*/

    // @Test
    // @AfterReturning(value = "execution(* org.springframework.data.repository.*.*(..))",returning = "entity")
    //@Async
    public static Map<String, Object> checkDifferences(Object oldEntity, Object newEntity) {
        Map<String, Object> result;
        ObjectDifferBuilder builder = ObjectDifferBuilder.startBuilding();
        Class parentNodeClass = oldEntity.getClass();
        DiffNode diff = builder.build().compare(newEntity, oldEntity);
        final Map<String, Object> diffResult = new HashMap<>();
        diff.visit(new DiffNode.Visitor() {
            @Override
            public void node(DiffNode arg0, Visit arg1) {
                final Object oldValue = arg0.canonicalGet(oldEntity);
                final Object newValue = arg0.canonicalGet(newEntity);
                if(!isIgnoreLogging(arg0)) {
                    updateMap(arg0, oldValue, newValue, arg0.getPropertyName(), diffResult, parentNodeClass);
                }
            }

        });
        diffResult.put("loggingType", getLoggingType(ObjectMapperUtils.copyPropertiesByMapper(oldEntity, HashMap.class), ObjectMapperUtils.copyPropertiesByMapper(newEntity, HashMap.class)));
        result = diffResult;
        mongoTemplate.save(result, newEntity.getClass().getSimpleName());
        LOGGER.info("test {}", oldEntity);
        return result;
    }

    private static boolean isIgnoreLogging(DiffNode arg0) {
        LOGGER.info("property Name {}", arg0.getPropertyName());
        boolean isIgnoreLogging = false;
        if(isIgnoredField(arg0) || isIgnoredMethod(arg0) || (isNotNull(arg0.getParentNode()) && isIgnoredClass(arg0.getParentNode().getValueType()))) {
            isIgnoreLogging = true;
        }
        return isIgnoreLogging;
    }

    private static boolean isIgnoredMethod(DiffNode arg0) {
        return arg0.getPropertyAnnotation(IgnoreLogging.class) != null;
    }

    private static boolean isIgnoredClass(Class className) {
        return className.getAnnotation(IgnoreLogging.class) != null;
    }

    private static boolean isIgnoredField(DiffNode arg0) {
        return arg0.getFieldAnnotation(IgnoreLogging.class) != null;
    }

    private static void updateMap(DiffNode arg0, Object oldValue, Object newValue, String properteyName, Map<String, Object> result, Class parentNodeClass) {
        if(isArgumentValid(arg0, properteyName) && isPropertyValid(arg0, properteyName) && parentNodeClass.equals(arg0.getParentNode().getValueType())) {
            if(!primitives.contains(arg0.getValueType().getSimpleName())){// && arg0.getValueType().isAnnotationPresent(NodeEntity.class)) {
                result.put(properteyName, checkDifferences(oldValue, newValue));
            } else {
                result.put(properteyName, newValue);
                result.put("old_" + properteyName, oldValue);
            }
        }
    }

    private static boolean isArgumentValid(DiffNode arg0, String properteyName) {
        return (isNotNull(properteyName) && isValid(arg0)) || isParentValid(arg0);
    }

    private static boolean isPropertyValid(DiffNode arg0, String properteyName) {
        return newHashSet(ADDED, CHANGED).contains(arg0.getState()) && !properteyName.toUpperCase().contains("UPDATEDATE") && !properteyName.equals("/");
    }

    static boolean isValid(DiffNode arg0) {
        return primitives.contains(arg0.getValueType().getName()) && isNotNull(arg0.getParentNode()) && arg0.getParentNode().getValueType().getPackage().getName().contains(PACKAGE_NAME);
    }

    static boolean isParentValid(DiffNode arg0) {
        LOGGER.debug("property {}", arg0.getPropertyName());
        return isNotNull(arg0.getParentNode()) && arg0.getParentNode().getValueType().getPackage().getName().contains(PACKAGE_NAME);
    }

    private static LoggingType getLoggingType(Map<String, Object> oldEntity, Map<String, Object> newEntity) {
        if(!oldEntity.containsKey("id")) {
            return LoggingType.CREATED;
        } else if((Boolean) newEntity.get("deleted")) {
            return LoggingType.DELETED;
        } else {
            return LoggingType.UPDATED;
        }
    }

    /*ActivityDTO oldActivityDTO = new ActivityDTO(new BigInteger("12"),"test",new BigInteger("123"));
        ActivityDTO newActivityDTO = new ActivityDTO(new BigInteger("125"),"test",new BigInteger("124"));
        ShiftActivityDTO newShiftActivity = new ShiftActivityDTO("dsada",asDate(LocalDate.now()),asDate(LocalDate.now().plusDays(1)),new BigInteger("12"),154l);
        ShiftActivityDTO oldShiftActivity = new ShiftActivityDTO("sadasdsasdas",asDate(LocalDate.of(2019,12,15)),asDate(LocalDate.of(2019,12,16)),new BigInteger("125"),152l);
        newShiftActivity.setActivity(newActivityDTO);
        oldShiftActivity.setActivity(oldActivityDTO);
        */
}
