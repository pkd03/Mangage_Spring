package vn.spring.pkd.service.product;

import vn.spring.pkd.dto.request.product.ProductRequest;
import vn.spring.pkd.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {
    ProductResponse create(ProductRequest productRequest);

    ProductResponse update(String id, ProductRequest productRequest);

    ProductResponse getById(String id);

    void delete(String id);

    List<ProductResponse> getAll();




}
