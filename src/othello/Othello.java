package mohothello;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Matt Hayworth
 * @version April/22/2016
 * Othello Game Program
 */
public class Othello {

    public static char[] board;
    public static int[] directions = new int[]{-11, -10, -9, -1, 1, 9, 10, 11};
    static double timerIncrementer[] = {0.015, 0.015, 0.015, 0.015, 0.025, 0.025, 0.025, 0.025, 0.025, 0.025,
                                        0.048, 0.048, 0.048, 0.048, 0.048, 0.048, 0.050, 0.051, 0.052, 0.053,
                                        0.044, 0.045, 0.049, 0.049, 0.049, 0.051, 0.053, 0.055, 0.057, 0.059,
                                        0.060, 0.060, 0.061, 0.062, 0.063, 0.064, 0.065, 0.065, 0.065, 0.065,
                                        0.167, 0.168, 0.169, 0.169, 0.171, 0.172, 0.173, 0.175, 0.180, 0.180,
                                        0.181, 0.187, 0.196, 0.199, 0.220, 0.220, 0.220, 0.220, 0.220, 0.220,
                                        0.220, 0.250, 0.250, 0.250, 0.250, 0.250, 0.250, 0.250, 0.250, 0.250};

    public static char EMPTY = '-';
    public static char BLACK = 'B';
    public static char WHITE = 'W';
    public static char EDGE = 'X';

    public static Scanner kb = new Scanner(System.in);

    public static Timer timer;
    public static boolean timesUp;

    //Helper method to return True if index corresponds to a space on the board.
    public static boolean validSpace(int position) {
        if (position < 10 || position > 89) {
            return false;
        } else if (position % 10 < 1 || position % 10 > 8) {
            return false;
        }
        return true;
    }

    //Generates board for the game.
    public static char[] generateBoard() {
        board = new char[100];
        for (int i = 0; i < board.length; i++) {
            if (validSpace(i) == true) {
                board[i] = EMPTY;
            } else {
                board[i] = EDGE;
            }
        }
        board[44] = WHITE;
        board[45] = BLACK;
        board[54] = BLACK;
        board[55] = WHITE;
        return board;
    }

    //Prints a String representation of the board.
    public static void printBoard(char[] board) {
        System.out.println("C ~ A B C D E F G H");
        for (int i = 1; i < 9; i++) {
            System.out.printf("C %d", i);
            for (int j = i * 10 + 1; j < i * 10 + 9; j++) {
                System.out.print(" " + board[j]);
            }
            System.out.println();
        }
    }

    //Helper method to return opposing player.
    public static char opponent(char player) {
        if (player == BLACK) {
            return WHITE;
        } else {
            return BLACK;
        }
    }

    //returns a move's adjacent peices required to flip opponents peices.
    public static int findBracket(int move, char player, char[] board, int direction) {
        char opp = opponent(player);
        int bracket = move + direction;
        if (board[bracket] == player) {
            return move;
        }
        while (board[bracket] == opp) {
            bracket += direction;
        }
        if (board[bracket] == player) {
            return bracket;
        } else {
            return move;
        }
    }

    //Returns True if move can result in flipping opponents pieces.
    public static boolean validMove(int move, char player, char[] board) {
        boolean valid = false;
        for (int i = 0; i < directions.length; i++) {
            if (findBracket(move, player, board, directions[i]) != move && board[move] == EMPTY) {
                valid = true;
            }
        }
        return valid;
    }

    //Returns an ArrayList of valid moves for a player.
    public static ArrayList moveList(char player, char[] board) {
        ArrayList<Integer> moveList = new ArrayList();
        for (int i = 0; i < board.length; i++) {
            if (validSpace(i) == true && validMove(i, player, board) == true) {
                moveList.add(i);
            }
        }
        return moveList;
    }

    //Returns a value for the board representing a players advantage.
    public static int evaluate(char player, char[] board) {
        int value = 0;
        int evaluateBoard[] = new int[]{0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
                                        0, 50,  5, 15, 10, 10, 15,  5, 50,  0,
                                        0,  5,  1,  5,  5,  5,  5,  1,  5,  0,
                                        0, 15,  5, 15,  5,  5, 15,  5, 15,  0,
                                        0, 10,  5,  5,  4,  4,  5,  5, 10,  0,
                                        0, 10,  5,  5,  4,  4,  5,  5, 10,  0,
                                        0, 15,  5, 15,  5,  5, 15,  5, 15,  0,
                                        0,  5,  1,  5,  5,  5,  5,  1,  5,  0,
                                        0, 50,  5, 15, 10, 10, 15,  5, 50,  0,
                                        0,  0,  0,  0,  0,  0,  0,  0,  0,  0,};
        for (int i = 0; i < board.length; i++) {
            if (board[i] == player) {
                value += evaluateBoard[i] * 100;
            }
        }
        return value;
    }

    //Places played peice and flips opponents peices then return changed board.
    public static char[] makeMove(int move, char player, char[] board) {
        if (move == 0) {
            return board;
        }
        board[move] = player;
        for (int i = 0; i < directions.length; i++) {
            int bracket = findBracket(move, player, board, directions[i]);
            int space = move;
            if (move != bracket) {
                space = move + directions[i];
            }
            while (space != bracket) {
                board[space] = player;
                space += directions[i];
            }
        }
        return board;
    }

    //Returns move to opptimize value of the board for program.
    public static int alphaBeta(char player, char[] board, int alpha, int beta, int depth, int maxDepth) {
        int bestMove = 0;
        while (!timesUp) {
            if (depth >= maxDepth) {
                return bestMove;
            } else {
                ArrayList<Integer> moveList = moveList(player, board);
                if (!moveList.isEmpty()) {
                    bestMove = moveList.get(0);
                } else if (moveList.isEmpty()) {
                    moveList.add(0);
                }
                for (Integer move : moveList) {
                    char[] newBoard = Arrays.copyOf(board, board.length);
                    makeMove(move, player, newBoard);
                    int tempMove = alphaBeta(opponent(player), newBoard, -alpha, -beta, depth + 1, maxDepth);
                    makeMove(tempMove, opponent(player), newBoard);
                    int tempMoveValue = evaluate(opponent(player), newBoard);
                    int moveValue = -tempMoveValue;
                    if (moveValue > alpha) {
                        bestMove = move;
                        alpha = moveValue;
                        if (alpha > beta) {
                            return bestMove;
                        }
                    }
                }
                return bestMove;
            }
        }
        return bestMove;
    }

    //Helper method to format programs move output.
    public static String convertMoveOut(int move) {
        int rem = move % 10;
        int row = (move - rem) / 10;
        char col = ' ';
        switch (rem) {
            case 1:
                col = 'a';
                break;
            case 2:
                col = 'b';
                break;
            case 3:
                col = 'c';
                break;
            case 4:
                col = 'd';
                break;
            case 5:
                col = 'e';
                break;
            case 6:
                col = 'f';
                break;
            case 7:
                col = 'g';
                break;
            case 8:
                col = 'h';
                break;
        }
        return " " + col + " " + row;
    }

    //Helper method to convert opponents move into an int.
    public static int convertMoveIn(String input) {
        char x = input.charAt(1);
        char y = input.charAt(3);
        int col = 0;
        int row = 0;
        switch (x) {
            case 'a':
                col = 1;
                break;
            case 'b':
                col = 2;
                break;
            case 'c':
                col = 3;
                break;
            case 'd':
                col = 4;
                break;
            case 'e':
                col = 5;
                break;
            case 'f':
                col = 6;
                break;
            case 'g':
                col = 7;
                break;
            case 'h':
                col = 8;
                break;
        }
        switch (y) {
            case '1':
                row = 10;
                break;
            case '2':
                row = 20;
                break;
            case '3':
                row = 30;
                break;
            case '4':
                row = 40;
                break;
            case '5':
                row = 50;
                break;
            case '6':
                row = 60;
                break;
            case '7':
                row = 70;
                break;
            case '8':
                row = 80;
                break;
        }
        return row + col;
    }

    //Ends timer during programs moves.
    public static class InterruptTask extends TimerTask {

        @Override
        public void run() {
            System.out.println("C <--Times Up!!-->");
            timesUp = true;
            timer.cancel();
        }
    }

    //Returns score for Black player at the end of the game.
    public static int score(char[] board) {
        int score = 0;
        for (int i = 0; i < board.length; i++) {
            if (board[i] == BLACK) {
                score++;
            }
        }
        return score;
    }

    public static void main(String[] args) {
        char player = BLACK;
        int moveNumber = 0;
        int timeLeft = 540;
        System.out.println("C Initialize Computer:");
        String color = kb.nextLine();
        switch (color) {
            case "I B":
                System.out.println("C Initialized As:");
                System.out.println("R B");
                break;
            case "I W":
                System.out.println("C Initialized As:");
                System.out.println("R W");
                break;
        }
        generateBoard();
        printBoard(board);

        while (player != ' ') {
            timer = new Timer();
            int move = 0;
            timesUp = false;
            char opp = opponent(player);
            moveNumber++;
            int moveTime = (int) (timerIncrementer[moveNumber] * (double) timeLeft);
            switch (color) {
                case "I B":
                    if (player == BLACK) {
                        System.out.println("C Program's Move:");
                        System.out.println("C Move Time:  " + moveTime);
                        timer.schedule(new InterruptTask(), moveTime * 1000);
                        int depth = 2;
                        while (!timesUp) {
                            move = alphaBeta(player, board, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, depth);
                            depth += 2;
                        }
                        System.out.println("B" + convertMoveOut(move));
                        if (!timesUp) {
                            timer.cancel();
                        }
                        timeLeft -= moveTime;
                        System.out.println("C Remaining Time: " + timeLeft);
                    }
                    if (player == WHITE) {
                        System.out.println("C Opponent's Move:");
                        kb.next();
                        String input = kb.nextLine();
                        move = convertMoveIn(input);
                    }
                    break;
                case "I W":
                    if (player == BLACK) {
                        System.out.println("C Opponent's Move:");
                        kb.next();
                        String input = kb.nextLine();
                        move = convertMoveIn(input);
                    }
                    if (player == WHITE) {
                        System.out.println("C Program's Move:");
                        System.out.println("C Move Time:  " + moveTime);
                        timer.schedule(new InterruptTask(), moveTime * 1000);
                        int depth = 2;
                        while (!timesUp) {
                            move = alphaBeta(player, board, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, depth);
                            depth += 2;
                        }
                        System.out.println("W" + convertMoveOut(move));
                        if (!timesUp) {
                            timer.cancel();
                        }
                        timeLeft -= moveTime;
                        System.out.println("C Remaining Time: " + timeLeft);
                    }
                    break;
            }
            makeMove(move, player, board);
            printBoard(board);
            System.out.println("C Board's Value: " + evaluate(player, board));

            if (!moveList(opp, board).isEmpty()) {
                player = opp;
            } else if (!moveList(player, board).isEmpty()) {
                if (color.equals("I B") && opp == BLACK) {
                    System.out.println(opp);
                }
                if (color.equals("I B") && opp == WHITE) {
                    System.out.println("C Please Pass White");
                    String pass = kb.next();
                    if (!pass.equals("W")) {
                        break;
                    }
                }
                if (color.equals("I W") && opp == BLACK) {
                    System.out.println("C Please Pass Black");
                    String pass = kb.next();
                    if (!pass.equals("B")) {
                        break;
                    }
                }
                if (color.equals("I W") && opp == WHITE) {
                    System.out.println(opp);
                }
            } else if (moveList(player, board).isEmpty() && moveList(opp, board).isEmpty()) {
                player = ' ';
            }
        }
        System.out.println("C Game Over:");
        printBoard(board);
        System.out.println(score(board));
    }

}
