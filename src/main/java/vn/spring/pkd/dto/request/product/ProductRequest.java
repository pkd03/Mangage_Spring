package vn.spring.pkd.dto.request.product;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductRequest {

    String name;

    String description;

    BigDecimal price;

    Integer quantity;

    Boolean active;
}


