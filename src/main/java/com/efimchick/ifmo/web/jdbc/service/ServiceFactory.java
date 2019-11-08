package com.efimchick.ifmo.web.jdbc.service;

import com.efimchick.ifmo.web.jdbc.domain.Department;
import com.efimchick.ifmo.web.jdbc.domain.Employee;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ServiceFactory {
    private ServiceDataAndUtils data = new ServiceDataAndUtils();

    private List<Employee> getPageOfEmployeeList(Paging paging, List<Employee> list) {
        int left = paging.itemPerPage * (paging.page - 1);
        int right = paging.itemPerPage * paging.page;
        return list.subList(left, Math.min(right, list.size()));
    }

    private List<Employee> getEmployeesByDepartment(Department department) {
        return data.employeesWithoutChain.stream()
                .filter(employee -> employee.getDepartment() != null)
                .filter(employee -> employee.getDepartment().getId().equals(department.getId()))
                .collect(Collectors.toList());
    }

    private List<Employee> getEmployeesByManager(Employee manager) {
        return data.employeesWithoutChain.stream()
                .filter(employee -> employee.getManager() != null)
                .filter(employee -> employee.getManager().getId().equals(manager.getId()))
                .collect(Collectors.toList());
    }

    private List<Employee> sortEmployeesByHire(List<Employee> employees) {
        employees.sort(Comparator.comparing(Employee::getHired));
        return employees;
    }

    private List<Employee> sortEmployeesByLastName(List<Employee> employees) {
        employees.sort(Comparator.comparing(e -> e.getFullName().getLastName()));
        return employees;
    }

    private List<Employee> sortEmployeesBySalary(List<Employee> employees) {
        employees.sort(Comparator.comparing(Employee::getSalary));
        return employees;
    }

    private List<Employee> sortEmployeesByDepartmentAndLastName(List<Employee> employees) {
        employees.sort((e1, e2) -> {
            if (e1.getDepartment() == null) {
                return -1;
            }
            if (e2.getDepartment() == null) {
                return 1;
            }
            int res = e1.getDepartment().getName().compareTo(e2.getDepartment().getName());
            if (res != 0) {
                return res;
            }
            return e1.getFullName().getLastName().compareTo(e2.getFullName().getLastName());
        });
        return employees;
    }

    public EmployeeService employeeService() {
        return new EmployeeService() {
            @Override
            public List<Employee> getAllSortByHireDate(Paging paging) {
                return getPageOfEmployeeList(paging, sortEmployeesByHire(data.employeesWithoutChain));
            }

            @Override
            public List<Employee> getAllSortByLastname(Paging paging) {
                return getPageOfEmployeeList(paging, sortEmployeesByLastName(data.employeesWithoutChain));
            }

            @Override
            public List<Employee> getAllSortBySalary(Paging paging) {
                return getPageOfEmployeeList(paging, sortEmployeesBySalary(data.employeesWithoutChain));
            }

            @Override
            public List<Employee> getAllSortByDepartmentNameAndLastname(Paging paging) {
                return getPageOfEmployeeList(paging, sortEmployeesByDepartmentAndLastName(data.employeesWithoutChain));
            }

            @Override
            public List<Employee> getByDepartmentSortByHireDate(Department department, Paging paging) {
                return getPageOfEmployeeList(paging, sortEmployeesByHire(getEmployeesByDepartment(department)));
            }

            @Override
            public List<Employee> getByDepartmentSortBySalary(Department department, Paging paging) {
                return getPageOfEmployeeList(paging, sortEmployeesBySalary(getEmployeesByDepartment(department)));
            }

            @Override
            public List<Employee> getByDepartmentSortByLastname(Department department, Paging paging) {
                return getPageOfEmployeeList(paging, sortEmployeesByLastName(getEmployeesByDepartment(department)));
            }

            @Override
            public List<Employee> getByManagerSortByLastname(Employee manager, Paging paging) {
                return getPageOfEmployeeList(paging, sortEmployeesByLastName(getEmployeesByManager(manager)));
            }

            @Override
            public List<Employee> getByManagerSortByHireDate(Employee manager, Paging paging) {
                return getPageOfEmployeeList(paging, sortEmployeesByHire(getEmployeesByManager(manager)));
            }

            @Override
            public List<Employee> getByManagerSortBySalary(Employee manager, Paging paging) {
                return getPageOfEmployeeList(paging, sortEmployeesBySalary(getEmployeesByManager(manager)));
            }

            @Override
            public Employee getWithDepartmentAndFullManagerChain(Employee employee) {
                for(Employee e : data.employeesWithChain){
                    if (e.getId().equals(employee.getId())){
                        return e;
                    }
                }
                return null;
            }

            @Override
            public Employee getTopNthBySalaryByDepartment(int salaryRank, Department department) {
                List<Employee> employees = getEmployeesByDepartment(department);
                employees.sort((e1, e2) -> e2.getSalary().compareTo(e1.getSalary()));
                return employees.get(salaryRank-1);
            }
        };
    }
}