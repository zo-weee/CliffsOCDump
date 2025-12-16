package main;

import audio.MusicPlayer;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import units.Unit;

public class GameScreen extends JPanel {

    private Board board;
    private ActionPanel actionPanel;
    private StatusBoard statusBoard;   
    private MusicPlayer music;

    public GameScreen(JFrame frame, ArrayList<Unit> team) {
        setLayout(new BorderLayout());

        actionPanel = new ActionPanel();
        statusBoard = new StatusBoard();             

        board = new Board(team, actionPanel, EnvironmentType.GRASS);
        board.setStatusBoard(statusBoard);          

        Input inputHandler = new Input(board);
        board.addMouseListener(inputHandler);
        board.addMouseMotionListener(inputHandler);

        add(board, BorderLayout.CENTER);
        add(statusBoard, BorderLayout.EAST);          
        add(actionPanel, BorderLayout.SOUTH);
        
        music = new MusicPlayer();
        music.play(getClass().getResource("/audio/battle.wav"), true);
        music.setVolume(0.7f);
    }

    public GameScreen(JFrame frame,
                  ArrayList<Unit> teamP1,
                  ArrayList<Unit> teamP2,
                  EnvironmentType env) {

        setLayout(new BorderLayout());

        ArrayList<Unit> allUnits = new ArrayList<>();
        allUnits.addAll(teamP1);
        allUnits.addAll(teamP2);

        actionPanel = new ActionPanel();
        statusBoard = new StatusBoard();

        board = new Board(allUnits, actionPanel, env);
        board.setStatusBoard(statusBoard);

        Input inputHandler = new Input(board);
        board.addMouseListener(inputHandler);
        board.addMouseMotionListener(inputHandler);

        add(board, BorderLayout.CENTER);
        add(statusBoard, BorderLayout.EAST);
        add(actionPanel, BorderLayout.SOUTH);

        music = new MusicPlayer();
        music.play(getClass().getResource("/audio/battle.wav"), true);
        music.setVolume(0.7f);
    }

    public Board getBoard() {
        return board;
    }
}
