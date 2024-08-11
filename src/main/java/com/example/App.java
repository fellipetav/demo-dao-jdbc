package com.example;

// import java.util.Date;

import model.dao.DaoFactory;
import model.dao.SellerDao;
// import model.entities.Department;
// import model.entities.Seller;

public final class App {

    public static void main(String[] args) {

        // Department obj = new Department(1, "Books");
        // Seller seller = new Seller(1, "Bob", "bob@gmail.com", new Date(), 1000.0, obj);

        SellerDao sellerDao = DaoFactory.createSellerDao(); 
        // Assim, meu programa não conhece a implementação, mas apenas a interface.
        // É também uma forma de injeção de independência sem explicitar a implementação.

        System.out.println("=== TEST 1: seller findById ===");
        System.out.println(sellerDao.findById(3));
    }
}
