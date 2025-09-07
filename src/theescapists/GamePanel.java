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
    private int exitX, exitY;
   
    private boolean keyCollected = false;	
    
    //vita del muro
    private int[][] wallHealth;
    
    //gaurdie
    private ArrayList<Guard> guards = new ArrayList<>();
    private javax.swing.Timer guardTimer;

    //inventario
    private List<Item> inventory = new ArrayList<>();
    
    //oggetti mappa
    private List<MapItem> mapItems = new ArrayList<>();

    public GamePanel() {
        setPreferredSize(new Dimension(15 * TILE_SIZE, 15 * TILE_SIZE));
        setBackground(Color.BLACK);

        buildMap();
        
        //uscita principale
        exitX = MAP_WIDTH - 2;
        exitY = 1;
        map[exitY][exitX] = 'E';
        
        //imposto le "vite" del muro (6)
        wallHealth = new int[MAP_HEIGHT][MAP_WIDTH];
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                if (map[y][x] == '#') {
                    wallHealth[y][x] = 6;
                }
            }
        }
        
        //posiziona oggetti
        mapItems.add(new MapItem(37, 21, new Key()));
        mapItems.add(new MapItem(8, 10, new Spoon(3)));
        mapItems.add(new MapItem(20, 12, new Spoon(3)));
        
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
        
        //muri esterni
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                if (x == 0 || y == 0 || x == MAP_WIDTH - 1 || y == MAP_HEIGHT - 1) {
                    map[y][x] = '#';
                } else {
                    map[y][x] = '.'; //pavimento
                }
            }
        }

        //corridoio centrale verticale
        int midX = MAP_WIDTH / 2;
        for (int y = 1; y < MAP_HEIGHT - 1; y++) {
            map[y][midX] = '.'; 
        }

        //celle a sinistra del corridoio
        for (int y = 2; y < MAP_HEIGHT - 5; y += 6) {
            for (int x = 2; x < midX - 2; x += 7) {
                buildCell(x, y, 5, 4);
            }
        }

        //celle a destra del corridoio
        for (int y = 2; y < MAP_HEIGHT - 5; y += 6) {
            for (int x = midX + 2; x < MAP_WIDTH - 6; x += 7) {
                buildCell(x, y, 5, 4);
            }
        }

        //uscita principale
        map[1][MAP_WIDTH - 2] = 'E';
        
    }
    
    private void buildCell(int startX, int startY, int w, int h) {
        for (int y = startY; y < startY + h; y++) {
            for (int x = startX; x < startX + w; x++) {
                if (y == startY || y == startY + h - 1 || x == startX || x == startX + w - 1) {
                    map[y][x] = '#'; //muri della cella
                }
            }
        }
        //porta della cella
        map[startY + 1][startX] = '.'; 
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
    	for (MapItem mi : mapItems) {
    	    if (!mi.isCollected() && playerX == mi.getX() && playerY == mi.getY()) {
    	        addItem(mi.getItem());
    	        mi.collect();
    	        
    	        if (mi.getItem() instanceof Key) {
                    keyCollected = true;
                }
    	    }
    	}

        //controllo uscita
        if (map[playerY][playerX] == 'E') {
            if (playerX == exitX && playerY == exitY) {
                // uscita principale, serve la chiave
                if (keyCollected) {
                    gameOver("Complimenti! Sei evaso dalla porta principale!");
                }else {
                	System.out.println("La porta è chiusa ti serve la chiave!");
                }
            } else if (map[playerY][playerX] == 'T') {
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
                    boolean isBorder = (tx == 0 || ty == 0 || tx == MAP_WIDTH - 1 || ty == MAP_HEIGHT - 1);

                    //se è bordo: è un tunnel di fuga quindi vittoria immediata
                    if (isBorder) {
                        map[ty][tx] = 'T';        //solo per disegno
                        repaint();
                        gameOver("Complimenti! Sei evaso scavando un tunnel!");
                        return;
                    } else {
                        //muro interno: diventa passaggio
                        map[ty][tx] = '.';
                        System.out.println("Hai aperto un varco nel muro!");
                    }
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
                else if (tile == 'E') g.setColor(Color.GREEN); 	//porta principale
                else if (tile == 'T') g.setColor(Color.ORANGE);	//tunnel scavato
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
        
        //disegna oggetti
        for (MapItem mi : mapItems) {
            if (!mi.isCollected()) {
                if (mi.getItem() instanceof Key) {
                    g.setColor(Color.YELLOW);
                    int ix = (mi.getX() - offsetX) * TILE_SIZE;
                    int iy = (mi.getY() - offsetY) * TILE_SIZE;
                    g.fillOval(ix + 8, iy + 8, TILE_SIZE - 16, TILE_SIZE - 16);
                }
                else if (mi.getItem() instanceof Spoon) {
                    g.setColor(Color.ORANGE);
                    int ix = (mi.getX() - offsetX) * TILE_SIZE;
                    int iy = (mi.getY() - offsetY) * TILE_SIZE;
                    g.fillRect(ix + 10, iy + 10, TILE_SIZE - 20, TILE_SIZE - 20);
                }
            }
        }
        
       
        
    }
}
