package vn.tnteco.study.service.product;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import vn.tnteco.study.dto.request.product.CreateProductRequest;
import vn.tnteco.study.dto.request.product.UpdateProductRequest;
import vn.tnteco.study.dto.response.product.ProductResponseForAdmin;
import vn.tnteco.study.dto.response.product.ProductResponseForClient;
import vn.tnteco.study.exception.AppException;
import vn.tnteco.study.exception.ErrorCode;
import vn.tnteco.study.model.Product;
import vn.tnteco.study.repository.ProductRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductServiceImpl implements ProductService{

    ProductRepository productRepository;

    @Override
    public ProductResponseForClient getProductByIdForClient(String id) {
        Product product = productRepository.findByIdAndActiveTrueAndIsDeleteFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        return toResponseForClient(product);
    }

    @Override
    public Page<ProductResponseForClient> getAllForClient(Pageable pageable) {
        Page<Product> productPage = productRepository.findByActiveTrueAndIsDeleteFalse(pageable);
        return productPage.map(this::toResponseForClient);
    }

    private ProductResponseForClient toResponseForClient(Product product){
        return ProductResponseForClient.builder()
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .build();
    }

////////////////////////////////////////////////////////////////////////////////////////////
///
    @Override
    public Page<ProductResponseForAdmin> getAllForAdmin(Pageable pageable) {
        Page<Product> productPage = productRepository.findByIsDeleteFalse(pageable);
        return productPage.map(this::toResponseForAdmin);
    }

    @Override
    public Page<ProductResponseForAdmin> getAllSoftDeleteForAdmin(Pageable pageable) {
        Page<Product> productPage = productRepository.findByIsDeleteTrue(pageable);
        return productPage.map(this::toResponseForAdmin);
    }

    @Override
    @Transactional
    public ProductResponseForAdmin createProduct(@RequestBody CreateProductRequest createProductRequest) {
        if(productRepository.existsByNameAndIsDeleteFalse(createProductRequest.getName())){
            throw new AppException(ErrorCode.PRODUCT_NAME_EXISTED);
        }
        if(productRepository.existsByCodeAndIsDeleteFalse(createProductRequest.getCode())){
            throw new AppException(ErrorCode.PRODUCT_CODE_EXISTED);
        }

        Product product = Product.builder()
                .code(createProductRequest.getCode())
                .name(createProductRequest.getName())
                .description(createProductRequest.getDescription())
                .price(createProductRequest.getPrice())
                .quantity(createProductRequest.getQuantity())
                .build();
        productRepository.save(product);
        return toResponseForAdmin(product);
    }

    //sửa sp isDelete=false(trong thùng rác chỉ đc xem)
    // trong phạm vi ngoài thùng rác mới cho sửa
    @Override
    @Transactional
    public ProductResponseForAdmin updateProduct(
            String id, UpdateProductRequest updateProductRequest) {
        // 1. Tìm sản phẩm cần cập nhật (chỉ cho phép sửa sản phẩm chưa bị xóa mềm)
        Product product = productRepository.findByIdAndIsDeleteFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        //với name, code k đc trùng nghệp vụ như sau
        // sửa A thành !A đồng thời k trùng với B,C,D,..(các sp khác) thì cập nhật
        // 2. Kiểm tra trùng tên (Chỉ kiểm tra nếu tên mới khác tên cũ)
        if (updateProductRequest.getName() != null && !updateProductRequest.getName().equals(product.getName())) {
            if (productRepository.existsByNameAndIsDeleteFalse(updateProductRequest.getName())) {
                throw new AppException(ErrorCode.PRODUCT_NAME_EXISTED);
            }
            product.setName(updateProductRequest.getName());
        }
        // 3. Kiểm tra trùng mã code (Chỉ kiểm tra nếu code mới khác code cũ)
        if (updateProductRequest.getCode() != null && !updateProductRequest.getCode().equals(product.getCode())) {
            if (productRepository.existsByCodeAndIsDeleteFalse(updateProductRequest.getCode())) {
                throw new AppException(ErrorCode.PRODUCT_CODE_EXISTED);
            }
            product.setCode(updateProductRequest.getCode());
        }

        //với các trường có thể được trùng nghệp vụ như sau
        // chỉ cần có sửa thì cho cập nhật
        // 4. Cập nhật các trường thông tin khác
        if (updateProductRequest.getDescription() != null) {
            product.setDescription(updateProductRequest.getDescription());
        }
        if (updateProductRequest.getPrice() != null) {
            product.setPrice(updateProductRequest.getPrice());
        }
        if (updateProductRequest.getQuantity() != null) {
            product.setQuantity(updateProductRequest.getQuantity());
        }
        if (updateProductRequest.getActive() != null) {
            product.setActive(updateProductRequest.getActive());
        }

        // 5. Lưu lại sản phẩm (Lúc này @PreUpdate trong Product.java sẽ tự cập nhật updateAt)
        productRepository.save(product);
        // 6. Trả về thông tin cập nhật cho Admin
        return toResponseForAdmin(product);
    }


//    isDelete = true (đánh dấu đã xóa).
//    deleteAt = LocalDateTime.now() (lưu thời điểm xóa vào thùng rác).
//    active = false (tắt trạng thái hoạt động để chắc chắn không hiển thị).
    @Override
    @Transactional
    public void softDeleteProduct(String id) {
        // 1. Kiểm tra sản phẩm có tồn tại và chưa bị xóa mềm không
        Product product = productRepository.findByIdAndIsDeleteFalse(id)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại hoặc đã nằm trong thùng rác!"));
        // 2. Cập nhật các trường xóa mềm
        product.setDelete(true);                      // Lombok tạo setter: setDelete(boolean)
        product.setDeleteAt(LocalDateTime.now());     // Lưu thời gian xóa
        product.setActive(false);                     // Tắt kích hoạt
        // 3. Lưu lại vào Database
        productRepository.save(product);
    }

    @Override
    @Transactional
    public void restoreProduct(String id) {
        Product product = productRepository.findByIdAndIsDeleteTrue(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        // 1. Kiểm tra mã của sản phẩm này có bị trùng với SP nào đang ở ngoài không
        if (productRepository.existsByCodeAndIsDeleteFalse(product.getCode())) {
            throw new AppException(ErrorCode.PRODUCT_RESTORE_CONFLICT);
        }
        // 2. Kiểm tra tên có bị trùng không
        if (productRepository.existsByNameAndIsDeleteFalse(product.getName())) {
            throw new AppException(ErrorCode.PRODUCT_RESTORE_CONFLICT);
        }
        // 3. Nếu không trùng gì thì mới khôi phục
        product.setDelete(false);
        product.setDeleteAt(null);
        productRepository.save(product);
    }


    @Override
    @Transactional
    public void deleteProduct(String id) {
        productRepository.findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        productRepository.deleteById(id);
    }

    @Override
    public ProductResponseForAdmin getProductByIdForAdmin(String id) {
    //dùng findById để xem chi tiết các sp trong/ngoài thùng rác
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        return toResponseForAdmin(product);
    }

    private ProductResponseForAdmin toResponseForAdmin(Product product){
        return ProductResponseForAdmin.builder()
                .id(product.getId())
                .name(product.getName())
                .code(product.getCode())
                .description(product.getDescription())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .active(product.getActive())
                .isDelete(product.isDelete())
                .build();
    }

}
