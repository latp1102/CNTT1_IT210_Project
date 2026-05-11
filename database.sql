drop database if exists project;
create database project;
use project;


create table departments(
    id bigint primary key auto_increment,
    name varchar(100) not null
);

insert into departments(name) values
('Công nghệ thông tin'),
('Điện tử viễn thông'),
('Trí tuệ nhân tạo'),
('Khoa học dữ liệu');


create table users(
    id bigint primary key auto_increment,
    username varchar(50) unique not null,
    password varchar(255) not null,
    role varchar(20) not null,
    enabled boolean default true,
    department_id bigint,

    foreign key (department_id)
        references departments(id)
);

insert into users(id,username,password,role,enabled,department_id) values

(1,'admin',
'$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi',
'ADMIN',true,null),

(2,'lecturer1',
'$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi',
'LECTURER',true,1),

(3,'lecturer2',
'$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi',
'LECTURER',true,3),

(4,'student1',
'$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi',
'STUDENT',true,1),

(5,'student2',
'$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi',
'STUDENT',true,4),

-- Thêm giáo viên cho ngành Điện tử viễn thông (department_id = 2)
(6,'lecturer3',
'$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi',
'LECTURER',true,2),

-- Thêm giáo viên cho ngành Khoa học dữ liệu (department_id = 4)
(7,'lecturer4',
'$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi',
'LECTURER',true,4);


create table user_profiles(
    id bigint primary key auto_increment,
    user_id bigint unique,
    full_name varchar(100),
    email varchar(100),
    phone varchar(20),

    foreign key (user_id) references users(id)
);

insert into user_profiles(user_id,full_name,email,phone) values
(1,'System Admin','admin@mail.com','0900000001'),
(2,'Nguyen Van Lecturer','lec1@mail.com','0900000002'),
(3,'Tran Thi Lecturer','lec2@mail.com','0900000003'),
(4,'Pham Student One','stu1@mail.com','0900000004'),
(5,'Le Student Two','stu2@mail.com','0900000005');


create table equipments(
    id bigint primary key auto_increment,
    name varchar(100),
    quantity int,
    description text
);

insert into equipments(name,quantity,description) values
('Arduino Kit',10,'Kit thực hành IoT'),
('Raspberry Pi',5,'Mini Computer'),
('GPU RTX Lab',3,'AI Training Device'),
('Oscilloscope',7,'Thiết bị đo tín hiệu');


create table mentoring_sessions(
    id bigint primary key auto_increment,
    student_id bigint,
    lecturer_id bigint,
    session_time datetime,
    status varchar(30),

    foreign key (student_id) references users(id),
    foreign key (lecturer_id) references users(id)
);

insert into mentoring_sessions(student_id,lecturer_id,session_time,status) values
(4,2,'2026-06-10 09:00:00','PENDING'),
(5,3,'2026-06-11 14:00:00','PENDING'),
(4,2,'2026-06-12 10:00:00','CONFIRMED'),
(5,3,'2026-06-13 15:00:00','CONFIRMED'),
(4,2,'2026-06-05 08:00:00','COMPLETED'),
(5,3,'2026-06-06 14:00:00','COMPLETED');


create table academic_evaluations(
    id bigint primary key auto_increment,
    session_id bigint,
    evaluation_text text,

    foreign key (session_id) references mentoring_sessions(id)
);

insert into academic_evaluations(session_id,evaluation_text) values
(5,'Sinh viên làm bài tập khá tốt, nắm vững kiến thức cơ bản'),
(6,'Sinh viên cần ôn tập thêm về các khái niệm nâng cao');


create table borrowing_records(
    id bigint primary key auto_increment,
    session_id bigint,
    status varchar(30),

    foreign key (session_id) references mentoring_sessions(id)
);

insert into borrowing_records(session_id,status) values
(5,'PENDING_ISSUE'),
(6,'PENDING_ISSUE');


create table borrowing_details(
    id bigint primary key auto_increment,
    record_id bigint,
    equipment_id bigint,
    quantity int,

    foreign key (record_id) references borrowing_records(id),
    foreign key (equipment_id) references equipments(id)
);

insert into borrowing_details(record_id,equipment_id,quantity) values
(1,1,2),
(1,2,1),
(2,3,1),
(2,4,1);

SELECT * FROM borrowing_details;

-- 2. Xem equipment nào đang được mượn
SELECT e.id, e.name, e.quantity, COUNT(bd.id) as borrowing_count
FROM equipments e
         LEFT JOIN borrowing_details bd ON e.id = bd.equipment_id
GROUP BY e.id, e.name, e.quantity
HAVING COUNT(bd.id) > 0;

DELETE FROM borrowing_details WHERE equipment_id = 1;

SELECT * FROM borrowing_details;



