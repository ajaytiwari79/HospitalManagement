package com.kairos.controller.processing_activity;


import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static com.kairos.constants.ApiConstant.API_PROCESSING_ACTIVITY;

@RestController
@RequestMapping(value = API_PROCESSING_ACTIVITY)
@Api(value =API_PROCESSING_ACTIVITY )
public class ProcessingActivityController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessingActivityController.class);
}
