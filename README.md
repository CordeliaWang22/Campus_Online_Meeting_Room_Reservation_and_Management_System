# Online Meeting Room Booking System / 在线会议室预约系统

A full-stack web application for campus meeting-room booking: student registration with email verification, room search and booking, personal booking management, and an admin console for rooms, bookings, and user accounts.

面向校园会议室资源的全栈 Web 系统：学生注册与邮箱验证、会议室检索与预约、个人预约管理，以及管理员对房间、预约和账号的后台维护。

> Course / portfolio project (XJTLU). Built with **Spring Boot + Thymeleaf + MySQL**.  
> 西交利物浦大学课程 / 作品集项目，技术栈为 **Spring Boot + Thymeleaf + MySQL**。

---

## Features / 功能概览

| User (学生用户) | Admin (管理员) |
| --- | --- |
| Register with university ID, email, avatar, and password | Maintain the meeting-room catalog (image, ID, capacity, IT facilities, location) |
| Email verification code to activate the account | Search / filter rooms; add, update, delete rooms |
| Login / logout, session-based access | View all bookings; search by booking ID or user; filter by status |
| Search rooms by ID; filter by date/time, capacity, IT facilities | Cancel / remove bookings (reject a booking) |
| Book a 30-minute slot, see occupied slots on a timetable | Lock / unlock accounts for excessive booking |
| View, update, or cancel bookings before the meeting starts | Reset all users’ booking counters |
| Edit profile, upload avatar, change password | |

---

## Tech stack / 技术栈

**Backend**

- Java 17, Spring Boot 3.4 (Spring MVC)
- Spring Data JPA + Hibernate (`ddl-auto=update`)
- MySQL 8
- Redis (temporary storage for registration / verification tokens)
- Spring Mail (QQ SMTP) for 6-digit verification codes
- Spring Security Crypto — BCrypt password hashing
- HttpSession for logged-in user state

**Frontend**

- Thymeleaf server-side templates
- HTML / CSS / JavaScript (no separate SPA framework)
- Responsive campus-style UI (Inter font, card layout)

**Build & run**

- Maven (wrapper: `mvnw.cmd`)
- Embedded Tomcat on port `8080`

**Architecture**

```
Browser (Thymeleaf pages)
    → Controller (MVC)
        → Service
            → Repository (JPA)
                → MySQL
            → Redis / Mail (verification)
```

Layered package layout: `Controller` / `Service` / `Repo` / `dto` / `config` / `util`.

---

## Screens / 页面说明

Put screenshot files under `docs/screenshots/` using the filenames below. GitHub will render them automatically.

请把截图放到 `docs/screenshots/`，文件名与下文一致，推送到 GitHub 后即可显示。

### 1. Login / 登录页

**Route:** `/login`

Students sign in with university **User ID** and password. Links to forgot-password and sign-up. Failed login shows “ID does not exist” or “Incorrect password”. After success the session is stored and the user is redirected to `/welcome`.

学生使用学号和密码登录，可跳转忘记密码或注册。失败时提示学号不存在或密码错误；成功后写入 Session 并进入首页。

**Stack:** Thymeleaf + Spring MVC `@PostMapping("/login")` + BCrypt `PasswordEncoder` + `HttpSession`.

![Login](docs/screenshots/01-login.png)

### 2. Register, email code, set password / 注册、邮箱验证码、设置密码

**Routes:** `/register` → `/VerifyPage` → `/setPasswordPage`

Only students whose ID exists in the `student` whitelist table can register. The user submits ID + email; the system emails a 6-digit code (valid 5 minutes). After verification, the user sets a password that must be at least 8 characters, mix letters and numbers, and include a special character.

仅白名单 `student` 表中的学号可注册。提交学号和邮箱后发送 6 位验证码（5 分钟有效）。验证通过后设置密码：至少 8 位、字母+数字、至少一个特殊字符。

**Stack:** Spring Mail, in-memory cooldown cache + Redis token for the verified user, Thymeleaf forms.

![Set password](docs/screenshots/02-set-password.png)

![Verification email](docs/screenshots/03-verification-email.png)

### 3. Home — room listing & filters / 首页：会议室列表与筛选

**Route:** `/welcome`

After login, users see all rooms as cards (ID, location, capacity, IT facilities). They can search by room ID and filter by date/time, capacity bands (1–5, 6–10, 11–20, 21+), and facilities (projector, air conditioner, computer, speaker).

登录后以卡片展示全部会议室。支持按房间号搜索，以及按日期时间、容量区间、IT 设施筛选。

**Stack:** Thymeleaf + CSS cards, `UserSearchRoomController`, JPA queries on `room`.

![Home](docs/screenshots/04-home.png)

### 4. Book a room / 预约会议室

**Route:** booking page for a selected room (e.g. FBG02)

Shows room photo, capacity, facilities, location, and a half-hour timetable for the chosen date. Occupied slots are highlighted; free slots can be selected. User enters a booking reason and submits.

展示房间图片、容量、设施、地点，以及所选日期的半小时时间表。已占用时段高亮，空闲时段可点选。填写预约原因后提交。

**Stack:** Thymeleaf + JS slot picker, `BookingController` / `ScheduleService`, overlap checks against `booking` / `schedule` tables.

![Book room](docs/screenshots/05-book-room.png)

### 5. My bookings / 我的预约

**Route:** user booking history (sidebar **My Bookings**)

Lists the current user’s bookings (Booking ID, Room ID, start/end, date) with **Cancel**, **View**, and **Update**. Users may change or cancel a booking before the meeting starts.

列出当前用户的预约，支持取消、查看、修改。会议开始前可改期或取消。

**Stack:** Thymeleaf table + `UserControllBooking` / `BookingService`, session user id.

![My bookings](docs/screenshots/06-my-bookings.png)

### 6. Update booking / 修改预约

Modal to change room, start/end time, and date, then confirm. The backend re-validates conflicts and time rules.

弹窗修改房间、开始/结束时间和日期。后端再次校验冲突与时间规则。

**Stack:** HTML modal + form POST, same booking service layer.

![Update booking](docs/screenshots/07-update-booking.png)

### 7. Personal information / 个人信息

**Route:** `/welcome/info`

Shows avatar, user ID, username, email, phone, booking count, account status (lock/unlock), and password (masked). Users can edit profile, upload an avatar (stored under the local `booking-system/avatars` directory), change password, or log out.

展示头像、学号、用户名、邮箱、电话、预约次数、账号状态。可编辑资料、上传头像（本地目录）、改密、退出。

**Stack:** Thymeleaf, multipart upload, `UserController`, static resource mapping in `WebConfig` / `AvatarConfig`.

![Profile](docs/screenshots/08-profile.png)

### 8. Admin — room management / 管理端：会议室管理

**Route:** `http://localhost:8080/admin/room`

Admin catalog: search by room ID, filter by IT facility, availability, building location, capacity range, and whether the room has an image. Actions: **Add New Room**, **Update**, **Delete**.

管理员房间目录：按房间号搜索，按设施、是否可预约、楼栋、容量、是否有图筛选。可新增、更新、删除。

**Stack:** Thymeleaf admin layout, `adminRoomController`, multipart room images, JPA `Room` entity.

![Admin rooms](docs/screenshots/09-admin-rooms.png)

### 9. Admin — booking management / 管理端：预约管理

**Route:** `http://localhost:8080/admin/booking`

Review all bookings (paginated). Search by booking ID or user, filter by status, and remove a booking (reject / cancel on behalf of the system).

分页查看全部预约，按预约号或用户搜索、按状态筛选，并可删除（驳回）预约。

**Stack:** Spring Data `Pageable`, `adminBookingController`, Thymeleaf `bookingList.html`.

### 10. Admin — user management / 管理端：用户管理

**Route:** `http://localhost:8080/admin/users`

Search users by name / email / phone. Filter by lock status and booking-count range. **Toggle Status** locks or unlocks an account. Batch actions reset all booking counters and unlock everyone (e.g. start of a new term).

按姓名/邮箱/电话搜索；按锁定状态和预约次数筛选。可锁定/解锁账号，也可一键清零预约次数并解锁全部用户。

**Stack:** `adminUserController`, projection `UserView` (password not exposed in the list).

![Admin users](docs/screenshots/10-admin-users.png)

---

## Data model (simplified) / 数据模型（简）

| Table | Role |
| --- | --- |
| `student` | Whitelist of valid university IDs (registration gate) |
| `user` | Account: id, email, BCrypt password, avatar, booking_times, lock status |
| `room` | Catalog: id, image, capacity, IT facilities, location, availability |
| `booking` | A reservation: user, room, date, start/end, reason |
| `schedule` | Occupied time slots used to render the timetable |

Hibernate can create/update tables on startup (`spring.jpa.hibernate.ddl-auto=update`).

---

## Run locally / 本地运行

**Requirements:** JDK 17, Maven (or `mvnw.cmd`), MySQL 8, Redis.

1. Create a MySQL database (this project uses schema `user` locally) and start Redis.
2. Edit `src/main/resources/application.properties` — JDBC URL, username, password. Do **not** commit real passwords.
3. Start the app:

```bash
./mvnw spring-boot:run
```

Windows:

```bat
mvnw.cmd spring-boot:run
```

4. Open:

| Role | URL |
| --- | --- |
| User login | http://localhost:8080/login |
| Admin rooms | http://localhost:8080/admin/room |
| Admin bookings | http://localhost:8080/admin/booking |
| Admin users | http://localhost:8080/admin/users |

Registration requires the student ID to already exist in table `student`.

---

## Project structure / 目录结构

```
src/main/java/com/example/demo/
  Controller/     # MVC endpoints (user + admin)
  Service/        # Business rules (booking conflict, lock, mail)
  Repo/           # JPA entities and repositories
  dto/            # UserDTO
  config/         # BCrypt, static file mapping
  util/           # MailService, verification cache
src/main/resources/
  templates/      # Thymeleaf HTML pages
  static/         # Images, default avatar
  application.properties
docs/screenshots/ # README images
```

---

## What to tell interviewers / 面试可强调的点

- End-to-end MVC: form → controller → service → MySQL, not a toy CRUD demo.
- Real constraints: student whitelist, email OTP, password policy, time-slot conflict, lock for over-booking.
- Two portals (user vs admin) with different operations on the same data.
- File upload for avatars and room photos; BCrypt instead of plain-text passwords.

- 完整 MVC 链路，而不是只做增删改查。
- 业务约束：学号白名单、邮箱验证码、密码规则、时段冲突、超额预约锁定。
- 用户端与管理端操作同一套数据。
- 头像/房间图上传；密码 BCrypt 存储。

---

## How to insert screenshots / 如何插入图片

1. Create folder `docs/screenshots/` in the repo (already referenced above).
2. Save your captures with these names:

```
docs/screenshots/01-login.png
docs/screenshots/02-set-password.png
docs/screenshots/03-verification-email.png
docs/screenshots/04-home.png
docs/screenshots/05-book-room.png
docs/screenshots/06-my-bookings.png
docs/screenshots/07-update-booking.png
docs/screenshots/08-profile.png
docs/screenshots/09-admin-rooms.png
docs/screenshots/10-admin-users.png
```

3. In Markdown the syntax is:

```markdown
![short description](docs/screenshots/01-login.png)
```

GitHub only shows images that are **committed and pushed** with the README. Do not paste WeChat / local absolute paths (`D:\...`) — they will be broken for the interviewer.

GitHub 只会显示和 README **一起提交并推送** 的图片。不要用微信或本地绝对路径，面试官那边会裂图。
