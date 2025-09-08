package theescapists;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GamePanel extends JPanel {
    private static final int TILE_SIZE = 32;
    private static final int MAP_WIDTH = 50;
    private static final int MAP_HEIGHT = 30;
    
    //immagini oggetti
    private BufferedImage muroTile;
    private BufferedImage pavimentoTile;
    private BufferedImage keyTile;
    private BufferedImage pickaxeTile;
    private BufferedImage lanternTile;
    private BufferedImage treeTile;
    private BufferedImage doorTile;
    private BufferedImage chestTile;
    private BufferedImage grassTile;
    
    //immagini prigioniero
    private BufferedImage[] playerUp = new BufferedImage[2];
    private BufferedImage[] playerDown = new BufferedImage[2];
    private BufferedImage[] playerLeft = new BufferedImage[2];
    private BufferedImage[] playerRight = new BufferedImage[2];
    
    //indica quale frame mostrare(0 o 1)
    private int playerFrame = 0;
    private Direction playerDir = Direction.DOWN;
    
    //immagini guardia
    private BufferedImage[] guardUp = new BufferedImage[2];
    private BufferedImage[] guardDown = new BufferedImage[2];
    private BufferedImage[] guardLeft = new BufferedImage[2];
    private BufferedImage[] guardRight = new BufferedImage[2];
    
    //indica quale frame mostrare(0 o 1)
    private int guardFrame = 0; 
    private Direction guardDir = Direction.DOWN;
    
    //mostra l'inventario
    private boolean showInventoryOverlay = false;
    
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

    
    private List<Item> inventory = new ArrayList<>(); 			//inventario
    private List<MapItem> mapItems = new ArrayList<>();			//oggetti mappa
    private List<Point> lanternPositions = new ArrayList<>();	//lanterne
    private List<Point> treePositions = new ArrayList<>(); 		//alberi
    private List<Rectangle> cells = new ArrayList<>();			//rettangolo formato dalle celle
    private List<Point> chestPositions = new ArrayList<>();		//cesta della cella
    private List<Point> grassPositions = new ArrayList<>();		//erba
    
    //messaggi a schermo
    private String gameMessage = "";
    private boolean showMessage = false;
    
    private boolean gameOverMessage = false;

    public GamePanel() {
        setPreferredSize(new Dimension(15 * TILE_SIZE, 15 * TILE_SIZE));
        setBackground(Color.BLACK);

        //caricamento immagini
        try {
        	//oggetti
            muroTile = ImageIO.read(getClass().getResource("/theescapists/assets/wall.png"));
            pavimentoTile = ImageIO.read(getClass().getResource("/theescapists/assets/flooring.png"));
            keyTile = ImageIO.read(getClass().getResource("/theescapists/assets/key.png"));
            pickaxeTile = ImageIO.read(getClass().getResource("/theescapists/assets/pickaxe.png"));
            lanternTile = ImageIO.read(getClass().getResource("/theescapists/assets/lantern.png"));
            treeTile = ImageIO.read(getClass().getResource("/theescapists/assets/tree.png"));
            doorTile = ImageIO.read(getClass().getResource("/theescapists/assets/door.png"));
            chestTile = ImageIO.read(getClass().getResource("/theescapists/assets/chest.png"));
            grassTile = ImageIO.read(getClass().getResource("/theescapists/assets/grass.png"));
            
            //prigioniero
            playerUp[0] = ImageIO.read(getClass().getResource("/theescapists/assets/prisoner_up_1.png"));
            playerUp[1] = ImageIO.read(getClass().getResource("/theescapists/assets/prisoner_up_2.png"));
            
            playerDown[0] = ImageIO.read(getClass().getResource("/theescapists/assets/prisoner_down_1.png"));
            playerDown[1] = ImageIO.read(getClass().getResource("/theescapists/assets/prisoner_down_2.png"));
            
            playerLeft[0] = ImageIO.read(getClass().getResource("/theescapists/assets/prisoner_left_1.png"));
            playerLeft[1] = ImageIO.read(getClass().getResource("/theescapists/assets/prisoner_left_2.png"));
            
            playerRight[0] = ImageIO.read(getClass().getResource("/theescapists/assets/prisoner_right_1.png"));
            playerRight[1] = ImageIO.read(getClass().getResource("/theescapists/assets/prisoner_right_2.png"));
            
            //guardia
            guardUp[0] = ImageIO.read(getClass().getResource("/theescapists/assets/guard_up_1.png"));
            guardUp[1] = ImageIO.read(getClass().getResource("/theescapists/assets/guard_up_2.png"));
            
            guardDown[0] = ImageIO.read(getClass().getResource("/theescapists/assets/guard_down_1.png"));
            guardDown[1] = ImageIO.read(getClass().getResource("/theescapists/assets/guard_down_2.png"));
            
            guardLeft[0] = ImageIO.read(getClass().getResource("/theescapists/assets/guard_left_1.png"));
            guardLeft[1] = ImageIO.read(getClass().getResource("/theescapists/assets/guard_left_2.png"));
            
            guardRight[0] = ImageIO.read(getClass().getResource("/theescapists/assets/guard_right_1.png"));
            guardRight[1] = ImageIO.read(getClass().getResource("/theescapists/assets/guard_right_2.png"));
        }catch (IOException e) {
            e.printStackTrace();
        }
        
        buildMap();  
        initLanternsBorders();
        initTrees(50);
        initGrass();
        
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
        mapItems.add(new MapItem(8, 10, new Pickaxe(3)));
        mapItems.add(new MapItem(20, 12, new Pickaxe(3)));
        
        //posiziona guardie
        spawnGuards(10);
        
        //timer: muove le guardie e controlla le collisioni
        guardTimer = new javax.swing.Timer(450, e -> {
            for (Guard g : guards) {
                int dist = Math.abs(g.getX() - playerX) + Math.abs(g.getY() - playerY);

                if (dist <= 6) {
                    //inseguimento verso il player
                    if (g.getX() < playerX && map[g.getY()][g.getX() + 1] != '#') {
                        g.setX(g.getX() + 1);
                        g.setDirection(Direction.RIGHT);
                    } else if (g.getX() > playerX && map[g.getY()][g.getX() - 1] != '#') {
                        g.setX(g.getX() - 1);
                        g.setDirection(Direction.LEFT);
                    } else if (g.getY() < playerY && map[g.getY() + 1][g.getX()] != '#') {
                        g.setY(g.getY() + 1);
                        g.setDirection(Direction.DOWN);
                    } else if (g.getY() > playerY && map[g.getY() - 1][g.getX()] != '#') {
                        g.setY(g.getY() - 1);
                        g.setDirection(Direction.UP);
                    }
                } else {
                    //movimento random
                    g.move(map);
                }

                //alterna il frame (camminata a due sprite)
                g.toggleFrame();
            }

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
                    case KeyEvent.VK_I -> { showInventoryOverlay = !showInventoryOverlay; //attiva/disattiva overlay
                    repaint();}
                    
                    //per usare il piccone
                    case KeyEvent.VK_SPACE -> {
                        for (Item item : new ArrayList<>(inventory)) {
                            if (item instanceof Pickaxe) {
                                item.use(GamePanel.this); //il piccone sa scavare
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
    
    //costruzione celle
    private void buildCell(int startX, int startY, int w, int h) {
    	cells.add(new Rectangle(startX, startY, w, h));
        for (int y = startY; y < startY + h; y++) {
            for (int x = startX; x < startX + w; x++) {
                if (y == startY || y == startY + h - 1 || x == startX || x == startX + w - 1) {
                    map[y][x] = '#'; //muri della cella
                }
            }
        }
        //porta della cella
        map[startY + 1][startX] = '.'; 
        
        //cesta della cella
        int chestX = startX + w / 2;
        int chestY = startY + h / 2;
        chestPositions.add(new Point(chestX, chestY));
    }
    
    //posizionamento lampade
    private void initLanternsBorders() {
    	lanternPositions.clear();
        int margin = 2; //distanza dai bordi

        //angolo in basso a sinistra
        lanternPositions.add(new Point(margin, MAP_HEIGHT - 1 - margin));

        //angolo in basso a destra
        lanternPositions.add(new Point(MAP_WIDTH - 1 - margin, MAP_HEIGHT - 1 - margin));
    }
    
    //posizionamento alberi
    private void initTrees(int count) {
    	treePositions.clear();
        
        int startX = 3, endX = MAP_WIDTH - 3;
        int startY = 3, endY = MAP_HEIGHT - 3;
        int step = 5; //intervallo tra gli alberi
        int midX = MAP_WIDTH / 2;

        for (int y = startY; y <= endY; y += step) {
            for (int x = startX; x <= endX; x += step) {
                //salta la zona centrale (4 tile centrali)
                if (Math.abs(x - midX) <= 2) continue;

                if (map[y][x] == '.' && !isInsideCell(x, y)) {
                    treePositions.add(new Point(x, y));
                }
            }
        } 
    }
    
    private void initGrass() {
    	grassPositions.clear();

        int midX = MAP_WIDTH / 2;

        //colonna centrale più tre verso sx
        int[] offsets = {0, -1, -2, -3}; // centrale + 3 verso sinistra
        for (int y = 1; y < MAP_HEIGHT - 1; y++) {
            for (int dx : offsets) {
                int x = midX + dx;
                if (map[y][x] == '.' && !isOccupiedByTreeOrChest(x, y)) {
                    grassPositions.add(new Point(x, y));
                }
            }
        }

        //fondo della mappa
        int lastCellRow = 24; 
        for (int y = lastCellRow; y < MAP_HEIGHT - 1; y++) {
            for (int x = 1; x < MAP_WIDTH - 1; x++) {
                if (map[y][x] == '.' && !isOccupiedByTreeOrChest(x, y)) {
                    grassPositions.add(new Point(x, y));
                }
            }
        }
    }
    
    private boolean isOccupiedByTreeOrChest(int x, int y) {
        for (Point p : treePositions) {
            if (p.x == x && p.y == y) return true;
        }
        for (Point p : chestPositions) {
            if (p.x == x && p.y == y) return true;
        }
        return false;
    }
    
    private boolean isInsideCell(int x, int y) {
        for (Rectangle cell : cells) {
            if (cell.contains(x, y)) return true;
        }
        return false;
    }
    
    //creazione guardie
    private void spawnGuards(int count) {
        for (int i = 0; i < count; i++) {
            int x, y;
            do {
                x = (int)(Math.random() * MAP_WIDTH);
                y = (int)(Math.random() * MAP_HEIGHT);
            } while (map[y][x] != '.'); //solo su pavimento
            guards.add(new Guard(x, y));
        }
    }

    private void movePlayer(int dx, int dy) {
        int newX = playerX + dx;
        int newY = playerY + dy;

        if (map[newY][newX] != '#') {
            playerX = newX;
            playerY = newY;
            
            //aggiorna la direzione
            if (dx == 1) playerDir = Direction.RIGHT;
            else if (dx == -1) playerDir = Direction.LEFT;
            else if (dy == 1) playerDir = Direction.DOWN;
            else if (dy == -1) playerDir = Direction.UP;

            //cambia frame per animazione
            playerFrame = 1 - playerFrame; //alterna tra 0 e 1          
        }        
        
        checkForItems();
    }
    
    private void showGameMessage(String msg) {
    	this.gameMessage = msg;
        this.showMessage = true;

        //timer 1.5 secondo
        new javax.swing.Timer(1500, e -> {
        	if (!gameOverMessage) {   //non spegnere se è un messaggio di vittoria
                showMessage = false;
                repaint();
            }
            ((javax.swing.Timer)e.getSource()).stop();
            repaint();
        }).start();
    }

    private void addItem(Item item) {
        inventory.add(item);
        showGameMessage("Hai raccolto: " + item.getName());
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
                	showGameMessage("La porta è chiusa ti serve la chiave!");
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
    	gameMessage = message;
    	gameOverMessage = true;
    	showMessage = true;
    	repaint();
    	
        //aspetto 2.5 secondi e poi chiudo
    	new javax.swing.Timer(2500, e -> {
            ((javax.swing.Timer)e.getSource()).stop(); //ferma il timer
            SwingUtilities.getWindowAncestor(this).dispose();
            System.exit(0);
        }).start();
    }

    public void startGame() {
        requestFocusInWindow();
    }
    
    public void digWall(Pickaxe spoon) {
        int[][] dirs = {{0,-1},{0,1},{-1,0},{1,0}};
        for (int[] d : dirs) {
            int tx = playerX + d[0];
            int ty = playerY + d[1];
            if (map[ty][tx] == '#') {
                wallHealth[ty][tx]--;

                spoon.reduceDurability();
                if (spoon.isBroken()) {
                    inventory.remove(spoon);
                    showGameMessage("Il piccone si è rotto!");
                } else {
                    showGameMessage("Hai scavato! Utilizzi rimasti: " + spoon.getDurability());
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
                        showGameMessage("Hai aperto un varco nel muro!");
                    }
                } else {
                    showGameMessage("Colpo al muro! Mancano " + wallHealth[ty][tx] + " colpi.");
                }
                repaint();
                return;
            }
        }
        showGameMessage("Non ci sono muri da scavare accanto a te.");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int viewCols = getWidth() / TILE_SIZE;
        int viewRows = getHeight() / TILE_SIZE;
        
        //offset per centrare il player
        int offsetX = Math.max(0, Math.min(playerX - viewCols / 2, MAP_WIDTH - viewCols));
        int offsetY = Math.max(0, Math.min(playerY - viewRows / 2, MAP_HEIGHT - viewRows));

        //disegno la mappa
        for (int y = 0; y < viewRows; y++) {
            for (int x = 0; x < viewCols; x++) {
                int mapX = x + offsetX;
                int mapY = y + offsetY;

                if (mapY >= MAP_HEIGHT || mapX >= MAP_WIDTH) continue;

                char tile = map[mapY][mapX];
                BufferedImage tileImg = null;
                
                switch (tile) {
                case '#' -> tileImg = muroTile;
                case '.' -> tileImg = pavimentoTile;
            }
                
                if (tileImg != null) {
                    g.drawImage(tileImg, x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE, null);
                } else if (tile == 'E') {
                    g.setColor(Color.GREEN);
                    g.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                } else if (tile == 'T') {
                    g.setColor(Color.ORANGE);
                    g.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }
        
        //erba
        for (Point p : grassPositions) {
            int gx = (p.x - offsetX) * TILE_SIZE;
            int gy = (p.y - offsetY) * TILE_SIZE;
            g.drawImage(grassTile, gx, gy, TILE_SIZE, TILE_SIZE, null);
        }
        
        //alberi
        for (Point p : treePositions) {
            int tx = (p.x - offsetX) * TILE_SIZE;
            int ty = (p.y - offsetY) * TILE_SIZE;
            g.drawImage(treeTile, tx, ty, TILE_SIZE, TILE_SIZE, null);
        }
        
        //lanterne
        for (Point p : lanternPositions) {
            int lx = (p.x - offsetX) * TILE_SIZE;
            int ly = (p.y - offsetY) * TILE_SIZE;
            g.drawImage(lanternTile, lx, ly, TILE_SIZE, TILE_SIZE, null);
        }
        
        //cesta nella cella
        for (Point p : chestPositions) {
            int cx = (p.x - offsetX) * TILE_SIZE;
            int cy = (p.y - offsetY) * TILE_SIZE;
            g.drawImage(chestTile, cx, cy, TILE_SIZE, TILE_SIZE, null);
        }
        
        //porta uscita
        if (map[exitY][exitX] == 'E') {
            int dx = (exitX - offsetX) * TILE_SIZE;
            int dy = (exitY - offsetY) * TILE_SIZE;
            g.drawImage(doorTile, dx, dy, TILE_SIZE, TILE_SIZE, null);
        }
        
        //disegna oggetti
        for (MapItem mi : mapItems) {
            if (!mi.isCollected()) {
                int ix = (mi.getX() - offsetX) * TILE_SIZE;
                int iy = (mi.getY() - offsetY) * TILE_SIZE;

                if (mi.getItem() instanceof Key) {
                    g.drawImage(keyTile, ix, iy, TILE_SIZE, TILE_SIZE, null); //immagine chiave
                } else if (mi.getItem() instanceof Pickaxe) {
                	g.drawImage(pickaxeTile, ix, iy, TILE_SIZE, TILE_SIZE, null); //immagine piccone
                }
            }
        }
        
      //disegna player
        BufferedImage[] playerFrames = switch (playerDir) {
        	case UP -> playerUp;
        	case DOWN -> playerDown;
        	case LEFT -> playerLeft;
        	case RIGHT -> playerRight;
        };

        //playerFrame alterna tra 0 e 1
        int px = (playerX - offsetX) * TILE_SIZE;
        int py = (playerY - offsetY) * TILE_SIZE;
        g.drawImage(playerFrames[playerFrame], px, py, TILE_SIZE, TILE_SIZE, null);
        
        //disegna guardie
        for (Guard guard : guards) {
            BufferedImage[] guardFrames = switch (guard.getDirection()) {
                case UP -> guardUp;
                case DOWN -> guardDown;
                case LEFT -> guardLeft;
                case RIGHT -> guardRight;
            };

            int gx = (guard.getX() - offsetX) * TILE_SIZE;
            int gy = (guard.getY() - offsetY) * TILE_SIZE;

            g.drawImage(guardFrames[guard.getFrame()], gx, gy, TILE_SIZE, TILE_SIZE, null);
        }
        
        //disegna inventario se attivo
        if (showInventoryOverlay) {
            int overlayWidth = 220;
            int overlayHeight = inventory.isEmpty() ? 60 : inventory.size() * 40 + 40;
            int marginTop = 40;
            int marginRight = 20;

            int x = getWidth() - overlayWidth - marginRight;
            int y = marginTop;

            //sfondo semitrasparente
            g.setColor(new Color(0, 0, 0, 180));
            g.fillRoundRect(x, y, overlayWidth, overlayHeight, 15, 15);

            //titolo
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.drawString("Inventario", x + 10, y + 25);

            //oggetti
            int slotSize = 32;
            int padding = 8;
            int itemY = y + 40;

            for (Item item : inventory) {
                //slot background
                g.setColor(new Color(50, 50, 50, 200));
                g.fillRect(x + 10, itemY, slotSize, slotSize);
                g.setColor(Color.WHITE);
                g.drawRect(x + 10, itemY, slotSize, slotSize);

                //icona oggetto
                if (item instanceof Key) {
                    g.drawImage(keyTile, x + 12, itemY + 2, slotSize - 4, slotSize - 4, null);
                } else if (item instanceof Pickaxe) {
                    g.drawImage(pickaxeTile, x + 12, itemY + 2, slotSize - 4, slotSize - 4, null);
                }

                //nome oggetto
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.PLAIN, 14));
                String label = item.getName();	
                if (item instanceof Pickaxe pickaxe) {
                    label += " (" + pickaxe.getDurability() + "/" + 3 + ")";
                }
                g.drawString(label, x + slotSize + 20, itemY + slotSize / 2 + 5);
                
                itemY += slotSize + padding;	//sposto per oggetto successivo
            }
        }
        
        //disegna messaggi
        if (showMessage && gameMessage != null) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(gameMessage);
            int x = (getWidth() - textWidth) / 2; //centrato orizzontalmente
            int y = 50; //altezza fissa dall'alto
            g.drawString(gameMessage, x, y);
        }    
    }
}
