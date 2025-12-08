package com.ddemott.chessai;

public class Player {
	private String name;
	private Side side;

	public Player(String name, Side side) {
		this.name = name;
		this.side = side;
	}

	public String getName() {
		return name;
	}

	public Side getSide() {
		return side;
	}

	// Helper for legacy code support
	public String getColor() {
		return side.toString();
	}
}