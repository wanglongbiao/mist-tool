package com.highlander.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DatabaseUtil {

    private static String url = "jdbc:sqlserver://172.16.60.93;databaseName=vts_base";
	private static String user = "sa";
	private static String password = "Admin123";

	public static ResultSet executeQuery(String sql) {
		try {
			// Thread.currentThread().sleep(1000);
			Connection con = DriverManager.getConnection(url, user, password);
			PreparedStatement ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();

			return rs;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;

	}
}
