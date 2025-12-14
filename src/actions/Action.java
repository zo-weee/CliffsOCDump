package actions;

import main.Board;
import units.Unit;

public interface Action {

    String getName();

    int getEnergyCost();

    // Can this action be used on this tile?
    boolean canTarget(Board board, Unit user, int col, int row);

    // What happens when the action is used
    void execute(Board board, Unit user, int col, int row);
}
