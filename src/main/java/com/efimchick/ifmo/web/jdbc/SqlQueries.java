package com.efimchick.ifmo.web.jdbc;

/**
 * Implement sql queries like described
 */
public class SqlQueries {
    //Select all employees sorted by last name in ascending order
    //language=HSQLDB
    String select01 = "SELECT * from EMPLOYEE ORDER BY LASTNAME ";

    //Select employees having no more than 5 characters in last name sorted by last name in ascending order
    //language=HSQLDB
    String select02 = "SELECT * from EMPLOYEE where length(LASTNAME)<6 order by LASTNAME";

    //Select employees having salary no less than 2000 and no more than 3000
    //language=HSQLDB
    String select03 = "SELECT * from EMPLOYEE where SALARY between 2000 and 3000";

    //Select employees having salary no more than 2000 or no less than 3000
    //language=HSQLDB
    String select04 = "SELECT * from EMPLOYEE where SALARY<=2000 OR SALARY>=3000";

    //Select employees assigned to a department and corresponding department name
    //language=HSQLDB
    String select05 = "SELECT LASTNAME,NAME,SALARY from EMPLOYEE E INNER JOIN DEPARTMENT D on E.DEPARTMENT = D.ID";

    //Select all employees and corresponding department name if there is one.
    //Name column containing name of the department "depname".
    //language=HSQLDB
    String select06 = "SELECT LASTNAME,NAME depname,SALARY from EMPLOYEE E LEFT JOIN DEPARTMENT D on E.DEPARTMENT = D.ID";

    //Select total salary pf all employees. Name it "total".
    //language=HSQLDB
    String select07 = "SELECT sum(SALARY) total from EMPLOYEE";

    //Select all departments and amount of employees assigned per department
    //Name column containing name of the department "depname".
    //Name column containing employee amount "staff_size".
    //language=HSQLDB
    String select08 = "SELECT D.NAME depname,count(D.NAME) staff_size FROM EMPLOYEE E INNER JOIN DEPARTMENT D on E.DEPARTMENT = D.ID group by D.NAME";

    //Select all departments and values of total and average salary per department
    //Name column containing name of the department "depname".
    //language=HSQLDB
    String select09 = "SELECT D.NAME depname,sum(E.SALARY) total, avg(E.SALARY) average  FROM EMPLOYEE E INNER JOIN DEPARTMENT D on E.DEPARTMENT = D.ID group by D.NAME";

    //Select all employees and their managers if there is one.
    //Name column containing employee lastname "employee".
    //Name column containing manager lastname "manager".
    //language=HSQLDB
    String select10 = "SELECT E.LASTNAME employee, M.LASTNAME manager FROM EMPLOYEE E LEFT JOIN EMPLOYEE M on E.MANAGER = M.ID";


}