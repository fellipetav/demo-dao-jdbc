package model.dao.impl;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import com.mysql.cj.protocol.Resultset;

import java.util.HashMap;

import db.DB;
import db.DbException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;

import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao {

    private Connection connection;

    public SellerDaoJDBC(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void insert(Seller object) {
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement(
                    "INSERT INTO seller "
                            + "(Name, Email, BirthDate, BaseSalary, DepartmentId) "
                            + "VALUES "
                            + "(?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(1, object.getName());
            preparedStatement.setString(2, object.getEmail());
            preparedStatement.setDate(
                    3,
                    new java.sql.Date(object.getBirthDate().getTime()));
            preparedStatement.setDouble(4, object.getBaseSalary());
            preparedStatement.setInt(5, object.getDepartment().getId());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    object.setId(resultSet.getInt(1));
                }
                DB.closeResultSet(resultSet);
            } else {
                throw new DbException("Unexpected error while inserting seller.");
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(preparedStatement);
        }
    }

    @Override
    public void update(Seller object) {
        PreparedStatement prepStat = null;
        try {
            prepStat = connection.prepareStatement(
                    "UPDATE seller "
                            + "SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? "
                            + "WHERE Id = ?");

            prepStat.setString(1, object.getName());
            prepStat.setString(2, object.getEmail());
            prepStat.setDate(3, new java.sql.Date(object.getBirthDate().getTime()));
            prepStat.setDouble(4, object.getBaseSalary());
            prepStat.setInt(5, object.getDepartment().getId());
            prepStat.setInt(6, object.getId());

            prepStat.executeUpdate();

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(prepStat);
        }
    }

    @Override
    public void deleteById(Integer id) {
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement(
                    "DELETE FROM seller "
                            + "WHERE Id = ?");

            preparedStatement.setInt(1, id);
            int rowsAffected = preparedStatement.executeUpdate();
            
            if (rowsAffected == 0) {
                throw new DbException("Error: this id doesn't exist.");
            } else {
                System.out.println("Deletion completed.");
            }

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public Seller findById(Integer id) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT seller.*,department.Name as DepName "
                            + "FROM seller INNER JOIN department "
                            + "ON seller.DepartmentId = department.Id "
                            + "WHERE seller.Id = ?");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Department dept = instantiateDepartment(resultSet);
                Seller seller = instantiateSeller(resultSet, dept);
                return seller;
            } else {
                return null;
            }

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(preparedStatement);
            DB.closeResultSet(resultSet);
        }
    }

    @Override
    public List<Seller> findAll() {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT seller.*,department.Name as DepName "
                            + "FROM seller INNER JOIN department "
                            + "ON seller.DepartmentId = department.Id "
                            + "ORDER BY Name");

            resultSet = preparedStatement.executeQuery();

            List<Seller> sellersFromResultSet = new ArrayList<>();

            // Usamos Map com o Id do departamento sendo a Key porque Map não deixa repetir
            // Keys. Então
            // ele é ótimo para fazermos a verificação de ir adicionando departamentos do
            // mesmo id
            // mas sabendo que ele vai ignorar quando for repetido.
            Map<Integer, Department> departments = new HashMap<>();

            while (resultSet.next()) {
                Integer mapKeyAsDepartmentId = resultSet.getInt("DepartmentId");
                Department mapValueAsDepartment = instantiateDepartment(resultSet);

                if (!departments.containsKey(mapKeyAsDepartmentId)) {
                    departments.put(mapKeyAsDepartmentId, mapValueAsDepartment);
                }
                Seller seller = instantiateSeller(resultSet, departments.get(mapKeyAsDepartmentId));
                sellersFromResultSet.add(seller);
            }
            return sellersFromResultSet;

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(preparedStatement);
            DB.closeResultSet(resultSet);
        }
    }

    @Override
    public List<Seller> findByDepartment(Department department) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT seller.*,department.Name as DepName "
                            + "FROM seller INNER JOIN department "
                            + "ON seller.DepartmentId = department.Id "
                            + "WHERE DepartmentId = ? "
                            + "ORDER BY Name");

            preparedStatement.setInt(1, department.getId());
            resultSet = preparedStatement.executeQuery();

            List<Seller> sellersFromResultSet = new ArrayList<>();

            // Usamos Map com o Id do departamento sendo a Key porque Map não deixa repetir
            // Keys. Então
            // ele é ótimo para fazermos a verificação de ir adicionando departamentos do
            // mesmo id
            // mas sabendo que ele vai ignorar quando for repetido.
            Map<Integer, Department> departments = new HashMap<>();

            while (resultSet.next()) {
                Integer mapKeyAsDepartmentId = resultSet.getInt("DepartmentId");
                Department mapValueAsDepartment = instantiateDepartment(resultSet);

                if (!departments.containsKey(mapKeyAsDepartmentId)) {
                    departments.put(mapKeyAsDepartmentId, mapValueAsDepartment);
                }
                Seller seller = instantiateSeller(resultSet, departments.get(mapKeyAsDepartmentId));
                sellersFromResultSet.add(seller);
            }
            return sellersFromResultSet;

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(preparedStatement);
            DB.closeResultSet(resultSet);
        }
    }

    private Department instantiateDepartment(ResultSet resultSet) throws SQLException {
        Department dept = new Department(
                resultSet.getInt("DepartmentId"),
                resultSet.getString("DepName"));
        return dept;
    }

    private Seller instantiateSeller(ResultSet resultSet, Department dept) throws SQLException {
        Seller seller = new Seller(
                resultSet.getInt("Id"),
                resultSet.getString("Name"),
                resultSet.getString("Email"),
                resultSet.getDate("BirthDate"),
                resultSet.getDouble("BaseSalary"),
                dept);
        return seller;
    }

}
