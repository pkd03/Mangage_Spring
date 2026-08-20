package vn.spring.pkd.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {
    String id;

    String name;

    String description;

    BigDecimal price;

    Integer quantity;

    Boolean active;
}
