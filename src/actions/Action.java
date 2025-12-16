package actions;

import main.Board;
import units.Unit;

public interface Action {

    String getName();
    int getEnergyCost();

    default boolean isSupport() {
        return false;
    }

    boolean canTarget(Board board, Unit user, int col, int row);

    void execute(Board board, Unit user, int col, int row);
}
