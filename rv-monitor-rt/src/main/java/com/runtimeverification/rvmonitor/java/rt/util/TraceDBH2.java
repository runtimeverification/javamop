package com.runtimeverification.rvmonitor.java.rt.util;

import javax.sql.rowset.serial.SerialClob;
import java.io.File;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class TraceDBH2 extends TraceDB{

    public TraceDBH2() {
        super();
    }

    public TraceDBH2(String dbFilePath) {
        super(dbFilePath);
    }

    @Override
    public void put(String monitorID, String trace, int length) {
        try {
            insert(monitorID, new SerialClob(trace.toCharArray()), length);
        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    private void insert(String monitorID, Clob trace, int length) {
        final String INSERT_TRACE_SQL = "INSERT INTO traces (monitorID, trace, length ) VALUES (?, ?, ?);";
        try(PreparedStatement preparedStatement = getConnection().prepareStatement(INSERT_TRACE_SQL)) {
            preparedStatement.setString(1, monitorID);
            preparedStatement.setClob(2, trace);
            preparedStatement.setInt(3, length);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    @Override
    public void update(String monitorID, String trace, int length) {
        final String UPDATE_TRACE_SQL = "update traces set trace = ?, length = ? where monitorID = ?;";
        try(PreparedStatement preparedStatement = getConnection().prepareStatement(UPDATE_TRACE_SQL)){
            preparedStatement.setClob(1, new SerialClob(trace.toCharArray()));
            preparedStatement.setInt(2, length);
            preparedStatement.setString(3, monitorID);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int size() {
        return super.size("select count(*) from traces");
    }

    public int uniqueTraces() {
        return super.uniqueTraces("select count(distinct(trace)) from traces");
    }

    public void dump() {
        System.out.println(getDbDir() + "::::traces");
        super.dump(getDbDir()+ File.pathSeparator + "monitor-table.csv", "traces");
    }

    public void createTable() {
        final String createTableSQL = "create table traces (monitorID  varchar(150) primary key, trace clob, length int);";
        try (Statement statement = getConnection().createStatement()) {
            statement.execute(createTableSQL);
        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    public List<Integer> getTraceLengths() {
        return super.getTraceLengths("select length from traces");
    }

    public Map<String, Integer> getTraceFrequencies() {
        return super.getTraceFrequencies("select count(*), trace from traces group by trace");
    }

    public static void main(String[] args) {
        TraceDB traceDB = new TraceDBH2();
        traceDB.createTable();
        System.out.println("Start: " + new Date().toString());
        traceDB.put("fy#"+1, "[a,b,b,c]", 4);
        traceDB.put("fy#"+2, "[a,b,b,c,d,e]", 6);
        traceDB.put("fy#"+3, "[a,b,b,c]", 4);
        for (int i = 4; i < 100000000; i++) {
            traceDB.put("fy#"+i, "[a,b,b,c,d,e]", 6);
            System.out.println(i);
        }
        System.out.println("Filled: " + new Date().toString());
        System.out.println(traceDB.uniqueTraces());
        System.out.println("Queried: " + new Date().toString());
    }
}
