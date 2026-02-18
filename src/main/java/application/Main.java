package application;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

import java.util.Date;

public class Main {
    public static void main(String[] args) {

        Department department = new Department(1, "Electronics");

        Seller seller = new Seller(
                1,
                "John Brown",
                "john@mail.com",
                new Date(),
                3000.0,
                department
        );

        SellerDao sellerDao = DaoFactory.createSellerDao();

        System.out.println(seller);
    }
}