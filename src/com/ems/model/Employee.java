package com.ems.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Plain Java object representing one row of the employees table.
 */
public class Employee {

    private int empId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String designation;
    private Integer deptId;
    private String deptName;      // populated from a JOIN, read-only
    private BigDecimal salary;
    private LocalDate dateJoined;

    public Employee() {
    }

    public Employee(String firstName, String lastName, String email, String phone,
                    String designation, Integer deptId, BigDecimal salary, LocalDate dateJoined) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.designation = designation;
        this.deptId = deptId;
        this.salary = salary;
        this.dateJoined = dateJoined;
    }

    public int getEmpId() { return empId; }
    public void setEmpId(int empId) { this.empId = empId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFullName() { return firstName + " " + lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public Integer getDeptId() { return deptId; }
    public void setDeptId(Integer deptId) { this.deptId = deptId; }

    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }

    public BigDecimal getSalary() { return salary; }
    public void setSalary(BigDecimal salary) { this.salary = salary; }

    public LocalDate getDateJoined() { return dateJoined; }
    public void setDateJoined(LocalDate dateJoined) { this.dateJoined = dateJoined; }

    @Override
    public String toString() {
        return String.format("| %-4d | %-20s | %-28s | %-20s | %-16s | %12s | %-10s |",
                empId,
                truncate(getFullName(), 20),
                truncate(email, 28),
                truncate(designation, 20),
                truncate(deptName == null ? "-" : deptName, 16),
                salary == null ? "-" : salary.toPlainString(),
                dateJoined == null ? "-" : dateJoined.toString());
    }

    private static String truncate(String value, int max) {
        if (value == null) return "";
        return value.length() <= max ? value : value.substring(0, max - 1) + "…";
    }
}
