package theescapists;

import javax.swing.JFrame;

public class Main {
	
	public static void main(String[] args) {
		
        JFrame frame = new JFrame("TheEscapists");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        GamePanel panel = new GamePanel(50, 50, 15, 15); // mappa 50x50, viewport 15x15
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        panel.startGame();
    }
	
}
