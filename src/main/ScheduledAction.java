package main;

import main.Board.TurnAction;
import main.Board.TurnPhase;

public class ScheduledAction {

    int turnsRemaining;
    TurnPhase phase;
    TurnAction action;
    
    ScheduledAction(int turns, TurnPhase phase, TurnAction action) {
        this.turnsRemaining = turns;
        this.phase = phase;
        this.action = action;
    }

}

// 14-JAN-2026: added the entirety of this .java file to handle AquaDetention