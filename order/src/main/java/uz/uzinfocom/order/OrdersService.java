package uz.uzinfocom.order;

import org.springframework.data.domain.Page;
import uz.uzinfocom.order.dto.OrdersRequestDto;
import uz.uzinfocom.order.dto.OrdersResponseDto;

public interface OrdersService {

  OrdersResponseDto createOrder(OrdersRequestDto requestDTO);

  Page<OrdersResponseDto> getAllOrders(int page, int size, String sortBy, String direction);

  Page<OrdersResponseDto> getOrdersByUserId(Long userId, int page, int size);
}
