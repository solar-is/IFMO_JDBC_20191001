package com.efimchick.ifmo.web.jdbc.service;

import com.efimchick.ifmo.web.jdbc.domain.Department;
import com.efimchick.ifmo.web.jdbc.domain.Employee;

import java.util.List;

public class ServiceFactory {

    public EmployeeService employeeService(){
        return new EmployeeService() {
            @Override
            public List<Employee> getAllSortByHireDate(Paging paging) {
                return null;
            }

            @Override
            public List<Employee> getAllSortByLastname(Paging paging) {
                return null;
            }

            @Override
            public List<Employee> getAllSortBySalary(Paging paging) {
                return null;
            }

            @Override
            public List<Employee> getAllSortByDepartmentNameAndLastname(Paging paging) {
                return null;
            }

            @Override
            public List<Employee> getByDepartmentSortByHireDate(Department department, Paging paging) {
                return null;
            }

            @Override
            public List<Employee> getByDepartmentSortBySalary(Department department, Paging paging) {
                return null;
            }

            @Override
            public List<Employee> getByDepartmentSortByLastname(Department department, Paging paging) {
                return null;
            }

            @Override
            public List<Employee> getByManagerSortByLastname(Employee manager, Paging paging) {
                return null;
            }

            @Override
            public List<Employee> getByManagerSortByHireDate(Employee manager, Paging paging) {
                return null;
            }

            @Override
            public List<Employee> getByManagerSortBySalary(Employee manager, Paging paging) {
                return null;
            }

            @Override
            public Employee getWithDepartmentAndFullManagerChain(Employee employee) {
                return null;
            }

            @Override
            public Employee getTopNthBySalaryByDepartment(int salaryRank, Department department) {
                return null;
            }
        };
    }
}
