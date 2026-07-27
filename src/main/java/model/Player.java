package model;

import java.util.ArrayList;


import java.util.List;
import java.util.Random;

/*
 * Player class contains information about each player's rack and points
 */

public class Player {
	
	private List<Tile> tileRack = new ArrayList<Tile>(); 
	private String playerName;
	private int points;
	
	/*
	 * Constructor assigns playerName, and the points to 0
	 */
	
	public Player() {
		playerName = "";
		points = 0;
	}
	
	/*
	 * Getter for name
	 */
	
	public String getPlayerName() {
		return playerName;
	}
	
	public void setPlayerName(String name) {
	    playerName = name;
	}
	
	/*
	 * Getters and Setters for points
	 */
	
	public int getPoints() {
		return points;
	}
	
	public void setPoints(int points) {
		this.points = points;
	}
	
	/*
	 * Checks if player has specific letter in their tile rack
	 */
	
	public boolean containsTile(String letter) {
		for(Tile tile : tileRack) {
			if(tile != null && tile.getLetter().equals(letter)) {
				return true;
			}
		}
		return false;
	}
	
	/*
	 * Return player's tiles in a rack
	 */
	
	public List<Tile> getTileRack() {
		return tileRack;		 
	}
	
	
	public void setTileRack(List<Tile> tileRack) {
		this.tileRack = tileRack;	
	}
	
	
	/*
	 * Create a random rack of tiles for the player
	 */
	
	public List<Tile> generateRack(ArrayList<Tile> tile_bag) {
	    Random rand = new Random(); // Generate random numbers
	    for (int i = 0; i < 7; i++) { // Iterate 7 times to fill the player's rack
	        int randomIndex = rand.nextInt(tile_bag.size()); // Get a random index within the tile_bag size
	        Tile tile = tile_bag.remove(randomIndex); // Remove the tile at the random index from the tile_bag
	        tileRack.add(tile); // Add the tile to the player's rack
	    }

	    return tileRack; // Return the updated player's rack
	}
	
	/*
	 * Refill rack once player has placed a word
	 */

	public void refillRack(ArrayList<Tile> tile_bag) {
	    Random rand = new Random(); // Create a new random object to generate random numbers
	    int rackSize = tileRack.size(); // Get the size of the player's rack
	    if (rackSize == 7 || tile_bag.isEmpty()) { // If the rack is full or the tile_bag is empty, return
	        return;
	    }

	    for (int i = rackSize; i < 7; i++) { // Iterate from the current rack size to 7 to fill the remaining spots
	        if (tile_bag.size() == 0) { // If the tile_bag is empty, break out of the loop
	            break;
	        }
	        int randomIndex = rand.nextInt(tile_bag.size()); // Get a random index within the tile_bag size
	        Tile tile = tile_bag.remove(randomIndex); // Remove the tile at the random index from the tile_bag
	        tileRack.add(tile); // Add the tile to the player's rack
	    }
	}
	
	/*
	 * Replace tiles from player's rack
	 */
	
	public void replaceTiles(ArrayList<Tile> tile_bag, List<Tile> tilesToReplace) {
	    // Remove the tiles to replace from the player's rack and add them back to the tile bag
	    for (Tile tile : tilesToReplace) {
	        removeTile(tile);
	        tile_bag.add(tile);
	    }

	    // Refill the player's rack with new tiles from the tile bag
	    refillRack(tile_bag);

	    // Print debugging statement to check the player's rack
	    System.out.println("Player's rack after replacing tiles: " + this.getTileRack());
	}

	/*
	 * Remove tile remove player's rack
	 */
	
	public void removeTile(Tile tile) {
	    tileRack.remove(tile);
	}
}
