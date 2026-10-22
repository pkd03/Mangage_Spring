package vn.tnteco.study.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.tnteco.study.dto.request.product.CreateProductRequest;
import vn.tnteco.study.dto.request.product.UpdateProductRequest;
import vn.tnteco.study.dto.response.product.ProductResponseForAdmin;
import vn.tnteco.study.service.product.ProductService;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@PreAuthorize("hasRole('ADMIN')")
public class ProductControllerAdmin {
    ProductService productService;

    @PostMapping
    public ProductResponseForAdmin createProduct(
            @Valid @RequestBody CreateProductRequest createProductRequest){
        return productService.createProduct(createProductRequest);
    }

    @PutMapping("/{id}")
    public ProductResponseForAdmin updateProduct(
            @PathVariable String id, @RequestBody UpdateProductRequest updateProductRequest){
        return productService.updateProduct(id, updateProductRequest);
    }

    @PreAuthorize("hasRole('STAFF')")
    @GetMapping
    public Page<ProductResponseForAdmin> getAllForAdmin(
            @PageableDefault(page = 0, size = 10, sort = "createAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return productService.getAllForAdmin(pageable);
    }

    @PreAuthorize("hasRole('STAFF')")
    @GetMapping("/{id}")
    public ProductResponseForAdmin getProductByIdForAdmin(@PathVariable String id) {
        return productService.getProductByIdForAdmin(id);
    }

    @GetMapping("/bin")
    public Page<ProductResponseForAdmin> getAllSoftDeleteForAdmin(
            @PageableDefault(page = 0, size = 10, sort = "deleteAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return productService.getAllSoftDeleteForAdmin(pageable);
    }

    // URL xóa mềm: DELETE http://localhost:8080/products/{id}
    // URL xóa cứng: DELETE http://localhost:8080/products/{id}?hard=true
    @DeleteMapping("/{id}")
    public String deleteProduct(
            @PathVariable String id,
            @RequestParam(defaultValue = "false") boolean hard) {
        if (hard) {
            productService.deleteProduct(id);
            return "Đã xóa vĩnh viễn sản phẩm khỏi hệ thống!";
        } else {
            productService.softDeleteProduct(id);
            return "Xóa mềm sản phẩm thành công (đã chuyển vào thùng rác)";
        }
    }

    // URL: PATCH http://localhost:8080/products/admin/bin/{id}/restore
    @PatchMapping("/bin/{id}/restore")
    public String restoreProduct(@PathVariable String id) {
        productService.restoreProduct(id);
        return "Khôi phục sản phẩm thành công!";
    }





}
