package model.dao;

import model.entities.Department;
import java.util.List;

public interface DepartmentDao {

    void insert(Department object);

    void update(Department object);

    void deleteById(Department object);

    Department findById(Integer id);

    List<Department> findAll();

}
