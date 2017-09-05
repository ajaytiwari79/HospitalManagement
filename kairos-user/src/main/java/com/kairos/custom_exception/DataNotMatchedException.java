package com.kairos.custom_exception;

/**
 * Created by vipul on 5/9/17.
 */
public class DataNotMatchedException  extends RuntimeException {

        public String message;

        public DataNotMatchedException(String message) {
            this.message = message;
        }

    }
