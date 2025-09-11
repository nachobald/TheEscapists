package theescapists;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class GameView extends JPanel {

    private final GameModel model;
    private static final int TILE_SIZE = 32;
    private static final int SCREEN_WIDTH = 20 * TILE_SIZE;
    private static final int SCREEN_HEIGHT = 15 * TILE_SIZE;

    //tile oggetti
    private BufferedImage muroTile, pavimentoTile, keyTile, pickaxeTile,
            lanternTile, treeTile, doorTile, chestTile, grassTile;

    //player
    private BufferedImage[] playerUp = new BufferedImage[2];
    private BufferedImage[] playerDown = new BufferedImage[2];
    private BufferedImage[] playerLeft = new BufferedImage[2];
    private BufferedImage[] playerRight = new BufferedImage[2];

    //guardie
    private BufferedImage[] guardUp = new BufferedImage[2];
    private BufferedImage[] guardDown = new BufferedImage[2];
    private BufferedImage[] guardLeft = new BufferedImage[2];
    private BufferedImage[] guardRight = new BufferedImage[2];

    public GameView(GameModel model) {
        this.model = model;
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setBackground(Color.BLACK);
        loadImages();
    }

    private void loadImages() {
        try {
            muroTile = ImageIO.read(getClass().getResource("/theescapists/assets/wall.png"));
            pavimentoTile = ImageIO.read(getClass().getResource("/theescapists/assets/flooring.png"));
            keyTile = ImageIO.read(getClass().getResource("/theescapists/assets/key.png"));
            pickaxeTile = ImageIO.read(getClass().getResource("/theescapists/assets/pickaxe.png"));
            lanternTile = ImageIO.read(getClass().getResource("/theescapists/assets/lantern.png"));
            treeTile = ImageIO.read(getClass().getResource("/theescapists/assets/tree.png"));
            doorTile = ImageIO.read(getClass().getResource("/theescapists/assets/door.png"));
            chestTile = ImageIO.read(getClass().getResource("/theescapists/assets/chest.png"));
            grassTile = ImageIO.read(getClass().getResource("/theescapists/assets/grass.png"));

            playerUp[0] = ImageIO.read(getClass().getResource("/theescapists/assets/prisoner_up_1.png"));
            playerUp[1] = ImageIO.read(getClass().getResource("/theescapists/assets/prisoner_up_2.png"));
            playerDown[0] = ImageIO.read(getClass().getResource("/theescapists/assets/prisoner_down_1.png"));
            playerDown[1] = ImageIO.read(getClass().getResource("/theescapists/assets/prisoner_down_2.png"));
            playerLeft[0] = ImageIO.read(getClass().getResource("/theescapists/assets/prisoner_left_1.png"));
            playerLeft[1] = ImageIO.read(getClass().getResource("/theescapists/assets/prisoner_left_2.png"));
            playerRight[0] = ImageIO.read(getClass().getResource("/theescapists/assets/prisoner_right_1.png"));
            playerRight[1] = ImageIO.read(getClass().getResource("/theescapists/assets/prisoner_right_2.png"));

            guardUp[0] = ImageIO.read(getClass().getResource("/theescapists/assets/guard_up_1.png"));
            guardUp[1] = ImageIO.read(getClass().getResource("/theescapists/assets/guard_up_2.png"));
            guardDown[0] = ImageIO.read(getClass().getResource("/theescapists/assets/guard_down_1.png"));
            guardDown[1] = ImageIO.read(getClass().getResource("/theescapists/assets/guard_down_2.png"));
            guardLeft[0] = ImageIO.read(getClass().getResource("/theescapists/assets/guard_left_1.png"));
            guardLeft[1] = ImageIO.read(getClass().getResource("/theescapists/assets/guard_left_2.png"));
            guardRight[0] = ImageIO.read(getClass().getResource("/theescapists/assets/guard_right_1.png"));
            guardRight[1] = ImageIO.read(getClass().getResource("/theescapists/assets/guard_right_2.png"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        char[][] map = model.getMap();
        Player player = model.getPlayer();
        int playerX = player.getX();
        int playerY = player.getY();

        //calcolo offset telecamera
        int camX = playerX * TILE_SIZE - SCREEN_WIDTH / 2;
        int camY = playerY * TILE_SIZE - SCREEN_HEIGHT / 2;
        camX = Math.max(0, Math.min(camX, map[0].length * TILE_SIZE - SCREEN_WIDTH));
        camY = Math.max(0, Math.min(camY, map.length * TILE_SIZE - SCREEN_HEIGHT));

        //disegna mappa
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                BufferedImage tileImg = null;
                switch (map[y][x]) {
                    case '#': tileImg = muroTile; break;
                    case '.': tileImg = pavimentoTile; break;
                    case 'E': tileImg = doorTile; break;
                    case 'T': g.setColor(Color.ORANGE); g.fillRect(x*TILE_SIZE - camX,y*TILE_SIZE - camY,TILE_SIZE,TILE_SIZE); continue;
                }
                if (tileImg != null) g.drawImage(tileImg,x*TILE_SIZE - camX,y*TILE_SIZE - camY,TILE_SIZE,TILE_SIZE,null);
            }
        }

        //elementi mappa
        drawPoints(g, model.getGrassPositions(), grassTile, camX, camY);
        drawPoints(g, model.getTreePositions(), treeTile, camX, camY);
        drawPoints(g, model.getLanternPositions(), lanternTile, camX, camY);
        drawPoints(g, model.getChestPositions(), chestTile, camX, camY);

        //oggetti sulla mappa
        for (MapItem mi : model.getMapItems()) {
            if (!mi.isCollected()) {
                BufferedImage img = (mi.getItem() instanceof Key) ? keyTile : pickaxeTile;
                g.drawImage(img, mi.getX()*TILE_SIZE - camX, mi.getY()*TILE_SIZE - camY, TILE_SIZE, TILE_SIZE, null);
            }
        }

        //player
        BufferedImage[] pFrames = switch (player.getDir()) {
            case UP -> playerUp;
            case DOWN -> playerDown;
            case LEFT -> playerLeft;
            case RIGHT -> playerRight;
        };
        
        g.drawImage(pFrames[player.getFrame()], playerX*TILE_SIZE - camX, playerY*TILE_SIZE - camY, TILE_SIZE, TILE_SIZE, null);

        //guardie
        for (Guard guard : model.getGuards()) {
            BufferedImage[] gFrames = switch (guard.getDirection()) {
                case UP -> guardUp;
                case DOWN -> guardDown;
                case LEFT -> guardLeft;
                case RIGHT -> guardRight;
            };
            g.drawImage(gFrames[guard.getFrame()], guard.getX()*TILE_SIZE - camX, guard.getY()*TILE_SIZE - camY, TILE_SIZE, TILE_SIZE, null);
        }

        //messaggi
        if (model.isShowMessage()) {
            String msg = model.getGameMessage();
            g.setFont(new Font("Arial", Font.BOLD, 18));
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(msg);
            int textHeight = fm.getHeight();

            //centra il messaggio nello schermo
            int rectWidth = textWidth + 40;
            int rectHeight = textHeight + 20;
            int rectX = (SCREEN_WIDTH - rectWidth) / 2;
            int rectY = 50; //distanza dal top

            //sfondo semi-trasparente
            g.setColor(new Color(0, 0, 0, 180));
            g.fillRoundRect(rectX, rectY, rectWidth, rectHeight, 10, 10);

            //testo bianco centrato
            g.setColor(Color.WHITE);
            int textX = rectX + (rectWidth - textWidth) / 2;
            int textY = rectY + ((rectHeight - textHeight) / 2) + fm.getAscent();
            g.drawString(msg, textX, textY);
        }
        
        if (model.isShowInventoryOverlay()) {
            drawInventory(g);
        }
    }

    //disegna oggetti
    private void drawPoints(Graphics g, List<Point> points, BufferedImage img, int camX, int camY) {
        for (Point p : points) {
            g.drawImage(img, p.x*TILE_SIZE - camX, p.y*TILE_SIZE - camY, TILE_SIZE, TILE_SIZE, null);
        }
    }
    
    //disegna inventario
    private void drawInventory(Graphics g) {
        List<Item> inventory = model.getInventory();
        int overlayWidth = 220;
        int overlayHeight = inventory.isEmpty() ? 60 : inventory.size() * 40 + 40;
        int marginTop = 40;
        int marginRight = 20;
        int x = getWidth() - overlayWidth - marginRight;
        int y = marginTop;

        g.setColor(new Color(0,0,0,180));
        g.fillRoundRect(x,y,overlayWidth,overlayHeight,15,15);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD,16));
        g.drawString("Inventario", x+10, y+25);

        int itemY = y+40;
        for (Item item : inventory) {
            g.setColor(new Color(50,50,50,200));
            g.fillRect(x+10, itemY, 32,32);
            g.setColor(Color.WHITE);
            g.drawRect(x+10, itemY, 32,32);

            if(item instanceof Key) g.drawImage(keyTile,x+12,itemY+2,28,28,null);
            else if(item instanceof Pickaxe) g.drawImage(pickaxeTile,x+12,itemY+2,28,28,null);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN,14));
            String label = item.getName();
            if(item instanceof Pickaxe p) label += " ("+p.getDurability()+"/3)";
            g.drawString(label,x+32+20,itemY+32/2+5);

            itemY += 32 + 8;
        }
    }
}
