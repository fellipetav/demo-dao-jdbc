package com.example;

import java.util.Date;

import model.entities.Department;
import model.entities.Seller;

public final class App {

    public static void main(String[] args) {

        Department obj = new Department(1, "Books");
        Seller seller = new Seller(1, "Bob", "bob@gmail.com", new Date(), 1000.0, obj);
        System.out.println(seller);
    }
}
