package com.kairos.activity.service.open_shift;

import com.kairos.activity.custom_exception.DataNotFoundByIdException;
import com.kairos.activity.persistence.model.open_shift.Order;
import com.kairos.activity.persistence.repository.open_shift.OrderMongoRepository;
import com.kairos.activity.persistence.repository.phase.PhaseMongoRepository;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.service.phase.PhaseService;
import com.kairos.activity.util.ObjectMapperUtils;
import com.kairos.response.dto.web.open_shift.OrderResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService extends MongoBaseService {

    private static final Logger logger = LoggerFactory.getLogger(PhaseService.class);
    @Inject
    private OrderMongoRepository orderMongoRepository;


   public void createOrder(OrderResponseDTO orderResponseDTO) {

    Order order = new Order();
    ObjectMapperUtils.copyProperties(orderResponseDTO,order);
    save(order);

    }

    public void updateOrder(OrderResponseDTO orderResponseDTO,BigInteger orderId) {

        Order order = orderMongoRepository.findOrderByIdAndEnabled(orderId);
        if(!Optional.ofNullable(order).isPresent()) {
            throw new DataNotFoundByIdException("Order doesn not exist by id"+ orderId);
        }
        ObjectMapperUtils.copyProperties(orderResponseDTO,order);
        save(order);

    }

    public void deleteOrder(BigInteger orderId) {

        Order order = orderMongoRepository.findOrderByIdAndEnabled(orderId);
        if(!Optional.ofNullable(order).isPresent()) {
            throw new DataNotFoundByIdException("Order doesn not exist by id"+ orderId);
        }
        order.setDeleted(true);
        save(order);

    }


    public List<Order> getOrdersByUnitId(Long unitId) {

        List<Order> orders = orderMongoRepository.findOrdersByUnitId(unitId);
        return orders;
    }
}
