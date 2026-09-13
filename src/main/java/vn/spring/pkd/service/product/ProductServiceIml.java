package vn.spring.pkd.service.product;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vn.spring.pkd.dto.request.product.ProductRequest;
import vn.spring.pkd.dto.response.PageResponse;
import vn.spring.pkd.dto.response.ProductResponse;
import vn.spring.pkd.entity.Product;
import vn.spring.pkd.repository.ProductRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductServiceIml implements ProductService{
    ProductRepository productRepository;

    @Override
    public ProductResponse create(ProductRequest productRequest) {
        if(productRepository.checkProductName(productRequest.getName())){
            throw new RuntimeException("Productname already exists");
        }

        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .quantity(productRequest.getQuantity())
                .active(productRequest.getActive())
                .build();
        productRepository.save(product);
        return toResponse(product);
    }

    @Override
    public ProductResponse update(String id, ProductRequest productRequest) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setQuantity(productRequest.getQuantity());
        product.setActive(productRequest.getActive());

        productRepository.save(product);
        return toResponse(product);
    }

    @Override
    public ProductResponse getById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return toResponse(product);
    }

    @Override
    public void delete(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        productRepository.delete(product);
    }

    @Override
    public List<ProductResponse> getAll() {
        return productRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public PageResponse<?> getAllWithPage(int pageNo, int pageSize, String sortBy) {
        Sort sort = Sort.by(sortBy);
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<Product> products = productRepository.findAllByActiveTrue(pageable);
        List<Product> productList = products.getContent();
        return PageResponse.<Product>builder()
                .page(products.getTotalPages())
                .total(products.getTotalElements())
                .rows(productList)
                .size(products.getSize())
                .build();
    }

//    @Override
//    public Page<ProductResponse> getAllWithPage(Pageable pageable) {
//        return null;
//    }

    private ProductResponse toResponse(Product product){
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .active(product.getActive())
                .build();

    }
}
