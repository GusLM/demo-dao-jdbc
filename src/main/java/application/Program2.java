package application;

import db.DB;
import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.entities.Department;

public class Program2 {

    public static void main(String[] args) {

        DepartmentDao departmentDao = DaoFactory.createDepartmentDao();

        System.out.println("=== TEST 1: Department insert ===");
        Department department = new Department(null, "Mobile");
        departmentDao.insert(department);
        System.out.println("Inserted! New id = " + department.getId());

        DB.closeConnection();
    }
}
