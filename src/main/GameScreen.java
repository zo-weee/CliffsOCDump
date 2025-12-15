package main;

import units.Unit;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GameScreen extends JPanel {

    private Board board;
    private ActionPanel actionPanel;

    public GameScreen(JFrame frame, ArrayList<Unit> team) {
        setLayout(new BorderLayout());

        actionPanel = new ActionPanel();
        board = new Board(team, actionPanel);

        Input inputHandler = new Input(board);
        board.addMouseListener(inputHandler);
        board.addMouseMotionListener(inputHandler);

        add(board, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);
    }

    public GameScreen(JFrame frame, ArrayList<Unit> teamP1, ArrayList<Unit> teamP2) {
        setLayout(new BorderLayout());

        ArrayList<Unit> allUnits = new ArrayList<>();
        allUnits.addAll(teamP1);
        allUnits.addAll(teamP2);

        actionPanel = new ActionPanel();
        board = new Board(allUnits, actionPanel);

        Input inputHandler = new Input(board);
        board.addMouseListener(inputHandler);
        board.addMouseMotionListener(inputHandler);

        add(board, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);
    }

    public Board getBoard() {
        return board;
    }
}
