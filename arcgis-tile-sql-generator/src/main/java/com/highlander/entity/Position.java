package com.highlander.entity;

public class Position {
	public double x;
	public double y;

	public Position() {

	}

	public Position(double x, double y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		// System.out.println("x:" + x);
		// System.out.println("y:" + y);
		return "x:" + x + "\ny:" + y;
	}
}
