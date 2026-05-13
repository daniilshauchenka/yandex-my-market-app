package ru.yandex.practicum.mymarket.entity;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("cart_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {

    @Id private Long id;

    @Column("item_id")
    private Long itemId;

    private Integer count;

    public BigDecimal getTotalPrice(BigDecimal itemPrice) {
        return itemPrice.multiply(BigDecimal.valueOf(count));
    }
}
