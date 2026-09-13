package vn.spring.pkd.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.query.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import vn.spring.pkd.dto.request.ApiResponse;
import vn.spring.pkd.dto.request.product.ProductRequest;
import vn.spring.pkd.dto.response.PageResponse;
import vn.spring.pkd.dto.response.ProductResponse;
import vn.spring.pkd.service.product.ProductService;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductController {
    ProductService productService;

    @PostMapping
    ApiResponse<ProductResponse> create(@RequestBody ProductRequest productRequest){
        return ApiResponse.<ProductResponse>builder()
                .result(productService.create(productRequest))
                .build();
    }

    @GetMapping
    ApiResponse<List<ProductResponse>> getAll(){
        return ApiResponse.<List<ProductResponse>>builder()
                .result(productService.getAll())
                .build();
    }

    @GetMapping("/page")
    public PageResponse<?> getAllWithPage(
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "pageNo", defaultValue = "0") int pageNo,
            @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy) {
        return productService.getAllWithPage(pageNo, pageSize, sortBy);
    }

    @GetMapping("/{id}")
    ApiResponse<ProductResponse> getById(@PathVariable() String id){
        return ApiResponse.<ProductResponse>builder()
                .result(productService.getById(id))
                .build();
    }

    @PutMapping("/{id}")
    ApiResponse<ProductResponse> update(@PathVariable String id, @RequestBody ProductRequest productRequest){
        return ApiResponse.<ProductResponse>builder()
                .result(productService.update(id, productRequest))
                .build();
    }


    @DeleteMapping("/{id}")
    ApiResponse<Void> delete(@PathVariable String id){
        productService.delete(id);
        return ApiResponse.<Void>builder().build();
    }

}
