package com.ems.dao;

import com.ems.db.DBConnection;
import com.ems.model.Employee;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Data Access Object: every SQL statement for the employees table lives here.
 * All queries use PreparedStatement to prevent SQL injection.
 */
public class EmployeeDAO {

    private static final String SELECT_BASE =
            "SELECT e.emp_id, e.first_name, e.last_name, e.email, e.phone, e.designation, "
          + "       e.dept_id, d.dept_name, e.salary, e.date_joined "
          + "FROM employees e LEFT JOIN departments d ON e.dept_id = d.dept_id ";

    private static final String INSERT_SQL =
            "INSERT INTO employees (first_name, last_name, email, phone, designation, dept_id, salary, date_joined) "
          + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_SQL =
            "UPDATE employees SET first_name = ?, last_name = ?, email = ?, phone = ?, designation = ?, "
          + "dept_id = ?, salary = ?, date_joined = ? WHERE emp_id = ?";

    private static final String DELETE_SQL = "DELETE FROM employees WHERE emp_id = ?";

    // ---------- CREATE ----------
    public int addEmployee(Employee emp) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            bindEmployee(ps, emp);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    emp.setEmpId(id);
                    return id;
                }
            }
        }
        throw new SQLException("Insert succeeded but no ID was returned.");
    }

    // ---------- READ ----------
    public List<Employee> getAllEmployees() throws SQLException {
        return query(SELECT_BASE + "ORDER BY e.emp_id");
    }

    public Optional<Employee> getEmployeeById(int id) throws SQLException {
        List<Employee> result = query(SELECT_BASE + "WHERE e.emp_id = ?", id);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    public List<Employee> searchByName(String keyword) throws SQLException {
        String like = "%" + keyword + "%";
        return query(SELECT_BASE
                + "WHERE e.first_name LIKE ? OR e.last_name LIKE ? OR CONCAT(e.first_name, ' ', e.last_name) LIKE ? "
                + "ORDER BY e.first_name", like, like, like);
    }

    public List<Employee> getByDepartment(int deptId) throws SQLException {
        return query(SELECT_BASE + "WHERE e.dept_id = ? ORDER BY e.salary DESC", deptId);
    }

    // ---------- UPDATE ----------
    public boolean updateEmployee(Employee emp) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(UPDATE_SQL)) {
            bindEmployee(ps, emp);
            ps.setInt(9, emp.getEmpId());
            return ps.executeUpdate() == 1;
        }
    }

    // ---------- DELETE ----------
    public boolean deleteEmployee(int id) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(DELETE_SQL)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        }
    }

    // ---------- REPORTS ----------
    /** Department name -> {headcount, average salary}, using GROUP BY. */
    public Map<String, String> departmentSummary() throws SQLException {
        String sql = "SELECT COALESCE(d.dept_name, 'Unassigned') AS dept, COUNT(e.emp_id) AS headcount, "
                   + "ROUND(AVG(e.salary), 2) AS avg_salary "
                   + "FROM employees e LEFT JOIN departments d ON e.dept_id = d.dept_id "
                   + "GROUP BY dept ORDER BY headcount DESC";
        Map<String, String> summary = new LinkedHashMap<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                summary.put(rs.getString("dept"),
                        rs.getInt("headcount") + " employee(s), avg salary " + rs.getBigDecimal("avg_salary"));
            }
        }
        return summary;
    }

    /** Lists departments as id -> name, for menus. */
    public Map<Integer, String> getDepartments() throws SQLException {
        Map<Integer, String> depts = new LinkedHashMap<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT dept_id, dept_name FROM departments ORDER BY dept_id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                depts.put(rs.getInt(1), rs.getString(2));
            }
        }
        return depts;
    }

    // ---------- helpers ----------
    private void bindEmployee(PreparedStatement ps, Employee emp) throws SQLException {
        ps.setString(1, emp.getFirstName());
        ps.setString(2, emp.getLastName());
        ps.setString(3, emp.getEmail());
        ps.setString(4, emp.getPhone());
        ps.setString(5, emp.getDesignation());
        if (emp.getDeptId() == null) {
            ps.setNull(6, Types.INTEGER);
        } else {
            ps.setInt(6, emp.getDeptId());
        }
        ps.setBigDecimal(7, emp.getSalary());
        ps.setDate(8, Date.valueOf(emp.getDateJoined()));
    }

    private List<Employee> query(String sql, Object... params) throws SQLException {
        List<Employee> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    private Employee mapRow(ResultSet rs) throws SQLException {
        Employee e = new Employee();
        e.setEmpId(rs.getInt("emp_id"));
        e.setFirstName(rs.getString("first_name"));
        e.setLastName(rs.getString("last_name"));
        e.setEmail(rs.getString("email"));
        e.setPhone(rs.getString("phone"));
        e.setDesignation(rs.getString("designation"));
        int deptId = rs.getInt("dept_id");
        e.setDeptId(rs.wasNull() ? null : deptId);
        e.setDeptName(rs.getString("dept_name"));
        e.setSalary(rs.getBigDecimal("salary"));
        Date joined = rs.getDate("date_joined");
        e.setDateJoined(joined == null ? null : joined.toLocalDate());
        return e;
    }
}
