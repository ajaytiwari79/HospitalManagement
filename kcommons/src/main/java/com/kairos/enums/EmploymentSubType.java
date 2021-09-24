package com.kairos.enums;

public enum EmploymentSubType {

   MAIN("Main"),SECONDARY("Secondary"),NONE("None");

   public String value;

   EmploymentSubType(String value) {
      this.value = value;
   }

   public static EmploymentSubType getByValue(String value) {
      for (EmploymentSubType employmentSubType : EmploymentSubType.values()) {
         if (employmentSubType.value.equals(value)) {
            return employmentSubType;
         }
      }
      return null;
   }

}
