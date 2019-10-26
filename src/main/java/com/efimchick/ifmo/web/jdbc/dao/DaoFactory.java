package com.efimchick.ifmo.web.jdbc.dao;

import com.efimchick.ifmo.web.jdbc.domain.Department;
import com.efimchick.ifmo.web.jdbc.domain.Employee;
import com.efimchick.ifmo.web.jdbc.domain.FullName;
import com.efimchick.ifmo.web.jdbc.domain.Position;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class DaoFactory {
    private Employee getRowEmployee(ResultSet resultSet) {
        Employee cur = null;
        try {
            BigInteger managerId = (BigInteger) resultSet.getObject("MANAGER");
            BigInteger departmentId = (BigInteger) resultSet.getObject("DEPARTMENT");
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
                    managerId,
                    departmentId
            );
        } catch (SQLException ignored) {
        }
        return cur;
    }

    private Employee getManagerOfEmployee(ResultSet resultSet) throws SQLException {
        Integer managerId = (Integer) resultSet.getObject("MANAGER");
        if (managerId == null)
            return null;
        int initRowNumber = resultSet.getRow();
        Employee manager = null;
        resultSet.beforeFirst();
        while (resultSet.next()) {
            if (resultSet.getInt("ID") == managerId) {
                manager = getRowEmployee(resultSet);
                break;
            }
        }
        resultSet.absolute(initRowNumber);
        return manager;
    }

    public Set<Employee> employeesSetMapper(ResultSet resultSet) throws SQLException {
        resultSet.beforeFirst();
        Set<Employee> ans = new LinkedHashSet<>();
        try {
            while (resultSet.next()) {
                Employee cur = getRowEmployee(resultSet);
                ans.add(cur);
            }
        } catch (SQLException ignored) {
        }
        return ans;

    }

    public EmployeeDao employeeDAO() {
        return new EmployeeDao() {
            @Override
            public List<Employee> getByDepartment(Department department) {
                return null;
            }

            @Override
            public List<Employee> getByManager(Employee employee) {
                return null;
            }

            @Override
            public Optional<Employee> getById(BigInteger Id) {
                return Optional.empty();
            }

            @Override
            public List<Employee> getAll() {
                return null;
            }

            @Override
            public Employee save(Employee employee) {
                return null;
            }

            @Override
            public void delete(Employee employee) {

            }
        };
    }

    public DepartmentDao departmentDAO() {
        return new DepartmentDao() {
            @Override
            public Optional<Department> getById(BigInteger Id) {
                return Optional.empty();
            }

            @Override
            public List<Department> getAll() {
                return null;
            }

            @Override
            public Department save(Department department) {
                return null;
            }

            @Override
            public void delete(Department department) {

            }
        };
    }
}
