package com.efimchick.ifmo.web.jdbc.service;

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
import java.util.ArrayList;
import java.util.List;

class ServiceDataAndUtils {

    private List<Department> departments = getAllDepartments();
    List<Employee> employeesWithoutChain = getAllEmployees(false);
    List<Employee> employeesWithChain = getAllEmployees(true);

    private ResultSet getResultSetFromDB(String SQL) throws SQLException {
        final ConnectionSource connectionSource = ConnectionSource.instance();
        final Connection connection = connectionSource.createConnection();
        return connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE).executeQuery(SQL);
    }

    private List<Employee> getAllEmployees(boolean chain) {
        List<Employee> employees = null;
        try (ResultSet resultSetFromDB = getResultSetFromDB("SELECT * FROM EMPLOYEE")) {
            employees = new ArrayList<>();
            while (resultSetFromDB.next()) {
                employees.add(employeesRowMapper(resultSetFromDB, chain, true));
            }
        } catch (SQLException ignored) {
        }
        return employees;
    }

    private List<Department> getAllDepartments() {
        List<Department> departments = null;
        try (ResultSet resultSetFromDB = getResultSetFromDB("SELECT * FROM DEPARTMENT")) {
            departments = new ArrayList<>();
            while (resultSetFromDB.next()) {
                departments.add(new Department(
                        BigInteger.valueOf(resultSetFromDB.getInt("ID")),
                        resultSetFromDB.getString("NAME"),
                        resultSetFromDB.getString("LOCATION"))
                );
            }

        } catch (SQLException ignored) {
        }
        return departments;
    }

    private Employee employeesRowMapper(ResultSet resultSet, boolean continueChain, boolean isFirst) {
        Employee employee = null;
        try {
            Integer departmentId = (Integer) resultSet.getObject("DEPARTMENT");
            Integer managerId = (Integer) resultSet.getObject("MANAGER");
            if (!isFirst && !continueChain) {
                managerId = null;
            }
            employee = new Employee(
                    new BigInteger(String.valueOf(resultSet.getInt("ID"))),
                    new FullName(
                            resultSet.getString("FIRSTNAME"),
                            resultSet.getString("LASTNAME"),
                            resultSet.getString("MIDDLENAME")
                    ),
                    Position.valueOf(resultSet.getString("POSITION")),
                    LocalDate.parse(resultSet.getString("HIREDATE")),
                    new BigDecimal(resultSet.getInt("SALARY")),
                    managerId == null ? null : getManagerById(resultSet, managerId, continueChain),
                    departmentId == null ? null : getDepartmentFromList(departmentId)
            );
        } catch (SQLException ignored) {
        }
        return employee;
    }

    private Employee getManagerById(ResultSet resultSet, int managerId, boolean continueChain) throws SQLException {
        Employee manager = null;
        int n = resultSet.getRow();
        resultSet.beforeFirst();
        while (resultSet.next()) {
            if (resultSet.getInt("ID") == managerId) {
                manager = employeesRowMapper(resultSet, continueChain, false);
                break;
            }
        }
        resultSet.absolute(n);
        return manager;
    }


    private Department getDepartmentFromList(Integer departmentId) {
        for (Department d : departments) {
            if (d.getId().equals(BigInteger.valueOf(departmentId)))
                return d;
        }
        return null;
    }
}