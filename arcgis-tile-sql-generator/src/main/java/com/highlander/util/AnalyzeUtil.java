package com.highlander.util;

import java.sql.ResultSet;

public class AnalyzeUtil {
	public static void main(String[] args) {
		start();
	}

	private static void start() {
		try {
			String sql = "select top 50 * from t_tile_position t where  t.col=201";

			ResultSet rs = DatabaseUtil.executeQuery(sql);
			int preX = 0, preY = 0;
			while (rs.next()) {
				int level = rs.getInt("level");
				int leftTopX = rs.getInt("left_top_x");
				int leftTopY = rs.getInt("left_top_y");

				if (preX == 0) {
					preX = leftTopX;
					preY = leftTopY;
					continue;
				}
				long differX = leftTopX - preX;
				long differY = leftTopY - preY;

				System.out.println(String.format("level:%s\tx:%s\ty:%s", level, differX, differY));

				preX = leftTopX;
				preY = leftTopY;

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
