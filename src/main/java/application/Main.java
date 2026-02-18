package application;

import db.DB;
import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

import java.util.Date;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        SellerDao sellerDao = DaoFactory.createSellerDao();

        System.out.println("=== TEST 1: Seller findById ===");
        Seller seller = sellerDao.findById(3);
        System.out.println(seller);

        System.out.println("\n=== TEST 2: Seller findByDepartment ===");
        Department department = new Department(2, null);
        List<Seller> sellerList = sellerDao.findByDepartment(department);
        sellerList.forEach(System.out::println);

        System.out.println("\n=== TEST 3: Seller findAll ===");
        sellerList = sellerDao.findAll();
        sellerList.forEach(System.out::println);

        System.out.println("\n=== TEST 4: Seller insert ===");
        Seller newSeller = new Seller(
                null, "Johnny Bravo", "johnnyb@mail.com", new Date(), 3200.0, department
        );
        sellerDao.insert(newSeller);
        System.out.println("Inserted! New id = " + newSeller.getId());

        DB.closeConnection();
    }
}