package vn.tnteco.study.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateProductRequest {
    String code;

    String name;

    String description;

    BigDecimal price;

    Integer quantity;

    Boolean active;
}
