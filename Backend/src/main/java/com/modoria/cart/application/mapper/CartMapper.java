package com.modoria.cart.application.mapper;

import com.modoria.cart.application.dto.CartItemResponseDTO;
import com.modoria.cart.application.dto.CartResponseDTO;
import com.modoria.cart.domain.model.Cart;
import com.modoria.cart.domain.model.CartItem;
import com.modoria.catalog.application.mapper.product.ProductMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { ProductMapper.class })
public interface CartMapper {

    CartResponseDTO toCartResponseDTO(Cart cart);

    @Mapping(target = "product", source = "product")
    CartItemResponseDTO toCartItemResponseDTO(CartItem cartItem);
}
