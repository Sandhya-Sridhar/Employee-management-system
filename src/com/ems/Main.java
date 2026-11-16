package com.ems;

import com.ems.dao.EmployeeDAO;
import com.ems.model.Employee;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Console front end for the Employee Management System.
 */
public class Main {

    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[\\w.]+$");
    private static final Pattern PHONE = Pattern.compile("^\\d{10}$");
    private static final String LINE = "-".repeat(132);

    private final Scanner in = new Scanner(System.in);
    private final EmployeeDAO dao = new EmployeeDAO();

    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {
        System.out.println("=========================================");
        System.out.println("       EMPLOYEE MANAGEMENT SYSTEM        ");
        System.out.println("=========================================");
        while (true) {
            printMenu();
            String choice = in.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> addEmployee();
                    case "2" -> listEmployees();
                    case "3" -> findById();
                    case "4" -> searchByName();
                    case "5" -> listByDepartment();
                    case "6" -> updateEmployee();
                    case "7" -> deleteEmployee();
                    case "8" -> departmentReport();
                    case "0" -> {
                        System.out.println("Goodbye!");
                        return;
                    }
                    default -> System.out.println("Invalid choice, please try again.");
                }
            } catch (SQLIntegrityConstraintViolationException e) {
                System.out.println("Database constraint error: " + e.getMessage()
                        + " (is the email already used, or the department ID invalid?)");
            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("1. Add employee");
        System.out.println("2. View all employees");
        System.out.println("3. Find employee by ID");
        System.out.println("4. Search employees by name");
        System.out.println("5. View employees by department");
        System.out.println("6. Update employee");
        System.out.println("7. Delete employee");
        System.out.println("8. Department summary report");
        System.out.println("0. Exit");
        System.out.print("Enter choice: ");
    }

    // ---------- actions ----------

    private void addEmployee() throws SQLException {
        System.out.println("\n-- Add Employee --");
        Employee e = new Employee();
        e.setFirstName(readRequired("First name: "));
        e.setLastName(readRequired("Last name: "));
        e.setEmail(readMatching("Email: ", EMAIL, "Enter a valid email address."));
        e.setPhone(readMatching("Phone (10 digits): ", PHONE, "Phone must be exactly 10 digits."));
        e.setDesignation(readRequired("Designation: "));
        e.setDeptId(readDepartment(null));
        e.setSalary(readSalary(null));
        e.setDateJoined(readDate(null));
        int id = dao.addEmployee(e);
        System.out.println("Employee added with ID " + id + ".");
    }

    private void listEmployees() throws SQLException {
        printTable(dao.getAllEmployees());
    }

    private void findById() throws SQLException {
        int id = readInt("Employee ID: ");
        Optional<Employee> emp = dao.getEmployeeById(id);
        if (emp.isPresent()) {
            printTable(List.of(emp.get()));
        } else {
            System.out.println("No employee found with ID " + id + ".");
        }
    }

    private void searchByName() throws SQLException {
        String keyword = readRequired("Name contains: ");
        printTable(dao.searchByName(keyword));
    }

    private void listByDepartment() throws SQLException {
        Integer deptId = readDepartment(null);
        if (deptId == null) {
            System.out.println("No department selected.");
            return;
        }
        printTable(dao.getByDepartment(deptId));
    }

    private void updateEmployee() throws SQLException {
        int id = readInt("ID of employee to update: ");
        Optional<Employee> found = dao.getEmployeeById(id);
        if (found.isEmpty()) {
            System.out.println("No employee found with ID " + id + ".");
            return;
        }
        Employee e = found.get();
        System.out.println("Press Enter to keep the current value shown in [brackets].");
        e.setFirstName(readOptional("First name", e.getFirstName()));
        e.setLastName(readOptional("Last name", e.getLastName()));
        e.setEmail(readOptionalMatching("Email", e.getEmail(), EMAIL, "Enter a valid email address."));
        e.setPhone(readOptionalMatching("Phone", e.getPhone(), PHONE, "Phone must be exactly 10 digits."));
        e.setDesignation(readOptional("Designation", e.getDesignation()));
        e.setDeptId(readDepartment(e.getDeptId()));
        e.setSalary(readSalary(e.getSalary()));
        e.setDateJoined(readDate(e.getDateJoined()));
        System.out.println(dao.updateEmployee(e) ? "Employee updated." : "Update failed.");
    }

    private void deleteEmployee() throws SQLException {
        int id = readInt("ID of employee to delete: ");
        Optional<Employee> found = dao.getEmployeeById(id);
        if (found.isEmpty()) {
            System.out.println("No employee found with ID " + id + ".");
            return;
        }
        System.out.print("Delete " + found.get().getFullName() + "? (y/n): ");
        if (in.nextLine().trim().equalsIgnoreCase("y")) {
            System.out.println(dao.deleteEmployee(id) ? "Employee deleted." : "Delete failed.");
        } else {
            System.out.println("Cancelled.");
        }
    }

    private void departmentReport() throws SQLException {
        System.out.println("\n-- Department Summary --");
        Map<String, String> summary = dao.departmentSummary();
        if (summary.isEmpty()) {
            System.out.println("No data.");
        }
        summary.forEach((dept, info) -> System.out.printf("%-18s %s%n", dept, info));
    }

    // ---------- input helpers ----------

    private void printTable(List<Employee> employees) {
        if (employees.isEmpty()) {
            System.out.println("No records found.");
            return;
        }
        System.out.println(LINE);
        System.out.printf("| %-4s | %-20s | %-28s | %-20s | %-16s | %12s | %-10s |%n",
                "ID", "Name", "Email", "Designation", "Department", "Salary", "Joined");
        System.out.println(LINE);
        employees.forEach(System.out::println);
        System.out.println(LINE);
        System.out.println(employees.size() + " record(s).");
    }

    private String readRequired(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = in.nextLine().trim();
            if (!value.isEmpty()) return value;
            System.out.println("This field is required.");
        }
    }

    private String readMatching(String prompt, Pattern pattern, String error) {
        while (true) {
            String value = readRequired(prompt);
            if (pattern.matcher(value).matches()) return value;
            System.out.println(error);
        }
    }

    private String readOptional(String label, String current) {
        System.out.print(label + " [" + current + "]: ");
        String value = in.nextLine().trim();
        return value.isEmpty() ? current : value;
    }

    private String readOptionalMatching(String label, String current, Pattern pattern, String error) {
        while (true) {
            String value = readOptional(label, current);
            if (value == null || pattern.matcher(value).matches()) return value;
            System.out.println(error);
        }
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(in.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number.");
            }
        }
    }

    private Integer readDepartment(Integer current) throws SQLException {
        Map<Integer, String> depts = dao.getDepartments();
        System.out.println("Departments:");
        depts.forEach((id, name) -> System.out.println("  " + id + ". " + name));
        while (true) {
            System.out.print("Department ID" + (current != null ? " [" + current + "]" : "") + ": ");
            String value = in.nextLine().trim();
            if (value.isEmpty()) return current;
            try {
                int id = Integer.parseInt(value);
                if (depts.containsKey(id)) return id;
            } catch (NumberFormatException ignored) {
                // fall through to error message
            }
            System.out.println("Choose one of the department IDs listed above.");
        }
    }

    private BigDecimal readSalary(BigDecimal current) {
        while (true) {
            System.out.print("Annual salary" + (current != null ? " [" + current.toPlainString() + "]" : "") + ": ");
            String value = in.nextLine().trim();
            if (value.isEmpty() && current != null) return current;
            try {
                BigDecimal salary = new BigDecimal(value);
                if (salary.signum() >= 0) return salary;
            } catch (NumberFormatException ignored) {
                // fall through
            }
            System.out.println("Enter a non-negative number, e.g. 550000.");
        }
    }

    private LocalDate readDate(LocalDate current) {
        while (true) {
            System.out.print("Date joined (YYYY-MM-DD)" + (current != null ? " [" + current + "]" : "") + ": ");
            String value = in.nextLine().trim();
            if (value.isEmpty() && current != null) return current;
            try {
                LocalDate date = LocalDate.parse(value);
                if (!date.isAfter(LocalDate.now())) return date;
                System.out.println("Joining date cannot be in the future.");
            } catch (DateTimeParseException e) {
                System.out.println("Use the format YYYY-MM-DD, e.g. 2024-07-01.");
            }
        }
    }
}
