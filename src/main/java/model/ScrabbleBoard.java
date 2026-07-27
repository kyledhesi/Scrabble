package model;


import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import controller.ScrabbleMain;
import view.pvcBoard;
import view.pvpBoard;

/*
 * Scrabble board class represents the internal game board & manages operations - placing tiles, word validation, replacing tiles and score calculations
 */

public class ScrabbleBoard {
	private static Tile[][] gameBoard = new Tile[15][15];	// 15x15 matrix to represent the gameboard
	private static ArrayList<Tile> lettersInPlay;	// ArrayList containing all tiles on the board
	private static Stack<Tile> placedTiles = new Stack<>();	// Stack containing all tiles placed in a turn
	
	/*
	 * Contructor that initalises lettersInPlay 
	 */
	
	public ScrabbleBoard() {
	    lettersInPlay = new ArrayList<>();
	}
	
	/*
	 * Getter for gameBoard
	 */

	public static Tile[][] getGameBoard() {
		return gameBoard;
	}
	
	/*
	 * Setter for gameBoard
	 */

	public static void setGameBoard(Tile[][] gameBoard) {
		ScrabbleBoard.gameBoard = gameBoard;
	}

	/*
	 * Places word onto the board - returns true if tile is successfully placed
	 */

	public static boolean placeTile(int row, int col, Tile tile) {
	    if (gameBoard[row][col] != null && gameBoard[row][col].isLocked()) {	// Tile is locked, tile is not placed over it
	        return false;
	    }   
	    gameBoard[row][col] = tile;	// Place tile on the board
	    return true;
	}

	/*
	 * Checks if board contains a certain letter at given row and column - returns the tile at specified location 
	 */
	
	public static Tile boardContains(int row, int col) {
		return gameBoard[row][col];
	}
	
	/*
	 * Getter for lettersInPlay
	 */
	
	public static List<Tile> getTiles() {
        return lettersInPlay;
    }
	
	
	public static Stack<Tile> getPlacedTiles() {
		return placedTiles;
	}

	public static void setPlacedTiles(Stack<Tile> placedTiles) {
		ScrabbleBoard.placedTiles = placedTiles;
	}
	
	/*
	 * Removes tile from board
	 */
	
	public static void removeTileFromBoard(Tile tile) {
	    for (int row = 0; row < gameBoard.length; row++) {	// Iterate through the gameBoard to find the tile
	        for (int col = 0; col < gameBoard[row].length; col++) {
	            if (gameBoard[row][col] == tile) {
	                if (!tile.isLocked()) {		// Check if the tile is not locked
	                    gameBoard[row][col] = null;	// If not locked - remove tile from gameBoard and lettersInPlay array
	                    lettersInPlay.remove(tile);
	                }
	                return;	//If the tile is locked, it can't be removed from the board and no changes are made
	            }
	        }
	    }
	}
	
	/*
	 * Gets the words formed from the placed tiles during the current turn
	 */
	
	public static List<String> getFormedWords() {
	    List<String> formedWords = new ArrayList<>(); // List to store the formed words
	    for (Tile tile : getPlacedTiles()) { // Iterate through the placedTiles
	        for (int row = 0; row < gameBoard.length; row++) { // Iterate through the gameBoard to find the tile
	            for (int col = 0; col < gameBoard[row].length; col++) {
	                if (gameBoard[row][col] == tile) { // If the current position on the board has the tile
	                    String horizontalWord = getWordAt(row, col, true); // Get the horizontal word at the tile's position
	                    String verticalWord = getWordAt(row, col, false); // Get the vertical word at the tile's position

	                    // Add the horizontal word to the formedWords list if it's not a single letter and not already in the list
	                    if (horizontalWord.length() > 1 && !formedWords.contains(horizontalWord)) {
	                        formedWords.add(horizontalWord);
	                    }
	                    // Add the vertical word to the formedWords list if it's not a single letter and not already in the list
	                    if (verticalWord.length() > 1 && !formedWords.contains(verticalWord)) {
	                        formedWords.add(verticalWord);
	                    }
	                }
	            }
	        }
	    }
	    return formedWords; // Returns the list of the words formed
	}
	
	/*
	 * Gets the word constructed from the placed tiles horizontally or vertically
	 */

	public static String getWordAt(int row, int col, boolean isHorizontal) {
	    StringBuilder wordBuilder = new StringBuilder(); // StringBuilder to construct the word
	    int[][] directions = {{0, 1}, {1, 0}}; // Directions array - horizontal and vertical
	    int[] direction = directions[isHorizontal ? 0 : 1]; // Select the direction based on isHorizontal flag

	    // Move to the start of the word in the specified direction
	    while (row >= 0 && row < gameBoard.length && col >= 0 && col < gameBoard[row].length && gameBoard[row][col] != null) {
	        row -= direction[0];    // Move row in the opposite direction
	        col -= direction[1];    // Move col in the opposite direction
	    }
	    row += direction[0]; // Adjust row back to valid position
	    col += direction[1]; // Adjust col back to valid position

	    // Construct the word
	    while (row >= 0 && row < gameBoard.length && col >= 0 && col < gameBoard[row].length && gameBoard[row][col] != null) {
	        wordBuilder.append(gameBoard[row][col].getLetter()); // Append the letter at the current position to the word
	        row += direction[0]; // Move to the next position in the direction
	        col += direction[1]; // Move to the next position in the direction
	    }
	    return wordBuilder.toString(); // Return the constructed word
	}
	
	/*
	 * Creates a copy of the current gameboard state and can be reverted using revertBoardState method
	 */
	
	public static Tile[][] saveBoardState() {
	    Tile[][] savedBoardState = new Tile[15][15];
	    for (int row = 0; row < gameBoard.length; row++) {
	        for (int col = 0; col < gameBoard[row].length; col++) {
	            savedBoardState[row][col] = gameBoard[row][col];
	        }
	    }
	    return savedBoardState;
	}
	
	/*
	 * Revert the board state back to the last savedBoardState
	 */
	
	public static void revertBoardState(Tile[][] savedBoardState) {
	    for (int row = 0; row < gameBoard.length; row++) {
	        for (int col = 0; col < gameBoard[row].length; col++) {
	            gameBoard[row][col] = savedBoardState[row][col];
	        }
	    }
	}
	
	/*
     * Checks if the placed word is valid and conforms to the rules of Scrabble
     */
	
	public static boolean areWordsValid(boolean isFirstMove) {
        List<String> formedWords = getFormedWords();

        if (formedWords.isEmpty()) {
            System.out.println("No words formed by the placed tiles");
            return false;
        }
        if (isFirstMove && gameBoard[7][7] == null) {
            System.out.println("Center star is not covered on the first move");
            return false;
        }
        
        for (String word : formedWords) {
            // Check if the word is in the dictionary
            if (!ScrabbleMain.dictionary.getWords().contains(word)) {
                System.out.println("Word is not in dictionary: " + word);
                return false;
            }
            int index = ScrabbleMain.dictionary.getWords().indexOf(word);
            System.out.println("Valid word found in dictionary: " + word + " at index " + index);
        }
        
        if (!isFirstMove && !areTilesConnected()) {
	        System.out.println("The placed word is not adjacent to any other word");
	        return false;
	    }

        return true;
    }
	
	/*
	 * Checks if the placed tiles are connected to existing words on the board 
	 * returns true if at least one of the placed tiles is adjacent to an existing word.
	 */
	
	public static boolean areTilesConnected() {
	    
	    if (getPlacedTiles().isEmpty()) {
	        return false;	// If there are no placed tiles, return false
	    }
	    
	    // Initialise the isConnectedToExistingWords flag as false
	    boolean isConnectedToExistingWords = false;
	    for (Tile tile : getPlacedTiles()) {	// Iterate through all placed tiles
	        // Iterate through the game board to find the row and column of the current tile
	        for (int row = 0; row < gameBoard.length; row++) {
	            for (int col = 0; col < gameBoard[row].length; col++) {
	                // If the current position on the board has the tile
	                if (gameBoard[row][col] == tile) {
	                    // Check if there are existing words adjacent to the placed tile
	                    // If isConnectedToExistingWords is already true, skip the check to save time
	                    if (!isConnectedToExistingWords) {
	                        isConnectedToExistingWords = checkAdjacentWords(row, col);
	                    }
	                }
	            }
	        }
	    }
	    return isConnectedToExistingWords; // Return true if at least one of the placed tiles is adjacent to an existing word, false otherwise
	}
	
	/*
	 * Checks if there are any locked tiles adjacent to the tile at the given row and col - returns true if at least one locked tile is found in the adjacent square
	 */
	
	public static boolean checkAdjacentWords(int row, int col) {
	    int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};	// an array of possible directions to check for adjacent locked tiles

	    // Iterate through the defined directions
	    for (int[] direction : directions) {
	        // Calculate the new row and col based on the current direction
	        int newRow = row + direction[0];
	        int newCol = col + direction[1];

	        // Check if the newRow and newCol are within the bounds of the game board
	        if (newRow >= 0 && newRow < gameBoard.length && newCol >= 0 && newCol < gameBoard[newRow].length) {
	            // Check if there is a tile at the new position and if it is locked
	            if (gameBoard[newRow][newCol] != null && gameBoard[newRow][newCol].isLocked()) {
	                return true; // If a locked tile is found in the adjacent square, return true
	            }
	        }
	    }
	    return false;  // If no locked tiles are found in any adjacent squares, return false
	}

	
	/*
	 * Locks the tiles on the board so they can't be removed by iterating through the board and setting the tiles to locked
	 */
	
	public static void lockPlacedTiles() {
	    for (int row = 0; row < gameBoard.length; row++) {
	        for (int col = 0; col < gameBoard[row].length; col++) {
	            if (gameBoard[row][col] != null && !gameBoard[row][col].isLocked()) {
	                gameBoard[row][col].setLocked(true);
	            }
	        }
	    }
	}
	
	/*
	 * Calculate the score for a turn taking into account score modifiers
	 */
	
	public static int calculateTotalScore(boolean isFirstTurn) {
	    int totalScore = 0; // Initialise the total score for the turn
	    List<String> formedWords = getFormedWords(); // Get the list of words formed by the placed tiles
	    boolean[][] usedSpecialSquares = new boolean[gameBoard.length][gameBoard[0].length]; // Create a 2D boolean array to track if special squares have been used

	    for (String word : formedWords) { // Iterate through each formed word
	        int wordScore = 0; // Initialise the score for the current word
	        int wordMultiplier = 1; // Initialise the word multiplier for the current word

	        for (int i = 0; i < word.length(); i++) { // Iterate through each letter in the word
	            String letter = String.valueOf(word.charAt(i)); // Get the current letter as a string
	            int letterScore = 0; // Initialise the score for the current letter
	            int letterMultiplier = 1; // Initialise the letter multiplier for the current letter
	            int row = -1; // Initialise the row of the current letter on the board
	            int col = -1; // Initialise the column of the current letter on the board
	            boolean found = false; // Add this boolean variable to track if the correct instance of the letter has been found

	            // Find the position of the letter on the board
	            outerloop:
	            	
	            	for (int r = 0; r < gameBoard.length; r++) {	// Iterate through the rows of gameBoard array
	            	    for (int c = 0; c < gameBoard[r].length; c++) { // Iterarte through the cols of gameBoard array
	            	        
	            	       
	            	        if (gameBoard[r][c] != null && gameBoard[r][c].getLetter().equals(letter)) { // Check if the current cell is not null and contains the required letter
	            	            row = r;
	            	            col = c;
	            	            
	            	            // Get the horizontal and vertical words formed at the current cell
	            	            String horizontalWord = getWordAt(row, col, true);
	            	            String verticalWord = getWordAt(row, col, false);

	            	            // Check if either the horizontal or vertical word matches the given word
	            	            if (horizontalWord.equals(word) || verticalWord.equals(word)) {
	            	                letterScore = gameBoard[row][col].getScore();	// Get the score of the letter found in the game board
	                            found = true; // Mark the correct instance of the letter as found

	                            // Only count the score for the newly placed tiles
	                            if (getPlacedTiles().contains(gameBoard[r][c])) {
	                                // Check for double letter score
	                                if (pvpBoard.isDoubleLetter(row, col) && !usedSpecialSquares[row][col]) {
	                                    letterMultiplier = 2;
	                                    usedSpecialSquares[row][col] = true;
	                                }
	                                // Check for triple letter score
	                                if (pvpBoard.isTripleLetter(row, col) && !usedSpecialSquares[row][col]) {
	                                    letterMultiplier = 3;
	                                    usedSpecialSquares[row][col] = true;
	                                }
	                            }

	                            wordScore += letterScore * letterMultiplier;

	                            // Check for double word score
	                            if (pvpBoard.isDoubleWord(row, col) && getPlacedTiles().contains(gameBoard[row][col]) && !usedSpecialSquares[row][col]) {
	                                wordMultiplier *= 2;
	                                usedSpecialSquares[row][col] = true;
	                            }
	                            // Check for triple word score
	                            if (pvpBoard.isTripleWord(row, col) && getPlacedTiles().contains(gameBoard[row][col]) && !usedSpecialSquares[row][col]) {
	                                wordMultiplier *= 3;
	                                usedSpecialSquares[row][col] = true;
	                            }
	                            
	                            break; // Remove the outerloop label and use a simple break
	                        }
	                    }
	                    if (found) break; // Break out of the inner loop if the correct instance of the letter has been found
	                }
	                if (found) break; // Break out of the outer loop if the correct instance of the letter has been found        
	            }
	        }
	        totalScore += wordScore * wordMultiplier;
	    }

	    if (isFirstTurn) {
	        totalScore *= 2;
	    }
	    
	    if(getPlacedTiles().size()==7) {
	    	totalScore += 50;
	    }

	    System.out.println(totalScore);
	    return totalScore;
	}

	
	public static int calculateTotalScoreAI(boolean isFirstTurn) {
	    int totalScore = 0;
	    List<String> formedWords = getFormedWords();
	    boolean[][] usedSpecialSquares = new boolean[gameBoard.length][gameBoard[0].length];

	    for (String word : formedWords) {
	        int wordScore = 0;
	        int wordMultiplier = 1;

	        for (int i = 0; i < word.length(); i++) {
	            String letter = String.valueOf(word.charAt(i));
	            int letterScore = 0;
	            int letterMultiplier = 1;
	            int row = -1;
	            int col = -1;
	            boolean found = false; // Add this boolean variable to track if the correct instance of the letter has been found

	            // Find the position of the letter on the board
	            outerloop:
	            for (int r = 0; r < gameBoard.length; r++) {
	                for (int c = 0; c < gameBoard[r].length; c++) {
	                    if (gameBoard[r][c] != null && gameBoard[r][c].getLetter().equals(letter)) {
	                        row = r;
	                        col = c;
	                        String horizontalWord = getWordAt(row, col, true);
	                        String verticalWord = getWordAt(row, col, false);

	                        if (horizontalWord.equals(word) || verticalWord.equals(word)) {
	                            letterScore = gameBoard[row][col].getScore();
	                            found = true; // Mark the correct instance of the letter as found

	                            // Only count the score for the newly placed tiles
	                            if (getPlacedTiles().contains(gameBoard[r][c])) {
	                                // Check for double letter score
	                                if (pvcBoard.isDoubleLetter(row, col) && !usedSpecialSquares[row][col]) {
	                                    letterMultiplier = 2;
	                                    usedSpecialSquares[row][col] = true;
	                                }
	                                // Check for triple letter score
	                                if (pvcBoard.isTripleLetter(row, col) && !usedSpecialSquares[row][col]) {
	                                    letterMultiplier = 3;
	                                    usedSpecialSquares[row][col] = true;
	                                }
	                            }

	                            wordScore += letterScore * letterMultiplier;

	                            // Check for double word score
	                            if (pvcBoard.isDoubleWord(row, col) && getPlacedTiles().contains(gameBoard[row][col]) && !usedSpecialSquares[row][col]) {
	                                wordMultiplier *= 2;
	                                usedSpecialSquares[row][col] = true;
	                            }
	                            // Check for triple word score
	                            if (pvcBoard.isTripleWord(row, col) && getPlacedTiles().contains(gameBoard[row][col]) && !usedSpecialSquares[row][col]) {
	                                wordMultiplier *= 3;
	                                usedSpecialSquares[row][col] = true;
	                            }

	                            break; // Remove the outerloop label and use a simple break
	                        }
	                    }
	                    if (found) break; // Break out of the inner loop if the correct instance of the letter has been found
	                }
	                if (found) break; // Break out of the outer loop if the correct instance of the letter has been found        
	            }
	        }
	        totalScore += wordScore * wordMultiplier;
	    }

	    if (isFirstTurn) {
	        totalScore *= 2;
	    }
	    
	    if(getPlacedTiles().size()==7) {
	    	totalScore += 50;
	    }

	    System.out.println(totalScore);
	    return totalScore;
	}

	
	public static void printGameBoard() {
	    for (int row = 0; row < gameBoard.length; row++) {
	        for (int col = 0; col < gameBoard[row].length; col++) {
	            if (gameBoard[row][col] == null) {
	                System.out.print("_ ");
	            } else {
	                System.out.print(gameBoard[row][col].getLetter() + " ");
	            }
	        }
	        System.out.println();
	    }
	}
}