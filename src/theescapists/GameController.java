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
    	Player player = model.getPlayer();                          
        char[][] map = model.getMap();                             
        int newX = player.getX() + dx;
        int newY = player.getY() + dy;
        if(map[newY][newX] != '#') {
            player.setX(newX);                                
            player.setY(newY);

            if(dx==1) player.setDir(Direction.RIGHT);                
            else if(dx==-1) player.setDir(Direction.LEFT);
            else if(dy==1) player.setDir(Direction.DOWN);
            else if(dy==-1) player.setDir(Direction.UP);

            player.toggleFrame();                                      
        }

        checkForItems();
        model.updateMessage();
        view.repaint();
    }

    private void toggleInventory() {
        model.toggleInventoryOverlay();
        view.repaint();
    }

    private void usePickaxe() {
        for(Item item : new ArrayList<>(model.getInventory().getItems())) {
            if(item instanceof Pickaxe p) {
                digWall(p);
                break;
            }
        }
    }

    //raccolta oggetti e uscita
    private void checkForItems() {
    	Player player = model.getPlayer();                            
        for(MapItem mi : model.getMapItems()) {
            if(!mi.isCollected() && player.getX()==mi.getX() && player.getY()==mi.getY()) {  
                model.getInventory().add(mi.getItem());
                mi.collect();
                model.setGameMessage("Hai raccolto: " + mi.getItem().getName());
                if(mi.getItem() instanceof Key) model.setKeyCollected(true);
            }
        }

        int px = player.getX();                                         
        int py = player.getY();                                         
        if(model.getMap()[py][px]=='E') {
            if(px==model.getExitX() && py==model.getExitY()) {
                if(model.isKeyCollected()) gameOver("Complimenti! Sei evaso dalla porta principale!");
                else model.setGameMessage("La porta è chiusa, ti serve la chiave!");
            }
        }
    }

    //piccone
    public void digWall(Pickaxe pickaxe) {
    	Player player = model.getPlayer();                               
        int px = player.getX();
        int py = player.getY();
        char[][] map = model.getMap();
        int[][] health = model.getWallHealth();
        for(int[] d : new int[][] {{0,-1},{0,1},{-1,0},{1,0}}) {
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
            Player player = model.getPlayer();                          
            for(Guard g : model.getGuards()) {
                moveGuard(g, player);                                    
                g.toggleFrame();
            }

            // collisione guardia-player
            for(Guard g : model.getGuards()) {
                if(g.getX()==player.getX() && g.getY()==player.getY()) {  
                    gameOver("Sei stato catturato!");
                    return;
                }
            }
            model.updateMessage();
            view.repaint();
        });
        guardTimer.start();
    }

    private void moveGuard(Guard g, Player player) {
        int px = player.getX();
        int py = player.getY();
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
