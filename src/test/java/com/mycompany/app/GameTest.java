package com.mycompany.app;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for simple Programm.
 */
public class GameTest {

    private static char[] emptyBoard() {
        char[] board = new char[9];
        for (int i = 0; i < board.length; i++) {
            board[i] = ' ';
        }
        return board;
    }

    private static char[] board(String rows) {
        if (rows.length() != 9) {
            throw new IllegalArgumentException("Board string must contain exactly 9 characters");
        }
        return rows.toCharArray();
    }

    @Test
    void constructorInitializesEmptyGame() {
        Game game = new Game();

        assertEquals(State.PLAYING, game.state);
        assertNotNull(game.player1);
        assertNotNull(game.player2);
        assertEquals('X', game.player1.symbol);
        assertEquals('O', game.player2.symbol);

        for (char c : game.board) {
            assertEquals(' ', c);
        }
    }

    @Test
    void checkStateDetectsXWinByRow() {
        Game game = new Game();
        game.symbol = 'X';

        State state = game.checkState(board("XXX      "));

        assertEquals(State.XWIN, state);
    }

    @Test
    void checkStateDetectsOWinByColumn() {
        Game game = new Game();
        game.symbol = 'O';

        State state = game.checkState(board("O  O  O  "));

        assertEquals(State.OWIN, state);
    }

    @Test
    void checkStateDetectsXWinByMainDiagonal() {
        Game game = new Game();
        game.symbol = 'X';

        State state = game.checkState(board("X   X   X"));

        assertEquals(State.XWIN, state);
    }

    @Test
    void checkStateDetectsXWinByAntiDiagonal() {
        Game game = new Game();
        game.symbol = 'X';

        State state = game.checkState(board("  X X X  "));

        assertEquals(State.XWIN, state);
    }

    @Test
    void checkStateDetectsDraw() {
        Game game = new Game();
        game.symbol = 'X';

        State state = game.checkState(board("XOXOOXXXO"));

        assertEquals(State.DRAW, state);
    }

    @Test
    void checkStateDetectsPlayingWhenMovesRemain() {
        Game game = new Game();
        game.symbol = 'X';

        State state = game.checkState(board("XO XO    "));

        assertEquals(State.PLAYING, state);
    }

    @Test
    void generateMovesReturnsAllEmptyCells() {
        Game game = new Game();
        ArrayList<Integer> moves = new ArrayList<>();
        char[] b = board("XO XO    ");

        game.generateMoves(b, moves);

        assertEquals(Arrays.asList(2, 5, 6, 7, 8), moves);
    }

    @Test
    void evaluatePositionReturnsPositiveInfinityForWinner() {
        Game game = new Game();
        game.symbol = 'X';
        Player player = new Player();
        player.symbol = 'X';

        int value = game.evaluatePosition(board("XXX      "), player);

        assertEquals(Game.INF, value);
    }

    @Test
    void evaluatePositionReturnsNegativeInfinityForLosingState() {
        Game game = new Game();
        game.symbol = 'X';
        Player player = new Player();
        player.symbol = 'O';

        int value = game.evaluatePosition(board("XXX      "), player);

        assertEquals(-Game.INF, value);
    }

    @Test
    void evaluatePositionReturnsZeroForDraw() {
        Game game = new Game();
        game.symbol = 'X';
        Player player = new Player();
        player.symbol = 'X';

        int value = game.evaluatePosition(board("XOXOOXXXO"), player);

        assertEquals(0, value);
    }

    @Test
    void evaluatePositionReturnsMinusOneForOngoingGame() {
        Game game = new Game();
        game.symbol = 'X';
        Player player = new Player();
        player.symbol = 'X';

        int value = game.evaluatePosition(board("XO XO    "), player);

        assertEquals(-1, value);
    }

    @Test
    void minimaxChoosesImmediateWinningMoveForX() {
        Game game = new Game();
        Player player = new Player();
        player.symbol = 'X';

        char[] b = board("XX OO    "); // X может выиграть ходом в позицию 3 (1-based)

        int move = game.MiniMax(b, player);

        assertEquals(3, move);
        assertEquals(0, game.q); // MiniMax сбрасывает счетчик
    }

    @Test
    void minimaxChoosesImmediateWinningMoveForO() {
        Game game = new Game();
        Player player = new Player();
        player.symbol = 'O';

        char[] b = board("OO XX    "); // O может выиграть ходом в позицию 3 (1-based)

        int move = game.MiniMax(b, player);

        assertEquals(3, move);
        assertEquals(0, game.q);
    }

    @Test
    void minimaxDoesNotMutateBoardAfterSearch() {
        Game game = new Game();
        Player player = new Player();
        player.symbol = 'X';

        char[] b = board("XX OO    ");
        char[] original = b.clone();

        game.MiniMax(b, player);

        assertArrayEquals(original, b);
    }

    @Test
    void checkStateReturnsOWinWhenSymbolO() {
        Game game = new Game();
        game.symbol = 'O';

        State state = game.checkState(board("OOO      "));

        assertEquals(State.OWIN, state);
    }

    @Test
    void checkStateWithInvalidSymbolDoesNotReturnWin() {
        Game game = new Game();
        game.symbol = 'Z';

        State state = game.checkState(board("ZZZ      "));

        assertNotEquals(State.XWIN, state);
        assertNotEquals(State.OWIN, state);
    }

    @Test
    void minimaxHandlesMultipleBestMoves() {
        Game game = new Game();
        Player player = new Player();
        player.symbol = 'X';

        char[] b = emptyBoard();

        int move = game.MiniMax(b, player);

        // Проверяем, что вернулся валидный ход
        assertTrue(move >= 1 && move <= 9);
    }

    @Test
    void ticTacToeCellStoresCoordinatesAndMarker() {
        TicTacToeCell cell = new TicTacToeCell(5, 2, 1);

        assertEquals(5, cell.getNum());
        assertEquals(2, cell.getCol());
        assertEquals(1, cell.getRow());
        assertEquals(' ', cell.getMarker());

        cell.setMarker("X");

        assertEquals('X', cell.getMarker());
        assertFalse(cell.isEnabled());
        assertEquals("X", cell.getText());
    }
}

class UtilityTest {

    private String captureOutput(Runnable action) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        try {
            action.run();
        } finally {
            System.setOut(originalOut);
        }

        return baos.toString();
    }

    @Test
    void utilityConstructorIsCallable() {
        assertDoesNotThrow(Utility::new);
    }

    @Test
    void utilityClassCanBeUsed() {
        assertDoesNotThrow(() -> {
            char[] board = {'X','O','X','O','X','O','X','O','X'};
            Utility.print(board);

            int[] ints = {1,2,3,4,5,6,7,8,9};
            Utility.print(ints);

            ArrayList<Integer> moves = new ArrayList<>();
            moves.add(1);
            moves.add(5);
            moves.add(9);
            Utility.print(moves);
        });
    }

    @Test
    void printCharArrayOutputsCorrectFormat() {
        char[] board = {'X','O','X',' ','O',' ','X',' ','O'};

        String output = captureOutput(() -> Utility.print(board));

        String expected = System.lineSeparator() +
                "X-O-X- -O- -X- -O-" +
                System.lineSeparator();

        assertEquals(expected, output);
    }

    @Test
    void printIntArrayOutputsCorrectFormat() {
        int[] board = {1,2,3,4,5,6,7,8,9};

        String output = captureOutput(() -> Utility.print(board));

        String expected = System.lineSeparator() +
                "1-2-3-4-5-6-7-8-9-" +
                System.lineSeparator();

        assertEquals(expected, output);
    }

    @Test
    void printMovesOutputsCorrectFormat() {
        ArrayList<Integer> moves = new ArrayList<>();
        moves.add(3);
        moves.add(6);
        moves.add(7);

        String output = captureOutput(() -> Utility.print(moves));

        String expected = System.lineSeparator() +
                "3-6-7-" +
                System.lineSeparator();

        assertEquals(expected, output);
    }
}

class TestTicTacToePanel extends TicTacToePanel {

    boolean messageShown = false;
    boolean windowClosed = false;
    String lastMessage;

    TestTicTacToePanel(GridLayout layout) {
        super(layout);
    }

    @Override
    protected void showEndGame(String message) {
        messageShown = true;
        lastMessage = message;
    }

    @Override
    protected void closeWindow() {
        windowClosed = true;
    }
}

class TicTacToePanelTest {
    private TestTicTacToePanel panel;

    private Game getGame() throws Exception {
        Field f = TicTacToePanel.class.getDeclaredField("game");
        f.setAccessible(true);
        return (Game) f.get(panel);
    }

    @BeforeEach
    void setUp() {
        panel = new TestTicTacToePanel(new GridLayout(3,3));
    }

    @Test
    void panelCreates9Cells() {
        Component[] components = panel.getComponents();
        assertEquals(9, components.length);

        for (Component c : components) {
            assertTrue(c instanceof TicTacToeCell);
        }
    }

    @Test
    void gameIsInitializedWithPlayer1() throws Exception {
        Game game = getGame();

        assertNotNull(game);
        assertSame(game.player1, game.cplayer);
    }

    @Test
    void firstClickSetsXOnBoard() throws Exception {
        Game game = getGame();

        TicTacToeCell cell = (TicTacToeCell) panel.getComponent(0);
        cell.doClick();

        assertEquals('X', game.board[0]);
    }

    @Test
    void boardIsUpdatedAfterMove() throws Exception {
        Game game = getGame();

        TicTacToeCell cell = (TicTacToeCell) panel.getComponent(4);
        cell.doClick();

        for (int i = 0; i < 9; i++) {
            assertEquals(game.board[i], ((TicTacToeCell) panel.getComponent(i)).getMarker());
        }
    }

    @Test
    void doesNotAutoClickWhenMoveIsInvalid() throws Exception {
        Game game = getGame();

        game.player2.move = 0;

        game.cplayer = game.player1;

        TicTacToeCell cell = (TicTacToeCell) panel.getComponent(0);
        cell.doClick();

        assertEquals('X', game.cplayer.symbol);
    }

    @Test
    void endGame_XWin_showsMessageAndClosesWindow() {
        panel.endGame(State.XWIN);
        assertTrue(panel.messageShown);
        assertTrue(panel.windowClosed);
        assertEquals("Выиграли крестики", panel.lastMessage);
    }

    @Test
    void endGame_OWin_showsMessageAndClosesWindow() {
        panel.endGame(State.OWIN);

        assertTrue(panel.messageShown);
        assertTrue(panel.windowClosed);
        assertEquals("Выиграли нолики", panel.lastMessage);
    }

    @Test
    void endGame_Draw_showsMessageAndClosesWindow() {
        panel.endGame(State.DRAW);

        assertTrue(panel.messageShown);
        assertTrue(panel.windowClosed);
        assertEquals("Ничья", panel.lastMessage);
    }

    @Test
    void endGame_Playing_doesNothing() {
        panel.endGame(State.PLAYING);

        assertFalse(panel.messageShown);
        assertFalse(panel.windowClosed);
    }

    @Test
    void endGame_XWin_callsMethods() {
        TestTicTacToePanel panel = new TestTicTacToePanel(new GridLayout(3,3));

        panel.endGame(State.XWIN);

        assertTrue(panel.messageShown);
        assertTrue(panel.windowClosed);
        assertEquals("Выиграли крестики", panel.lastMessage);
    }
}

class ProgramTest {
    @BeforeAll
    static void setup() {
        System.setProperty("java.awt.headless", "true");
    }
    @Test
    void createFrameContainsPanel() {
        JFrame frame = Program.createFrame();

        assertEquals(1, frame.getContentPane().getComponentCount());
        assertTrue(frame.getContentPane().getComponent(0) instanceof TicTacToePanel);
    }
}