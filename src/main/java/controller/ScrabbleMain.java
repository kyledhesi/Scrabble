package controller;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import model.Dictionary;
import model.LetterTree;
import model.Player;
import model.ScrabbleBoard;
import model.Tile;
import model.aiPlayer;
import view.AddPlayerName;
import view.ScrabbleMenu;


/*
 * Main class which runs the game 
 */

public class ScrabbleMain {
	public static ArrayList<Tile> tile_bag; // An ArrayList to hold the tiles in the game.
	public static int player_turn; // Represents the current player's turn.
	public static Player playerOne = new Player(); // Create player one of our game 
	public static Player playerTwo = new Player(); // Create player two of our game 
	public static aiPlayer AI; // Create AI opponent // Create AI player object.
	public static boolean singlePlayer; // Indicates if the game is in single-player mode.
	public static Dictionary dictionary; // Initalise dictionary
	public static ScrabbleBoard scrabbleBoard; // Initalise scrabbleBoard
	public static LetterTree letterTree; // Initialise letterTree
	public static boolean gameOver; //	gameOver check
	
	
	public ScrabbleMain() throws Exception {
	    tile_bag = createTileBag();	// Initialises the tile_bag with tiles
	    player_turn = 0;	// Set the intitial player turn
	    letterTree = LetterTree.basic_english();	// Initialises the letter tree
	    AI = new aiPlayer(letterTree);	// Initialises the AI player
		AddPlayerName addPlayerName = new AddPlayerName(playerOne, playerTwo, AI, tile_bag, singlePlayer);	// Initialise players' names
		dictionary = new Dictionary("dictionary.txt");	// Load the dictionary
		scrabbleBoard = new ScrabbleBoard();	// Initialise the scrabble board
	}
	
	/*
	 * Create the tile_bag from reading file
	 */

	public static ArrayList<Tile> createTileBag() throws IOException {
		ArrayList<Tile> tile_bag = new ArrayList(); 
		File tileBagFile = new File("tile.txt"); 
		Scanner scanner = new Scanner(new FileReader(tileBagFile)); 
		
		while(scanner.hasNext()) {
			String[] tileData = scanner.next().split(","); 
			String letter = tileData[0];
			int points = Integer.parseInt(tileData[1]); 
			int amount = Integer.parseInt(tileData[2]);
			for (int i = 0; i < amount; i++) {
				Tile tile = new Tile(letter, points); 
				tile_bag.add(tile); 
			}	
		}
		scanner.close();
		return tile_bag;
	}
	
	/*
	 * Get player's turn
	 */

	public int getPlayerTurn() {
		return player_turn; 
	}
	
	/*
	 * Increase player's turn 
	 */
	
	public static void increasePlayerTurn() {
		player_turn++;  
	}
	
	/*
	 * Check if the game is over  
	 */
	
	public static boolean isGameOver() {
	    if (tile_bag.isEmpty()) {
	        if (playerOne.getTileRack().size() == 0 || playerTwo.getTileRack().size() == 0) {
	            return true;
	        }
	    } 
	    
	    if (gameOver) {
	        return true;
	    }
	    return false;
	}
	
	/*
	 * Declare the winner and minus any tiles left in the rack
	 */
	
	private void declareWinner() {
	    if (ScrabbleMain.isGameOver() == true) {
	        int playerOneTileScore = 0;
	        for (Tile tile : playerOne.getTileRack()) {
	            playerOneTileScore += tile.getScore();
	        }

	        int playerTwoTileScore = 0;
	        for (Tile tile : playerTwo.getTileRack()) {
	            playerTwoTileScore += tile.getScore();
	        }

	        playerOne.setPoints(playerOne.getPoints() - playerOneTileScore);
	        playerTwo.setPoints(playerTwo.getPoints() - playerTwoTileScore);
	    }
	}
	
	/*
	 * Start a new game
	 */

	public void resetGame() throws Exception {
		playerOne = new Player(); 
	    playerTwo = new Player(); 
	    tile_bag = createTileBag();
	    player_turn = 0;
	    letterTree = LetterTree.basic_english();
	    AI = new aiPlayer(letterTree);
	    AddPlayerName addPlayerName = new AddPlayerName(playerOne, playerTwo, AI, tile_bag, singlePlayer);
	    dictionary = new Dictionary("dictionary.txt");
	    scrabbleBoard = new ScrabbleBoard();
	}

	
	public static void main(String[] args) {
        try {
            ScrabbleMenu mainMenu = new ScrabbleMenu();
            mainMenu.setVisible(true);
            ScrabbleMain scrabbleMain = new ScrabbleMain();
            scrabbleMain.resetGame();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
