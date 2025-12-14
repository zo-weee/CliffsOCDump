package status;

import units.Unit;
import main.Board;

public interface Status {
    void onTurnEnd(Board board, Unit owner);
    boolean isExpired();
}

