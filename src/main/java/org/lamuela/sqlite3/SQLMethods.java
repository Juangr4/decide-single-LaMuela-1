package org.lamuela.sqlite3;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLMethods {

    public static void main(String[] args){
        String jdbcUrl = "jdbc:sqlite:C:\\Users\\kikov\\Desktop\\EGC\\decide-single-LaMuela-1\\src\\main\\resources\\db\\data.db";
        java.sql.Connection connection = null;

        try{

            connection = DriverManager.getConnection(jdbcUrl);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30); 

            statement.executeUpdate("drop table if exists user");
            statement.executeUpdate("create table user (id integer primary key, discUser string, token string, username string, password string)");

        }catch(SQLException e){

            System.out.println("Error connecting to SQL Database");
            e.printStackTrace();

        }finally{

            try{

                if(connection != null) connection.close();

            }catch(SQLException e){

                System.out.println("Error connecting to SQL Database");
                e.printStackTrace();
            }
        }
    }

    public static void insertUser(String discUser, String token, String username, String password){

        String jdbcUrl = "jdbc:sqlite:C:\\Users\\kikov\\Desktop\\EGC\\decide-single-LaMuela-1\\src\\main\\resources\\db\\data.db";
        java.sql.Connection connection = null;

        try{

            connection = DriverManager.getConnection(jdbcUrl);
            String query = "insert into user (discUser, token, username, password) values (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            statement.setString(1, discUser);
            statement.setString(2, token);
            statement.setString(3, username);
            statement.setString(4, password);
            statement.executeUpdate();

        }catch(SQLException e){

            System.out.println("Error connecting to SQL Database");
            e.printStackTrace();

        }finally{

            try{

                if(connection != null) connection.close();

            }catch(SQLException e){

                System.out.println("Error connecting to SQL Database");
                e.printStackTrace();
            }
        }

    }
    
}
