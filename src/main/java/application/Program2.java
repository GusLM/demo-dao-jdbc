package application;

import db.DB;
import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.entities.Department;

public class Program2 {

    public static void main(String[] args) {

        DepartmentDao departmentDao = DaoFactory.createDepartmentDao();

        System.out.println("=== TEST 1: Department findById ===");
        Department department = departmentDao.findById(4);
        System.out.println(department);

        System.out.println("\n=== TEST 2: Department insert ===");
        Department newDepartment = new Department(null, "Garden");
        departmentDao.insert(newDepartment);
        System.out.println("Inserted! New id = " + newDepartment.getId());

        System.out.println("\n=== TEST 3: Department update ===");
        department = departmentDao.findById(6);
        department.setName("Fitness");
        departmentDao.update(department);
        System.out.println("The department name has been updated");

        DB.closeConnection();
    }
}
