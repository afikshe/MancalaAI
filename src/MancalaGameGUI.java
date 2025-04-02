import javax.swing.*;
import java.awt.*;

public class MancalaGameGUI extends JFrame {
    private MancalaGame game;
    private JButton[] pitButtons;
    private JLabel playerTurnLabel;
    private JLabel scoreLabel;
    private int animationDelay = 200;

    public MancalaGameGUI() {
        game = new MancalaGame();
        setTitle("Mancala Game 1v1");
        setSize(1000, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setJMenuBar(createMenuBar());

        playerTurnLabel = new JLabel(game.getCurrentPlayer().getName() + "'s Turn", SwingConstants.CENTER);
        playerTurnLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(playerTurnLabel, BorderLayout.NORTH);

        pitButtons = new JButton[14];
        JPanel boardPanel = new JPanel(new BorderLayout());
        JPanel centerPanel = new JPanel(new GridLayout(2, 6));
        JPanel leftStore = new JPanel(new BorderLayout());
        JPanel rightStore = new JPanel(new BorderLayout());

        for (int i = 12; i >= 7; i--) {
            pitButtons[i] = createPitButton(i);
            centerPanel.add(pitButtons[i]);
        }
        for (int i = 0; i <= 5; i++) {
            pitButtons[i] = createPitButton(i);
            centerPanel.add(pitButtons[i]);
        }

        pitButtons[13] = createPitButton(13);
        leftStore.add(pitButtons[13], BorderLayout.CENTER);

        pitButtons[6] = createPitButton(6);
        rightStore.add(pitButtons[6], BorderLayout.CENTER);

        boardPanel.add(leftStore, BorderLayout.WEST);
        boardPanel.add(centerPanel, BorderLayout.CENTER);
        boardPanel.add(rightStore, BorderLayout.EAST);

        add(boardPanel, BorderLayout.CENTER);

        scoreLabel = new JLabel("Score - P1: 0 | P2: 0", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        add(scoreLabel, BorderLayout.SOUTH);

        updateBoard();
        setVisible(true);
    }

    private JButton createPitButton(int index) {
        JButton button = new JButton();
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setPreferredSize(new Dimension(80, 80));
        button.setFocusPainted(false);
        button.addActionListener(e -> handleMove(index));
        return button;
    }

    private void handleMove(int move) {
        if (!game.isValidMove(move)) {
            JOptionPane.showMessageDialog(this, "Invalid move, try again.");
            return;
        }
        animateMove(move);
    }

    private void animateMove(int startIndex) {
        int stones = game.board[startIndex];
        game.board[startIndex] = 0;
        updateBoard();

        final int[] index = {startIndex};
        final int[] remaining = {stones};

        Timer timer = new Timer(animationDelay, null);
        timer.addActionListener(e -> {
            do {
                index[0] = (index[0] + 1) % 14;
            } while (index[0] == game.getOpponentPlayer().getStoreIndex());

            game.board[index[0]]++;
            remaining[0]--;
            updateBoard();

            if (remaining[0] == 0) {
                timer.stop();
                handlePostMoveLogic(index[0]);
            }
        });
        timer.start();
    }

    private void handlePostMoveLogic(int lastIndex) {
        if (game.board[lastIndex] == 1 && game.getCurrentPlayer().ownsPit(lastIndex)) {
            int opposite = 12 - lastIndex;
            if (game.board[opposite] > 0) {
                int store = game.getCurrentPlayer().getStoreIndex();
                game.board[store] += game.board[opposite] + game.board[lastIndex];
                game.board[opposite] = 0;
                game.board[lastIndex] = 0;
            }
        }

        boolean extraTurn = lastIndex == game.getCurrentPlayer().getStoreIndex();
        updateBoard();

        if (game.isGameOver()) {
            game.finalizeGame();
            updateBoard();
            JOptionPane.showMessageDialog(this, game.getWinnerMessage());
        } else if (extraTurn) {
            JOptionPane.showMessageDialog(this, "Extra turn for " + game.getCurrentPlayer().getName());
        } else {
            game.isPlayerOneTurn = !game.isPlayerOneTurn;
        }

        updateBoard();
    }

    private void updateBoard() {
        for (int i = 0; i < 14; i++) {
            if (pitButtons[i] != null)
                pitButtons[i].setText("" + game.board[i]);
        }

        playerTurnLabel.setText(game.getCurrentPlayer().getName() + "'s Turn");
        scoreLabel.setText("Score - " + game.player1.getName() + ": " + game.board[6] +
                " | " + game.player2.getName() + ": " + game.board[13]);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu gameMenu = new JMenu("Menu");

        JMenuItem newGameItem = new JMenuItem("New Game");
        newGameItem.addActionListener(e -> {
            game = new MancalaGame();
            updateBoard();
        });

        JMenu speedMenu = new JMenu("Animation Speed");
        JMenuItem slow = new JMenuItem("Slow");
        JMenuItem normal = new JMenuItem("Normal");
        JMenuItem fast = new JMenuItem("Fast");

        slow.addActionListener(e -> animationDelay = 500);
        normal.addActionListener(e -> animationDelay = 200);
        fast.addActionListener(e -> animationDelay = 100);

        speedMenu.add(slow);
        speedMenu.add(normal);
        speedMenu.add(fast);

        JMenuItem rulesItem = new JMenuItem("Game Rules");
        rulesItem.addActionListener(e -> showRulesDialog());

        gameMenu.add(newGameItem);
        gameMenu.add(speedMenu);
        gameMenu.addSeparator();
        gameMenu.add(rulesItem);

        menuBar.add(gameMenu);
        return menuBar;
    }

    private void showRulesDialog() {
        String rules = """
        🪨 Mancala Rules:

        🎯 Objective: Collect as many stones as possible in your storage pit.

        ✅ On your turn, choose one of your pits.
        ✅ Distribute the stones one by one counter-clockwise.
        ✅ Skip the opponent's storage pit.
        ✅ If the last stone lands in your own empty pit and the opposite pit has stones, capture both into your storage.
        ✅ If the last stone lands in your storage pit, you get another turn.
        ✅ When one side of the board is empty, the game ends.

        🏆 The player with the most stones in their storage pit wins.
        """;

        JOptionPane.showMessageDialog(this, rules, "Game Rules", JOptionPane.INFORMATION_MESSAGE);
    }
}
