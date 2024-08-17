package com.example;

import java.util.Date;

// import java.util.Date;

import model.dao.DaoFactory;
import model.dao.SellerDao;
// import model.entities.Department;
// import model.entities.Seller;
import model.entities.Department;
import model.entities.Seller;

public final class App {

    public static void main(String[] args) {

        // Department obj = new Department(1, "Books");
        // Seller seller = new Seller(1, "Bob", "bob@gmail.com", new Date(), 1000.0,
        // obj);

        SellerDao sellerDao = DaoFactory.createSellerDao();
        // Assim, meu programa não conhece a implementação, mas apenas a interface.
        // É também uma forma de injeção de independência sem explicitar a
        // implementação.

        System.out.println("=== TEST 1: seller findById ===");
        System.out.println(sellerDao.findById(3));

        System.out.println("\n=== TEST 2: seller findByDepartment ===");
        Department department = new Department(2, null);
        sellerDao.findByDepartment(department).forEach(System.out::println);
        // System.out.println(sellerDao.findByDepartment(new Department(2, null)));

        System.out.println("\n=== TEST 3: seller findAll ===");
        sellerDao.findAll().forEach(System.out::println);

        System.out.println("\n=== TEST 4: seller insert ===");
        Seller seller = new Seller(null,
                "Vendedor Maroto",
                "marotinho@email.com",
                new Date(),
                2000.00,
                department);
        sellerDao.insert(seller);
        System.out.println("Seller was inserted. Id = " + seller.getId());
    }
}
