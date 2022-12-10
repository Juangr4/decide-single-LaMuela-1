package org.lamuela.sqlite3;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLMethods {

    private SQLMethods(){};

    private static final String JDBC_URL = "jdbc:sqlite:src/main/resources/data.db";

    private static final String ErrorMsg1 = "Ha ocurrido un error al cerrar la conexión con la base de datos, porfavor inténtelo de nuevo más tarde";

    private static final String ErrorMsg2 = "Ha ocurrido un error al conectar con la base de datos, porfavor inténtelo de nuevo más tarde";

    public static void initDB(){
        
        java.sql.Connection connection = null;

        Logger logger = Logger.getLogger(SQLMethods.class.getName());

        try{

            connection = DriverManager.getConnection(JDBC_URL);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30); 

            statement.executeUpdate("drop table if exists user");
            statement.executeUpdate("create table user (id integer primary key, discUser string, token string, username string, password string)");

        }catch(SQLException e){

            logger.log(Level.INFO, ErrorMsg2, e); 
            
        }finally{

            try{

                if(connection != null) connection.close();

            }catch(SQLException e){

                logger.log(Level.INFO, ErrorMsg1, e);

            }
        }
    }

    public static void insertUser(String discUser, String token, String username, String password){

        java.sql.Connection connection = null;

        Logger logger = Logger.getLogger(SQLMethods.class.getName());

        try{

            connection = DriverManager.getConnection(JDBC_URL);
            String query = "insert into user (discUser, token, username, password) values (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            statement.setString(1, discUser);
            statement.setString(2, token);
            statement.setString(3, username);
            statement.setString(4, password);
            statement.executeUpdate();

        }
        catch(SQLException e){

            logger.log(Level.INFO, ErrorMsg2, e); 

        }finally{

            try{

                if(connection != null) connection.close();

            }catch(SQLException e){

                logger.log(Level.INFO, ErrorMsg1, e); 

            }
        }

    }

    public static String getTokenByDiscUser(String discUser){

        java.sql.Connection connection = null;

        Logger logger = Logger.getLogger(SQLMethods.class.getName());

        String token = null;

        try{

            connection = DriverManager.getConnection(JDBC_URL);
            PreparedStatement statement = connection.prepareStatement("select token from user where discUser = ?");

            statement.setString(1, discUser);

            ResultSet rs = statement.executeQuery();

            token = rs.getString(1);

        }
        catch(SQLException e){

            logger.log(Level.INFO, ErrorMsg2, e); 

        }finally{

            try{

                if(connection != null) connection.close();

            }catch(SQLException e){

                logger.log(Level.INFO, ErrorMsg1, e); 

            }
        }

        return token;
    }
    
}
