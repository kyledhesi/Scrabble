package model;

public class BoardPosition {
    private int row;
    private int col;
    private boolean isHorizontal;

    public BoardPosition(int row, int col, boolean isHorizontal) {
        this.row = row;
        this.col = col;
        this.isHorizontal = isHorizontal;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public boolean isHorizontal() {
        return isHorizontal;
    }

    public void setHorizontal(boolean isHorizontal) {
        this.isHorizontal = isHorizontal;
    }
}
