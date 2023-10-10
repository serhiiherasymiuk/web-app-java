package org.example.mappers;

import org.example.dto.cart.CartItemDTO;
import org.example.entities.CartEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CartMapper {
    @Mapping(target = "userId", source = "user.id")
    CartItemDTO cartToItemDTO(CartEntity cart);
    List<CartItemDTO> listCartsToItemDTO(List<CartEntity> list);
}
