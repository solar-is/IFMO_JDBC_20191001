package com.efimchick.ifmo.web.jdbc.dao;

import com.efimchick.ifmo.web.jdbc.ConnectionSource;
import com.efimchick.ifmo.web.jdbc.domain.Department;
import com.efimchick.ifmo.web.jdbc.domain.Employee;
import com.efimchick.ifmo.web.jdbc.domain.FullName;
import com.efimchick.ifmo.web.jdbc.domain.Position;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class DaoFactory {

    private Employee employeesRowMapper(ResultSet resultSet) {
        Employee cur = null;
        try {
            Object managerId = resultSet.getObject("MANAGER");
            Object departmentId = resultSet.getObject("DEPARTMENT");
            cur = new Employee(
                    new BigInteger(String.valueOf(resultSet.getInt("ID"))),
                    new FullName(
                            resultSet.getString("FIRSTNAME"),
                            resultSet.getString("LASTNAME"),
                            resultSet.getString("MIDDLENAME")
                    ),
                    Position.valueOf(resultSet.getString("POSITION")),
                    LocalDate.parse(resultSet.getString("HIREDATE")),
                    new BigDecimal(resultSet.getInt("SALARY")),
                    managerId == null ? BigInteger.ZERO : BigInteger.valueOf((Integer) managerId),
                    departmentId == null ? BigInteger.ZERO : BigInteger.valueOf((Integer) departmentId)
            );
        } catch (SQLException ignored) {
        }
        return cur;
    }

    private ResultSet getResultSetFromDB(String SQL) throws SQLException {
        ConnectionSource connectionSource = ConnectionSource.instance();
        Connection connection = connectionSource.createConnection();
        return connection.createStatement().executeQuery(SQL);
    }

    private List<Employee> getAllEmployees() throws SQLException {
        ResultSet resultSetFromDB = getResultSetFromDB("SELECT * FROM EMPLOYEE");
        List<Employee> employees = new ArrayList<>();
        while (resultSetFromDB.next()) {
            Employee employee = employeesRowMapper(resultSetFromDB);
            employees.add(employee);
        }
        return employees;
    }

    private List<Department> getAllDepartments() throws SQLException {
        ResultSet resultSetFromDB = getResultSetFromDB("SELECT * FROM DEPARTMENT");
        List<Department> departments = new ArrayList<>();
        while (resultSetFromDB.next()) {
            Department department = new Department(
                    BigInteger.valueOf(resultSetFromDB.getInt("ID")),
                    resultSetFromDB.getString("NAME"),
                    resultSetFromDB.getString("LOCATION")
            );
            departments.add(department);
        }
        return departments;
    }

    private List<Employee> employees;
    private List<Department> departments;

    {
        try {
            employees = getAllEmployees();
            departments = getAllDepartments();
        } catch (SQLException ignored) {
        }
    }


    private final EmployeeDao employeeDaoInstance = new EmployeeDao() {
        @Override
        public List<Employee> getByDepartment(Department department) {
            List<Employee> ans = new ArrayList<>();
            for (Employee e : employees) {
                if (e.getDepartmentId() != null)
                    if (e.getDepartmentId().equals(department.getId()))
                        ans.add(e);
            }
            return ans;
        }

        @Override
        public List<Employee> getByManager(Employee manager) {
            List<Employee> ans = new ArrayList<>();
            for (Employee e : employees)
                if (e.getManagerId() != null)
                    if (e.getManagerId().equals(manager.getId())) {
                        ans.add(e);
                    }
            return ans;
        }

        @Override
        public Optional<Employee> getById(BigInteger Id) {
            for (Employee e : employees) {
                if (e.getId().equals(Id)) {
                    return Optional.of(e);
                }
            }
            return Optional.empty();
        }

        @Override
        public List<Employee> getAll() {
            return employees;
        }


        @Override
        public Employee save(Employee employee) {
            employees.add(employee);
            return employee;
        }

        @Override
        public void delete(Employee employee) {
            employees.remove(employee);
        }
    };

    private final DepartmentDao departmentDaoInstance = new DepartmentDao() {
        @Override
        public Optional<Department> getById(BigInteger Id) {
            for (Department d : departments) {
                if (d.getId().equals(Id)) {
                    return Optional.of(d);
                }
            }
            return Optional.empty();
        }

        @Override
        public List<Department> getAll() {
            return departments;
        }

        @Override
        public Department save(Department department) {
            departments.removeIf(next -> next.getId().equals(department.getId()));
            departments.add(department);
            return department;
        }

        @Override
        public void delete(Department department) {
            departments.remove(department);
        }
    };

    public EmployeeDao employeeDAO() {
        return employeeDaoInstance;
    }

    public DepartmentDao departmentDAO() {
        return departmentDaoInstance;
    }
}
