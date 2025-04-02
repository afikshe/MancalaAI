public class MancalaGame {
    public int[] board;
    public Player player1;
    public Player player2;
    public boolean isPlayerOneTurn;

    public MancalaGame() {
        board = new int[14];
        for (int i = 0; i < 14; i++) {
            if (i != 6 && i != 13) board[i] = 4;
        }
        player1 = new Player("Player 1", 0, 5, 6);
        player2 = new Player("Player 2", 7, 12, 13);
        isPlayerOneTurn = true;
    }

    public Player getCurrentPlayer() {
        return isPlayerOneTurn ? player1 : player2;
    }

    public Player getOpponentPlayer() {
        return isPlayerOneTurn ? player2 : player1;
    }

    public boolean isValidMove(int move) {
        if (move < 0 || move > 13 || board[move] == 0) return false;
        return getCurrentPlayer().ownsPit(move);
    }

    public boolean isGameOver() {
        boolean playerOneEmpty = true, playerTwoEmpty = true;
        for (int i = 0; i < 6; i++) if (board[i] != 0) playerOneEmpty = false;
        for (int i = 7; i < 13; i++) if (board[i] != 0) playerTwoEmpty = false;
        return playerOneEmpty || playerTwoEmpty;
    }

    public void finalizeGame() {
        for (int i = 0; i < 6; i++) {
            board[6] += board[i];
            board[i] = 0;
        }
        for (int i = 7; i < 13; i++) {
            board[13] += board[i];
            board[i] = 0;
        }
    }

    public String getWinnerMessage() {
        String message = player1.getName() + " Score: " + board[6] +
                "\n" + player2.getName() + " Score: " + board[13] + "\n";
        if (board[6] > board[13]) {
            message += player1.getName() + " Wins!";
        } else if (board[6] < board[13]) {
            message += player2.getName() + " Wins!";
        } else {
            message += "It's a tie!";
        }
        return message;
    }
}
