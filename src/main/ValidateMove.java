package main;
import units.*;

public class ValidateMove {
    public static boolean isMoveValid(Board board, Unit unit, int new_col, int new_row) {

        
        int og_col = unit.getX();
        int og_row = unit.getY();
        int moveRange = unit.moveRange;
        if (og_col == new_col && og_row == new_row) {
            return false;
        }

        int dx = Math.abs(og_col - new_col);
        int dy = Math.abs(og_row - new_row);

        boolean isInRange = (dx <= moveRange) && (dy <= moveRange);

        if (!isInRange) {
            return false;
        }

        if (board.isTileBlocked(new_col, new_row)) return false;

        Unit isOccupied = board.getUnit(new_col, new_row);

        return isOccupied == null;


    }
}