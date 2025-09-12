package theescapists;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GameModel {
    public static final int MAP_WIDTH = 50;
    public static final int MAP_HEIGHT = 30;

    //mappa di gioco
    private char[][] map;

    //player
    private Player player = new Player(1, 1);

    //stato chiave
    private boolean keyCollected = false;

    //uscita principale
    private int exitX = MAP_WIDTH - 2;
    private int exitY = 1;

    //vita dei muri
    private int[][] wallHealth;

    //oggetti
    private List<MapItem> mapItems = new ArrayList<>();
    
    //inventario
    private Inventory inventory = new Inventory();

    //guardie
    private List<Guard> guards = new ArrayList<>();

    //elementi mappa
    private List<Point> treePositions = new ArrayList<>();
    private List<Point> lanternPositions = new ArrayList<>();
    private List<Point> chestPositions = new ArrayList<>();
    private List<Point> grassPositions = new ArrayList<>();
    private List<Rectangle> cells = new ArrayList<>();

    //user interface
    private boolean showInventoryOverlay = false;
    private String gameMessage = "";
    private boolean showMessage = false;
    private boolean gameOverMessage = false;
    
    //riferimento al controller
    private GameController controller;
    
    //timer per messaggi
    private long messageStartTime = 0;
    private static final long MESSAGE_DURATION = 1500;

    //costruttore
    public GameModel() {
        map = new char[MAP_HEIGHT][MAP_WIDTH];
        buildMap();
        buildWallHealth();
        spawnMapItems();
        spawnGuards(15);
        initTrees();
        initLanterns();
        initGrass();
    }

    //mappa
    private void buildMap() {
        for (int y = 0; y < MAP_HEIGHT; y++)
            for (int x = 0; x < MAP_WIDTH; x++)
                map[y][x] = (x == 0 || y == 0 || x == MAP_WIDTH - 1 || y == MAP_HEIGHT - 1) ? '#' : '.';

        //corridoio centrale verticale
        int midX = MAP_WIDTH / 2;
        for (int y = 1; y < MAP_HEIGHT - 1; y++) map[y][midX] = '.';

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
        map[exitY][exitX] = 'E';
    }

    private void buildCell(int startX, int startY, int w, int h) {
        cells.add(new Rectangle(startX, startY, w, h));
        for (int y = startY; y < startY + h; y++) {
            for (int x = startX; x < startX + w; x++) {
                if (y == startY || y == startY + h - 1 || x == startX || x == startX + w - 1) {
                    map[y][x] = '#';
                }
            }
        }
        //porta della cella
        map[startY + 1][startX] = '.';

        //cesta nella cella
        int chestX = startX + w / 2;
        int chestY = startY + h / 2;
        chestPositions.add(new Point(chestX, chestY));
    }

    private void buildWallHealth() {
        wallHealth = new int[MAP_HEIGHT][MAP_WIDTH];
        for (int y = 0; y < MAP_HEIGHT; y++)
            for (int x = 0; x < MAP_WIDTH; x++)
                if (map[y][x] == '#') wallHealth[y][x] = 6;
    }

    //oggetti
    private void spawnMapItems() {
        mapItems.add(new MapItem(37, 21, new Key()));
        mapItems.add(new MapItem(8, 10, new Pickaxe(3)));
        mapItems.add(new MapItem(GameModel.MAP_WIDTH - 6, 12, new Pickaxe(3)));
    }

    //guardie
    private void spawnGuards(int count) {
        for (int i = 0; i < count; i++) {
            int x, y;
            do {
                x = (int)(Math.random() * MAP_WIDTH);
                y = (int)(Math.random() * MAP_HEIGHT);
            } while (map[y][x] != '.');
            guards.add(new Guard(x, y));
        }
    }

    //elementi mappa
    private void initTrees() {
    	int midX = MAP_WIDTH / 2;
        for (int y = 3; y < MAP_HEIGHT - 3; y += 5) {
            for (int x = 3; x < MAP_WIDTH - 3; x += 5) {
                if (map[y][x] == '.' && Math.abs(x - midX) > 2 && !isInCell(x, y)) {
                    treePositions.add(new Point(x, y));
                }
            }
        }
    }
    
    //controlla se un punto è dentro una cella
    private boolean isInCell(int x, int y) {
        for (Rectangle cell : cells) {
            if (cell.contains(x, y)) return true;
        }
        return false;
    }

    private void initLanterns() {
        lanternPositions.add(new Point(2, MAP_HEIGHT - 3));
        lanternPositions.add(new Point(MAP_WIDTH - 3, MAP_HEIGHT - 3));
    }

    private void initGrass() {
        int midX = MAP_WIDTH / 2;
        
        //erba nel corridoio centrale
        for (int y = 1; y < MAP_HEIGHT - 1; y++)
            for (int dx = 0; dx >= -3; dx--)
                if (map[y][midX + dx] == '.') grassPositions.add(new Point(midX + dx, y));
        
    }
    
    public Player getPlayer() { 
    	return player; 
    }

    public char[][] getMap() { 
    	return map; 
    }
    
    public int getPlayerX() { 
    	return player.getX(); 
    }
    
    public int getPlayerY() { 
    	return player.getY(); 
    }
    
    public Direction getPlayerDir() { 
    	return player.getDir(); 
    }
    
    public void setGameOverMessage(boolean b) { 
    	gameOverMessage = b; 
    }
    
    public void setController(GameController controller) {
        this.controller = controller;
    }
    
    public int getExitX() { 
    	return exitX; 
    }
    
    public int getExitY() { 
    	return exitY; 
    }
    
    public void setKeyCollected(boolean collected) { 
    	keyCollected = collected; 
    }
    
    public String getGameMessage() { 
    	return gameMessage; 
    }
    
    public void setGameMessage(String msg) {
    	gameMessage = msg; 
    	showMessage=true;
    	messageStartTime = System.currentTimeMillis();
    	}
    
    public int[][] getWallHealth() { 
    	return wallHealth; 
    }
    
    
    //list
    public List<Guard> getGuards() { 
    	return guards; 
    }
    
    public List<MapItem> getMapItems() { 
    	return mapItems; 
    }
    
    public Inventory getInventory() { 
        return inventory; 
    }
    
    public List<Point> getTreePositions() { 
    	return treePositions; 
    }
    
    public List<Point> getLanternPositions() { 
    	return lanternPositions; 
    }
    
    public List<Point> getChestPositions() { 
    	return chestPositions; 
    }
    
    public List<Point> getGrassPositions() { 
    	return grassPositions; 
    }
    
    public List<Rectangle> getCells() { 
    	return cells; 
    }
     
    //inventario
    public boolean isShowInventoryOverlay() { 
    	return showInventoryOverlay; 
    }
    
    //apertura inventario
    public void toggleInventoryOverlay() { 
    	showInventoryOverlay = !showInventoryOverlay; 
    }
    
    
    //messaggio
    public boolean isShowMessage() { 
    	return showMessage; 
    }
    
    public void hideMessage() { 
    	showMessage=false; 
    }
    
    public boolean isGameOverMessage() { 
    	return gameOverMessage; 
    }
    
    public void updateMessage() {
        if (showMessage && System.currentTimeMillis() - messageStartTime > MESSAGE_DURATION) {
            showMessage = false;
        }
    }
    
    public GameController getController() {
        return controller;
    }
    
    public boolean isKeyCollected() { 
    	return keyCollected; 
    }

    
}

