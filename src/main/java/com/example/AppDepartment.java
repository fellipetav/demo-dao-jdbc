package com.example;

import java.util.List;
import java.util.Scanner;

import model.dao.DaoFactory;
import model.dao.DepartmentDao;

import model.entities.Department;

public final class AppDepartment {
    public static void main(String[] args) {
        
        DepartmentDao departmentDao = DaoFactory.createDepartmentDao();

        System.out.println("=== TEST 1: department findById ===");
        Department department = departmentDao.findById(3);
        System.out.println(department);

        System.out.println("\n=== TEST 2: department findAll ===");
        List<Department> departmentsFromDao = departmentDao.findAll();
        departmentsFromDao.forEach(System.out::println);

        System.out.println("\n=== TEST 3: department insert ===");
        Department object = new Department(null, "Health");
        departmentDao.insert(object);

        System.out.println("\n=== TEST 4: department update ===");
        Department departmentToBeUpdated = departmentDao.findById(10);
        departmentToBeUpdated.setName("Lux furniture");
        departmentDao.update(departmentToBeUpdated);

        System.out.println("\n=== TEST 5: department delete ===");
        Scanner input = new Scanner(System.in);
        System.out.print("Which department do you want to delete? (insert 'id'): ");
        int departmentIdToBeDeleted = input.nextInt();

        departmentDao.deleteById(departmentIdToBeDeleted);

        input.close();
    }

}
