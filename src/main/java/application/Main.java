package application;

import db.DB;
import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

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
                null, "Clark Kent", "superman@mail.com", new Date(), 4200.0, department
        );
        sellerDao.insert(newSeller);
        System.out.println("Inserted! New id = " + newSeller.getId());

        System.out.println("\n=== TEST 5: Seller update ===");
        seller = sellerDao.findById(1);
        seller.setName("Bruce Wayne");
        seller.setBaseSalary(7000.0);
        seller.setEmail("batman@batmail.com");
        sellerDao.update(seller);
        System.out.println("Seller updated!");

        System.out.println("\n=== TEST 6: Seller deleteById ===");
        System.out.print("Enter id for delete test: ");
        int id = scanner.nextInt();
        sellerDao.deleteById(id);
        System.out.println("Delete completed");

        System.out.println("\n=== FINAL SELLER LIST ===");
        sellerList.forEach(System.out::println);

        scanner.close();
        DB.closeConnection();
    }
}