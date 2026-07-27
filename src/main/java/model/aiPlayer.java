package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import controller.ScrabbleMain;

public class aiPlayer extends Player {
    
    private final LetterTree letterTree;

    public aiPlayer(LetterTree letterTree) {
    	super();
        this.letterTree = letterTree;
    }
    
    /*
     * Generate all the possible words that can be placed from tile rack and tiles on the board
     */
    
    public List<String> generatePossibleWords(ScrabbleBoard board, List<Tile> tileRack) {
        List<String> possibleWords = new ArrayList<>(); // Initialise a list to store possible words
        List<Tile> lockedTiles = getLockedTiles(board); // Get all the locked tiles

        // Iterate through each locked tile on the board
        for (Tile lockedTile : lockedTiles) {
            String lockedLetter = lockedTile.getLetter(); // Get the letter of the locked tile
            List<Tile> extendedRack = new ArrayList<>(tileRack); // Create a new list containing the tiles from the tile rack

            boolean letterExists = false;
            
            // Check if the locked letter is already in the tile rack
            for (Tile tile : tileRack) {
                if (tile.getLetter().equals(lockedLetter)) {
                    letterExists = true;
                    break;
                }
            }
            
            // If the locked letter is not in the tile rack, add it to the extended rack
            if (!letterExists) {
                extendedRack.add(new Tile(lockedLetter, 0));
            }

            // Get all possible words containing the locked letter using the extended rack using LetterTree class
            Set<String> extendedWords = letterTree.wordsContainingLetter(extendedRack, lockedLetter);

            // Add the generated words to the possibleWords list - ensuring no duplicates
            for (String word : extendedWords) {
                if (!possibleWords.contains(word)) {
                    possibleWords.add(word);
                }
            }
        }
        return possibleWords; // Return the list of possible words
    }
     
    private List<Tile> getLockedTiles(ScrabbleBoard board) {
        List<Tile> lockedTiles = new ArrayList<>();
        for (int row = 0; row < ScrabbleBoard.getGameBoard().length; row++) {
            for (int col = 0; col < ScrabbleBoard.getGameBoard()[row].length; col++) {
            	Tile tile = ScrabbleBoard.boardContains(row, col);
            	if (tile == null) continue;
                if (tile != null && tile.isLocked()) {
                    lockedTiles.add(tile);
                }
            }
        }
        return lockedTiles;
    }
   
    private int findTileInRack(List<Tile> tileRack, char letter) {
        for (int i = 0; i < tileRack.size(); i++) {
            if (tileRack.get(i).getLetter().equals(String.valueOf(letter))) {
                return i;
            }
        }
        return -1;
    }
    
    public List<BoardPosition> getPossiblePositions(ScrabbleBoard board, String word, Tile lockedTile) {
        List<BoardPosition> positions = new ArrayList<>(); // Initialise a list to store valid positions for placing the word
        
        // Iterate through all rows and columns of the board
        for (int row = 0; row < ScrabbleBoard.getGameBoard().length; row++) {
            for (int col = 0; col < ScrabbleBoard.getGameBoard()[row].length; col++) {
                Tile tile = ScrabbleBoard.boardContains(row, col); // Get the tile at the current position

                // If there is a locked tile at the current position and its letter matches the lockedTile's letter
                if (tile != null && tile.isLocked() && tile.getLetter().equals(lockedTile.getLetter())) {
                    int wordLength = word.length();

                    // Iterate through the characters in the word
                    for (int i = 0; i < wordLength; i++) {

                        // If the character at the current index of the word matches the lockedTile's letter
                        if (word.charAt(i) == lockedTile.getLetter().charAt(0)) {
                        	// Calculate starting positions for horizontal and vertical placements
                        	
                        	int startRowHorizontal = row; // The starting row for horizontal placement remains the same as the current row
                        	int startColHorizontal = col - i; // The starting column for horizontal placement is adjusted by subtracting the index of the locked letter in the word

                        	int startRowVertical = row - i; // The starting row for vertical placement is adjusted by subtracting the index of the locked letter in the word
                        	int startColVertical = col; // The starting column for vertical placement remains the same as the current column

                            // Check if the horizontal position is within board boundaries
                            if (startColHorizontal >= 0 && startColHorizontal + wordLength <= ScrabbleBoard.getGameBoard()[row].length) {
                                BoardPosition horizontalPosition = new BoardPosition(startRowHorizontal, startColHorizontal, true);
                                Tile[][] savedBoardState = ScrabbleBoard.saveBoardState(); // Save the current board state
                                placeWordOnBoard(board, new ArrayList<>(getTileRack()), word, horizontalPosition); // Place the word horizontally
                                
                                // If the horizontal placement creates valid words and the tiles are connected
                                if (ScrabbleBoard.areWordsValid(false) && ScrabbleBoard.areTilesConnected()) {
                                    positions.add(horizontalPosition); // Add the horizontal position to the valid positions list
                                }
                                ScrabbleBoard.revertBoardState(savedBoardState); // Revert the board state to the saved state
                            }

                            // Check if the vertical position is within board boundaries
                            if (startRowVertical >= 0 && startRowVertical + wordLength <= ScrabbleBoard.getGameBoard().length) {
                                BoardPosition verticalPosition = new BoardPosition(startRowVertical, startColVertical, false);
                                Tile[][] savedBoardState = ScrabbleBoard.saveBoardState(); // Save the current board state
                                placeWordOnBoard(board, new ArrayList<>(getTileRack()), word, verticalPosition); // Place the word vertically
                                
                                // If the vertical placement creates valid words and the tiles are connected
                                if (ScrabbleBoard.areWordsValid(false) && ScrabbleBoard.areTilesConnected()) {
                                    positions.add(verticalPosition); // Add the vertical position to the valid positions list
                                }
                                ScrabbleBoard.revertBoardState(savedBoardState); // Revert the board state to the saved state
                            }
                        }
                    }
                }
            }
        }
        return positions; // Return the list of valid positions for placing the word
    }

    public boolean makeMove(ScrabbleBoard board, List<Tile> tileRack) {
        int maxScore = Integer.MIN_VALUE; // Initialise the maximum score 
        String bestWord = null; // Initialise the best word to null
        BoardPosition bestPosition = null; // Initialise the best position to null

       
        List<String> possibleWords = generatePossibleWords(board, tileRack); // Generate a list of possible words that can be placed on the board using generatePossibleWords()

        long startTime = System.currentTimeMillis(); // Record the start time for the AI's move

        // Iterate through each possible word
        for (String word : possibleWords) {
            System.out.println("AI is trying to place word: " + word); // Debugging statement

            // Iterate through each locked tile on the board
            for (Tile lockedTile : getLockedTiles(board)) {
                // Get the list of possible positions for placing the current word using the current locked tile
                List<BoardPosition> positions = getPossiblePositions(board, word, lockedTile);
                // Iterate through each possible position
                for (BoardPosition position : positions) {
                    Tile[][] savedBoardState = ScrabbleBoard.saveBoardState(); // Save current board state
                    // Place the word on the board at the current position
                    boolean isSuccess = placeWordOnBoard(board, new ArrayList<>(tileRack), word, position);
                    if (isSuccess) {
                        // Check if the placed word creates valid words on the board
                        boolean areWordsValid = ScrabbleBoard.areWordsValid(true);
                        if (areWordsValid) {
                            // Calculate the score for the current placement
                            int currentScore = ScrabbleBoard.calculateTotalScoreAI(false);
                            // Update the maximum score, best word, and best position if the current score is greater than the max score
                            if (currentScore > maxScore) {
                                maxScore = currentScore;
                                bestWord = word;
                                bestPosition = position;
                            }
                        }
                    }
                    ScrabbleBoard.revertBoardState(savedBoardState); // Revert the board state to the saved state

                    // Check if the 1-minute time limit has been reached
                    long elapsedTime = System.currentTimeMillis() - startTime;
                    if (elapsedTime >= 60000) {
                        break;
                    }
                }

                // Check if the 1-minute time limit has been reached
                long elapsedTime = System.currentTimeMillis() - startTime;
                if (elapsedTime >= 60000) {
                    break;
                }
            }

            // Check if the 1-minute time limit has been reached
            long elapsedTime = System.currentTimeMillis() - startTime;
            if (elapsedTime >= 60000) {
                break;
            }
        }

        // If a best word and best position were found, place the word on the board and lock the placed tiles
        if (bestWord != null && bestPosition != null) {
            placeWordOnBoard(board, tileRack, bestWord, bestPosition);
            ScrabbleBoard.lockPlacedTiles();

            int index = ScrabbleMain.dictionary.getWords().indexOf(bestWord);
            System.out.println("AI placed word: " + bestWord + " with score: " + maxScore + " at index " + index);  
            return true; // Return true as the AI successfully made a move
        } else {
            System.out.println("AI couldn't make a move");
            return false; // Return false as the AI couldn't make a move
        }   
    }
    
    /*
     * Attempts to place the word onto the board using the tile rack and board position
     */

    private boolean placeWordOnBoard(ScrabbleBoard board, List<Tile> tileRack, String word, BoardPosition position) {
        int row = position.getRow(); // Get the starting row for the word placement
        int col = position.getCol(); // Get the starting column for the word placement
        boolean isHorizontal = position.isHorizontal(); // Determine if the word placement is horizontal or not

        // Iterate through the characters in the word
        for (int i = 0; i < word.length(); i++) {
            char letter = word.charAt(i); // Get the current character from the word
            int tileIndex = findTileInRack(tileRack, letter); // Find the index of the tile with the current character in the tile rack
            Tile tile = tileIndex != -1 ? tileRack.get(tileIndex) : null; // Get the tile from the tile rack if found, otherwise set it to null
            Tile boardTile = ScrabbleBoard.boardContains(row, col); // Get the tile at the current position on the board

            // If the board position is empty
            if (boardTile == null) {
                // If the tile for the current character is found in the tile rack
                if (tile != null) {
                    if (tileRack.contains(tile)) {
                        ScrabbleBoard.placeTile(row, col, tile); // Place the tile on the board
                        ScrabbleBoard.getPlacedTiles().push(tile); // Add the placed tile to the placedTiles stack
                        tileRack.remove(tileIndex); // Remove the tile from the tile rack

                    } else {
                        return false; // If the tile is not in the tile rack, return false
                    }
                } else {
                    return false; // If the tile for the current character is not found, return false
                }
            } else if (boardTile.isLocked()) {
                // If the locked tile on the board has the same letter as the current character, continue
                if (boardTile.getLetter().equals(String.valueOf(letter))) {
                } else {
                    return false; // If the locked tile on the board does not have the same letter, return false
                }
            } else {
                return false; // If the board position is neither empty nor locked, return false
            }

            // If the word is placed horizontally, increment the column, otherwise increment the row
            if (isHorizontal) {
                col++;
            } else {
                row++;
            }
        }
        return true; // Return true if the word is successfully placed on the board
    }
    
    /*
     * AI takes its turn
     */

	public void aiTurn(ScrabbleBoard scrabbleBoard) {
		makeMove(scrabbleBoard, getTileRack());	// makeMove()
		
		if (!makeMove(scrabbleBoard, getTileRack())) {
	        ScrabbleMain.gameOver = true;
		}
	}
}