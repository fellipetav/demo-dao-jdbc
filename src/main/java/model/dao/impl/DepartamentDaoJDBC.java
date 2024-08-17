package model.dao.impl;

import java.util.List;

import db.DB;
import db.DbException;

import java.util.ArrayList;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartamentDaoJDBC implements DepartmentDao {

    // We initialize a Connection [object] to be used always when the
    // [DaoFactory.createDepartmentDao] is called
    private Connection connection;

    // For that, we need a Constructor which requires the object connection as
    // argument
    public DepartamentDaoJDBC(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void insert(Department object) {
        PreparedStatement prepStat = null;

        try {
            connection.setAutoCommit(false);
            prepStat = connection.prepareStatement(
                    "INSERT INTO department "
                            + "(Name) "
                            + "VALUES "
                            + "(?)",
                    Statement.RETURN_GENERATED_KEYS);

            prepStat.setString(1, object.getName());
            int rowsAffected = prepStat.executeUpdate();
            
            ResultSet resultSet = prepStat.getGeneratedKeys();
            if (resultSet.next()) {
                object.setId(resultSet.getInt(1));
            }
            DB.closeResultSet(resultSet);

            System.out.printf("Insert completed: %d rows affected.%n", rowsAffected);

            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
                throw new DbException("Operation failed: " + e.getMessage());
            } catch (SQLException e2) {
                throw new DbException("Operation failed ("
                        + e.getMessage() + ")."
                        + "\nAnd subsequent rollback also failed: "
                        + e2.getMessage());
            }
        } finally {
            DB.closeStatement(prepStat);
        }
    }

    @Override
    public void update(Department object) {
        PreparedStatement preparedStatement = null;
        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(
                "UPDATE department " 
                + "SET Name = ? "
                + "WHERE Id = ?");
                preparedStatement.setString(1, object.getName());
                preparedStatement.setInt(2, object.getId());

                int rowsAffected = preparedStatement.executeUpdate();
                connection.commit();

                System.out.printf("Update successfull: %d rows affected.%n", rowsAffected);

        } catch (SQLException e) {
            try {
                connection.rollback();
                throw new DbException("Operation failed due to error: " + e.getMessage() + ".\nRollback was done!");
            } catch (SQLException e2) {
                throw new DbException("Operation failed due to error: " + e.getMessage() + ".\nRollback attempting also failed due to error: " + e2.getMessage());
            } finally {
                DB.closeStatement(preparedStatement);
            }
        }
    }

    @Override
    public void deleteById(Integer id) {
        PreparedStatement preparedStatement = null;
        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(
                "DELETE FROM department "
                + "WHERE Id = ?");
            preparedStatement.setInt(1, id);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                connection.commit();
                System.out.println("Deletion completed: " + rowsAffected + " rows affected.");
            } else {
                throw new DbException("Delete operation failed: this id doesn't exist.");
            }

        } catch (SQLException e) {
            try {
                connection.rollback();
                throw new DbException("Delete operation failed due to error: " + e.getMessage());
            } catch (SQLException e2) {
                throw new DbException("Operation failed due to error: " + e.getMessage() + "\nRollback also failed due to error: " + e2.getMessage());
            }
        } finally {
            DB.closeStatement(preparedStatement);
        }
    }

    @Override
    public Department findById(Integer id) {
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM department "
                            + "WHERE department.Id = ?");
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Department departmentFoundById = instantiateDepartment(resultSet);
                DB.closeResultSet(resultSet);
                return departmentFoundById;
            } else {
                DB.closeResultSet(resultSet);
                throw new DbException("Error: could not find this department. Id may not exist.");
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(preparedStatement);
        }
    }

    @Override
    public List<Department> findAll() {
        PreparedStatement preparedStatement = null;
        List<Department> departments = new ArrayList<>();

        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM department "
                            + "ORDER BY Id");

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Department department = instantiateDepartment(resultSet);
                departments.add(department);
            }
            DB.closeResultSet(resultSet);
            return departments;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(preparedStatement);
        }
    }

    private Department instantiateDepartment(ResultSet resultSet) throws SQLException {
        Department department = new Department(resultSet.getInt(1), resultSet.getString(2));
        return department;
    }

}
