package vn.tnteco.study.repository;

import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.tnteco.study.model.Product;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    //    Khách
    //    query product active= true and isDelete = false cho khách xem
    Page<Product> findByActiveTrueAndIsDeleteFalse(Pageable pageable);

    Optional<Product> findByIdAndActiveTrueAndIsDeleteFalse(String id);



////////////////////////////////////////////////////////////////////////////////////////////

    //admin
    boolean existsByNameAndIsDeleteFalse( String name);

    boolean existsByCodeAndIsDeleteFalse(String code);




//    Xem danh sách quản lý: isDelete = false (lọc được theo active = true/false).
    Page<Product> findByIsDeleteFalse(Pageable pageable);

//    Xem thùng rác (Trash): isDelete = true
    Page<Product> findByIsDeleteTrue(Pageable pageable);

//    Khôi phục (Restore): Đổi isDelete = false, deleteAt = null.

//    Xóa hẳn (Hard delete): Dùng productRepository.delete(product)

    //các loại findById
    //findByIdAndIsDeleteFalse: sp ngoài thùng rác(xem chi tiết)
    //findByIdAndIsDeleteTrue: sp trong thùng rác( xóa hẳn, khôi phục)
    //findById: trong + ngoài thùng rác

    //xem chi tiết sp
    Optional<Product> findByIdAndIsDeleteFalse(String id);

    Optional<Product> findByIdAndIsDeleteTrue(String id);

    long countByActiveTrue();

//    số sp xóa mềm(trong thùng rác)
    long countByIsDeleteTrue();


}

