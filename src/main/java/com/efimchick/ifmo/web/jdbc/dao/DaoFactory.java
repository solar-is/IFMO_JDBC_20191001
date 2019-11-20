package com.efimchick.ifmo.web.jdbc.dao;

import com.efimchick.ifmo.web.jdbc.ConnectionSource;
import com.efimchick.ifmo.web.jdbc.domain.Department;
import com.efimchick.ifmo.web.jdbc.domain.Employee;
import com.efimchick.ifmo.web.jdbc.domain.FullName;
import com.efimchick.ifmo.web.jdbc.domain.Position;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class DaoFactory {

    private ResultSet getResultSetOfExecute(String SQL) throws SQLException {
        ConnectionSource connectionSource = ConnectionSource.instance();
        Connection connection = connectionSource.createConnection();
        return connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE).executeQuery(SQL);
    }

    private void Update(String SQL) throws SQLException {
        ConnectionSource connectionSource = ConnectionSource.instance();
        Connection connection = connectionSource.createConnection();
        connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE).executeUpdate(SQL);
    }

    private Employee employeeRowMapper(ResultSet resultSet) {
        Employee cur = null;
        try {
            String managerId = resultSet.getString("MANAGER");
            String departmentId = resultSet.getString("DEPARTMENT");
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
                    managerId == null ? BigInteger.ZERO : new BigInteger(managerId),
                    departmentId == null ? BigInteger.ZERO : new BigInteger(departmentId)
            );
        } catch (SQLException ignored) {
        }
        return cur;
    }

    private Department departmentRowMapper(ResultSet res) {
        Department cur = null;
        try {
            cur = new Department(
                new BigInteger(res.getString("ID")),
                res.getString("NAME"),
                res.getString("LOCATION")
            );
        } catch (SQLException ignored) {
        }
        return cur;
    }

    private List<Employee> getEmployeeListByResultSet(ResultSet res) throws SQLException {
        res.beforeFirst();
        List<Employee> ans = new ArrayList<>();
        while (res.next()) {
            ans.add(employeeRowMapper(res));
        }
        return ans;
    }

    private List<Department> getDepartmentListByResultSet(ResultSet res) throws SQLException {
        res.beforeFirst();
        List<Department> ans = new ArrayList<>();
        while (res.next()) {
            ans.add(departmentRowMapper(res));
        }
        return ans;
    }


    public EmployeeDao employeeDAO() {
        return new EmployeeDao() {
            @Override
            public List<Employee> getByDepartment(Department department) {
                try {
                    ResultSet res = getResultSetOfExecute("select * from employee where department = " + department.getId());
                    return getEmployeeListByResultSet(res);
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public List<Employee> getByManager(Employee manager) {
                try {
                    ResultSet res = getResultSetOfExecute("select * from employee where manager = " + manager.getId());
                    return getEmployeeListByResultSet(res);
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public Optional<Employee> getById(BigInteger Id) {
                try {
                    ResultSet res = getResultSetOfExecute("select * from employee where id = " + Id.intValue());
                    if (!res.next()){
                        return Optional.empty();
                    } else {
                        return Optional.of(getEmployeeListByResultSet(res).get(0));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    return Optional.empty();
                }
            }

            @Override
            public List<Employee> getAll() {
                try {
                    ResultSet res = getResultSetOfExecute("select * from employee");
                    return getEmployeeListByResultSet(res);
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }


            @Override
            public Employee save(Employee employee) {
                try {
                    /// some issue with hsqldb: need "'var'" instead of "var"
                    Update("insert into employee values" +
                            "('" + employee.getId() +
                            "','" + employee.getFullName().getFirstName() +
                            "','" + employee.getFullName().getLastName() +
                            "','" + employee.getFullName().getMiddleName() +
                            "','" + employee.getPosition() +
                            "','" + employee.getManagerId() +
                            "','" + employee.getHired() +
                            "','" + employee.getSalary() +
                            "','" + employee.getDepartmentId() +
                            "')"
                    );
                    return employee;
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void delete(Employee employee) {
                try {
                    Update("delete from employee where id = " + employee.getId());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        };
    }


    public DepartmentDao departmentDAO() {
        return new DepartmentDao() {
            @Override
            public Optional<Department> getById(BigInteger Id) {
                try {
                    ResultSet res = getResultSetOfExecute("select * from department where id = " + Id);
                    if (!res.next()){
                        return Optional.empty();
                    } else {
                        return Optional.of(getDepartmentListByResultSet(res).get(0));
                    }
                }
                catch (SQLException e) {
                    e.printStackTrace();
                    return Optional.empty();
                }
            }

            @Override
            public List<Department> getAll() {
                try {
                    ResultSet res = getResultSetOfExecute("select * from department");
                    return getDepartmentListByResultSet(res);
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public Department save(Department department) {
                try {
                    /// some issue with hsqldb: need "'var'" instead of "var"
                    ResultSet isPresent = getResultSetOfExecute("select * from department where id = " + department.getId());
                    if (!isPresent.next()) {
                        Update("insert into department values" +
                                "('" + department.getId() +
                                "','" + department.getName() +
                                "','" + department.getLocation() +
                                "')"
                        );
                    }
                    else {
                        Update("update department set name = " +
                                "'" + department.getName() +
                                "', LOCATION = '" + department.getLocation() +
                                "' WHERE ID = '" + department.getId() +
                                "'"
                        );
                    }
                    return department;
                } catch (SQLException e) {
                    e.printStackTrace();

                    return null;
                }
                //ОРШВГЙЦРВШГЦЙПВНГЦФПШВГФУПЦШНВРГЩйфцвршунгфкарщцгрвШнцПВШЙЦПВНГЦЙПВГнцпвгнвпнгй
            }

            @Override
            public void delete(Department department) {
                try {
                    Update("delete from department where id = " + department.getId());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
