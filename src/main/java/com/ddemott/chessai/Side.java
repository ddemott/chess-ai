package com.ddemott.chessai;

public enum Side {
    WHITE,
    BLACK;

    @Override
    public String toString() {
        // Capitalize first letter for backward compatibility with "White"/"Black" strings during transition
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
    
    public Side flip() {
        return this == WHITE ? BLACK : WHITE;
    }
}
