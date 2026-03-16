package com.modoria.order.application.mapper;

import com.modoria.catalog.application.mapper.product.ProductMapper;
import com.modoria.order.application.dto.OrderItemResponseDTO;
import com.modoria.order.application.dto.OrderResponseDTO;
import com.modoria.order.domain.model.Order;
import com.modoria.order.domain.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { ProductMapper.class })
public interface OrderMapper {

    @Mapping(target = "customerId", source = "user.id")
    @Mapping(target = "customerName", source = "user.fullName")
    @Mapping(target = "customerEmail", source = "user.email")
    OrderResponseDTO toOrderResponseDTO(Order order);

    @Mapping(target = "product", source = "product")
    OrderItemResponseDTO toOrderItemResponseDTO(OrderItem orderItem);
}
