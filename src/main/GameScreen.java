package main;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import units.Unit;

public class GameScreen extends JPanel {

    private Board board;
    private ActionPanel actionPanel;
    private StatusBoard statusBoard;

    /* ===== SINGLE-PLAYER / DEFAULT (GRASS) ===== */
    public GameScreen(JFrame frame, ArrayList<Unit> team) {
        setLayout(new BorderLayout());

        actionPanel = new ActionPanel();
        statusBoard = new StatusBoard();

        board = new Board(team, actionPanel, EnvironmentType.GRASS);
        board.setStatusBoard(statusBoard);

        board.setGameOverListener(winner -> {
            java.util.List<Unit> winningUnits =
                    winner == Unit.Team.ALLY ? team : new ArrayList<>();

            frame.setContentPane(new GameOver(frame, winner, winningUnits));
            frame.pack();
            frame.revalidate();
            frame.repaint();
        });

        Input inputHandler = new Input(board);
        board.addMouseListener(inputHandler);
        board.addMouseMotionListener(inputHandler);

        add(board, BorderLayout.CENTER);
        add(statusBoard, BorderLayout.EAST);
        add(actionPanel, BorderLayout.SOUTH);

        playBattleMusic(EnvironmentType.GRASS);
    }

    /* ===== MULTIPLAYER / ENVIRONMENT SELECT ===== */
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

        board.setGameOverListener(winner -> {
            frame.setContentPane(
                new GameOver(frame, winner,
                    winner == Unit.Team.ALLY ? teamP1 : teamP2)
            );
            frame.pack();
            frame.revalidate();
            frame.repaint();
        });

        Input inputHandler = new Input(board);
        board.addMouseListener(inputHandler);
        board.addMouseMotionListener(inputHandler);

        add(board, BorderLayout.CENTER);
        add(statusBoard, BorderLayout.EAST);
        add(actionPanel, BorderLayout.SOUTH);

        playBattleMusic(env);
    }

    /* ===== MUSIC HANDLER ===== */
    private void playBattleMusic(EnvironmentType env) {
        Main.music.stop();

        String musicPath;

        switch (env) {
            case GRASS:
                musicPath = "/audio/battle_grass.wav";
                break;
            case CASTLE:
                musicPath = "/audio/battle_castle.wav";
                break;
            case GALAXY:
                musicPath = "/audio/battle_stars.wav";
                break;
            default:
                musicPath = "/audio/battle_grass.wav";
                break;
        }

        Main.music.play(getClass().getResource(musicPath), true);
    }

    public Board getBoard() {
        return board;
    }
}
