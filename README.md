# Product Management API 📦

Hệ thống RESTful API quản lý sản phẩm chuyên nghiệp được xây dựng trên nền tảng **Spring Boot**, tích hợp cơ chế xác thực không lưu trạng thái **Spring Security + JWT (Stateless)**, phân quyền kế thừa theo thứ bậc **Role Hierarchy (`ADMIN > STAFF > CLIENT`)**, kiểm soát vòng đời dữ liệu với **Thùng rác thông minh (Soft Delete, Restore, Hard Delete)**, kiểm tra xung đột mã/tên linh hoạt theo ngữ cảnh, xử lý ngoại lệ tập trung (**Global Exception Handling**) và tài liệu hóa tự động qua **Swagger UI / OpenAPI 3**.

---

## 📑 Mục lục
1. [Công nghệ sử dụng (Tech Stack)](#-công-nghệ-sử-dụng-tech-stack)
2. [Cấu trúc thư mục dự án (Project Structure)](#-cấu-trúc-thư-mục-dự-án-project-structure)
3. [Thiết kế Cơ sở dữ liệu (Database Schema)](#-thiết-kế-cơ-sở-dữ-liệu-database-schema)
4. [Các quy tắc nghiệp vụ cốt lõi (Core Business Rules)](#-các-quy-tắc-nghiệp-vụ-cốt-lõi-core-business-rules)
5. [Chi tiết các đầu API Endpoints](#-chi-tiết-các-đầu-api-endpoints)
   - [5.1. Authentication (Xác thực JWT)](#51-authentication-xác-thực-jwt)
   - [5.2. Client APIs (Dành cho Khách hàng)](#52-client-apis-dành-cho-khách-hàng)
   - [5.3. Admin & Staff APIs (Quản trị & Nhân viên)](#53-admin--staff-apis-quản-trị--nhân-viên)
6. [Chuẩn hóa xử lý ngoại lệ (Error Handling & Error Codes)](#-chuẩn-hóa-xử-lý-ngoại-lệ-error-handling--error-codes)
7. [Hướng dẫn cài đặt & Khởi chạy (Getting Started)](#-hướng-dẫn-cài-đặt--khởi-chạy-getting-started)
8. [Tài liệu tương tác Swagger UI](#-tài-liệu-tương-tác-swagger-ui)
9. [Hướng dẫn kiểm thử tự động với Postman](#-hướng-dẫn-kiểm-thử-tự-động-với-postman)

---

## 🛠️ Công nghệ sử dụng (Tech Stack)

| Thành phần | Công nghệ / Thư viện | Phiên bản / Ghi chú |
| :--- | :--- | :--- |
| **Ngôn ngữ** | Java | 17 (LTS) |
| **Framework nền tảng** | Spring Boot | Spring Web MVC, Spring Data JPA, Spring Security |
| **Bảo mật & Token** | Spring Security & JJWT | `io.jsonwebtoken` (0.12.5) - HMAC-SHA256 |
| **Mã hóa mật khẩu** | BCrypt | `BCryptPasswordEncoder` |
| **Cơ sở dữ liệu** | PostgreSQL | PostgreSQL 13+ |
| **ORM / Persistence** | Hibernate / JPA | Tự động cập nhật DDL (`hibernate.ddl-auto: update`) |
| **Validation** | Jakarta Bean Validation | `spring-boot-starter-validation` (Hibernate Validator) |
| **Quản lý biến môi trường** | `spring-dotenv` | 4.0.0 (Tự động nạp file `.env` khi khởi động) |
| **Tài liệu hóa API** | SpringDoc OpenAPI 3 | `springdoc-openapi-starter-webmvc-ui` (Swagger UI) |
| **Tiện ích & Mapping** | Lombok & MapStruct | Tối ưu boilerplate code và mapper hiệu năng cao |
| **Quản lý build** | Maven Wrapper | `mvnw`, `mvnw.cmd` |

---

## 📁 Cấu trúc thư mục dự án (Project Structure)

```text
src/main/java/vn/tnteco/study/
├── StudyApplication.java                      # Lớp khởi chạy ứng dụng Spring Boot
├── config/
│   └── PasswordEncoderConfig.java             # Cấu hình Bean BCryptPasswordEncoder dùng chung
├── controller/
│   ├── AuthController.java                    # API đăng nhập (/api/auth/login)
│   ├── ProductControllerAdmin.java            # API quản trị sản phẩm (/api/admin/products)
│   └── ProductControllerClient.java           # API khách hàng xem sản phẩm (/api/client/products)
├── dto/
│   ├── request/
│   │   ├── product/
│   │   │   ├── CreateProductRequest.java      # Validation dữ liệu tạo mới sản phẩm
│   │   │   └── UpdateProductRequest.java      # Validation dữ liệu cập nhật sản phẩm
│   │   └── user/
│   │       └── LoginRequest.java              # Validation dữ liệu đăng nhập tài khoản
│   └── response/
│       ├── auth/
│       │   └── AuthenticationResponse.java    # Trả về Token JWT, username và danh sách roles
│       └── product/
│           ├── ProductResponseForAdmin.java   # Đầy đủ trường: id, code, active, isDelete, timestamps
│           └── ProductResponseForClient.java  # Lược bỏ trường nhạy cảm: chỉ trả name, desc, price, quantity
├── exception/
│   ├── AppException.java                      # Custom runtime exception đóng gói ErrorCode
│   ├── ErrorCode.java                         # Enum định nghĩa mã lỗi, HTTP status và thông điệp tiếng Việt
│   ├── ErrorResponse.java                     # Cấu trúc JSON trả về đồng nhất khi xảy ra lỗi
│   └── GlobalExceptionHandler.java            # @RestControllerAdvice xử lý tập trung mọi ngoại lệ hệ thống
├── model/
│   ├── Product.java                           # Entity bảng `product` (UUID, Soft Delete, Auditing)
│   ├── Role.java                              # Entity bảng `roles` (ADMIN, STAFF, CLIENT)
│   ├── User.java                              # Entity bảng `users` (id, username, password)
│   └── UserRole.java                          # Entity bảng trung gian `user_roles` (N-N giữa User và Role)
├── repository/
│   ├── ProductRepository.java                 # Truy vấn JPA sản phẩm (lọc active, thùng rác, kiểm tra tồn tại)
│   └── user/
│       ├── UserRepository.java                # Tìm kiếm User theo username
│       └── UserRoleRepository.java            # Lấy danh sách Role IDs theo User ID
├── security/
│   ├── JwtAccessDeniedHandler.java            # Bắt lỗi 403 Forbidden -> trả JSON ErrorResponse chuẩn
│   ├── JwtAuthenticationEntryPoint.java       # Bắt lỗi 401 Unauthorized -> trả JSON ErrorResponse chuẩn
│   ├── JwtAuthenticationFilter.java           # Filter phân tích Bearer Token trong Authorization Header
│   ├── JwtTokenProvider.java                  # Sinh JWT, trích xuất Claims (username, roles), kiểm tra chữ ký
│   └── SecurityConfig.java                    # Cấu hình SecurityFilterChain & RoleHierarchy
└── service/
    ├── auth/
    │   └── AuthenticationService.java         # Xác thực username/password và cấp phát JWT
    └── product/
        ├── ProductService.java                # Interface nghiệp vụ sản phẩm
        └── ProductServiceImpl.java            # Cài đặt chi tiết nghiệp vụ sản phẩm & quy tắc thùng rác
```

---

## 🗄️ Thiết kế Cơ sở dữ liệu (Database Schema)

Hệ thống quản lý dữ liệu trên 4 bảng chính với quan hệ chặt chẽ:

```mermaid
erDiagram
    users ||--o{ user_roles : "has"
    roles ||--o{ user_roles : "belongs to"
    users {
        varchar(255) id PK "UUID"
        varchar(255) username UK "Not Null"
        varchar(255) password "BCrypt Hash"
    }
    roles {
        varchar(255) id PK "UUID"
        varchar(255) name "Tên Role (ADMIN, STAFF, CLIENT)"
        varchar(255) description "Mô tả vai trò"
    }
    user_roles {
        varchar(255) id PK "UUID"
        varchar(255) user_id FK "Liên kết users.id"
        varchar(255) role_id FK "Liên kết roles.id"
    }
    product {
        varchar(255) id PK "UUID"
        varchar(20) code "Mã sản phẩm (duy nhất ngoài thùng rác)"
        varchar(150) name "Tên sản phẩm (duy nhất ngoài thùng rác)"
        varchar(1000) description "Mô tả chi tiết"
        numeric(15,3) price "Giá bán sản phẩm (> 0)"
        integer quantity "Số lượng tồn kho (>= 0)"
        boolean active "Trạng thái mở bán (true/false)"
        boolean is_delete "Cờ đánh dấu xóa mềm"
        timestamp create_at "Thời điểm tạo bản ghi"
        timestamp update_at "Thời điểm cập nhật bản ghi"
        timestamp delete_at "Thời điểm đưa vào thùng rác"
    }
```

---

## 📌 Các quy tắc nghiệp vụ cốt lõi (Core Business Rules)

### 1. Cơ chế Bảo mật & Phân quyền kế thừa (Role Hierarchy)
- **Kiến trúc Stateless**: Toàn bộ phiên làm việc không dùng HTTP Session; mọi request được xác thực độc lập thông qua header:
  ```http
  Authorization: Bearer <token_jwt>
  ```
- **Phân cấp quyền hạn (`RoleHierarchy`)**:
  $$\text{ROLE\_ADMIN} > \text{ROLE\_STAFF} > \text{ROLE\_CLIENT}$$
  - **`ADMIN`**: Có quyền cao nhất trong hệ thống; thực hiện toàn bộ thao tác CRUD sản phẩm, xem thùng rác, khôi phục và xóa vĩnh viễn.
  - **`STAFF`**: Kế thừa toàn bộ quyền của `CLIENT`, đồng thời có quyền xem danh sách quản trị sản phẩm (`GET /api/admin/products`, `GET /api/admin/products/{id}`).
  - **`CLIENT`**: Khách hàng thông thường, có quyền truy cập các API mua sắm/xem hàng của client.
- **Xử lý ngoại lệ bảo mật trả về JSON chuẩn**:
  - Khi chưa đăng nhập hoặc token không hợp lệ $\rightarrow$ `JwtAuthenticationEntryPoint` trả về HTTP `401 Unauthorized` kèm mã lỗi `1004 UNAUTHENTICATED`.
  - Khi token không đủ quyền (ví dụ Client gọi API Admin) $\rightarrow$ `JwtAccessDeniedHandler` trả về HTTP `403 Forbidden` kèm mã lỗi `1005 UNAUTHORIZED`.

### 2. Phân tách Dữ liệu Client vs Quản trị
- **Dành cho Khách hàng (`/api/client/products`)**:
  - Chỉ xem các sản phẩm thỏa mãn đồng thời: `active = true` (đang mở bán) và `isDelete = false` (chưa bị xóa).
  - Áp dụng DTO `ProductResponseForClient`: Ẩn hoàn toàn `id`, `code`, `active`, `isDelete`, `createAt`, `updateAt`, `deleteAt` nhằm bảo mật dữ liệu nội bộ.
- **Dành cho Quản trị viên & Nhân viên (`/api/admin/products`)**:
  - Xem được toàn bộ sản phẩm ngoài thùng rác, bao gồm cả sản phẩm đang tạm tắt (`active = false`).
  - Áp dụng DTO `ProductResponseForAdmin`: Hiển thị đầy đủ mọi trường dữ liệu và lịch sử audit.

### 3. Vòng đời dữ liệu & Cơ chế Thùng rác (Trash Lifecycle)
- **Nguyên tắc duy nhất theo ngữ cảnh**: Ràng buộc `code` và `name` chỉ áp dụng trong tập hợp sản phẩm **đang hoạt động bên ngoài thùng rác (`isDelete = false`)**.
- **Thêm mới (Create)**:
  - Cho phép đặt `code` hoặc `name` trùng với sản phẩm đã bị xóa vào thùng rác (`isDelete = true`).
  - Chặn thêm mới nếu `code` hoặc `name` trùng với sản phẩm đang hoạt động ngoài thùng rác $\rightarrow$ HTTP `409 Conflict`.
- **Cập nhật (Update)**:
  - Chỉ cho phép sửa sản phẩm ngoài thùng rác (`isDelete = false`). Không được phép sửa sản phẩm đang nằm trong thùng rác.
  - Cho phép giữ nguyên mã/tên của chính sản phẩm đó. Nếu đổi sang mã/tên mới, hệ thống kiểm tra không trùng với các sản phẩm khác ngoài thùng rác.
- **Xóa mềm (Soft Delete)**:
  - Chuyển sản phẩm vào thùng rác: Đánh dấu `isDelete = true`, lưu thời điểm `deleteAt = LocalDateTime.now()`, và tự động tắt `active = false`.
  - Bản ghi vẫn tồn tại trong database để phục vụ khôi phục hoặc đối soát dữ liệu.
- **Khôi phục (Restore)**:
  - Trước khi khôi phục sản phẩm từ thùng rác, hệ thống thực hiện kiểm tra kiểm soát:
    - Nếu `code` hoặc `name` của sản phẩm này đã bị một sản phẩm mới ngoài thùng rác sử dụng $\rightarrow$ **Chặn khôi phục**, trả về mã lỗi `2005 PRODUCT_RESTORE_CONFLICT` (HTTP 409).
    - Nếu không bị chiếm dụng $\rightarrow$ Khôi phục trạng thái: `isDelete = false`, `deleteAt = null`.
- **Xóa vĩnh viễn (Hard Delete)**:
  - Xóa triệt để bản ghi khỏi cơ sở dữ liệu (`DELETE FROM product WHERE id = ?`) khi có tham số `hard=true`.

---

## 📖 Chi tiết các đầu API Endpoints

### 5.1. Authentication (Xác thực JWT)

#### `POST /api/auth/login`
- **Quyền hạn**: Public (Không yêu cầu Token)
- **Mô tả**: Đăng nhập hệ thống bằng username và password, nhận JWT Bearer Token chứa roles.
- **Request Body**:
```json
{
  "username": "admin",
  "password": "adminpassword"
}
```
- **Response thành công (`200 OK`)**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsInVzZXJJZCI6IjFkMmUzZjQtLi4uIiwicm9sZXMiOlsiUk9MRV9BRE1JTiJdLCJpYXQiOjE3Mjk1MDAwMDAsImV4cCI6MTcyOTU4NjQwMH0.signature",
  "username": "admin",
  "roles": [
    "ROLE_ADMIN"
  ]
}
```
- **Response thất bại (`401 Unauthorized`)**:
```json
{
  "code": 1004,
  "message": "Chưa xác thực hoặc token không hợp lệ",
  "timestamp": "2026-10-21T16:00:00"
}
```

---

### 5.2. Client APIs (Dành cho Khách hàng)

#### `GET /api/client/products`
- **Quyền hạn**: Public
- **Mô tả**: Lấy danh sách sản phẩm đang mở bán cho khách hàng (`active = true`, `isDelete = false`) kèm phân trang & sắp xếp.
- **Query Parameters**:
  - `page`: Chỉ số trang, bắt đầu từ `0` (Mặc định: `0`)
  - `size`: Số phần tử trên mỗi trang (Mặc định: `10`)
  - `sort`: Tiêu chí sắp xếp, định dạng `field,asc|desc` (Mặc định: `name,desc`)
- **Response thành công (`200 OK`)**:
```json
{
  "content": [
    {
      "name": "Bàn phím cơ DareU EK87",
      "description": "Bàn phím cơ gaming switch Blue",
      "price": 590000.000,
      "quantity": 25
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    }
  },
  "totalElements": 1,
  "totalPages": 1,
  "last": true,
  "size": 10,
  "number": 0,
  "first": true,
  "numberOfElements": 1,
  "empty": false
}
```

#### `GET /api/client/products/{id}`
- **Quyền hạn**: Yêu cầu xác thực (`CLIENT`, `STAFF`, hoặc `ADMIN`)
- **Mô tả**: Xem chi tiết 1 sản phẩm đang hoạt động dành cho khách hàng.
- **Path Variable**: `id` (UUID của sản phẩm)
- **Response thành công (`200 OK`)**:
```json
{
  "name": "Bàn phím cơ DareU EK87",
  "description": "Bàn phím cơ gaming switch Blue",
  "price": 590000.000,
  "quantity": 25
}
```
- **Response lỗi (`404 Not Found`)**: Nếu ID không tồn tại hoặc sản phẩm đã bị xóa/tắt hiển thị:
```json
{
  "code": 2001,
  "message": "Sản phẩm không tồn tại hoặc trong thùng racs",
  "timestamp": "2026-10-21T16:00:00"
}
```

---

### 5.3. Admin & Staff APIs (Quản trị & Nhân viên)

> [!IMPORTANT]
> Tất cả các endpoint dưới đây bắt buộc phải đính kèm Header:  
> `Authorization: Bearer <admin_or_staff_jwt_token>`

#### `POST /api/admin/products`
- **Quyền hạn**: `ADMIN`
- **Mô tả**: Tạo mới một sản phẩm vào hệ thống.
- **Request Body**:
```json
{
  "code": "SP001",
  "name": "Bàn phím cơ DareU EK87",
  "description": "Bàn phím cơ gaming switch Blue",
  "price": 590000,
  "quantity": 25
}
```
- **Response thành công (`200 OK`)**:
```json
{
  "id": "e8d2e821-4f12-4cf3-a602-77298642939b",
  "code": "SP001",
  "name": "Bàn phím cơ DareU EK87",
  "description": "Bàn phím cơ gaming switch Blue",
  "price": 590000.000,
  "quantity": 25,
  "active": true,
  "isDelete": false
}
```

#### `GET /api/admin/products`
- **Quyền hạn**: `STAFF` hoặc `ADMIN` (Kế thừa qua RoleHierarchy)
- **Mô tả**: Lấy danh sách toàn bộ sản phẩm quản lý ngoài thùng rác (bao gồm cả sản phẩm có `active = false`).
- **Query Parameters**: `?page=0&size=10&sort=createAt,desc`
- **Response thành công (`200 OK`)**: Trả về `Page<ProductResponseForAdmin>`.

#### `GET /api/admin/products/{id}`
- **Quyền hạn**: `STAFF` hoặc `ADMIN`
- **Mô tả**: Xem thông tin chi tiết một sản phẩm bất kỳ theo ID (hỗ trợ tra cứu cả sản phẩm trong thùng rác).
- **Path Variable**: `id` (UUID)
- **Response thành công (`200 OK`)**: Trả về `ProductResponseForAdmin`.

#### `PUT /api/admin/products/{id}`
- **Quyền hạn**: `ADMIN`
- **Mô tả**: Cập nhật thông tin sản phẩm ngoài thùng rác.
- **Path Variable**: `id` (UUID)
- **Request Body**:
```json
{
  "code": "SP001_UPDATED",
  "name": "Bàn phím cơ DareU EK87 V2",
  "description": "Bàn phím cơ DareU nâng cấp LED RGB",
  "price": 650000,
  "quantity": 30,
  "active": true
}
```
- **Response thành công (`200 OK`)**: Trả về `ProductResponseForAdmin` đã cập nhật.

#### `DELETE /api/admin/products/{id}` (Xóa mềm)
- **Quyền hạn**: `ADMIN`
- **Mô tả**: Chuyển sản phẩm vào thùng rác (đặt `isDelete = true`, `active = false`, lưu `deleteAt`).
- **Path Variable**: `id` (UUID)
- **Response thành công (`200 OK`)**:
```text
Xóa mềm sản phẩm thành công (đã chuyển vào thùng rác)
```

#### `GET /api/admin/products/bin`
- **Quyền hạn**: `ADMIN`
- **Mô tả**: Xem danh sách các sản phẩm đang nằm trong thùng rác (`isDelete = true`).
- **Query Parameters**: `?page=0&size=10&sort=deleteAt,desc`
- **Response thành công (`200 OK`)**: Trả về `Page<ProductResponseForAdmin>`.

#### `PATCH /api/admin/products/bin/{id}/restore`
- **Quyền hạn**: `ADMIN`
- **Mô tả**: Khôi phục sản phẩm từ thùng rác về trạng thái hoạt động bình thường nếu mã/tên chưa bị sản phẩm khác chiếm dụng.
- **Path Variable**: `id` (UUID)
- **Response thành công (`200 OK`)**:
```text
Khôi phục sản phẩm thành công!
```
- **Response xung đột (`409 Conflict`)**: Khi mã hoặc tên sản phẩm đã bị một sản phẩm ngoài thùng rác sử dụng:
```json
{
  "code": 2005,
  "message": "Không thể khôi phục vì mã hoặc tên sản phẩm đã được sử dụng bởi sản phẩm khác",
  "timestamp": "2026-10-21T16:00:00"
}
```

#### `DELETE /api/admin/products/{id}?hard=true` (Xóa vĩnh viễn)
- **Quyền hạn**: `ADMIN`
- **Mô tả**: Xóa triệt để bản ghi sản phẩm khỏi cơ sở dữ liệu.
- **Path Variable**: `id` (UUID)
- **Query Parameter**: `hard=true`
- **Response thành công (`200 OK`)**:
```text
Đã xóa vĩnh viễn sản phẩm khỏi hệ thống!
```

---

## ⚠️ Chuẩn hóa xử lý ngoại lệ (Error Handling & Error Codes)

Hệ thống bắt và xử lý tập trung mọi ngoại lệ qua lớp [`GlobalExceptionHandler.java`](file:///d:/intern/w3/code/study/src/main/java/vn/tnteco/study/exception/GlobalExceptionHandler.java).

### Cấu trúc JSON báo lỗi thống nhất (`ErrorResponse`):
```json
{
  "code": 1002,
  "message": "Dữ liệu đầu vào không hợp lệ",
  "errors": {
    "name": "Tên sản phẩm phải từ 2 đến 150 ký tự",
    "price": "Giá sản phẩm phải lớn hơn 0"
  },
  "timestamp": "2026-10-21T16:00:00"
}
```

### Bảng tra cứu toàn bộ danh mục mã lỗi (`ErrorCode`):

| Mã lỗi (`code`) | HTTP Status | Định danh Enum | Ý nghĩa & Tình huống kích hoạt |
| :---: | :---: | :--- | :--- |
| **1001** | `400 Bad Request` | `INVALID_KEY` | Mã key nghiệp vụ không hợp lệ. |
| **1002** | `400 Bad Request` | `INVALID_INPUT` | Vi phạm validation dữ liệu `@Valid` (kèm danh sách `errors`). |
| **1003** | `404 Not Found` | `PATH_NOT_FOUND` | Gửi request đến sai đường dẫn API hoặc URL không tồn tại. |
| **1004** | `401 Unauthorized` | `UNAUTHENTICATED` | Sai username/password hoặc không có/hết hạn Token JWT. |
| **1005** | `403 Forbidden` | `UNAUTHORIZED` | Token hợp lệ nhưng không đủ quyền hạn (bị chặn bởi `@PreAuthorize`). |
| **2001** | `404 Not Found` | `PRODUCT_NOT_FOUND` | Sản phẩm không tồn tại hoặc đã nằm trong thùng rác. |
| **2002** | `409 Conflict` | `PRODUCT_NAME_EXISTED` | Tên sản phẩm đã được sử dụng bởi một sản phẩm ngoài thùng rác. |
| **2003** | `409 Conflict` | `PRODUCT_CODE_EXISTED` | Mã sản phẩm (`code`) đã được sử dụng ngoài thùng rác. |
| **2004** | `400 Bad Request` | `PRODUCT_IN_TRASH_CANNOT_EDIT` | Chặn chỉnh sửa sản phẩm khi đang nằm trong thùng rác. |
| **2005** | `409 Conflict` | `PRODUCT_RESTORE_CONFLICT` | Chặn khôi phục vì mã/tên đã bị sản phẩm ngoài thùng rác chiếm giữ. |
| **9999** | `500 Internal Error` | `UNCATEGORIZED_EXCEPTION` | Lỗi ngoại lệ chưa được phân loại (NullPointer, lỗi kết nối DB,...). |

---

## 🚀 Hướng dẫn cài đặt & Khởi chạy (Getting Started)

### 1. Yêu cầu môi trường
- **Java**: JDK 17 trở lên (khuyên dùng Eclipse Temurin hoặc Oracle OpenJDK 17).
- **PostgreSQL**: Phiên bản 13 trở lên, đã tạo database (ví dụ: `study_db`).
- **Git**: Đã cài đặt để quản lý mã nguồn.

### 2. Thiết lập Biến môi trường (`.env`)
Dự án tích hợp sẵn thư viện `spring-dotenv`. Tạo một file có tên `.env` tại **thư mục gốc dự án** với nội dung:

```env
# 1. Cấu hình Cơ sở dữ liệu PostgreSQL
DB_URL=jdbc:postgresql://localhost:5432/study_db
DB_USERNAME=postgres
DB_PASSWORD=your_postgres_password

# 2. Cấu hình JWT Security
# Thời gian sống của Token (tính bằng mili-giây, ví dụ: 86400000 = 24 giờ)
VALID_DURATION=86400000

# Khóa bí mật ký Token (Yêu cầu tối thiểu 256 bits / 32 ký tự ngẫu nhiên bảo mật)
SIGNER_KEY=9a7e6f8d1c2b3a4e5f60718293a4b5c6d7e8f9a0b1c2d3e4f5a6b7c8d9e0f1a2
```

### 3. Lệnh khởi chạy ứng dụng
Mở terminal tại thư mục gốc của dự án:

```bash
# Trên hệ điều hành Windows (PowerShell hoặc Command Prompt)
.\mvnw.cmd spring-boot:run

# Trên hệ điều hành Linux / macOS
chmod +x mvnw
./mvnw spring-boot:run
```

Khi console hiển thị:
```text
Started StudyApplication in X.XXX seconds (process running for X.XXX)
```
Ứng dụng đã sẵn sàng phục vụ tại cổng mặc định: `http://localhost:8080`.

---

## 📖 Tài liệu tương tác Swagger UI

Dự án tích hợp SpringDoc OpenAPI giúp bạn trực quan hóa toàn bộ API và gọi thử trực tiếp trên trình duyệt:

- **Giao diện trực quan Swagger UI**:  
  👉 [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **OpenAPI Schema (JSON)**:  
  👉 [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

> [!TIP]
> Trên giao diện Swagger UI, bạn có thể nhấn nút **Authorize 🔓** ở góc phải trên cùng và dán `Bearer <token>` để kiểm thử các API yêu cầu quyền `ADMIN` hoặc `STAFF`.

---

## 🧪 Hướng dẫn kiểm thử tự động với Postman

Dự án cung cấp sẵn trọn bộ kịch bản kiểm thử tự động trong file:  
👉 [`Product_Management_API.postman_collection.json`](Product_Management_API.postman_collection.json)

### Các bước Import & Thực thi:
1. Mở **Postman** $\rightarrow$ Nhấn **Import** $\rightarrow$ Chọn file `Product_Management_API.postman_collection.json`.
2. Trong Collection Variables, cập nhật các biến cơ sở nếu cần:
   - `baseUrl`: `http://localhost:8080`
   - `adminUsername`: `admin` (hoặc tài khoản admin có trong DB của bạn)
   - `adminPassword`: `adminpassword`
3. Cấu trúc 5 bộ test cases:
   - **`00. Authentication - Xác thực (JWT)`**: Gửi API login $\rightarrow$ Test script tự động lưu Token nhận được vào biến `{{adminToken}}`. Toàn bộ các request phía sau sẽ tự động kế thừa Token này!
   - **`01. Admin - Luồng cơ bản (Happy Path)`**: Tạo sản phẩm `SP001`, `SP002`, lấy danh sách phân trang, sửa sản phẩm, xóa mềm, xem thùng rác, khôi phục và xóa cứng.
   - **`02. Client - Luồng khách hàng (Happy Path)`**: Lấy danh sách sản phẩm mở bán (đã ẩn `code`, `active`, `isDelete`), xem chi tiết theo ID.
   - **`03. Nghiệp vụ Thùng rác & Trùng lặp`**: Kiểm tra việc cho phép tạo trùng mã với thùng rác, chặn khôi phục khi bị chiếm mã ngoài thùng rác (báo lỗi 409 Conflict), chặn sửa sản phẩm trong thùng rác.
   - **`04. Các trường hợp lỗi & Validation`**: Kiểm tra dữ liệu rỗng, nhập số âm, nhập sai URL trả về `404 Not Found` (thay vì 403 Forbidden).
