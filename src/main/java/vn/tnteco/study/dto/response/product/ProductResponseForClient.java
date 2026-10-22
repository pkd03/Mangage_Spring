package vn.tnteco.study.dto.response.product;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponseForClient {
    String name;

    String description;

    BigDecimal price;

    Integer quantity;
}
