# 📚 PHÂN TÍCH CHI TIẾT CODE DỰ ÁN - HƯỚNG DẪN HỌC & TRÌNH BÀY

> **Tài liệu này giúp bạn hiểu chi tiết từng file code, tính năng chúng làm gì, và cách trình bày với thầy/cô giáo**

---

## 📋 MỤC LỤC

1. [Tổng Quan Dự Án](#tổng-quan-dự-án)
2. [Công Nghệ Sử Dụng](#công-nghệ-sử-dụng)
3. [Kiến Trúc Hệ Thống](#kiến-trúc-hệ-thống)
4. [Phân Tích Chi Tiết Các Module](#phân-tích-chi-tiết-các-module)
5. [Luồng Dữ Liệu & Tính Năng](#luồng-dữ-liệu--tính-năng)
6. [Ánh Xạ Yêu Cầu](#ánh-xạ-yêu-cầu)

---

## 🎯 Tổng Quan Dự Án

### **Tên Dự Án:**

Hệ Thống Quản Lý Tư Vấn Học Thuật & Quản Lý Tài Sản Thiết Bị

### **Mục Đích:**

- Quản lý sinh viên, giảng viên, bộ phận
- Đặt lịch tư vấn học thuật giữa sinh viên và giảng viên
- Quản lý thiết bị phòng lab (mượn/trả)
- Đánh giá học thuật cho sinh viên
- Báo cáo thống kê

### **Loại Dự Án:**

- **Framework:** Spring Boot (Java)
- **Database:** MySQL
- **Frontend:** Thymeleaf (Server-side templating)
- **Kiến Trúc:** MVC (Model-View-Controller) + Layered Architecture

### **Số Lượng File:**

- **Java files:** 50+ files
- **HTML templates:** 13 templates
- **Configuration files:** 5+ files

---

## 🔧 Công Nghệ Sử Dụng

### **Tại Sao Chọn Spring Boot?**

- ✅ **Spring Boot:** Framework phát triển web nhanh, tích hợp sẵn nhiều thư viện
- ✅ **Spring Security:** Xác thực (authentication) & Phân quyền (authorization)
- ✅ **Spring Data JPA:** ORM giảm code viết SQL trực tiếp
- ✅ **Thymeleaf:** Template engine để render HTML động ở server

### **Tại Sao Chọn MySQL?**

- ✅ Có sẵn, miễn phí, ổn định
- ✅ Hỗ trợ transaction (giao dịch) cho dữ liệu quan trọng
- ✅ Thích hợp với JPA/Hibernate

### **Build Tool: Gradle**

- ✅ Quản lý dependencies (thư viện phụ thuộc)
- ✅ Biên dịch, test, và run ứng dụng

---

## 🏗️ Kiến Trúc Hệ Thống

### **Cấu Trúc Tầng (Layered Architecture)**

```
┌─────────────────────────────────────────────┐
│         PRESENTATION LAYER                  │
│  (Templates HTML + CSS + JavaScript)        │
│  Templates: login, register, dashboard...   │
└─────────────────────────────────────────────┘
                      ↓
┌─────────────────────────────────────────────┐
│         CONTROLLER LAYER                    │
│  AuthController, StudentController,         │
│  LecturerController, AdminController...     │
│  ➜ Nhận HTTP request & gọi Service          │
└─────────────────────────────────────────────┘
                      ↓
┌─────────────────────────────────────────────┐
│         SERVICE LAYER                       │
│  UserService, MentoringSessionService...    │
│  ➜ Xử lý business logic (logic nghiệp vụ)  │
└─────────────────────────────────────────────┘
                      ↓
┌─────────────────────────────────────────────┐
│         REPOSITORY LAYER                    │
│  UserRepository, SessionRepository...       │
│  ➜ Truy vấn database qua JPA                │
└─────────────────────────────────────────────┘
                      ↓
┌─────────────────────────────────────────────┐
│         DATABASE LAYER                      │
│  MySQL (tables: users, sessions, equipment) │
└─────────────────────────────────────────────┘
```

### **Tại Sao Dùng Kiến Trúc Này?**

1. **Tách biệt trách nhiệm (Separation of Concerns)** - Dễ bảo trì
2. **Tái sử dụng code** - Service có thể gọi từ nhiều Controller
3. **Dễ test** - Có thể test từng tầng riêng
4. **Mở rộng dễ** - Thêm tính năng không ảnh hưởng tầng khác

---

## 📁 Phân Tích Chi Tiết Các Module

### **1️⃣ MODULE: CẤU HÌNH & DATABASE**

#### **📄 File: `build.gradle`**

**Vị trí:** `/Projects/build.gradle`

**Chức Năng:**

- Cấu hình Spring Boot version 4.0.6
- Khai báo Java version 17
- Liệt kê thư viện cần thiết (dependencies)

**Thư Viện Chính:**

```gradle
spring-boot-starter-web          // Tạo web server
spring-boot-starter-data-jpa     // ORM (Object Relational Mapping)
spring-boot-starter-security     // Xác thực & phân quyền
spring-boot-starter-thymeleaf    // Template engine
mysql-connector-java             // Driver kết nối MySQL
lombok                           // Giảm code (auto getter/setter)
spring-boot-devtools             // Reload hot code khi phát triển
```

**Tại Sao Cần:**

- Gradle tự động download thư viện, quản lý version
- Dễ build & run project

---

#### **📄 File: `database.sql`**

**Vị trí:** `/Projects/database.sql`

**Chức Năng:** Định nghĩa schema (cấu trúc) database

**Bảng Chính:**

| Bảng                   | Mục Đích                   | Trường Chính                                      | Quan Hệ                               |
| ---------------------- | -------------------------- | ------------------------------------------------- | ------------------------------------- |
| `departments`          | Lưu bộ phận/khoa           | id, name                                          | 1 khoa có nhiều users                 |
| `users`                | Tài khoản người dùng       | id, username, password (hash), role, enabled      | Liên kết profile, department          |
| `user_profiles`        | Thông tin cá nhân          | id, user_id, full_name, email, phone              | 1-1 với users                         |
| `equipments`           | Thiết bị phòng lab         | id, name, quantity, description                   | 1 thiết bị có nhiều borrowing_details |
| `mentoring_sessions`   | Lịch tư vấn                | id, student_id, lecturer_id, session_time, status | Liên kết students & lecturers         |
| `borrowing_records`    | Phiếu mượn thiết bị        | id, mentoring_session_id, status                  | 1 phiếu mượn có nhiều chi tiết        |
| `borrowing_details`    | Chi tiết mỗi thiết bị mượn | equipment_id, quantity, record_id                 | Danh sách thiết bị trong 1 phiếu      |
| `academic_evaluations` | Đánh giá học tập           | lecturer_id, student_id, score, feedback          | Giảng viên đánh giá sinh viên         |

**Mối Quan Hệ Chính:**

```
Student ──(books)──> MentoringSession <──(confirms)── Lecturer
                          ↓
                    BorrowingRecord ──(contains)──> BorrowingDetail
                          ↓
                       Equipment

Lecturer ──(creates)──> AcademicEvaluation ──(for)──> Student
```

**Tại Sao Thiết Kế Này:**

- Normalize database (tránh trùng lặp dữ liệu)
- Foreign key đảm bảo tính toàn vẹn dữ liệu
- Dễ query & update

---

#### **📄 File: `application.properties`**

**Vị Trí:** `/Projects/src/main/resources/application.properties`

**Cấu Hình Chính:**

```properties
# Kết nối MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/project
spring.datasource.username=root
spring.datasource.password=1234

# Hibernate JPA
spring.jpa.hibernate.ddl-auto=update        # Tự động cập nhật schema
spring.jpa.show-sql=true                    # In ra SQL statements
spring.jpa.properties.hibernate.format_sql=true

# Thymeleaf
spring.thymeleaf.cache=false               # Không cache templates (dev mode)
```

**Ý Nghĩa:**

- Kết nối database MySQL trên máy local
- Hibernate tự động update schema nếu entity thay đổi
- Cache disabled để phát triển nhanh hơn

---

### **2️⃣ MODULE: ENTITY (MÔ HÌNH DỮ LIỆU)**

#### **📊 Phân Loại Entity:**

**A) ENTITY CÓ LIÊN QUAN NGƯỜI DÙNG:**

##### **`UserAccount.java`**

```java
@Entity
@Table(name = "users")
public class UserAccount {
    @Id Long id;
    String username;              // Tên đăng nhập
    String password;              // Mật khẩu (được hash bởi BCrypt)
    UserRole role;                // ADMIN / LECTURER / STUDENT
    boolean enabled;              // Tài khoản có hoạt động không?

    @ManyToOne
    Department department;        // Thuộc bộ phận nào

    @OneToOne
    UserProfile profile;          // Chi tiết cá nhân
}
```

**Chức Năng:** Lưu tài khoản & vai trò người dùng

**Tại Sao Cần `enabled`:** Quản trị viên có thể khóa tài khoản mà không xóa

---

##### **`UserProfile.java`**

```java
@Entity
@Table(name = "user_profiles")
public class UserProfile {
    @Id Long id;
    String fullName;              // Họ tên đầy đủ
    String email;                 // Email
    String phone;                 // Điện thoại

    @OneToOne
    UserAccount user;             // Liên kết với UserAccount
}
```

**Chức Năng:** Lưu thông tin chi tiết của người dùng

**Tại Sao Tách Ra Riêng:**

- UserAccount là authentication (ai đó)
- UserProfile là thông tin cá nhân (là ai chi tiết)
- Dễ mở rộng nếu sau này cần thêm thông tin (address, avatar, v.v)

---

##### **`UserRole.java`**

```java
@Enum
public enum UserRole {
    ADMIN,      // Quản trị viên - quản lý toàn hệ thống
    LECTURER,   // Giảng viên - tư vấn & đánh giá sinh viên
    STUDENT     // Sinh viên - đặt lịch & mượn thiết bị
}
```

**Chức Năng:** Định nghĩa 3 loại người dùng

---

##### **`Department.java`**

```java
@Entity
@Table(name = "departments")
public class Department {
    @Id Long id;
    String name;                  // Tên bộ phận/khoa

    @OneToMany
    List<UserAccount> users;      // Danh sách users trong bộ phận
}
```

**Chức Năng:** Lưu thông tin bộ phận

**Tại Sao Cần:**

- Hệ thống cần biết sinh viên thuộc khoa nào
- Tìm giảng viên cùng khoa để đặt lịch

---

**B) ENTITY CÓ LIÊN QUAN TƯ VẤN HỌC THUẬT:**

##### **`MentoringSession.java`**

```java
@Entity
@Table(name = "mentoring_sessions")
public class MentoringSession {
    @Id Long id;

    @ManyToOne
    UserAccount student;          // Sinh viên đặt lịch

    @ManyToOne
    UserAccount lecturer;         // Giảng viên tư vấn

    LocalDateTime sessionTime;    // Thời gian lịch (ngày + giờ)
    MentoringSessionStatus status; // PENDING / CONFIRMED / CANCELLED / COMPLETED
    String description;           // Mô tả nội dung tư vấn
}
```

**Chức Năng:** Lưu lịch tư vấn giữa sinh viên & giảng viên

**Luồng Trạng Thái:**

```
PENDING ──(Lecturer xác nhận)──> CONFIRMED ──(Hoàn thành)──> COMPLETED
  ↓ (Hủy)
CANCELLED
```

---

##### **`MentoringSessionStatus.java`**

```java
@Enum
public enum MentoringSessionStatus {
    PENDING,      // Chờ giảng viên xác nhận
    CONFIRMED,    // Giảng viên đã xác nhận
    CANCELLED,    // Đã hủy
    COMPLETED     // Đã hoàn thành
}
```

---

##### **`AcademicEvaluation.java`**

```java
@Entity
@Table(name = "academic_evaluations")
public class AcademicEvaluation {
    @Id
    @EmbeddedId
    EvaluationId id;              // Primary key: (lecturer_id, student_id)

    @ManyToOne
    UserAccount lecturer;         // Giảng viên đánh giá

    @ManyToOne
    UserAccount student;          // Sinh viên được đánh giá

    int score;                    // Điểm (0-100)
    String feedback;              // Nhận xét
    LocalDateTime evaluatedAt;    // Thời gian đánh giá
}
```

**Chức Năng:** Lưu đánh giá học thuật

**Tại Sao Dùng EmbeddedId:**

- 1 giảng viên chỉ đánh giá 1 sinh viên 1 lần
- Composite key: (lecturer_id, student_id) là duy nhất

---

**C) ENTITY CÓ LIÊN QUAN THIẾT BỊ & MƯỢN:**

##### **`Equipment.java`**

```java
@Entity
@Table(name = "equipments")
public class Equipment {
    @Id Long id;
    String name;                  // Tên thiết bị (e.g. "Laptop")
    int quantity;                 // Số lượng còn trong kho
    String description;           // Mô tả

    @OneToMany
    List<BorrowingDetail> borrowingDetails;
}
```

**Chức Năng:** Lưu danh sách thiết bị & số lượng

---

##### **`BorrowingRecord.java`**

```java
@Entity
@Table(name = "borrowing_records")
public class BorrowingRecord {
    @Id Long id;

    @ManyToOne
    MentoringSession mentoringSession;  // Lịch tư vấn liên quan

    BorrowingRecordStatus status;       // PENDING_ISSUE / ISSUED / RETURNED
    LocalDateTime borrowedAt;          // Thời gian mượn
    LocalDateTime returnedAt;          // Thời gian trả (null nếu chưa trả)

    @OneToMany
    List<BorrowingDetail> details;      // Danh sách từng thiết bị mượn
}
```

**Chức Năng:** Lưu phiếu mượn thiết bị

**Luồng Mượn Thiết Bị:**

```
User booking session
      ↓
MentoringSession (CONFIRMED)
      ↓
Issue equipment (BorrowingRecord tạo, status = PENDING_ISSUE)
      ↓
Staff phát hàng (status = ISSUED)
      ↓
User trả hàng (status = RETURNED)
```

---

##### **`BorrowingDetail.java`**

```java
@Entity
@Table(name = "borrowing_details")
public class BorrowingDetail {
    @Id
    @EmbeddedId
    BorrowingDetailId id;          // (record_id, equipment_id)

    @ManyToOne
    BorrowingRecord borrowingRecord;

    @ManyToOne
    Equipment equipment;           // Loại thiết bị

    int quantity;                  // Số lượng thiết bị mượn
}
```

**Chức Năng:** Chi tiết từng thiết bị trong một phiếu mượn

**Ví Dụ:**

```
BorrowingRecord #1:
├─ BorrowingDetail: Laptop (qty=2)
├─ BorrowingDetail: Mouse (qty=5)
└─ BorrowingDetail: Monitor (qty=2)
```

---

### **3️⃣ MODULE: SERVICE (LOGIC NGHIỆP VỤ)**

**Service là tầng xử lý logic, không trực tiếp liên quan HTTP request**

#### **`UserService.java`**

**Vị Trí:** `/src/main/java/org/example/projects/service/UserService.java`

**Chức Năng Chính:**

```java
public class UserService {
    // Đăng ký sinh viên mới
    public void registerStudent(RegistrationForm form) {
        // 1. Validate form (kiểm tra username, password)
        // 2. Hash password bằng BCrypt
        // 3. Tạo UserAccount + UserProfile
        // 4. Save vào database
    }

    // Cập nhật profile người dùng
    public void updateProfile(Long userId, ProfileForm form) {
        // 1. Tìm UserProfile theo userId
        // 2. Update fullName, email, phone
        // 3. Save vào database
    }

    // Tìm user theo username
    public UserAccount findByUsername(String username) {
        // Truy vấn database: WHERE username = ?
    }

    // Encode password
    private String encodePassword(String rawPassword) {
        // Dùng BCrypt để hash password
        // Ví dụ: "123456" → "$2a$10$..."
    }
}
```

**Tại Sao Cần Có Service:**

- **Tách biệt logic:** Controller chỉ handle HTTP, Service xử lý business
- **Tái sử dụng:** Controller & cron job đều có thể gọi service
- **Dễ test:** Test service riêng không cần mock HTTP

---

#### **`MentoringSessionService.java`**

**Vị Trí:** `/src/main/java/org/example/projects/service/MentoringSessionService.java`

**Chức Năng Chính:**

```java
public class MentoringSessionService {

    // Sinh viên đặt lịch tư vấn
    public void bookSession(BookingForm form) {
        // 1. Validate: thời gian hợp lệ?
        // 2. Kiểm tra xung đột: giảng viên có bận không?
        // 3. Tạo MentoringSession(status = PENDING)
        // 4. Save vào database
    }

    // Kiểm tra xung đột lịch
    private boolean hasConflict(Long lecturerId, LocalDateTime sessionTime) {
        // SELECT * FROM mentoring_sessions
        // WHERE lecturer_id = ? AND session_time = ?
        //   AND status != 'CANCELLED'
        //
        // Nếu tìm được row → xung đột → return true
    }

    // Giảng viên xác nhận lịch
    public void confirmSession(Long sessionId) {
        // 1. Tìm session theo id
        // 2. Kiểm tra status = PENDING
        // 3. Update status → CONFIRMED
        // 4. Save
    }

    // Hủy lịch
    public void cancelSession(Long sessionId, String reason) {
        // 1. Tìm session
        // 2. Update status → CANCELLED
        // 3. Lưu lý do (tùy chọn)
    }

    // Lấy danh sách lịch chưa xác nhận của giảng viên
    public List<MentoringSession> getPendingSessions(Long lecturerId) {
        // SELECT * FROM mentoring_sessions
        // WHERE lecturer_id = ? AND status = 'PENDING'
    }
}
```

**Business Logic Quan Trọng:**

- ✅ Kiểm tra xung đột thời gian
- ✅ Validate dữ liệu trước khi lưu
- ✅ Quản lý trạng thái (state machine)

---

#### **`EquipmentService.java`**

**Vị Trí:** `/src/main/java/org/example/projects/service/EquipmentService.java`

**Chức Năng Chính:**

```java
public class EquipmentService {

    // Admin thêm thiết bị mới
    public void createEquipment(EquipmentForm form) {
        // 1. Tạo Equipment entity
        // 2. Set: name, quantity, description
        // 3. Save vào database
    }

    // Cập nhật thông tin thiết bị
    public void updateEquipment(Long id, EquipmentForm form) {
        // 1. Tìm equipment theo id
        // 2. Update fields
        // 3. Save
    }

    // Xóa thiết bị
    public void deleteEquipment(Long id) {
        // DELETE FROM equipments WHERE id = ?
    }

    // Lấy danh sách tất cả thiết bị
    public List<Equipment> getAllEquipments() {
        // SELECT * FROM equipments
    }

    // Tìm kiếm thiết bị
    public List<Equipment> searchEquipments(String keyword) {
        // SELECT * FROM equipments WHERE name LIKE ?
    }
}
```

---

#### **`InventoryService.java`**

**Vị Trí:** `/src/main/java/org/example/projects/service/InventoryService.java`

**Chức Năng Chính:** Quản lý mượn/trả thiết bị

```java
public class InventoryService {

    // Phát hàng (sau khi xác nhận lịch tư vấn)
    public void issueBorrowingRecord(Long recordId) {
        // 1. Tìm BorrowingRecord
        // 2. FOR EACH BorrowingDetail:
        //    - Lấy Equipment
        //    - Giảm quantity đi
        //    - Save Equipment
        // 3. Update BorrowingRecord status → ISSUED
        // 4. Kiểm tra hết stock: quantity < 0 → Lỗi!
    }

    // Nhận lại hàng
    public void returnBorrowingRecord(Long recordId) {
        // 1. Tìm BorrowingRecord
        // 2. FOR EACH BorrowingDetail:
        //    - Lấy Equipment
        //    - Tăng quantity lên
        //    - Save Equipment
        // 3. Update BorrowingRecord status → RETURNED
        // 4. Set returnedAt = now()
    }

    // Tạo phiếu mượn từ session xác nhận
    public void createBorrowingRecordFromSession(Long sessionId) {
        // 1. Tìm MentoringSession
        // 2. Tạo BorrowingRecord mới (status = PENDING_ISSUE)
        // 3. Save
    }
}
```

**Tại Sao Cần TRANSACTION:**

- ❌ Nếu phát hàng xong, server crash trước khi update database
- ❌ → Quantity giảm nhưng status không update → sai dữ liệu!
- ✅ **Dùng @Transactional:** Spring đảm bảo tất cả thay đổi được commit hoặc rollback cùng lúc

```java
@Transactional  // ← Quan trọng!
public void issueBorrowingRecord(Long recordId) {
    // Nếu lỗi ở giữa → Rollback tất cả thay đổi
}
```

---

#### **`EvaluationService.java`**

```java
public class EvaluationService {

    // Giảng viên đánh giá sinh viên
    public void evaluateStudent(Long lecturerId, Long studentId,
                                int score, String feedback) {
        // 1. Validate score (0-100?)
        // 2. Tạo AcademicEvaluation
        // 3. Save vào database
    }

    // Lấy đánh giá của một sinh viên
    public List<AcademicEvaluation> getStudentEvaluations(Long studentId) {
        // SELECT * FROM academic_evaluations WHERE student_id = ?
    }
}
```

---

#### **`StatisticsService.java`**

```java
public class StatisticsService {

    // Thống kê cho Admin Dashboard
    public DashboardStats getAdminStats() {
        // SELECT COUNT(*) FROM users
        // SELECT COUNT(*) FROM mentoring_sessions
        // SELECT SUM(quantity) FROM borrowing_records
        // → Trả về object với: totalUsers, totalSessions, totalBorrowings
    }

    // Thống kê cho Lecturer
    public LecturerStats getLecturerStats(Long lecturerId) {
        // SELECT COUNT(*) WHERE lecturer_id = ? AND status = 'COMPLETED'
        // → Số lịch hoàn thành
    }
}
```

---

### **4️⃣ MODULE: CONTROLLER (XỬ LÝ HTTP REQUEST)**

**Controller nhận request từ frontend, gọi Service, trả về view**

#### **`AuthController.java`**

**Vị Trí:** `/src/main/java/org/example/projects/controller/AuthController.java`

**Các Endpoint:**

```java
@Controller
@RequestMapping
public class AuthController {

    // GET /login → Hiển thị form đăng nhập
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        // Tạo LoginForm object trống
        model.addAttribute("loginForm", new LoginForm());
        // Trả về login.html
        return "login";
    }

    // POST /login → Xử lý đăng nhập
    @PostMapping("/login")
    public String login(@ModelAttribute LoginForm form,
                        HttpSession session,
                        RedirectAttributes redirectAttrs) {
        try {
            // 1. Validate form
            // 2. Spring Security check username/password
            // 3. Nếu đúng → Create session
            // 4. Redirect đến dashboard dựa trên role

            if (user.getRole() == ADMIN)
                return "redirect:/admin/home";
            else if (user.getRole() == LECTURER)
                return "redirect:/lecturer/home";
            else
                return "redirect:/student/home";

        } catch (Exception e) {
            // Lỗi → Quay lại login form
            redirectAttrs.addFlashAttribute("error", "Sai tài khoản hoặc mật khẩu");
            return "redirect:/login";
        }
    }

    // GET /register → Hiển thị form đăng ký
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registrationForm", new RegistrationForm());
        model.addAttribute("departments", departmentService.getAll());
        return "register";
    }

    // POST /register → Xử lý đăng ký
    @PostMapping("/register")
    public String register(@ModelAttribute RegistrationForm form,
                          RedirectAttributes redirectAttrs) {
        try {
            // 1. Validate form (password match?, username unique?)
            // 2. Gọi UserService.registerStudent()
            // 3. Tạo UserAccount với role = STUDENT
            redirectAttrs.addFlashAttribute("success", "Đăng ký thành công! Đăng nhập ngay");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }

    // POST /logout → Đăng xuất
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        // Spring Security xóa session
        // Redirect về login
        return "redirect:/login";
    }
}
```

**HTTP Method Trong RESTful:**

- **GET** - Lấy dữ liệu, hiển thị form
- **POST** - Gửi form, tạo mới dữ liệu
- **PUT/PATCH** - Cập nhật dữ liệu (không dùng trong web form)
- **DELETE** - Xóa dữ liệu (không dùng trong web form)

---

#### **`StudentController.java`**

**Vị Trí:** `/src/main/java/org/example/projects/controller/StudentController.java`

```java
@Controller
@RequestMapping("/student")
@PreAuthorize("hasRole('STUDENT')")  // ← Chỉ STUDENT có quyền truy cập
public class StudentController {

    // GET /student/home → Dashboard sinh viên
    @GetMapping("/home")
    public String home(Model model, @AuthenticationPrincipal UserAccount user) {
        // Lấy thông tin cá nhân sinh viên
        model.addAttribute("user", user);
        model.addAttribute("profile", user.getProfile());
        // Lấy số lịch sắp tới
        model.addAttribute("upcomingSessions",
            sessionService.getUpcomingSessions(user.getId()));
        return "student/home";
    }

    // GET /student/booking → Form đặt lịch
    @GetMapping("/booking")
    public String showBookingForm(Model model) {
        // Lấy danh sách khoa
        model.addAttribute("departments", departmentService.getAll());
        // Lấy danh sách giảng viên (trống đầu tiên)
        model.addAttribute("lecturers", new ArrayList<>());
        model.addAttribute("bookingForm", new BookingForm());
        return "student/booking-form";
    }

    // POST /student/booking → Xử lý đặt lịch
    @PostMapping("/booking")
    public String bookSession(@ModelAttribute BookingForm form,
                             @AuthenticationPrincipal UserAccount student,
                             RedirectAttributes redirectAttrs) {
        try {
            // 1. Validate form
            // 2. Gọi MentoringSessionService.bookSession()
            // 3. Tạo session mới (status = PENDING)
            redirectAttrs.addFlashAttribute("success",
                "Đặt lịch thành công! Chờ giảng viên xác nhận");
            return "redirect:/student/history";
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
            return "redirect:/student/booking";
        }
    }

    // GET /student/history → Lịch sử lịch tư vấn
    @GetMapping("/history")
    public String getHistory(Model model,
                            @AuthenticationPrincipal UserAccount student,
                            @RequestParam(defaultValue = "0") int page) {
        // Lấy trang (pagination)
        Page<MentoringSession> sessions =
            sessionService.getStudentSessions(student.getId(), PageRequest.of(page, 10));
        model.addAttribute("sessions", sessions);
        model.addAttribute("currentPage", page);
        return "student/history";
    }
}
```

**@PreAuthorize:** Spring Security decorator

- `@PreAuthorize("hasRole('STUDENT')")` → Chỉ cho STUDENT truy cập
- Nếu không có quyền → 403 Forbidden

---

#### **`LecturerController.java`**

```java
@Controller
@RequestMapping("/lecturer")
@PreAuthorize("hasRole('LECTURER')")
public class LecturerController {

    // GET /lecturer/home → Dashboard giảng viên
    @GetMapping("/home")
    public String home(Model model, @AuthenticationPrincipal UserAccount lecturer) {
        int pendingCount = sessionService.getPendingCount(lecturer.getId());
        model.addAttribute("pendingCount", pendingCount);
        return "lecturer/home";
    }

    // GET /lecturer/sessions → Danh sách lịch chưa xác nhận
    @GetMapping("/sessions")
    public String getPendingSessions(Model model,
                                    @AuthenticationPrincipal UserAccount lecturer) {
        List<MentoringSession> sessions =
            sessionService.getPendingSessions(lecturer.getId());
        model.addAttribute("sessions", sessions);
        return "lecturer/sessions";
    }

    // POST /lecturer/confirm/{id} → Xác nhận lịch
    @PostMapping("/confirm/{id}")
    public String confirmSession(@PathVariable Long id,
                                RedirectAttributes redirectAttrs) {
        try {
            sessionService.confirmSession(id);
            redirectAttrs.addFlashAttribute("success", "Xác nhận lịch thành công");
            return "redirect:/lecturer/sessions";
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
            return "redirect:/lecturer/sessions";
        }
    }

    // GET /lecturer/evaluate → Form đánh giá
    @GetMapping("/evaluate")
    public String showEvaluateForm(Model model,
                                  @AuthenticationPrincipal UserAccount lecturer) {
        // Lấy danh sách sinh viên đã xác nhận lịch
        model.addAttribute("students",
            sessionService.getConfirmedStudents(lecturer.getId()));
        model.addAttribute("evaluationForm", new EvaluationForm());
        return "lecturer/evaluate-form";
    }

    // POST /lecturer/evaluate → Lưu đánh giá
    @PostMapping("/evaluate")
    public String submitEvaluation(@ModelAttribute EvaluationForm form,
                                  @AuthenticationPrincipal UserAccount lecturer) {
        evaluationService.evaluateStudent(
            lecturer.getId(),
            form.getStudentId(),
            form.getScore(),
            form.getFeedback()
        );
        return "redirect:/lecturer/home";
    }
}
```

---

#### **`AdminController.java`**

```java
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    // GET /admin/home → Admin Dashboard
    @GetMapping("/home")
    public String home(Model model) {
        // Thống kê hệ thống
        model.addAttribute("stats", statisticsService.getAdminStats());
        model.addAttribute("recentUsers", userService.getRecentUsers(10));
        model.addAttribute("recentSessions",
            sessionService.getRecentSessions(10));
        return "admin/home";
    }

    // GET /admin/equipments → Danh sách thiết bị
    @GetMapping("/equipments")
    public String listEquipments(Model model) {
        model.addAttribute("equipments", equipmentService.getAllEquipments());
        return "admin/equipment-list";
    }

    // POST /admin/equipments → Thêm thiết bị
    @PostMapping("/equipments")
    public String createEquipment(@ModelAttribute EquipmentForm form) {
        equipmentService.createEquipment(form);
        return "redirect:/admin/equipments";
    }

    // GET /admin/equipments/{id}/edit → Form sửa
    @GetMapping("/equipments/{id}/edit")
    public String editEquipment(@PathVariable Long id, Model model) {
        model.addAttribute("equipment", equipmentService.getById(id));
        return "admin/equipment-form";
    }

    // POST /admin/equipments/{id} → Lưu sửa
    @PostMapping("/equipments/{id}")
    public String updateEquipment(@PathVariable Long id,
                                 @ModelAttribute EquipmentForm form) {
        equipmentService.updateEquipment(id, form);
        return "redirect:/admin/equipments";
    }

    // DELETE /admin/equipments/{id} → Xóa thiết bị
    @DeleteMapping("/equipments/{id}")
    public String deleteEquipment(@PathVariable Long id) {
        equipmentService.deleteEquipment(id);
        return "redirect:/admin/equipments";
    }
}
```

---

### **5️⃣ MODULE: SECURITY (XÁC THỰC & PHÂN QUYỀN)**

#### **`SecurityConfig.java`**

**Vị Trí:** `/src/main/java/org/example/projects/security/SecurityConfig.java`

**Chức Năng:** Cấu hình Spring Security

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // ========== XÁC THỰC ==========
            .authenticationProvider(daoAuthenticationProvider())
            .formLogin()
                .loginPage("/login")              // Trang login
                .defaultSuccessUrl("/dashboard")  // Sau đăng nhập thành công
                .permitAll()
            .and()
            .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
            .and()

            // ========== PHÂN QUYỀN ==========
            .authorizeRequests()
                // Public URLs (không cần login)
                .antMatchers("/login", "/register").permitAll()
                .antMatchers("/css/**", "/js/**", "/images/**").permitAll()

                // Admin only
                .antMatchers("/admin/**").hasRole("ADMIN")

                // Lecturer only
                .antMatchers("/lecturer/**").hasRole("LECTURER")

                // Student only
                .antMatchers("/student/**").hasRole("STUDENT")

                // Dashboard for any authenticated user
                .antMatchers("/dashboard", "/profile/**")
                    .authenticated()

                // Tất cả request khác cần authenticate
                .anyRequest().authenticated()
            .and()

            // ========== Session Management ==========
            .sessionManagement()
                .sessionFixationProtection(SessionFixationProtection.MIGRATEAFTER_LOGIN)
                .maximumSessions(1)  // 1 session mỗi user
            .and()

            // ========== CSRF Protection ==========
            .csrf()
                .disable();  // Tùy chỉnh, nên enable trong production

        return http.build();
    }

    // ========== PASSWORD ENCODING ==========
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
        // BCrypt: algorithm lưu password an toàn
        // Không thể reverse → chỉ có thể verify
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
}
```

**Tại Sao Dùng BCrypt:**

```
Raw Password:  "123456"
BCrypt Hash:   "$2a$10$N9qo8uLOickgx2ZMRZoMye..."
                ↑     ↑
            Salt + Hash
```

- ✅ Một chiều: không thể decode hash → password
- ✅ Có salt: tránh rainbow table attack
- ✅ Slow: tăng computational cost cho attacker

**Phân Quyền:**

```
User request /admin/users
    ↓
Spring Security kiểm tra:
  - Có session? (authenticated?)
  - Role = ADMIN?
    ↓
  - Yes → Cho phép
  - No → 403 Forbidden
```

---

### **6️⃣ MODULE: TEMPLATE (HTML FRONTEND)**

#### **Cấu Trúc Template:**

```
templates/
├── login.html              ← Form đăng nhập
├── register.html           ← Form đăng ký
├── layout.html             ← Base layout (header, footer)
├── fragments.html          ← Reusable components
├── dashboard.html          ← Role-based redirect
├── profile.html            ← Profile view/edit
├── error.html              ← Error page
│
├── admin/
│   ├── home.html           ← Admin dashboard
│   ├── equipment-list.html ← Danh sách thiết bị
│   ├── equipment-form.html ← Form thêm/sửa thiết bị
│   └── statistics.html     ← Thống kê
│
├── lecturer/
│   ├── home.html           ← Lecturer dashboard
│   ├── sessions.html       ← Lịch chờ xác nhận
│   └── evaluate-form.html  ← Form đánh giá
│
└── student/
    ├── home.html           ← Student dashboard
    ├── booking-form.html   ← Form đặt lịch
    └── history.html        ← Lịch sử lịch tư vấn
```

#### **Thymeleaf Syntax (Server-side rendering):**

```html
<!-- Hiển thị biến từ Model -->
<p>Xin chào, <span th:text="${user.profile.fullName}"></span></p>

<!-- Điều kiện -->
<div th:if="${session.status == 'PENDING'}">Lịch đang chờ xác nhận</div>

<!-- Vòng lặp -->
<tr th:each="session : ${sessions}">
  <td th:text="${session.sessionTime}"></td>
  <td th:text="${session.lecturer.profile.fullName}"></td>
</tr>

<!-- Form submit -->
<form th:action="@{/student/booking}" method="POST" th:object="${bookingForm}">
  <input type="text" th:field="*{description}" />
  <button type="submit">Đặt lịch</button>
</form>

<!-- Link -->
<a th:href="@{/student/history}">Lịch sử</a>
```

---

### **7️⃣ MODULE: DTO (DATA TRANSFER OBJECT)**

**DTO = Đối tượng để transfer data giữa tầng (form → service)**

#### **`LoginForm.java`**

```java
@Data
@NoArgsConstructor
public class LoginForm {
    @NotBlank(message = "Username không được để trống")
    private String username;

    @NotBlank(message = "Password không được để trống")
    private String password;
}
```

**Sử Dụng:** `/login` endpoint

---

#### **`RegistrationForm.java`**

```java
@Data
@NoArgsConstructor
public class RegistrationForm {
    @NotBlank(message = "Username không được để trống")
    @Size(min = 5, message = "Username ít nhất 5 ký tự")
    private String username;

    @NotBlank
    @Size(min = 6, message = "Password ít nhất 6 ký tự")
    private String password;

    @NotBlank
    private String confirmPassword;

    @NotBlank
    private String fullName;

    @Email(message = "Email không hợp lệ")
    private String email;

    private Long departmentId;

    // Validate password match
    @AssertTrue(message = "Password không khớp")
    public boolean isPasswordsMatching() {
        return password != null && password.equals(confirmPassword);
    }
}
```

---

#### **`BookingForm.java`**

```java
@Data
@NoArgsConstructor
public class BookingForm {
    @NotNull(message = "Chọn giảng viên")
    private Long lecturerId;

    @NotNull(message = "Chọn thời gian")
    @FutureOrPresent(message = "Thời gian phải trong tương lai")
    private LocalDateTime sessionTime;

    @NotBlank(message = "Mô tả nội dung không được để trống")
    @Size(max = 500, message = "Mô tả không quá 500 ký tự")
    private String description;
}
```

---

### **8️⃣ MODULE: REPOSITORY (DATABASE QUERIES)**

**Repository = Interface để query database (không viết SQL)**

#### **`UserRepository.java`**

```java
@Repository
public interface UserRepository extends JpaRepository<UserAccount, Long> {

    // Tìm user theo username (custom query)
    Optional<UserAccount> findByUsername(String username);

    // Tìm user theo email (JOIN profile)
    @Query("SELECT u FROM UserAccount u " +
           "JOIN u.profile p WHERE p.email = :email")
    Optional<UserAccount> findByProfileEmail(@Param("email") String email);

    // Tìm lecturer theo department
    @Query("SELECT u FROM UserAccount u WHERE u.role = 'LECTURER' " +
           "AND u.department.id = :departmentId")
    List<UserAccount> findLecturersByDepartment(Long departmentId);
}
```

**Spring Data JPA tự động tạo query:**

```java
findByUsername("john")
// ↓
// SELECT * FROM users WHERE username = 'john'

findByRole(UserRole.ADMIN)
// ↓
// SELECT * FROM users WHERE role = 'ADMIN'
```

---

#### **`MentoringSessionRepository.java`**

```java
@Repository
public interface MentoringSessionRepository
    extends JpaRepository<MentoringSession, Long> {

    // Tìm lịch của sinh viên
    List<MentoringSession> findByStudentId(Long studentId);

    // Tìm lịch chưa xác nhận của giảng viên
    List<MentoringSession> findByLecturerIdAndStatus(
        Long lecturerId,
        MentoringSessionStatus status);

    // Kiểm tra xung đột
    @Query("SELECT COUNT(*) FROM MentoringSession s WHERE " +
           "s.lecturer.id = :lecturerId AND " +
           "s.sessionTime = :sessionTime AND " +
           "s.status != 'CANCELLED'")
    int countConflicts(@Param("lecturerId") Long lecturerId,
                       @Param("sessionTime") LocalDateTime sessionTime);
}
```

---

## 🔄 Luồng Dữ Liệu & Tính Năng

### **LUỒNG 1: ĐẶT LỊCH TƯ VẤN**

```
┌─────────────────────────────────────────────────────────────┐
│ 1. SINH VIÊN TRUY CẬP /student/booking                      │
├─────────────────────────────────────────────────────────────┤
│ StudentController.showBookingForm()                         │
│ ├─ Lấy danh sách departments                               │
│ ├─ Pass vào Model                                          │
│ └─ Render booking-form.html                                │
├─────────────────────────────────────────────────────────────┤
│ 2. FRONTEND LOAD, SINH VIÊN CHỌN KHOA                       │
├─────────────────────────────────────────────────────────────┤
│ AJAX: GET /student/booking/lecturers?dept=1                │
│ ├─ LecturerController.getLecturersByDepartment()           │
│ ├─ LookupService.getLecturers(1)                           │
│ └─ Trả về JSON danh sách giảng viên                        │
├─────────────────────────────────────────────────────────────┤
│ 3. SINH VIÊN ĐIỀN FORM & SUBMIT                             │
├─────────────────────────────────────────────────────────────┤
│ POST /student/booking                                       │
│ ├─ BookingForm validation                                  │
│ ├─ StudentController.bookSession()                         │
│ └─ MentoringSessionService.bookSession()                   │
│    ├─ Kiểm tra xung đột:                                   │
│    │  "SELECT * FROM mentoring_sessions WHERE             │
│    │   lecturer_id = 1 AND session_time = '2025-01-15'    │
│    │   AND status != 'CANCELLED'"                          │
│    │                                                        │
│    ├─ Nếu xung đột → Throw exception                       │
│    └─ Nếu không → Tạo MentoringSession(status=PENDING)    │
│       └─ Lưu vào database                                  │
├─────────────────────────────────────────────────────────────┤
│ 4. REDIRECT ĐẾN LỊCH SỬ                                     │
├─────────────────────────────────────────────────────────────┤
│ GET /student/history                                        │
│ ├─ StudentController.getHistory()                          │
│ ├─ MentoringSessionService.getStudentSessions()            │
│ │  "SELECT * FROM mentoring_sessions WHERE student_id = 1"│
│ └─ Render history.html với danh sách (bao gồm lịch vừa mới)│
└─────────────────────────────────────────────────────────────┘

LƯU Ý:
- Status = PENDING (chờ giảng viên xác nhận)
- Xung đột: Nếu giảng viên đã có lịch cùng giờ → Không cho đặt
```

---

### **LUỒNG 2: GIẢNG VIÊN XÁC NHẬN LỊCH**

```
┌─────────────────────────────────────────────────────────────┐
│ 1. GIẢNG VIÊN VÀO /lecturer/sessions                        │
├─────────────────────────────────────────────────────────────┤
│ LecturerController.getPendingSessions()                     │
│ ├─ MentoringSessionService.getPendingSessions()            │
│ │  "SELECT * FROM mentoring_sessions WHERE                │
│ │   lecturer_id = 2 AND status = 'PENDING'"               │
│ ├─ Render sessions.html với danh sách                     │
│ └─ Hiển thị nút [Xác nhận] & [Từ chối]                    │
├─────────────────────────────────────────────────────────────┤
│ 2. GIẢNG VIÊN CLICK [Xác nhận]                              │
├─────────────────────────────────────────────────────────────┤
│ POST /lecturer/confirm/123                                  │
│ ├─ LecturerController.confirmSession(123)                 │
│ ├─ MentoringSessionService.confirmSession(123)            │
│ │  1. Tìm session với id = 123                            │
│ │  2. Kiểm tra: status = PENDING?                         │
│ │  3. Update: status = CONFIRMED                          │
│ │  4. Commit database                                     │
│ └─ TRIGGER: Tạo BorrowingRecord                           │
│    ├─ InventoryService.createBorrowingRecord()            │
│    └─ Tạo record mới (status=PENDING_ISSUE)               │
├─────────────────────────────────────────────────────────────┤
│ 3. REDIRECT VỀ DANH SÁCH                                    │
├─────────────────────────────────────────────────────────────┤
│ GET /lecturer/sessions                                      │
│ ├─ Danh sách PENDING giảng viên cập nhật (ít hơn 1 cái)   │
│ └─ Flash message: "Xác nhận lịch thành công"              │
└─────────────────────────────────────────────────────────────┘

KẾT QUẢ:
- MentoringSession.status: PENDING → CONFIRMED
- BorrowingRecord tạo mới để quản lý mượn thiết bị
```

---

### **LUỒNG 3: PHÁT THIẾT BỊ**

```
┌─────────────────────────────────────────────────────────────┐
│ 1. ADMIN/STAFF TRUY CẬP /inventory/pending                  │
├─────────────────────────────────────────────────────────────┤
│ InventoryController.listPendingBorrowings()                │
│ ├─ Lấy BorrowingRecord với status = PENDING_ISSUE          │
│ └─ Hiển thị danh sách (bao gồm chi tiết từng thiết bị)     │
├─────────────────────────────────────────────────────────────┤
│ 2. STAFF CLICK [PHÁT HÀNG]                                  │
├─────────────────────────────────────────────────────────────┤
│ POST /inventory/issue/456                                   │
│ ├─ InventoryController.issueBorrowing(456)                │
│ ├─ @Transactional                                          │
│ └─ InventoryService.issueBorrowingRecord(456)             │
│    1. TÌM: BorrowingRecord(456)                            │
│    2. FOR EACH BorrowingDetail trong 456:                  │
│       ├─ Lấy Equipment                                     │
│       ├─ equipment.quantity -= borrowingDetail.quantity    │
│       └─ Save equipment                                    │
│           Example:                                         │
│           - Laptop: 5 cái → 3 cái (mượn 2)                 │
│           - Mouse: 20 cái → 15 cái (mượn 5)                │
│    3. UPDATE: BorrowingRecord.status = ISSUED              │
│    4. Nếu lỗi (quantity < 0) → ROLLBACK tất cả            │
├─────────────────────────────────────────────────────────────┤
│ 3. REDIRECT & SUCCESS MESSAGE                               │
├─────────────────────────────────────────────────────────────┤
│ GET /inventory/pending                                      │
│ ├─ Danh sách cập nhật (ít hơn 1 cái)                       │
│ └─ Flash: "Phát hàng thành công"                           │
└─────────────────────────────────────────────────────────────┘

DATABASE CHANGES:
- BorrowingRecord(456): PENDING_ISSUE → ISSUED
- Equipment(Laptop): quantity = 5 → 3
- Equipment(Mouse): quantity = 20 → 15

TRANSACTION:
- Nếu bất cứ bước nào fail → ROLLBACK tất cả
- Không bao giờ: quantity giảm nhưng status không update
```

---

### **LUỒNG 4: GIẢNG VIÊN ĐÁNH GIÁ SINH VIÊN**

```
┌─────────────────────────────────────────────────────────────┐
│ 1. GIẢNG VIÊN TRUY CẬP /lecturer/evaluate                   │
├─────────────────────────────────────────────────────────────┤
│ LecturerController.showEvaluateForm()                       │
│ ├─ Lấy sinh viên đã hoàn thành lịch tư vấn                 │
│ │  (status = COMPLETED)                                    │
│ └─ Hiển thị form + danh sách sinh viên                     │
├─────────────────────────────────────────────────────────────┤
│ 2. GIẢNG VIÊN CHỌN SINH VIÊN & ĐIỀN SCORE/FEEDBACK          │
├─────────────────────────────────────────────────────────────┤
│ POST /lecturer/evaluate                                     │
│ ├─ EvaluationForm validation                               │
│ │  ├─ Score: 0-100?                                       │
│ │  └─ Feedback: không trống?                              │
│ ├─ LecturerController.submitEvaluation()                  │
│ └─ EvaluationService.evaluateStudent()                    │
│    1. TẠO AcademicEvaluation mới                           │
│       ├─ lecturer_id = 2                                   │
│       ├─ student_id = 5                                    │
│       ├─ score = 85                                        │
│       ├─ feedback = "Rất tích cực trong học tập"          │
│       └─ evaluatedAt = NOW()                               │
│    2. SAVE vào database                                    │
├─────────────────────────────────────────────────────────────┤
│ 3. REDIRECT VỀ HOME                                         │
│ ├─ Flash: "Đánh giá thành công"                            │
│ └─ Sinh viên có thể xem đánh giá trong profile              │
└─────────────────────────────────────────────────────────────┘

DATABASE:
- INSERT INTO academic_evaluations
  VALUES (lecturer=2, student=5, score=85, ...)
```

---

## 📋 Ánh Xạ Yêu Cầu

**Dự án này thỏa mãn các yêu cầu chính (CORE) như sau:**

| CORE    | Yêu Cầu                                                                              | Tính Năng                        | File Liên Quan                                                                |
| ------- | ------------------------------------------------------------------------------------ | -------------------------------- | ----------------------------------------------------------------------------- |
| CORE-01 | Hash password trong database                                                         | BCrypt encoding                  | SecurityConfig.java, UserService.java                                         |
| CORE-02 | Kiểm soát truy cập, không cho phép vào trang Giảng viên nếu không phải role LECTURER | @PreAuthorize, SecurityConfig    | SecurityConfig.java, LecturerController.java                                  |
| CORE-03 | Quản lý hồ sơ cá nhân (Profile)                                                      | User profile view/edit           | UserProfile.java, ProfileController.java                                      |
| CORE-04 | Admin quản lý danh mục (Thêm/Xem/Sửa/Xóa) thiết bị                                   | CRUD equipment                   | EquipmentAdminController.java, EquipmentService.java                          |
| CORE-05 | Lịch tư vấn: SV chọn Khoa → Giảng viên → Đặt lịch → Xác nhận                         | Booking system                   | MentoringSessionService.java, StudentController.java, LecturerController.java |
| CORE-06 | Transaction: Phát thiết bị, trừ kho                                                  | @Transactional, InventoryService | InventoryService.java, BorrowingRecord.java                                   |
| CORE-07 | Truy cập lịch sử của Giảng viên                                                      | Session history                  | SessionHistoryDto.java, LecturerController.java                               |
| CORE-08 | Giảng viên đánh giá sinh viên                                                        | Academic evaluation              | AcademicEvaluation.java, EvaluationService.java                               |
| CORE-09 | Quản lý lịch tư vấn, xác nhận                                                        | Mentoring session workflow       | MentoringSession.java, MentoringSessionService.java                           |
| CORE-10 | Các tính năng nâng cao                                                               | Statistics, pagination, search   | StatisticsService.java                                                        |

---

## 📚 TỰ HỌC & TRÌNH BÀY VỚI THẦY/CÔ

### **Khi Thầy/Cô Hỏi "Code Của Em Làm Gì?"**

**Cách Trả Lời:**

1. **Tổng Quan:** "Đây là hệ thống quản lý tư vấn học thuật, em tạo bằng Spring Boot..."
2. **Kiến Trúc:** "Em sử dụng MVC architecture với 5 tầng: Presentation → Controller → Service → Repository → Database..."
3. **Module Chính:** "Dự án có 4 module chính: User Management, Mentoring Sessions, Equipment Borrowing, và Academic Evaluation..."
4. **Một Tính Năng:** "Ví dụ về tính năng đặt lịch tư vấn: Sinh viên chọn giảng viên, hệ thống kiểm tra xung đột thời gian, sau đó giảng viên xác nhận. Nếu xác nhận, tự động tạo phiếu mượn thiết bị..."

### **Khi Thầy/Cô Hỏi "Tại Sao Dùng Công Nghệ X?"**

- **Spring Boot:** "Em chọn Spring Boot vì nó tích hợp sẵn Spring Security để xác thực/phân quyền, JPA để truy vấn database mà không phải viết SQL, và Thymeleaf để render HTML động..."
- **MySQL:** "MySQL phù hợp cho project này vì nó hỗ trợ transaction, foreign key, và dễ sử dụng..."
- **BCrypt:** "Em dùng BCrypt để hash password vì nó một chiều (không thể reverse), có salt chống rainbow table attack, và khi attacker brute force sẽ chậm..."
- **@Transactional:** "Khi phát thiết bị, em cần đảm bảo tất cả thay đổi (trừ kho, update status) xảy ra cùng lúc. Nếu bất cứ bước nào fail, tất cả được rollback để tránh sai dữ liệu..."

---

**Lưu Ý Quan Trọng:**

- ✅ Hiểu rõ **tại sao** không chỉ **cái gì**
- ✅ Có thể **giải thích chi tiết** từng tính năng
- ✅ Biết **cách data flow** qua các tầng
- ✅ Chuẩn bị **ví dụ cụ thể** khi trình bày
