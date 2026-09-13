package vn.spring.pkd.service.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.spring.pkd.dto.request.product.ProductRequest;
import vn.spring.pkd.dto.response.PageResponse;
import vn.spring.pkd.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {
    ProductResponse create(ProductRequest productRequest);

    ProductResponse update(String id, ProductRequest productRequest);

    ProductResponse getById(String id);

    void delete(String id);

    List<ProductResponse> getAll();

//    Page<ProductResponse> getAllWithPage(Pageable pageable);

    PageResponse<?> getAllWithPage(int pageNo, int pageSize, String sortBy);




}
