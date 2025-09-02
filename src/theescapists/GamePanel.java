package theescapists;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GamePanel extends JPanel {
    
	private final int TILE_SIZE = 32;
    private int mapRows, mapCols;
    private int viewRows, viewCols;

    private char[][] map;
    private int playerX = 1;
    private int playerY = 1;

    public GamePanel(int mapRows, int mapCols, int viewRows, int viewCols) {
        this.mapRows = mapRows;
        this.mapCols = mapCols;
        this.viewRows = viewRows;
        this.viewCols = viewCols;

        setPreferredSize(new Dimension(viewCols * TILE_SIZE, viewRows * TILE_SIZE));
        setBackground(Color.BLACK);

        buildMap();

        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int newX = playerX;
                int newY = playerY;
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W -> newY--;
                    case KeyEvent.VK_S -> newY++;
                    case KeyEvent.VK_A -> newX--;
                    case KeyEvent.VK_D -> newX++;
                }
                if (isWalkable(newX, newY)) {
                    playerX = newX;
                    playerY = newY;
                    repaint();
                }
            }
        });
    }

    private void buildMap() {
        map = new char[mapRows][mapCols];
        for (int y = 0; y < mapRows; y++) {
            for (int x = 0; x < mapCols; x++) {
                if (y == 0 || y == mapRows - 1 || x == 0 || x == mapCols - 1) map[y][x] = '#';
                else map[y][x] = '.';
            }
        }

        // muri interni casuali
        for (int y = 2; y < mapRows - 2; y += 4) {
            for (int x = 2; x < mapCols - 2; x += 6) map[y][x] = '#';
        }

        map[mapRows - 2][mapCols - 2] = 'E';
    }

    private boolean isWalkable(int x, int y) {
        return map[y][x] != '#';
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int offsetX = Math.max(0, Math.min(playerX - viewCols / 2, mapCols - viewCols));
        int offsetY = Math.max(0, Math.min(playerY - viewRows / 2, mapRows - viewRows));

        for (int y = 0; y < viewRows; y++) {
            for (int x = 0; x < viewCols; x++) {
                int mapX = x + offsetX;
                int mapY = y + offsetY;

                if (map[mapY][mapX] == '#') g.setColor(Color.DARK_GRAY);
                else if (map[mapY][mapX] == 'E') g.setColor(Color.GREEN);
                else g.setColor(Color.LIGHT_GRAY);

                g.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }

        // player
        g.setColor(Color.RED);
        int px = (playerX - offsetX) * TILE_SIZE;
        int py = (playerY - offsetY) * TILE_SIZE;
        g.fillOval(px + 4, py + 4, TILE_SIZE - 8, TILE_SIZE - 8);
    }

    public void startGame() {
        requestFocusInWindow();
    }
}
