package org.example.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="tbl_cart_products")
public class CartProductEntity {
    @Id
    @ManyToOne
    @JoinColumn(name="product_id", nullable = false)
    private ProductEntity product;
    @Id
    @ManyToOne
    @JoinColumn(name="cart_id", nullable = false)
    private CartEntity cart;
}
