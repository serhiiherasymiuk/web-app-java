package org.example.dto.cart;

import lombok.Data;
import org.example.dto.product.ProductItemDTO;

import java.util.List;

@Data
public class CartItemDTO {
    private int id;
    private List<ProductItemDTO> products;
    private int userId;
}
