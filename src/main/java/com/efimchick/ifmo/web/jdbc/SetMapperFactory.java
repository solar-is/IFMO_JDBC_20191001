package com.efimchick.ifmo.web.jdbc;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

import com.efimchick.ifmo.web.jdbc.domain.Employee;
import com.efimchick.ifmo.web.jdbc.domain.FullName;
import com.efimchick.ifmo.web.jdbc.domain.Position;

public class SetMapperFactory {

    public SetMapper<Set<Employee>> employeesSetMapper() {
        return resultSet -> {
            Set<Employee> ans = new LinkedHashSet<>();
            try {
                while (resultSet.next()) {
                    Employee cur = getRowEmployee(resultSet);
                    ans.add(cur);
                }
            } catch (SQLException ignored) {
            }
            return ans;
        };
    }

    private Employee getRowEmployee(ResultSet resultSet) throws SQLException {
        Employee manager = getManagerOfEmployee(resultSet);
        return new Employee(
                new BigInteger(String.valueOf(resultSet.getInt("ID"))),
                new FullName(
                        resultSet.getString("FIRSTNAME"),
                        resultSet.getString("LASTNAME"),
                        resultSet.getString("MIDDLENAME")
                ),
                Position.valueOf(resultSet.getString("POSITION")),
                LocalDate.parse(resultSet.getString("HIREDATE")),
                new BigDecimal(resultSet.getInt("SALARY")),
                manager
        );
    }

    private Employee getManagerOfEmployee(ResultSet resultSet) throws SQLException {
        Employee manager = null;
        Integer managerId = (Integer) resultSet.getObject("MANAGER");
        if (managerId == null)
            return null;
        int initRowNumber = resultSet.getRow();
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
}
