package Database;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.sql.ordering.antlr.Factory;
import org.springframework.data.jpa.provider.HibernateUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.sql.*;

public class DBConnection {
    private static Connection connection;


    private static final String DB_USER = "root";
    private static final String DB_PASS = "password";


    private DBConnection() throws SQLException {

    }

    public static Connection getConnection() throws SQLException {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/search_engine?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Tomsk&allowPublicKeyRetrieval=true&useSSL=false&rewriteBatchedStatements=true"
                        , DB_USER, DB_PASS);
                connection.setAutoCommit(true);
                return connection;
            } catch (SQLException exception) {
                exception.printStackTrace();
            }

        }
        return connection;
    }
}