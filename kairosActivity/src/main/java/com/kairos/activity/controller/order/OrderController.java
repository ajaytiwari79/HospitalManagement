package com.kairos.activity.controller.order;

import com.kairos.activity.service.open_shift.OrderService;
import com.kairos.activity.util.response.ResponseHandler;
import com.kairos.response.dto.web.open_shift.OrderOpenshiftResponseDTO;
import com.kairos.response.dto.web.open_shift.OrderResponseDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.Map;

import static com.kairos.activity.constants.ApiConstants.ORDER_URL;
import static com.kairos.activity.constants.ApiConstants.UNIT_URL;

@RestController
@Api(ORDER_URL)
@RequestMapping(ORDER_URL)
public class OrderController {

    @Inject
    OrderService orderService;

    @RequestMapping(value = "", method = RequestMethod.POST)
    @ApiOperation("create orders")
    public ResponseEntity<Map<String, Object>> createOrder(@PathVariable Long unitId,@RequestBody OrderOpenshiftResponseDTO orderResponseDto)  {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,orderService.createOrder(unitId,orderResponseDto));
    }

    @RequestMapping(value = "/{orderId}", method = RequestMethod.PUT)
    @ApiOperation("update orders")
    public ResponseEntity<Map<String, Object>> updateOrder(@PathVariable BigInteger orderId, @RequestBody OrderOpenshiftResponseDTO orderResponseDto)  {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, orderService.updateOrder(orderResponseDto,orderId));
    }

    @ApiOperation("delete an order")
    @DeleteMapping(value = "/{orderId}")
    public ResponseEntity<Map<String, Object>> deleteShift(@PathVariable BigInteger orderId) {
        orderService.deleteOrder(orderId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, true);
    }

    @ApiOperation(value = "Get All orders by unitId")
    @RequestMapping(method = RequestMethod.GET)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllOrdersByUnitId(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, orderService.getOrdersByUnitId(unitId));
    }

    @ApiOperation(value = "Get All PriorityGroup and OpenShifts by orderId")
    @RequestMapping(value = "/{orderId}/openshifts", method = RequestMethod.GET)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllPriorityGroupAndOrdersByUnitId(@PathVariable Long unitId,@PathVariable BigInteger orderId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, orderService.getPriorityGroupAndOpenShiftsByOrderId(unitId,orderId));
    }



}

