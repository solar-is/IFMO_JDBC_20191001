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

public class ServiceFactory {

    private ResultSet getResultSetOfExecute(String SQL) throws SQLException {
        ConnectionSource connectionSource = ConnectionSource.instance();
        Connection connection = connectionSource.createConnection();
        return connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE).executeQuery(SQL);
    }

    private List<Employee> getEmployeeListByResultSet(ResultSet res) throws SQLException {
        res.beforeFirst();
        List<Employee> ans = new ArrayList<>();
        while (res.next()) {
            ans.add(employeeRowMapper(res,true));
        }
        return ans;
    }

    private Employee employeeRowMapper(ResultSet resultSet, boolean isFirstLevel) {
        Employee cur = null;
        try {
            String managerId = resultSet.getString("MANAGER");
            if (!isFirstLevel){
                managerId = null;
            }
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
                    managerId == null ? null : getEmployeeById(new BigInteger(managerId)),
                    departmentId == null ? null : getDepartmentById(new BigInteger(departmentId))
            );
        } catch (SQLException ignored) {
        }
        return cur;
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
                    managerId == null ? null : getEmployeeByIdWithChain(new BigInteger(managerId)),
                    departmentId == null ? null : getDepartmentById(new BigInteger(departmentId))
            );
        } catch (SQLException ignored) {
        }
        return cur;
    }

    private Employee getEmployeeByIdWithChain(BigInteger Id) {
        try {
            ResultSet res = getResultSetOfExecute("select * from employee where id = " + Id);
            res.next();
            return employeeRowMapper(res);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Employee getEmployeeById(BigInteger Id) {
        try {
            ResultSet res = getResultSetOfExecute("select * from employee where id = " + Id);
            res.next();
            return employeeRowMapper(res,false);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Department getDepartmentById(BigInteger Id) {
        try {
            ResultSet res = getResultSetOfExecute("select * from department where id = " + Id);
            return getDepartmentListByResultSet(res).get(0);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
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

    private List<Department> getDepartmentListByResultSet(ResultSet res) throws SQLException {
        res.beforeFirst();
        List<Department> ans = new ArrayList<>();
        while (res.next()) {
            ans.add(departmentRowMapper(res));
        }
        return ans;
    }


    public EmployeeService employeeService() {
        return new EmployeeService() {
            @Override
            public List<Employee> getAllSortByHireDate(Paging paging) {
                try {
                    ResultSet res = getResultSetOfExecute(
                            "select * from employee order by hiredate" +
                                    " limit " + paging.itemPerPage +
                                    " offset " + paging.itemPerPage * (paging.page - 1)
                    );
                    return getEmployeeListByResultSet(res);
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public List<Employee> getAllSortByLastname(Paging paging) {
                try {
                    ResultSet res = getResultSetOfExecute(
                            "select * from employee order by lastname" +
                                    " limit " + paging.itemPerPage +
                                    " offset " + paging.itemPerPage * (paging.page - 1)
                    );
                    return getEmployeeListByResultSet(res);
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public List<Employee> getAllSortBySalary(Paging paging) {
                try {
                    ResultSet res = getResultSetOfExecute(
                            "select * from employee order by salary" +
                                    " limit " + paging.itemPerPage +
                                    " offset " + paging.itemPerPage * (paging.page - 1)
                    );
                    return getEmployeeListByResultSet(res);
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public List<Employee> getAllSortByDepartmentNameAndLastname(Paging paging) {
                try {
                    ResultSet res = getResultSetOfExecute(
                            "select * from employee left join department on " +
                                    " employee.department = department.id order by department.name,employee.lastname " +
                                    " limit " + paging.itemPerPage +
                                    " offset " + paging.itemPerPage * (paging.page - 1)
                    );
                    return getEmployeeListByResultSet(res);
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public List<Employee> getByDepartmentSortByHireDate(Department department, Paging paging) {
                try {
                    ResultSet res = getResultSetOfExecute(
                            "select * from employee where department = "+department.getId()+"order by hiredate" +
                                    " limit " + paging.itemPerPage +
                                    " offset " + paging.itemPerPage * (paging.page - 1)
                    );
                    return getEmployeeListByResultSet(res);
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public List<Employee> getByDepartmentSortBySalary(Department department, Paging paging) {
                try {
                    ResultSet res = getResultSetOfExecute(
                            "select * from employee where department = "+department.getId()+"order by salary" +
                                    " limit " + paging.itemPerPage +
                                    " offset " + paging.itemPerPage * (paging.page - 1)
                    );
                    return getEmployeeListByResultSet(res);
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public List<Employee> getByDepartmentSortByLastname(Department department, Paging paging) {
                try {
                    ResultSet res = getResultSetOfExecute(
                            "select * from employee where department = "+department.getId()+"order by lastname" +
                                    " limit " + paging.itemPerPage +
                                    " offset " + paging.itemPerPage * (paging.page - 1)
                    );
                    return getEmployeeListByResultSet(res);
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public List<Employee> getByManagerSortByLastname(Employee manager, Paging paging) {
                try {
                    ResultSet res = getResultSetOfExecute(
                            "select * from employee where manager = "+manager.getId()+"order by lastname" +
                                    " limit " + paging.itemPerPage +
                                    " offset " + paging.itemPerPage * (paging.page - 1)
                    );
                    return getEmployeeListByResultSet(res);
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public List<Employee> getByManagerSortByHireDate(Employee manager, Paging paging) {
                try {
                    ResultSet res = getResultSetOfExecute(
                            "select * from employee where manager = "+manager.getId()+"order by hiredate" +
                                    " limit " + paging.itemPerPage +
                                    " offset " + paging.itemPerPage * (paging.page - 1)
                    );
                    return getEmployeeListByResultSet(res);
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public List<Employee> getByManagerSortBySalary(Employee manager, Paging paging) {
                try {
                    ResultSet res = getResultSetOfExecute(
                            "select * from employee where manager = "+manager.getId()+"order by salary" +
                                    " limit " + paging.itemPerPage +
                                    " offset " + paging.itemPerPage * (paging.page - 1)
                    );
                    return getEmployeeListByResultSet(res);
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public Employee getWithDepartmentAndFullManagerChain(Employee employee) {
                try {
                    ResultSet resultSet = getResultSetOfExecute("select * from employee where id = " + employee.getId());
                    resultSet.next();
                    return employeeRowMapper(resultSet);
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public Employee getTopNthBySalaryByDepartment(int salaryRank, Department department) {
                try {
                    ResultSet res = getResultSetOfExecute(
                            "select * from employee where department = "+department.getId()+"order by salary desc" +
                                    " limit 1" +
                                    " offset " + (salaryRank-1)
                    );
                    return getEmployeeListByResultSet(res).get(0);
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }
}