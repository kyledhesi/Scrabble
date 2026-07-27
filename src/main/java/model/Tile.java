package model;

import view.TileGUI;


/*
 * Tile class represents a tile in game, with properties letter, points and board location
 */

public class Tile {
	private String letter;  
	private int score = 0;    
	private int row = -1;      
	private int col = -1;     
	private TileGUI tileGUI;
	      
	private boolean locked;
	
	public Tile(String letter, int score) {
		this.letter = letter;   
	    this.score = score;  
	    tileGUI = new TileGUI(this);
	}
	
	public boolean isBlank() {
		if(letter.equals("_")) {
			return true;
		}
		return false;
	}
	  
	public String getLetter() {
		return letter;	
	}
	   
	   
	public void setLetter(String letter) {
		this.letter = letter;
		this.tileGUI = new TileGUI(this);
	}
	
	public int getScore() {
		return score;	
	}
	
	public void setScore(int score) {
		this.score = score;	
	}
	   
	public int getRow() {
		return row;	
	} 
	  
	public int getCol() {
		return col;	
	}	
	  
	public String toString() {
		return letter;
	}
	   
	public TileGUI getTileGUI() {
		return tileGUI;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public void setCol(int col) {
		this.col=col;
	}   
	   
	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}
}
