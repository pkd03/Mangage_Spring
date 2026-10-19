# Product Management API 📦

Hệ thống RESTful API quản lý sản phẩm với phân quyền **Khách hàng (Client)** và **Quản trị viên (Admin)**, tích hợp cơ chế **Thùng rác (Soft Delete, Restore, Hard Delete)**, kiểm soát trùng lặp thông minh và **Phân trang (Pagination)** chuẩn Spring Data JPA.

---

## 🛠️ Công nghệ sử dụng (Tech Stack)

- **Ngôn ngữ**: Java 17
- **Framework**: Spring Boot (Spring Web, Spring Data JPA)
- **Cơ sở dữ liệu**: PostgreSQL
- **ORM / Persistence**: Hibernate / JPA
- **Thư viện tiện ích**: Lombok
- **Quản lý build**: Maven Wrapper (`mvnw`)
- **Công cụ kiểm thử API**: Postman (Đính kèm sẵn collection trong dự án)

---

## 📁 Cấu trúc thư mục (Project Structure)

```text
src/main/java/vn/tnteco/study/
├── controller/
│   ├── ProductControllerAdmin.java     # Các endpoint quản trị (/products/admin)
│   └── ProductControllerClient.java    # Các endpoint cho khách hàng (/products/client)
├── dto/
│   ├── request/
│   │   ├── CreateProductRequest.java   # DTO tạo sản phẩm mới
│   │   ├── UpdateProductRequest.java   # DTO cập nhật sản phẩm
│   │   └── ProductFilterRequest.java   # DTO tiêu chí tìm kiếm & lọc
│   └── response/
│       ├── ProductResponseForAdmin.java   # Trả về cho Admin (đầy đủ code, active, isDelete)
│       └── ProductResponseForClient.java  # Trả về cho Khách (ẩn code, active, isDelete)
├── model/
│   └── Product.java                    # Entity ánh xạ bảng `product` trong PostgreSQL
├── repository/
│   └── ProductRepository.java          # JPA Repository xử lý truy vấn dữ liệu & điều kiện thùng rác
└── service/
    └── product/
        ├── ProductService.java         # Interface định nghĩa nghiệp vụ
        └── ProductServiceImpl.java     # Cài đặt chi tiết nghiệp vụ và ràng buộc dữ liệu
```

---

## 📌 Các quy tắc nghiệp vụ cốt lõi (Core Business Rules)

### 1. Phân quyền hiển thị (Client vs Admin)
- **Khách hàng (`/products/client`)**:
  - Chỉ xem các sản phẩm **đang mở bán** (`active = true`) và **chưa bị xóa** (`isDelete = false`).
  - DTO trả về được lược bỏ các trường nhạy cảm: **ẩn `code`, `active`, `isDelete`**.
- **Admin (`/products/admin`)**:
  - Xem được toàn bộ sản phẩm (kể cả sản phẩm đang tạm tắt `active = false`).
  - Có toàn quyền CRUD: Tạo, Sửa, Xóa mềm, Xem thùng rác, Khôi phục, Xóa cứng.
  - DTO trả về đầy đủ mọi thông tin.

### 2. Cơ chế Thùng rác & Ràng buộc mã/tên (`code`, `name`)
- **Nguyên tắc duy nhất**: `code` và `name` chỉ cần duy nhất trong tập hợp các sản phẩm **đang hoạt động ngoài thùng rác (`isDelete = false`)**.
- **Thêm mới (Create)**: Cho phép trùng `code` hoặc `name` với sản phẩm đã bị xóa vào thùng rác (`isDelete = true`).
- **Sửa sản phẩm (Update)**: Chỉ cho phép sửa sản phẩm ngoài thùng rác. Kiểm tra không trùng mã/tên với các sản phẩm khác ngoài thùng rác.
- **Khôi phục (Restore)**: Trước khi khôi phục một sản phẩm từ thùng rác, hệ thống kiểm tra xem mã hoặc tên của nó có bị sản phẩm nào bên ngoài chiếm chỗ hay chưa. Nếu đã bị chiếm chỗ -> **Chặn khôi phục và thông báo đổi mã**.
- **Xóa mềm (Soft Delete)**: Đánh dấu `isDelete = true`, lưu thời gian `deleteAt = LocalDateTime.now()` và đặt `active = false`.
- **Xóa cứng (Hard Delete)**: Xóa vĩnh viễn bản ghi khỏi database bằng `DELETE FROM product WHERE id = ?`.

---

## 📖 Danh sách API Endpoints

### 1. Dành cho Khách hàng (`/products/client`)

| Method | Endpoint | Mô tả | Tham số / Body |
| :--- | :--- | :--- | :--- |
| `GET` | `/products/client` | Lấy danh sách sản phẩm hiển thị (có phân trang) | `?page=0&size=10&sort=name,desc` |
| `GET` | `/products/client/{id}` | Xem chi tiết sản phẩm cho khách | `id` (Path variable) |

### 2. Dành cho Quản trị viên (`/products/admin`)

| Method | Endpoint | Mô tả | Tham số / Body |
| :--- | :--- | :--- | :--- |
| `POST` | `/products/admin` | Tạo sản phẩm mới | JSON `CreateProductRequest` |
| `GET` | `/products/admin` | Xem danh sách quản lý sản phẩm (phân trang, sort) | `?page=0&size=10&sort=createAt,desc` |
| `GET` | `/products/admin/{id}` | Xem chi tiết sản phẩm cho Admin | `id` (Path variable) |
| `PUT` | `/products/admin/{id}` | Cập nhật thông tin sản phẩm | JSON `UpdateProductRequest` |
| `DELETE` | `/products/admin/{id}` | Xóa mềm sản phẩm (chuyển vào thùng rác) | `id` (Path variable) |
| `GET` | `/products/admin/bin` | Xem danh sách thùng rác (phân trang) | `?page=0&size=10&sort=deleteAt,desc` |
| `PATCH`| `/products/admin/bin/{id}/restore` | Khôi phục sản phẩm từ thùng rác | `id` (Path variable) |
| `DELETE` | `/products/admin/{id}?hard=true` | Xóa vĩnh viễn sản phẩm khỏi hệ thống | `id` (Path variable), `hard=true` |

---

## 🚀 Hướng dẫn cài đặt & Khởi chạy (Getting Started)

### 1. Yêu cầu môi trường
- **JDK**: Java 17 trở lên.
- **PostgreSQL**: Đã tạo sẵn database (ví dụ: `study_db`).

### 2. Cấu hình Database
Trong file [src/main/resources/application.yaml](file:///d:/intern/w3/code/study/src/main/resources/application.yaml), bạn có thể cấu hình trực tiếp hoặc thông qua biến môi trường:

```yaml
spring:
  datasource:
    url: ${DB_URL:jdbc:postgresql://localhost:5432/study_db}
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:postgres}
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

*Hoặc cấu hình biến môi trường trong IDE:*
- `DB_URL`: `jdbc:postgresql://localhost:5432/study_db`
- `DB_USERNAME`: `postgres`
- `DB_PASSWORD`: `mat_khau_cua_ban`

### 3. Lệnh khởi chạy ứng dụng
Mở terminal tại thư mục gốc dự án và chạy:

```bash
# Trên Windows (PowerShell / CMD)
.\mvnw.cmd spring-boot:run

# Trên Linux / macOS
./mvnw spring-boot:run
```

Ứng dụng sẽ khởi động tại cổng mặc định: `http://localhost:8080`.

---

## 🧪 Hướng dẫn kiểm thử với Postman

Dự án đã tích hợp sẵn trọn bộ test cases trong file:  
👉 [`Product_Management_API.postman_collection.json`](file:///d:/intern/w3/code/study/Product_Management_API.postman_collection.json)

### Các bước Import và Test:
1. Mở **Postman** -> Nhấn nút **Import**.
2. Chọn file `Product_Management_API.postman_collection.json` từ thư mục dự án.
3. Collection bao gồm 4 nhóm kiểm thử:
   - **`01. Admin - Luồng cơ bản (Happy Path)`**: Tạo, Sửa, Lấy danh sách phân trang, Xóa mềm, Khôi phục, Xóa cứng.
   - **`02. Client - Luồng khách hàng (Happy Path)`**: Xem danh sách ẩn code/active, Xem chi tiết sản phẩm.
   - **`03. Nghiệp vụ Thùng rác & Trùng lặp`**: Kiểm tra toàn bộ quy tắc thêm mới trùng mã với thùng rác, chặn khôi phục khi bị chiếm mã ngoài thùng rác, chặn sửa sản phẩm trong thùng rác.
   - **`04. Các trường hợp lỗi & Validation`**: Kiểm tra nhập trùng mã đang hoạt động, ID không tồn tại,...
