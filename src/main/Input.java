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

        Unit clickedUnit = board.getUnit(col, row);

        // === NORMAL SELECTION ===
        if (clickedUnit != null) {
            System.out.println("PRESSED: Unit selected at Grid (" + col + ", " + row + ")");
            board.onUnitSelected(clickedUnit);
        } else {
            if (board.currentMode == Board.ActionMode.NONE) {
                board.clearSelection();
            }
        }
    }


    @Override
    public void mouseReleased(MouseEvent e) {
        int targetCol = (e.getX() - board.offsetX) / board.tileSize;
        int targetRow = (e.getY() - board.offsetY) / board.tileSize;

        Unit u = board.selectedUnit;
        if (u == null) return;

        System.out.println(
            "DEBUG: hasActedThisTurn = " + u.hasActedThisTurn +
            ", mode = " + board.currentMode
        );

        if (board.currentMode == Board.ActionMode.MOVE) {

            if (u.hasActedThisTurn) {
                board.setActionMode(Board.ActionMode.NONE);
                return;
            }

            if (ValidateMove.isMoveValid(board, u, targetCol, targetRow)) {
                Move m = new Move(board, u, targetCol, targetRow);
                board.makeMove(m);
                u.hasActedThisTurn = true; // MOVE consumes turn
            }

            board.setActionMode(Board.ActionMode.NONE);

            if (board.allAlliesActed()) {
                board.endTurn();
            }
            return;
        } else if (board.currentMode == Board.ActionMode.ATTACK) {

            if (u.hasActedThisTurn) {
                System.out.println("Unit already acted this turn.");
                board.setActionMode(Board.ActionMode.NONE);
                return;
            }

            if (board.selectedAction != null &&
                board.selectedAction.canTarget(board, u, targetCol, targetRow)) {

                int cost = board.selectedAction.getEnergyCost();

                if (u.hasEnoughEnergy(cost)) {
                    board.selectedAction.execute(board, u, targetCol, targetRow);
                    u.spendEnergy(cost);
                    u.hasActedThisTurn = true; // 🔴 ATTACK ENDS TURN
                }
            }

            board.selectedAction = null;
            board.setActionMode(Board.ActionMode.NONE);

            if (board.allAlliesActed()) {
                board.endTurn();
            }
        }
    }
}
