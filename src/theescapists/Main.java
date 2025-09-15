package theescapists;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            //model
            GameModel model = new GameModel();

            //view
            GameView view = new GameView(model);

            //controller
            GameController controller = new GameController(model, view);

            //collego il controller al model per gli Item
            model.setController(controller);

            //JFrame
            JFrame frame = new JFrame("TheEscapists MVC");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.add(view);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            view.requestFocusInWindow();
        });
    }
}
