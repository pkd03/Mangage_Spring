package vn.spring.pkd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.spring.pkd.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    @Query("""
    SELECT COUNT(p) > 0
    FROM Product p
    WHERE p.name = :name
""")
    boolean checkProductName(@Param("name") String name);

}
