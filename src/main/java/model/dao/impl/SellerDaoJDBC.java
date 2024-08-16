package model.dao.impl;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import db.DB;
import db.DbException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'insert'");
    }

    @Override
    public void update(Seller object) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public void deleteById(Seller object) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteById'");
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
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'findAll'");
        return null;
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
