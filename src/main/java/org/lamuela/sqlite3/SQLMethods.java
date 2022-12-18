package org.lamuela.sqlite3;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLMethods {

    private SQLMethods(){}

    private static final String JDBC_URL = "jdbc:sqlite:data.db";

    public static void initDB(){
        try(Connection connection = DriverManager.getConnection(JDBC_URL)) {
            try(Statement statement = connection.createStatement()) {
                statement.executeUpdate("create table if not exists user (id integer primary key, discUser string, token string, username string, password string)");
            }
        } catch (SQLException e) {
            Logger.getLogger("Sqlite").log(Level.INFO, "No se ha podido crear la tabla de usuarios", e);
        }
    }

    public static void insertUser(String discUser, String token, String username, String password){
        try(Connection connection = DriverManager.getConnection(JDBC_URL)) {
            try(PreparedStatement statement = connection.prepareStatement("insert into user (discUser, token, username, password) values (?, ?, ?, ?)")) {
                statement.setString(1, discUser);
                statement.setString(2, token);
                statement.setString(3, username);
                statement.setString(4, password);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            Logger.getLogger("Sqlite").log(Level.INFO, "No se ha podido insertar el nuevo usuario usuario", e);
        }
    }

    public static String getTokenByDiscUser(String discUser){
        try(Connection connection = DriverManager.getConnection(JDBC_URL)) {
            String token;
            try(PreparedStatement statement = connection.prepareStatement("select token from user where discUser = ?")) {
                statement.setString(1, discUser);
                token = statement.executeQuery().getString(1);
            }
            return token;
        } catch (SQLException e) {
            return null;
        }
    }
    
}
