-- Employee Management System - MySQL schema
-- Run with: mysql -u root -p < database/schema.sql

CREATE DATABASE IF NOT EXISTS employee_db;
USE employee_db;

CREATE TABLE IF NOT EXISTS departments (
    dept_id     INT AUTO_INCREMENT PRIMARY KEY,
    dept_name   VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS employees (
    emp_id       INT AUTO_INCREMENT PRIMARY KEY,
    first_name   VARCHAR(50)    NOT NULL,
    last_name    VARCHAR(50)    NOT NULL,
    email        VARCHAR(100)   NOT NULL UNIQUE,
    phone        VARCHAR(15),
    designation  VARCHAR(60)    NOT NULL,
    dept_id      INT,
    salary       DECIMAL(10, 2) NOT NULL CHECK (salary >= 0),
    date_joined  DATE           NOT NULL,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_employees_dept (dept_id),
    CONSTRAINT fk_employee_department
        FOREIGN KEY (dept_id) REFERENCES departments (dept_id)
        ON DELETE SET NULL
);

-- Seed data
INSERT IGNORE INTO departments (dept_name) VALUES
    ('Engineering'), ('Human Resources'), ('Finance'), ('Marketing'), ('Operations');

INSERT IGNORE INTO employees
    (first_name, last_name, email, phone, designation, dept_id, salary, date_joined)
VALUES
    ('Arun',    'Kumar',   'arun.kumar@example.com',    '9876500001', 'Software Engineer',  1, 650000.00, '2023-06-12'),
    ('Priya',   'Raman',   'priya.raman@example.com',   '9876500002', 'HR Executive',       2, 480000.00, '2022-01-03'),
    ('Karthik', 'Suresh',  'karthik.s@example.com',     '9876500003', 'Financial Analyst',  3, 720000.00, '2021-09-20'),
    ('Divya',   'Mohan',   'divya.mohan@example.com',   '9876500004', 'Marketing Associate',4, 420000.00, '2024-02-15'),
    ('Rahul',   'Venkat',  'rahul.venkat@example.com',  '9876500005', 'Senior Engineer',    1, 1100000.00,'2019-11-04');
