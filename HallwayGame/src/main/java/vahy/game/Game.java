package vahy.game;

import vahy.game.cell.Cell;

import java.util.List;

public class Game {

    private final List<List<Cell>> cellField;

    public Game(List<List<Cell>> cellField) {
        this.cellField = cellField;
    }

    public String serializeToString() {
        return null;
    }
}
