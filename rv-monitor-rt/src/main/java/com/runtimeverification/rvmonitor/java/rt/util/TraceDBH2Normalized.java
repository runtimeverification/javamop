package com.runtimeverification.rvmonitor.java.rt.util;

import javax.sql.rowset.serial.SerialClob;
import javax.sql.rowset.serial.SerialException;
import java.io.IOException;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import org.h2.tools.Csv;

public class TraceDB {

    private Connection connection;
    private String jdbcURL = "jdbc:h2:/tmp/tracedb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=TRUE;DEFRAG_ALWAYS=TRUE;LOB_TIMEOUT=30000000;CACHE_SIZE=2048000";
    private String jdbcUsername = "tdb";
    private String jdbcPassword = "";

    // cache of traces and their IDs that we know of, to save trips to the DB
    private Map<Clob, Long> traceIDMap = new HashMap<>();

    // has the DB been cleaned since we last updated its contents?
    private boolean isCleaned = true;

    public TraceDB() {
        this.connection = getConnection();
    }

    public TraceDB(String dbFilePath) {
        this.jdbcURL =  "jdbc:h2:" + dbFilePath + ";DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";
        this.connection = getConnection();
    }

    public void put(String monitorID, String trace, int length) {
        try {
            insert(monitorID, new SerialClob(trace.toCharArray()), length);
        } catch (SQLException e) {
            printSQLException(e);
        }
        isCleaned = false;
    }

    private void insert(String monitorID, Clob trace, int length) {
        // first check if trace is already in the trace table and get its traceID
        long traceID = getTraceID(trace);
        // if trace is in the trace table, add monitorID and traceID to the monitor table
        if (traceID != -1) {
            addMonitor(monitorID, traceID);
        } else {
            // otherwise, add trace and then obtain its traceID
            addTrace(trace, length);
            // TODO: can we get the traceID at the same time as the add (to save one query)?
            traceID = getTraceID(trace);
            // add monitor and its traceID to the monitor table
            addMonitor(monitorID, traceID);
        }
        // we may now have dangling traces?
        isCleaned = false;
    }

    private void addTrace(Clob trace, int length) {
        final String INSERT_TRACE_SQL = "INSERT INTO traces (trace, length ) VALUES (?, ?);";
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(INSERT_TRACE_SQL)) {
            preparedStatement.setClob(1, trace);
            preparedStatement.setInt(2, length);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    private void addMonitor(String monitorID, long traceID) {
        final String INSERT_MONITOR_SQL = "INSERT INTO monitors (monitorID, traceID) VALUES (?, ?);";
        try(PreparedStatement preparedStatement = getConnection().prepareStatement(INSERT_MONITOR_SQL)) {
            preparedStatement.setString(1, monitorID);
            preparedStatement.setLong(2, traceID);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    private long getTraceID(Clob trace) {
        Long id = traceIDMap.get(trace);
        if (id != null) {
            return id;
        }
        long traceID = -1;
        final String TRACEID_QUERY = "select traceID from traces  where trace = ?;";
        try(PreparedStatement statement = getConnection().prepareStatement(TRACEID_QUERY)) {
            statement.setClob(1, trace);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                traceID = rs.getInt(1);
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return traceID;
    }

    public void update(String monitorID, String trace, int length) {
        try {
            SerialClob traceClob = new SerialClob(trace.toCharArray());
            long traceID = getTraceID(traceClob);
            if (traceID != -1) {
                // the trace was seen before, simply update traceID in the monitor table
                updateTraceID(monitorID, traceID);
            } else {
                // if the trace was not seen before, add it and update the traceID in th monitor table
                // TODO: can we get the traceID at the same time as the add (to save one query)?
                addTrace(traceClob, length);
                traceID = getTraceID(traceClob);
                // update the monitor to its new traceID
                updateTraceID(monitorID, traceID);
            }
            // we may now have dangling traces?
            isCleaned = false;
        } catch (SerialException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            printSQLException(e);
        }

    }

    private void updateTraceID(String monitorID, long traceID) {
        final String UPDATE_TRACE_SQL = "update monitors set traceID = ? where monitorID = ?;";
        try(PreparedStatement preparedStatement = getConnection().prepareStatement(UPDATE_TRACE_SQL)){
            preparedStatement.setLong(1, traceID);
            preparedStatement.setString(2, monitorID);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int size() {
        cleanDB();
        int count = -1;
        final String COUNT_QUERY = "select count(*) from traces";
        try(Statement statement =  getConnection().createStatement()){
            ResultSet rs = statement.executeQuery(COUNT_QUERY);
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return count;
    }

    public int uniqueTraces() {
        cleanDB();
        int count = -1;
        final String TRACE_QUERY = "select count(distinct(traceID)) from monitors";
        try (Statement statement = getConnection().createStatement()) {
            ResultSet rs = statement.executeQuery(TRACE_QUERY);
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return count;
    }

    public void dump(String csvDir, String tableName) {
        cleanDB();
        final String SELECT_QUERY = "select * from " + tableName;
        try(PreparedStatement preparedStatement = getConnection().prepareStatement(SELECT_QUERY)){
            ResultSet rs = preparedStatement.executeQuery();
            new Csv().write(csvDir, rs, null);
        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    private void cleanDB() {
        if (isCleaned) {
            return;
        }
        final String DELETE_QUERY = "delete from traces where traceID not in (select distinct(traceID) from monitors);";
        try (Statement statement = getConnection().createStatement()) {
            statement.executeUpdate(DELETE_QUERY);
            isCleaned = true;
        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    public void createTable() {
        final String createTraceTableSQL = "create table traces (traceID  bigint auto_increment primary key, trace clob, length int);";
        final String createMonitorTableSQL = "create table monitors (monitorID  varchar(150) primary key, traceID bigint, foreign key (traceID) references traces(traceID));";
        try (Statement statement = getConnection().createStatement()) {
            statement.execute(createTraceTableSQL);
            statement.execute(createMonitorTableSQL);
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

    public List<Integer> getTraceLengths() {
        cleanDB();
        List<Integer> lengths =  new ArrayList<>();
        final String LENGTHS_QUERY = "select length from traces inner join monitors on traces.traceID = monitors.traceID";
        try (Statement statement = getConnection().createStatement()) {
            ResultSet rs =  statement.executeQuery(LENGTHS_QUERY);
            while (rs.next()) {
                lengths.add(rs.getInt(1));
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return lengths;
    }

    public Map<String, Integer> getTraceFrequencies() {
        cleanDB();
        Map<String, Integer> traceFrequency = new HashMap<>();
        final String FREQUENCY_QUERY = "select count(*), trace from monitors inner join traces on monitors.traceID = traces.traceID group by monitors.traceID ";
        try(Statement statement = getConnection().createStatement()) {
            ResultSet rs = statement.executeQuery(FREQUENCY_QUERY);
            while (rs.next()) {
                traceFrequency.put(rs.getString(2), rs.getInt(1));
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return traceFrequency;
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

    public static void main(String[] args) throws SQLException, IOException {
        System.setProperty("h2.objectCacheMaxPerElementSize", "12288");
        System.setProperty("h2.objectCacheSize", "2048");
        TraceDB traceDB = new TraceDB();
        traceDB.createTable();
        System.out.println("Start: " + new Date());
        traceDB.put("fy#"+1, "[a,b,b,c]", 4);
        traceDB.put("fy#"+2, "[a,b,b,c,d,e]", 6);
        traceDB.put("fy#"+3, "[a,b,b,c,d,e]", 6);
        traceDB.put("fy#"+4, "[a,b,b,c,d,e]", 6);
        traceDB.put("fy#"+5, "[a,b,b,c]", 4);
        traceDB.update("fy#"+5, "[a,b,b,c,c]", 5);
        traceDB.update("fy#"+4, "[a,b,b,c,d,e,f]", 7);
        traceDB.update("fy#"+1, "[a,b,b,c,c]", 5);
        traceDB.update("fy#"+4, "[a,b,b,c,d,e,f,e]", 8);
//        for (int i = 4; i < 1000000; i++) {
//            System.out.println(i);
//            traceDB.put("fy#"+i, "[a,b,b,c,d,e]", 6);
//        }
        System.out.println("Filled: " + new Date());
        System.out.println(traceDB.getTraceFrequencies());
        System.out.println(traceDB.getTraceLengths());
        System.out.println(traceDB.uniqueTraces());
        System.out.println("Queried: " + new Date());

//        Clob trace1 = new SerialClob("[a,b,b,c]".toCharArray());

//        char[] cbuf = new char[(int) trace1.length()];
//        trace1.getCharacterStream().read(cbuf);
//        String s =  new String(cbuf);
//
//        Map<String, Integer> traceFrequency = new HashMap<>();
//        final String FREQUENCY_QUERY = "select traceID, trace from traces  T where  trace = ?;";
//        try(PreparedStatement statement = traceDB.getConnection().prepareStatement(FREQUENCY_QUERY)) {
//            statement.setClob(1, trace1);
//            ResultSet rs = statement.executeQuery();
//            while (rs.next()) {
//                System.out.println("GGGG: " + rs.getString(1) +  ":::" + rs.getClob(2));
//            }
//        } catch (SQLException e) {
//            traceDB.printSQLException(e);
//        }
//        System.out.println("Freq: " + traceFrequency);
//
//        System.out.println("FFF: " + s);
        traceDB.dump("/tmp/trace-table.csv", "traces");
        traceDB.dump("/tmp/monitor-table.csv", "monitors");

//        traceDB.put("fy#"+6, "[a,b,b,c]", 4);
//
//        traceDB.dump("/tmp/trace-table-2.csv", "traces");
//        traceDB.dump("/tmp/monitor-table.-2csv", "monitors");

    }
}
