package status;

import main.Board;
import units.Unit;

public class DelayedSpellStatus implements Status {

    private int turnsRemaining;
    private Runnable effect;
    private boolean expired = false;

    public DelayedSpellStatus(int delayTurns, Runnable effect) {
        this.turnsRemaining = delayTurns;
        this.effect = effect;
    }

    @Override
    public void onTurnEnd(Board board, Unit owner) {
        if (expired) return;

        turnsRemaining--;

        if (turnsRemaining <= 0) {
            effect.run();
            expired = true;
        }
    }

    @Override
    public boolean isExpired() {
        return expired;
    }
}
