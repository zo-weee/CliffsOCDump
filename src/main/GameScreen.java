package main;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import units.Unit;

public class GameScreen extends JPanel {

    private Board board;
    private ActionPanel actionPanel;
    private StatusBoard statusBoard;   

    public GameScreen(JFrame frame, ArrayList<Unit> team) {
        setLayout(new BorderLayout());

        actionPanel = new ActionPanel();
        statusBoard = new StatusBoard();              

        board = new Board(team, actionPanel);
        board.setStatusBoard(statusBoard);            

        Input inputHandler = new Input(board);
        board.addMouseListener(inputHandler);
        board.addMouseMotionListener(inputHandler);

        add(board, BorderLayout.CENTER);
        add(statusBoard, BorderLayout.EAST);          
        add(actionPanel, BorderLayout.SOUTH);
    }

    public GameScreen(JFrame frame, ArrayList<Unit> teamP1, ArrayList<Unit> teamP2) {
        setLayout(new BorderLayout());

        ArrayList<Unit> allUnits = new ArrayList<>();
        allUnits.addAll(teamP1);
        allUnits.addAll(teamP2);

        actionPanel = new ActionPanel();
        statusBoard = new StatusBoard();              

        board = new Board(allUnits, actionPanel);
        board.setStatusBoard(statusBoard);            

        Input inputHandler = new Input(board);
        board.addMouseListener(inputHandler);
        board.addMouseMotionListener(inputHandler);

        add(board, BorderLayout.CENTER);
        add(statusBoard, BorderLayout.EAST);          
        add(actionPanel, BorderLayout.SOUTH);
    }

    public Board getBoard() {
        return board;
    }
}
