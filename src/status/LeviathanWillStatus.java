package status;

import main.Board;
import units.Unit;

public class LeviathanWillStatus implements Status {

    private int turnsRemaining;

    public LeviathanWillStatus(int turns) {
        this.turnsRemaining = turns;
    }

    @Override
    public void onTurnEnd(Board board, Unit owner) {
        turnsRemaining--;
    }

    @Override
    public boolean isExpired() {
        return turnsRemaining <= 0;
    }
}
