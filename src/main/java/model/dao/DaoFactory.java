package model.dao;

import db.DB;
import model.dao.impl.DepartamentDaoJDBC;
import model.dao.impl.SellerDaoJDBC;

public class DaoFactory {

    /** This method exposes the interface type so as the program
     * does not expose its implementation.
     * @return an instance of the SellerDaoJDBC implementation.
     */
    public static SellerDao createSellerDao() {
        return new SellerDaoJDBC(DB.getConnection());
    }

    public static DepartmentDao createDepartmentDao(){
        return new DepartamentDaoJDBC(DB.getConnection());
    }

}
