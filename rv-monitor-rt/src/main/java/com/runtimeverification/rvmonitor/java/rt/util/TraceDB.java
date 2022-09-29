package com.runtimeverification.rvmonitor.java.rt.util;

import javax.sql.rowset.serial.SerialClob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.h2.tools.Csv;

public class TraceDB {

    private Connection connection;
    private String jdbcURL = "jdbc:h2:/tmp/tracedb";
    private String jdbcUsername = "tdb";
    private String jdbcPassword = "";

    public TraceDB() {
        this.connection = getConnection();
    }

    public TraceDB(String dbFilePath) {
        this.jdbcURL =  "jdbc:h2:" + dbFilePath;
        this.connection = getConnection();
    }

    public void put(String monitorID, String trace, int count) {
        try {
            insert(monitorID, new SerialClob(trace.toCharArray()), count);
        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    private void insert(String monitorID, Clob trace, int count) {
        final String INSERT_TRACE_SQL = "INSERT INTO traces (monitorID, trace, count) VALUES (?, ?, ?);";
        try(PreparedStatement preparedStatement = getConnection().prepareStatement(INSERT_TRACE_SQL)) {
            preparedStatement.setString(1, monitorID);
            preparedStatement.setClob(2, trace);
            preparedStatement.setInt(3, count);
            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    public void update(String monitorID, String trace, int count) {
        final String UPDATE_TRACE_SQL = "update users set trace = ?, count = ? where id = ?;";
        try(PreparedStatement preparedStatement = getConnection().prepareStatement(UPDATE_TRACE_SQL)){
            preparedStatement.setClob(1, new SerialClob(trace.toCharArray()));
            preparedStatement.setInt(2, count);
            preparedStatement.setString(3, monitorID);
            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void dump(String csvDir) {
        final String SELECT_QUERY = "select * from traces";
        try(PreparedStatement preparedStatement = getConnection().prepareStatement(SELECT_QUERY)){
            System.out.println(preparedStatement);
            ResultSet rs = preparedStatement.executeQuery();
            new Csv().write(csvDir, rs, null);
        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    public void createTable() {
        final String createTableSQL = "create table traces (\r\n" + "  monitorID  varchar(150) primary key,\r\n" +
                "  trace clob(10k),\r\n" + "  count int\r\n" + "  );";
        System.out.println(createTableSQL);
        try (Statement statement = getConnection().createStatement()) {
            statement.execute(createTableSQL);
        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    private Connection getConnection() {
        if (connection != null) {
            return connection;
        }
        try {
            connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
        } catch (SQLException e) {
            printSQLException(e);
        }
        return connection;
    }

    private void printSQLException(SQLException ex) {
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
