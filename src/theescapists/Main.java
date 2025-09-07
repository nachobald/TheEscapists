package theescapists;

import javax.swing.JFrame;

public class Main {
	
	public static void main(String[] args) {

		JFrame frame = new JFrame("TheEscapists");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

		GamePanel panel = new GamePanel(); //senza parametri
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

		panel.startGame(); //mette il focus sulla finestra
		    }
		

	
}
