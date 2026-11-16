# Employee Management System

A console-based Java application that manages employee records in a MySQL database using JDBC. It supports full CRUD (create, read, update, delete) operations, search, department filtering and a department-level summary report.

**Technologies used:** Java · JDBC · MySQL

## Features

- **Add** a new employee with input validation (email format, 10-digit phone, non-negative salary, joining date not in the future)
- **View** all employees in a formatted table, with department names pulled in through a SQL `JOIN`
- **Find** an employee by ID, **search** by name (`LIKE`), and **filter** by department
- **Update** an employee — press Enter to keep any existing value
- **Delete** an employee with a confirmation prompt
- **Department summary report** — headcount and average salary per department using `GROUP BY`
- All queries use `PreparedStatement` (protects against SQL injection), and connections are closed with try-with-resources

## Project structure

```
employee-management-system/
├── config/
│   └── db.properties.example   # copy to db.properties and add your credentials
├── database/
│   └── schema.sql              # creates employee_db, tables and sample data
├── lib/                        # place the MySQL JDBC driver (.jar) here
├── src/com/ems/
│   ├── Main.java               # console menu and input validation
│   ├── db/DBConnection.java    # loads config and opens JDBC connections
│   ├── dao/EmployeeDAO.java    # all SQL queries (CRUD + reports)
│   └── model/Employee.java     # employee entity class
├── run.sh                      # compile & run (macOS / Linux)
└── run.bat                     # compile & run (Windows)
```

## Database design

| Table         | Key columns                                                                                   |
|---------------|-----------------------------------------------------------------------------------------------|
| `departments` | `dept_id` (PK), `dept_name` (unique)                                                          |
| `employees`   | `emp_id` (PK), `first_name`, `last_name`, `email` (unique), `phone`, `designation`, `dept_id` (FK → departments), `salary`, `date_joined`, `created_at`, `updated_at` |

## How to run

**Prerequisites:** JDK 17 or later, MySQL 8.

1. **Create the database**
   ```bash
   mysql -u root -p < database/schema.sql
   ```
2. **Add the JDBC driver** — download *MySQL Connector/J* (Platform Independent) from https://dev.mysql.com/downloads/connector/j/ and put the `.jar` file in `lib/`.
3. **Configure the connection**
   ```bash
   cp config/db.properties.example config/db.properties
   ```
   Then edit `config/db.properties` with your MySQL username and password.
4. **Run**
   ```bash
   ./run.sh        # macOS / Linux
   run.bat         # Windows
   ```

## Sample session

```
1. Add employee
2. View all employees
...
Enter choice: 2
------------------------------------------------------------------------------------------------------------------------------------
| ID   | Name                 | Email                        | Designation          | Department       |       Salary | Joined     |
------------------------------------------------------------------------------------------------------------------------------------
| 1    | Arun Kumar           | arun.kumar@example.com       | Software Engineer    | Engineering      |    650000.00 | 2023-06-12 |
| 2    | Priya Raman          | priya.raman@example.com      | HR Executive         | Human Resources  |    480000.00 | 2022-01-03 |
...
```
