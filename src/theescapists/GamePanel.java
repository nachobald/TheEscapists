package theescapists;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel {
    private static final int TILE_SIZE = 32;
    private static final int MAP_WIDTH = 50;
    private static final int MAP_HEIGHT = 30;

    private char[][] map;
    private int playerX = 1, playerY = 1;

    //oggetti nella mappa
    private int exitX = MAP_WIDTH - 2, exitY = MAP_HEIGHT - 2;
    private final int keyX = 12, keyY = 8;		//cordinate chiave
    private boolean keyCollected = false;	
    private int spoon1X = 6, spoon1Y = 15;		//cordinate primo cucchiaio
    private boolean spoon1Collected = false;
    private int spoon2X = 17, spoon2Y;			//cordinate secondo cucchiaio
    private boolean spoon2Collected = false;
    
    //vita del muro
    private int[][] wallHealth;
    
    //gaurdie
    private ArrayList<Guard> guards = new ArrayList<>();
    private javax.swing.Timer guardTimer;

    //inventario
    private List<Item> inventory = new ArrayList<>();

    public GamePanel() {
        setPreferredSize(new Dimension(15 * TILE_SIZE, 15 * TILE_SIZE));
        setBackground(Color.BLACK);

        buildMap();
        
        //imposto le "vite" del muro (6)
        wallHealth = new int[MAP_HEIGHT][MAP_WIDTH];
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                if (map[y][x] == '#') {
                    wallHealth[y][x] = 6;
                }
            }
        }
        
        //posiziona 2 guardie
        guards.add(new Guard(10, 10));
        guards.add(new Guard(20, 5));

        //timer: muove le guardie e controlla le collisioni
        guardTimer = new javax.swing.Timer(450, e -> {
            for (Guard g : guards) g.move(map);

        //collisione guardia con player
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
                    
                    //per usare il cucchiaio
                    case KeyEvent.VK_SPACE -> {
                        for (Item item : new ArrayList<>(inventory)) {
                            if (item instanceof Spoon) {
                                item.use(GamePanel.this); // il cucchiaio sa scavare
                                break;
                            }
                        }
                    }
                    
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

    private void addItem(Item item) {
        inventory.add(item);
        System.out.println("Hai raccolto: " + item.getName());
    }
    
    private void checkForItems() {
        //raccolta chiave
        if (!keyCollected && playerX == keyX && playerY == keyY) {
            addItem(new Key());
            keyCollected = true;
        }
        
        //cucchiaio 1
        if (!spoon1Collected && playerX == spoon1X && playerY == spoon1Y) {
            addItem(new Spoon(3));
            spoon1Collected = true;
        }

        //cucchiaio 2
        if (!spoon2Collected && playerX == spoon2X && playerY == spoon2Y) {
            addItem(new Spoon(3));
            spoon2Collected = true;
        }

        //controllo uscita
        if (map[playerY][playerX] == 'E') {
            if (playerX == exitX && playerY == exitY) {
                // uscita principale, serve la chiave
                if (keyCollected) {
                    gameOver("Complimenti! Sei evaso dalla porta principale!");
                }
            } else {
                // uscita scavata, non serve la chiave
                gameOver("Complimenti! Sei evaso scavando un tunnel!");
            }
        }
    }

    public void showInventory() {
    	StringBuilder sb = new StringBuilder("Inventario:\n");
        System.out.println("Inventario:");
        if (inventory.isEmpty()) {
            sb.append("(vuoto)");
        } else {
            for (Item i : inventory) {
            	 sb.append("- ").append(i.getDescription()).append("\n");
            }
        }
        JOptionPane.showMessageDialog(this, sb.toString(), "Inventario", JOptionPane.INFORMATION_MESSAGE);
    }

    public void gameOver(String message) {
        JOptionPane.showMessageDialog(this, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);
        SwingUtilities.getWindowAncestor(this).dispose();
        System.exit(0);
    }

    public void startGame() {
        requestFocusInWindow();
    }
    
    public void digWall(Spoon spoon) {
        int[][] dirs = {{0,-1},{0,1},{-1,0},{1,0}};
        for (int[] d : dirs) {
            int tx = playerX + d[0];
            int ty = playerY + d[1];
            if (map[ty][tx] == '#') {
                wallHealth[ty][tx]--;

                spoon.reduceDurability();
                if (spoon.isBroken()) {
                    inventory.remove(spoon);
                    System.out.println("Il cucchiaio si è rotto!");
                }

                if (wallHealth[ty][tx] <= 0) {
                    map[ty][tx] = 'E';
                    System.out.println("Hai scavato un tunnel!");
                } else {
                    System.out.println("Colpo al muro! Mancano " + wallHealth[ty][tx] + " colpi.");
                }

                repaint();
                return;
            }
        }
        System.out.println("Non ci sono muri da scavare accanto a te.");
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

        //disegna player
        g.setColor(Color.RED);
        int px = (playerX - offsetX) * TILE_SIZE;
        int py = (playerY - offsetY) * TILE_SIZE;
        g.fillOval(px + 4, py + 4, TILE_SIZE - 8, TILE_SIZE - 8);
        
        //disegna guardia
        g.setColor(Color.BLUE);
        for (Guard guard : guards) {
            int gx = (guard.getX() - offsetX) * TILE_SIZE;
            int gy = (guard.getY() - offsetY) * TILE_SIZE;
            g.fillRect(gx + 4, gy + 4, TILE_SIZE - 8, TILE_SIZE - 8);
        }
        
        //disegna chiave
        if (!keyCollected) {
            g.setColor(Color.YELLOW);
            int kx = (keyX - offsetX) * TILE_SIZE;
            int ky = (keyY - offsetY) * TILE_SIZE;
            g.fillOval(kx + 8, ky + 8, TILE_SIZE - 16, TILE_SIZE - 16);
        }	
        
        //disegna cucchiaio 1
        if (!spoon1Collected) {
            g.setColor(Color.ORANGE);
            int sx = (spoon1X - offsetX) * TILE_SIZE;
            int sy = (spoon1Y - offsetY) * TILE_SIZE;
            g.fillRect(sx + 10, sy + 10, TILE_SIZE - 20, TILE_SIZE - 20);
        }

        //disegna cucchiaio 2
        if (!spoon2Collected) {
            g.setColor(Color.ORANGE);
            int sx = (spoon2X - offsetX) * TILE_SIZE;
            int sy = (spoon2Y - offsetY) * TILE_SIZE;
            g.fillRect(sx + 10, sy + 10, TILE_SIZE - 20, TILE_SIZE - 20);
        }
        
    }
}
