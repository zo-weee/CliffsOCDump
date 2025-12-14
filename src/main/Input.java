package main;

import units.Unit;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Input extends MouseAdapter {

    private final Board board;
    private int startCol;
    private int startRow;

    public Input(Board board) {
        this.board = board;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int col = (e.getX() - board.offsetX) / board.tileSize;
        int row = (e.getY() - board.offsetY) / board.tileSize;

        Unit unitXY = board.getUnit(col, row);

        if (unitXY != null) {
            System.out.println("PRESSED: Unit selected at Grid (" + col + ", " + row + ")");
            this.startCol = unitXY.getX();
            this.startRow = unitXY.getY();
            board.onUnitSelected(unitXY);  // updates selectedUnit + panel
        } else {
            // clicked empty tile: deselect everything
            board.clearSelection();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        int targetCol = (e.getX() - board.offsetX) / board.tileSize;
        int targetRow = (e.getY() - board.offsetY) / board.tileSize;

        Unit u = board.selectedUnit;
        if (u == null) return;

        if (board.currentMode == Board.ActionMode.MOVE) {

            if (ValidateMove.isMoveValid(board, u, targetCol, targetRow)) {
                Move m = new Move(board, u, targetCol, targetRow);
                board.makeMove(m);
            } else {
                // revert to original tile
                u.setPosition(this.startCol, this.startRow);
            }

            board.setActionMode(Board.ActionMode.NONE); // clears highlights but keeps selection

        } else if (board.currentMode == Board.ActionMode.ATTACK) {

            if (board.selectedAction != null &&
                board.selectedAction.canTarget(board, u, targetCol, targetRow)) {

                board.selectedAction.execute(board, u, targetCol, targetRow);
            }

            board.selectedAction = null;
            board.setActionMode(Board.ActionMode.NONE);
        }

        board.repaint();
    }
}
