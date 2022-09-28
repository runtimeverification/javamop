package com.runtimeverification.rvmonitor.java.rt.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class TraceDB {

    private static String jdbcURL = "jdbc:h2:/tmp/tracedb";
    private static String jdbcUsername = "tdb";
    private static String jdbcPassword = "";
    private static final String createTableSQL = "create table traces (\r\n" + "  monitorID  varchar(150) primary key,\r\n" +
            "  trace clob(10k),\r\n" + "  count int\r\n" + "  );";

    public TraceDB() {
        createTable();
    }

    public void createTable() throws SQLException {
        System.out.println(createTableSQL);
        // Step 1: Establishing a Connection
        try (Connection connection = getConnection();
             // Step 2:Create a statement using connection object
             Statement statement = connection.createStatement();) {

            // Step 3: Execute the query or update query
            statement.execute(createTableSQL);
        } catch (SQLException e) {
            // print SQL exception information
            printSQLException(e);
        }
    }

    public static Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return connection;
    }

    public static void printSQLException(SQLException ex) {
        for (Throwable e : ex) {
            if (e instanceof SQLException) {
                e.printStackTrace(System.err);
                System.err.println("SQLState: " + ((SQLException) e).getSQLState());
                System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
                System.err.println("Message: " + e.getMessage());
                Throwable t = ex.getCause();
                while (t != null) {
                    System.out.println("Cause: " + t);
                    t = t.getCause();
                }
            }
        }
    }
}
