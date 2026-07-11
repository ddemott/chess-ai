package com.ddemott.chessai.ai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Manages standard chess opening lines to guide the AI in the early game.
 */
public class OpeningBook {
	// Map from move history string to list of possible next moves
	// History format: "e4 e5 Nf3" (space separated)
	private final Map<String, List<String>> book;
	private final Random random;

	public OpeningBook() {
		this.book = new HashMap<>();
		this.random = new Random();
		initializeBook();
	}

	/**
	 * getMove returns a random move from the book for the given history, or null if
	 * no book move is available.
	 */
	public String getMove(String moveHistory) {
		String key = moveHistory.trim();
		if (book.containsKey(key)) {
			List<String> moves = book.get(key);
			if (moves != null && !moves.isEmpty()) {
				return moves.get(random.nextInt(moves.size()));
			}
		}
		return null;
	}

	private void addLine(String line) {
		String[] moves = line.trim().split(" ");
		StringBuilder history = new StringBuilder();

		for (String move : moves) {
			String currentHistory = history.toString().trim();

			book.putIfAbsent(currentHistory, new ArrayList<>());
			List<String> candidates = book.get(currentHistory);

			// Avoid duplicates
			if (!candidates.contains(move)) {
				candidates.add(move);
			}

			if (history.length() > 0) {
				history.append(" ");
			}
			history.append(move);
		}
	}

	private void initializeBook() {
		// --- Open Games (1. e4 e5) ---

		// Ruy Lopez (Spanish)
		addLine("e4 e5 Nf3 Nc6 Bb5 a6 Ba4 Nf6 O-O Be7"); // Closed
		addLine("e4 e5 Nf3 Nc6 Bb5 a6 Bxc6"); // Exchange
		addLine("e4 e5 Nf3 Nc6 Bb5 Nf6"); // Berlin Defense

		// Italian Game
		addLine("e4 e5 Nf3 Nc6 Bc4 Bc5 c3 Nf6"); // Giuoco Piano
		addLine("e4 e5 Nf3 Nc6 Bc4 Nf6 d3"); // Two Knights

		// Vienna Game (White defends e4 with Nc3)
		addLine("e4 e5 Nc3 Nc6 f4");
		addLine("e4 e5 Nc3 Nf6 g3");

		// Petrov's Defense (Black counter-attacks e4)
		addLine("e4 e5 Nf3 Nf6 Nxe5 d6 Nf3 Nxe4");

		// Philidor Defense (Black defends e5 with d6)
		addLine("e4 e5 Nf3 d6 d4 exd4");

		// Sicilian Defense (1. e4 c5)
		addLine("e4 c5 Nf3 d6 d4 cxd4 Nxd4 Nf6 Nc3 a6"); // Najdorf
		addLine("e4 c5 Nf3 Nc6 d4 cxd4 Nxd4 Nf6 Nc3 e5"); // Sveshnikov
		addLine("e4 c5 Nf3 e6 d4 cxd4 Nxd4 a6"); // Kan/Paulsen
		addLine("e4 c5 Nc3 Nc6 g3"); // Closed Sicilian

		// French Defense (1. e4 e6)
		addLine("e4 e6 d4 d5 Nc3 Bb4"); // Winawer
		addLine("e4 e6 d4 d5 Nd2 Nf6"); // Tarrasch
		addLine("e4 e6 d4 d5 e5 c5 c3"); // Advance

		// Caro-Kann (1. e4 c6)
		addLine("e4 c6 d4 d5 Nc3 dxe4 Nxe4 Bf5"); // Classical
		addLine("e4 c6 d4 d5 e5 Bf5"); // Advance

		// --- Closed Games (1. d4 d5) ---

		// Queen's Gambit
		addLine("d4 d5 c4 e6 Nc3 Nf6 Bg5 Be7"); // QGD Orthodox
		addLine("d4 d5 c4 c6 Nf3 Nf6"); // Slav
		addLine("d4 d5 c4 dxc4 e4"); // Queen's Gambit Accepted

		// Indian Defenses (1. d4 Nf6)
		addLine("d4 Nf6 c4 g6 Nc3 Bg7 e4 d6"); // King's Indian
		addLine("d4 Nf6 c4 e6 Nf3 b6"); // Queen's Indian
		addLine("d4 Nf6 c4 e6 Nc3 Bb4"); // Nimzo-Indian

		// Other d4
		addLine("d4 f5 c4 Nf6 g3"); // Dutch Defense

		// --- Flank Openings ---
		addLine("c4 e5 Nc3 Nf6 g3"); // English
		addLine("c4 Nf6 Nc3 e6 Nf3 d5"); // Anglo-Indian / QGD
		addLine("c4 Nf6 Nc3 g6"); // King's Indian English
		addLine("Nf3 d5 c4 e6"); // Reti
		addLine("b3 e5 Bb2 Nc6"); // Nimzo-Larsen
	}
}
