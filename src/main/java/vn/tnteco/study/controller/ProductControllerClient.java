package vn.tnteco.study.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.tnteco.study.dto.response.product.ProductResponseForClient;
import vn.tnteco.study.service.product.ProductService;

@RestController
@RequestMapping("/api/client/products")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductControllerClient {
    ProductService productService;

    //Client
    @GetMapping
    public Page<ProductResponseForClient> getAllForClient(
            @PageableDefault(page = 0, size = 10, sort = "name", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return productService.getAllForClient(pageable);
    }

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/{id}")
    public ProductResponseForClient getProductByIdForClient(@PathVariable String id){
        return productService.getProductByIdForClient(id);
    }


}
