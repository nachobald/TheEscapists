package theescapists;

public class GameMap {

	private int rows;
    private int cols;
    private char[][] map;

    public GameMap(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        map = new char[rows][cols];
        buildMap();
    }

    private void buildMap() {
        //implemento la mappa
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                map[y][x] = '.';
            }
        }

        //creo i muri perimetrali
        for (int x = 0; x < cols; x++) {
            map[0][x] = '#';
            map[rows - 1][x] = '#';
        }
        for (int y = 0; y < rows; y++) {
            map[y][0] = '#';
            map[y][cols - 1] = '#';
        }

        //muri interni
        for (int y = 2; y < rows - 2; y++) {
            map[y][4] = '#';
        }

        //posizione dell'uscita
        map[rows - 2][cols - 2] = 'E';
    }

    public void printMap(int playerX, int playerY) {
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                if (x == playerX && y == playerY) {
                    System.out.print('P');
                } else {
                    System.out.print(map[y][x]);
                }
            }
            System.out.println();
        }
    }

    public boolean isWalkable(int x, int y) {
        return map[y][x] != '#';
    }

    public boolean isExit(int x, int y) {
        return map[y][x] == 'E';
    }
    
}
