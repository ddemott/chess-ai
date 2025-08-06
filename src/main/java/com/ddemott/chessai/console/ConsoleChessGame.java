package com.ddemott.chessai.console;

import com.ddemott.chessai.engine.GameEngine;

import java.util.Scanner;

public class ConsoleChessGame {

    public static void main(String[] args) {
        GameEngine gameEngine = new GameEngine(4); // Set the depth as needed

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print(gameEngine.getBoardRepresentation());
            System.out.println("Current turn: " + gameEngine.getCurrentTurn());
            System.out.print("Enter your move (e.g., a2 a3): ");
            
            // Check if scanner has input available
            if (!scanner.hasNextLine()) {
                System.out.println("\nInput stream ended. Exiting game.");
                break;
            }
            
            String move = scanner.nextLine();

            if (move.equalsIgnoreCase("exit")) {
                break;
            }

            String[] positions = move.split(" ");
            if (positions.length != 2) {
                System.out.println("Invalid input. Please enter your move in the format 'a2 a3'.");
                continue;
            }

            boolean moveSuccessful = gameEngine.movePiece(positions[0], positions[1]);
            if (!moveSuccessful) {
                System.out.println("Invalid move. Try again.");
                continue;
            } else {
                System.out.println("Move from " + positions[0] + " to " + positions[1] + " is valid.");
            }

            // AI's turn
            if (gameEngine.getCurrentTurn().equals("Black")) {
                gameEngine.makeAIMove();
            }
        }

        scanner.close();
    }
}
