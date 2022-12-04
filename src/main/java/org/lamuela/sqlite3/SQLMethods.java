package org.lamuela.sqlite3;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLMethods {

    private SQLMethods(){};

    private static final String jdbcUrl = "jdbc:sqlite:src/main/resources/data.db";


    public static void initDB(){
        
        java.sql.Connection connection = null;

        Logger logger = Logger.getLogger(SQLMethods.class.getName());

        try{

            connection = DriverManager.getConnection(jdbcUrl);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30); 

            statement.executeUpdate("drop table if exists user");
            statement.executeUpdate("create table user (id integer primary key, discUser string, token string, username string, password string)");

        }catch(SQLException e){

            logger.log(Level.INFO, "Ha ocurrido un error al conectar con la base de datos, porfavor inténtelo de nuevo más tarde", e); 
            
        }finally{

            try{

                if(connection != null) connection.close();

            }catch(SQLException e){

                logger.log(Level.INFO, "Ha ocurrido un error al cerrar la conexión con la base de datos, porfavor inténtelo de nuevo más tarde", e); 

            }
        }
    }

    public static void insertUser(String discUser, String token, String username, String password){

        String jdbcUrl = "jdbc:sqlite:C:\\Users\\kikov\\Desktop\\EGC\\decide-single-LaMuela-1\\src\\main\\resources\\db\\data.db";
        java.sql.Connection connection = null;

        Logger logger = Logger.getLogger(SQLMethods.class.getName());

        try{

            connection = DriverManager.getConnection(jdbcUrl);
            String query = "insert into user (discUser, token, username, password) values (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            statement.setString(1, discUser);
            statement.setString(2, token);
            statement.setString(3, username);
            statement.setString(4, password);
            statement.executeUpdate();

        }
        catch(SQLException e){

            logger.log(Level.INFO, "Ha ocurrido un error al conectar con la base de datos, porfavor inténtelo de nuevo más tarde", e); 

        }finally{

            try{

                if(connection != null) connection.close();

            }catch(SQLException e){

                logger.log(Level.INFO, "Ha ocurrido un error al cerrar la conexión con la base de datos, porfavor inténtelo de nuevo más tarde", e); 

            }
        }

    }
    
}
