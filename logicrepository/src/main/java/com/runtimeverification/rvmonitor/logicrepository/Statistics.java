package com.runtimeverification.rvmonitor.logicrepository;

import java.sql.*;

public class Statistics {
	static boolean enabled = false;
	static boolean install = false;

	static String server = Configuration.getServerName();
	static String userid = Configuration.getID();
	static String passwd = Configuration.getPassword();
	static String database = Configuration.getDatabaseName();

	static public boolean init() {
		if (Configuration.isStatisticsOn())
			enabled = true;
		else {
			enabled = false;
			return true;
		}

		server = Configuration.getServerName();
		userid = Configuration.getID();
		passwd = Configuration.getPassword();
		database = Configuration.getDatabaseName();

		Connection con = null;
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();

			con = DriverManager.getConnection("jdbc:mysql://" + server + "/" + database, userid, passwd);

			if (!con.isClosed()) {
				if (Statistics.install) {
					Statement s = con.createStatement();

					String tableQuery = "create table LogicRepositoryStat (`client_name` varchar(40) NOT NULL DEFAULT '', `logic_name` varchar(40) not null default '', `count` int (11) not null default '0', PRIMARY KEY (`client_name`, `logic_name`)) ENGINE=InnoDB DEFAULT CHARSET=latin1";
					s.executeUpdate(tableQuery);
				}

				enabled = true;
			}

		} catch (Exception e) {
			enabled = false;

		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
			}
		}

		return enabled;
	}

	static public boolean increase(String client_name, String logic_name) {
		if (!enabled)
			return false;

		Connection con = null;
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			con = DriverManager.getConnection("jdbc:mysql://" + server + "/" + database, userid, passwd);

			if (!con.isClosed()) {
				Statement s = con.createStatement();
				int count;
				String updateQuery = "update LogicRepositoryStat set count = count + 1 where client_name='" + client_name + "' and logic_name='"
						+ logic_name + "'";
				String insertQuery = "insert into LogicRepositoryStat (client_name, logic_name) values ('" + client_name + "', '" + logic_name + "')";

				count = s.executeUpdate(updateQuery);

				if (count != 1) {
					count = s.executeUpdate(insertQuery);
					count = s.executeUpdate(updateQuery);
					if (count != 1) {
						enabled = false;
						return false;
					}
				}

				return true;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
			}
		}
		enabled = false;
		return false;
	}

	static public int getClientAndLogicCount(String client_name, String logic_name) {
		if (!enabled)
			return -1;

		Connection con = null;
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			con = DriverManager.getConnection("jdbc:mysql://" + server + "/" + database, userid, passwd);

			if (!con.isClosed()) {
				Statement s = con.createStatement();
				String selectQuery = "select count from LogicRepositoryStat where client_name = '" + client_name + "' and logic_name = '" + logic_name
						+ "'";

				s.executeQuery(selectQuery);
				ResultSet rs = s.getResultSet();
				int count = -1;
				while (rs.next()) {
					count = rs.getInt("count");
				}

				return count;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
			}
		}
		enabled = false;
		return -1;
	}

	static public int getClientCount(String client_name) {
		if (!enabled)
			return -1;

		Connection con = null;
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			con = DriverManager.getConnection("jdbc:mysql://" + server + "/" + database, userid, passwd);

			if (!con.isClosed()) {
				Statement s = con.createStatement();
				String selectQuery = "select sum(count) as total from LogicRepositoryStat where client_name = '" + client_name + "'";

				s.executeQuery(selectQuery);
				ResultSet rs = s.getResultSet();
				int count = -1;
				while (rs.next()) {
					count = rs.getInt("total");
				}

				return count;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
			}
		}
		enabled = false;
		return -1;
	}

	static public int getLogicCount(String logic_name) {
		if (!enabled)
			return -1;

		Connection con = null;
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			con = DriverManager.getConnection("jdbc:mysql://" + server + "/" + database, userid, passwd);

			if (!con.isClosed()) {
				Statement s = con.createStatement();
				String selectQuery = "select sum(count) as total from LogicRepositoryStat where logic_name = '" + logic_name + "'";

				s.executeQuery(selectQuery);
				ResultSet rs = s.getResultSet();
				int count = -1;
				while (rs.next()) {
					count = rs.getInt("total");
				}

				return count;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
			}
		}
		enabled = false;
		return -1;
	}

	static public int getTotalCount() {
		if (!enabled)
			return -1;

		Connection con = null;
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			con = DriverManager.getConnection("jdbc:mysql://" + server + "/" + database, userid, passwd);

			if (!con.isClosed()) {
				Statement s = con.createStatement();
				String selectQuery = "select sum(count) as total from LogicRepositoryStat";

				s.executeQuery(selectQuery);
				ResultSet rs = s.getResultSet();
				int count = -1;
				while (rs.next()) {
					count = rs.getInt("total");
				}

				return count;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
			}
		}
		enabled = false;
		return -1;
	}
}
