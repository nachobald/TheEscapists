package theescapists;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class GamePanel extends JPanel {
    private static final int TILE_SIZE = 32;
    private static final int MAP_WIDTH = 50;
    private static final int MAP_HEIGHT = 30;

    private char[][] map;
    private int playerX = 1, playerY = 1;

    //oggetti nella mappa
    private int keyX = 12, keyY = 8;
    private boolean keyCollected = false;
    private int exitX = MAP_WIDTH - 2, exitY = MAP_HEIGHT - 2;
    
    //gaurdie
    private ArrayList<Guard> guards = new ArrayList<>();
    private javax.swing.Timer guardTimer;

    //inventario
    private ArrayList<Item> inventory = new ArrayList<>();

    public GamePanel() {
        setPreferredSize(new Dimension(15 * TILE_SIZE, 15 * TILE_SIZE));
        setBackground(Color.BLACK);

        buildMap();
        
     //posiziona 2 guardie
        guards.add(new Guard(10, 10));
        guards.add(new Guard(20, 5));

        //timer: muove le guardie e controlla le collisioni
        guardTimer = new javax.swing.Timer(450, e -> {
            for (Guard g : guards) g.move(map);

            // Collisione guardia con player
            for (Guard g : guards) {
                if (g.getX() == playerX && g.getY() == playerY) {
                    gameOver("Sei stato catturato!");
                    return;
                }
            }
            repaint();
        });
        guardTimer.start();

        setFocusable(true);
        requestFocusInWindow();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W -> movePlayer(0, -1);
                    case KeyEvent.VK_S -> movePlayer(0, 1);
                    case KeyEvent.VK_A -> movePlayer(-1, 0);
                    case KeyEvent.VK_D -> movePlayer(1, 0);
                    case KeyEvent.VK_I -> showInventory();
                }
                repaint();
            }
        });
        
    }

    private void buildMap() {
        map = new char[MAP_HEIGHT][MAP_WIDTH];
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                if (x == 0 || y == 0 || x == MAP_WIDTH - 1 || y == MAP_HEIGHT - 1) {
                    map[y][x] = '#'; //bordi
                } else {
                    map[y][x] = '.'; //pavimento
                }
            }
        }

        //muri interni
        for (int y = 2; y < MAP_HEIGHT - 2; y += 4) {
            for (int x = 2; x < MAP_WIDTH - 2; x += 6) {
                map[y][x] = '#';
            }
        }

        // uscita
        map[exitY][exitX] = 'E';
    }

    private void movePlayer(int dx, int dy) {
        int newX = playerX + dx;
        int newY = playerY + dy;

        if (map[newY][newX] != '#') {
            playerX = newX;
            playerY = newY;
        }

        checkForItems();
    }

    private void checkForItems() {
        //raccolta chiave
        if (!keyCollected && playerX == keyX && playerY == keyY) {
            addItem(new Item("Chiave gialla", -1));
            keyCollected = true;
        }

        //controllo uscita
        if (playerX == exitX && playerY == exitY && keyCollected) {
            gameOver("Complimenti! Sei evaso!");
        }
    }

    public void addItem(Item item) {
        inventory.add(item);
        System.out.println("Hai raccolto: " + item.getName());
    }

    public void showInventory() {
        System.out.println("Inventario:");
        if (inventory.isEmpty()) {
            System.out.println("(vuoto)");
        } else {
            for (Item i : inventory) {
                System.out.println("- " + i.getName() + (i.getDurability() > 0 ? " (" + i.getDurability() + ")" : ""));
            }
        }
    }

    public void gameOver(String message) {
        JOptionPane.showMessageDialog(this, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);
        SwingUtilities.getWindowAncestor(this).dispose();
        System.exit(0);
    }

    public void startGame() {
        requestFocusInWindow();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int viewCols = getWidth() / TILE_SIZE;
        int viewRows = getHeight() / TILE_SIZE;
        int offsetX = Math.max(0, Math.min(playerX - viewCols / 2, MAP_WIDTH - viewCols));
        int offsetY = Math.max(0, Math.min(playerY - viewRows / 2, MAP_HEIGHT - viewRows));

        for (int y = 0; y < viewRows; y++) {
            for (int x = 0; x < viewCols; x++) {
                int mapX = x + offsetX;
                int mapY = y + offsetY;

                if (mapY >= MAP_HEIGHT || mapX >= MAP_WIDTH) continue;

                char tile = map[mapY][mapX];
                if (tile == '#') g.setColor(Color.DARK_GRAY);
                else if (tile == 'E') g.setColor(Color.GREEN);
                else g.setColor(Color.LIGHT_GRAY);

                g.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }

        //disegna chiave
        if (!keyCollected) {
            g.setColor(Color.YELLOW);
            int kx = (keyX - offsetX) * TILE_SIZE;
            int ky = (keyY - offsetY) * TILE_SIZE;
            g.fillOval(kx + 8, ky + 8, TILE_SIZE - 16, TILE_SIZE - 16);
        }

        //disegna player
        g.setColor(Color.RED);
        int px = (playerX - offsetX) * TILE_SIZE;
        int py = (playerY - offsetY) * TILE_SIZE;
        g.fillOval(px + 4, py + 4, TILE_SIZE - 8, TILE_SIZE - 8);
        
        //guard
        g.setColor(Color.BLUE);
        for (Guard guard : guards) {
            int gx = (guard.getX() - offsetX) * TILE_SIZE;
            int gy = (guard.getY() - offsetY) * TILE_SIZE;
            g.fillRect(gx + 4, gy + 4, TILE_SIZE - 8, TILE_SIZE - 8);
        }
        
    }
}
