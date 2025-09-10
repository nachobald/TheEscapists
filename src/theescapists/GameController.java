package theescapists;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class GameController {
    private final GameModel model;
    private final GameView view;

    private Timer guardTimer;

    public GameController(GameModel model, GameView view) {
        this.model = model;
        this.view = view;

        setupKeyListener();
        startGuardTimer();
    }

    private void setupKeyListener() {
        view.setFocusable(true);
        

        view.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch(e.getKeyCode()) {
                    case KeyEvent.VK_W -> movePlayer(0,-1);
                    case KeyEvent.VK_S -> movePlayer(0,1);
                    case KeyEvent.VK_A -> movePlayer(-1,0);
                    case KeyEvent.VK_D -> movePlayer(1,0);
                    case KeyEvent.VK_I -> toggleInventory();
                    case KeyEvent.VK_SPACE -> usePickaxe();
                }
                model.updateMessage();
                view.repaint();
            }
        });
        
        SwingUtilities.invokeLater(view::requestFocusInWindow);
    }

    //movimento player
    private void movePlayer(int dx, int dy) {
        int newX = model.getPlayerX() + dx;
        int newY = model.getPlayerY() + dy;

        char[][] map = model.getMap();
        if(map[newY][newX] != '#') {
            model.setPlayerPosition(newX,newY);

            if(dx==1) model.setPlayerDir(Direction.RIGHT);
            else if(dx==-1) model.setPlayerDir(Direction.LEFT);
            else if(dy==1) model.setPlayerDir(Direction.DOWN);
            else if(dy==-1) model.setPlayerDir(Direction.UP);

            model.togglePlayerFrame();
        }

        checkForItems();
    }

    private void toggleInventory() {
        model.toggleInventoryOverlay();
        view.repaint();
    }

    private void usePickaxe() {
        for(Item item : new ArrayList<>(model.getInventory())) {
            if(item instanceof Pickaxe p) {
                digWall(p);
                break;
            }
        }
    }

    //raccolta oggetti e uscita
    private void checkForItems() {
        for(MapItem mi : model.getMapItems()) {
            if(!mi.isCollected() && model.getPlayerX()==mi.getX() && model.getPlayerY()==mi.getY()) {
                model.getInventory().add(mi.getItem());
                mi.collect();
                model.setGameMessage("Hai raccolto: " + mi.getItem().getName());
                if(mi.getItem() instanceof Key) model.setKeyCollected(true);
            }
        }

        int px = model.getPlayerX();
        int py = model.getPlayerY();
        if(model.getMap()[py][px]=='E') {
            if(px==model.getExitX() && py==model.getExitY()) {
                if(model.isKeyCollected()) gameOver("Complimenti! Sei evaso dalla porta principale!");
                else model.setGameMessage("La porta è chiusa, ti serve la chiave!");
            }
        }
    }

    //piccone
    public void digWall(Pickaxe pickaxe) {
        int[][] dirs = {{0,-1},{0,1},{-1,0},{1,0}};
        int px = model.getPlayerX();
        int py = model.getPlayerY();
        char[][] map = model.getMap();
        int[][] health = model.getWallHealth();

        for(int[] d : dirs) {
            int tx = px+d[0];
            int ty = py+d[1];

            if(map[ty][tx]=='#') {
                health[ty][tx]--;
                pickaxe.reduceDurability();

                if(pickaxe.isBroken()) {
                    model.getInventory().remove(pickaxe);
                    model.setGameMessage("Il piccone si è rotto!");
                } else {
                    model.setGameMessage("Hai scavato! Utilizzi rimasti: "+pickaxe.getDurability());
                }

                if(health[ty][tx]<=0) {
                    boolean isBorder = (tx==0||ty==0||tx==GameModel.MAP_WIDTH-1||ty==GameModel.MAP_HEIGHT-1);
                    if(isBorder) {
                        map[ty][tx]='T';
                        gameOver("Complimenti! Sei evaso scavando un tunnel!");
                        return;
                    } else map[ty][tx]='.';
                }
                model.updateMessage();
                view.repaint();
                return;
            }
        }
        model.setGameMessage("Non ci sono muri da scavare vicino a te.");
    }

    //timer guardie per muoversi
    private void startGuardTimer() {
        guardTimer = new Timer(450, e -> {
            for(Guard g : model.getGuards()) {
                moveGuard(g);
                g.toggleFrame();
            }

            // ollisione guardia-player
            for(Guard g : model.getGuards()) {
                if(g.getX()==model.getPlayerX() && g.getY()==model.getPlayerY()) {
                    gameOver("Sei stato catturato!");
                    return;
                }
            }
            model.updateMessage();
            view.repaint();
        });
        guardTimer.start();
    }

    private void moveGuard(Guard g) {
        int px = model.getPlayerX();
        int py = model.getPlayerY();
        char[][] map = model.getMap();
        int dist = Math.abs(g.getX()-px)+Math.abs(g.getY()-py);

        if(dist<=6) {
            //inseguimento
            if(g.getX()<px && map[g.getY()][g.getX()+1]!='#') { g.setX(g.getX()+1); g.setDirection(Direction.RIGHT);}
            else if(g.getX()>px && map[g.getY()][g.getX()-1]!='#') { g.setX(g.getX()-1); g.setDirection(Direction.LEFT);}
            else if(g.getY()<py && map[g.getY()+1][g.getX()]!='#') { g.setY(g.getY()+1); g.setDirection(Direction.DOWN);}
            else if(g.getY()>py && map[g.getY()-1][g.getX()]!='#') { g.setY(g.getY()-1); g.setDirection(Direction.UP);}
        } else {
            g.move(map); //movimento casuale
        }
    }

    //game over
    private void gameOver(String message) {
        guardTimer.stop();
        model.setGameOverMessage(true);
        model.setGameMessage(message);
        model.updateMessage();
        view.repaint();

        new Timer(2500, e -> {
            ((Timer)e.getSource()).stop();
            SwingUtilities.getWindowAncestor(view).dispose();
            System.exit(0);
        }).start();
    }
}
