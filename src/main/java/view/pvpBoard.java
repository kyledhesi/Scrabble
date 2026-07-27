package view;

import javax.swing.*;

import controller.ScrabbleMain;
import model.Player;
import model.ScrabbleBoard;
import model.Tile;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/*
 * ScrabbleBoardGUI displays the gameboard, score, tiles and handles all in game interactions and operations
 */


public class pvpBoard extends JFrame {
	private Player playerOne; // Reference to player one
	private Player playerTwo; // Reference to player two
	private JPanel boardPanel; // Panel to hold the game board
	private JLabel playerOneScore; // Label to display the score for player one
	private JLabel playerTwoScore; // Label to display the score for player two
	private JLabel playerTurn; // Label to indicate the current player's turn
	private JLabel playerMessage; // Label to display messages to the player
	private JLabel playerSelectedTile; // Label to indicate the currently selected tile
	private static JLabel tileCount; // Label to display the count of available tiles
	private JButton submitWordButton; // Button to submit a word
	private JButton replaceTilesButton; // Button to replace tiles
	private JButton clearButton; // Button to clear the board
	private JButton backButton; // Button to go back to the main menu
	private JButton gameOverButton; // Button to end the game
	private static final int BOARD_SIZE = 15; // Size of the game board
	private static JButton[][] board; // Array of buttons representing the game board
	private JPanel tileRackPanel; // Panel to hold the tiles for each player
	private JPanel tilePanel; // Panel to display the tiles
	private static Tile selectedTile; // Currently selected tile
	private Tile[][] savedBoardState; // Array to store the saved state of the game board

    
    private static boolean replaceTiles = false;	// Check to see if in replaceTile mode
    private static ArrayList<Tile> tilesToReplace = new ArrayList<>();    // Array of tiles that need to be replaced
    
    /*
     * Initialise and setup the layout for the board and game related buttons
     */

    public pvpBoard(Player playerOne, Player playerTwo) throws Exception {
        initialisePlayers(playerOne, playerTwo);
        initialiseBoard();
        setVisible(true);
        
        ScrabbleBoard.printGameBoard();
    }

    private void initialisePlayers(Player playerOne, Player playerTwo) {
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        
        playerOne.generateRack(ScrabbleMain.tile_bag);
        playerTwo.generateRack(ScrabbleMain.tile_bag);

        System.out.println("Player One Rack: " + playerOne.getTileRack() + " " + playerOne.getPoints());
        System.out.println("Player Two Rack: " + playerTwo.getTileRack() + " " + playerTwo.getPoints());
    }

    private void initialiseBoard() {
    	setTitle("Scrabble");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        boardPanel = new JPanel(new GridLayout(15, 15));
        boardPanel.setPreferredSize(new Dimension(600, 600));
        board = new JButton[BOARD_SIZE][BOARD_SIZE];

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                JButton square = new JButton();
                drawScrabbleBoard(square, row, col);
                square.setPreferredSize(new Dimension(40, 40));
                square.setBorder(BorderFactory.createLineBorder(Color.white, 1));

                square.addMouseListener(new SquareMouseListener(row, col));

                board[row][col] = square;
                boardPanel.add(square);
                boardPanel.repaint();
            }
        }
        
        
        playerOneScore = new JLabel(playerOne.getPlayerName().toString() + ": 0");
        playerTwoScore = new JLabel(playerTwo.getPlayerName().toString() + ": 0");
        playerOneScore.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
        playerTwoScore.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
        
        if (ScrabbleMain.player_turn % 2 == 0) {
        	playerTurn = new JLabel(playerOne.getPlayerName().toString().concat("'s") + " turn");
        } else {
        	playerTurn = new JLabel(playerTwo.getPlayerName().toString().concat("'s") + " turn");
        }
        
        playerMessage = new JLabel("Submit a word: ");
        tileCount = new JLabel("Tiles remaining: " + ScrabbleMain.tile_bag.size());
        playerSelectedTile = new JLabel ("");

        submitWordButton = new JButton("Submit a word");
        submitWordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	boolean isFirstMove = ScrabbleMain.player_turn == 0;
            	
            	if (ScrabbleBoard.areWordsValid(isFirstMove)) {
            		playerMessage.setText("Last Word Played: " + String.join(", ", ScrabbleBoard.getFormedWords()) + " with score " + ScrabbleBoard.calculateTotalScore(isFirstMove));
            		
            		if (ScrabbleMain.player_turn % 2 == 0) {
                        playerOne.setPoints(playerOne.getPoints() + ScrabbleBoard.calculateTotalScore(isFirstMove));
                        playerOneScore.setText(playerOne.getPlayerName() + ": " + playerOne.getPoints());
                        playerOne.refillRack(ScrabbleMain.tile_bag);
                        
                        System.out.println(playerOne.getPlayerName() + " current score: " + playerOne.getPoints());
                    } else {
                        playerTwo.setPoints(playerTwo.getPoints() + ScrabbleBoard.calculateTotalScore(isFirstMove));
                        playerTwoScore.setText(playerTwo.getPlayerName() + ": " + playerTwo.getPoints());
                        playerTwo.refillRack(ScrabbleMain.tile_bag);           
                        
                        System.out.println(playerTwo.getPlayerName() + " current score: " + playerTwo.getPoints());
                    }
            		
            		checkGameOver();
            		
            		System.out.println("Tiles remaining: " + ScrabbleMain.tile_bag.size());
                    ScrabbleMain.increasePlayerTurn();
                   
                    ScrabbleBoard.getPlacedTiles().clear();
                	ScrabbleBoard.lockPlacedTiles();
                    ScrabbleBoard.saveBoardState();
                    ScrabbleBoard.printGameBoard();
                    
                    playerSelectedTile.setText("Selected Tile: ");
                    
                    startTurn();
                    updatePlayerTurnLabel();
                    updateTileCount(ScrabbleMain.tile_bag);
                    displayTiles(playerOne, playerTwo);
                    
                } else {
                	playerMessage.setText("Invalid move - try again");
                	
                }
            }
        });

        replaceTilesButton = new JButton("Replace tiles");
        replaceTilesButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {

                playerMessage.setText("Select the tiles you want to replace");
                replaceTiles = !replaceTiles; 
                if (replaceTiles) {
                    return;
                }

                if (tilesToReplace.isEmpty()) {
                    playerMessage.setText("No tiles selected to replace.");
                    return;
                }

                if (ScrabbleMain.player_turn == 0 || ScrabbleMain.player_turn % 2 == 0) {
                    playerOne.replaceTiles(ScrabbleMain.tile_bag, tilesToReplace);
                } else {
                    playerTwo.replaceTiles(ScrabbleMain.tile_bag, tilesToReplace);
                }

                tilesToReplace.clear();
                playerMessage.setText("Submit a word: ");
                playerSelectedTile.setText("Selected Tile: ");

                if (ScrabbleMain.player_turn != 0) {
                    ScrabbleMain.increasePlayerTurn();
                }

                startTurn();
                updatePlayerTurnLabel();
                updateTileCount(ScrabbleMain.tile_bag);
                displayTiles(playerOne, playerTwo);       
            }
        });

       
        clearButton = new JButton("Clear");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	
            	if (replaceTiles) {
                    replaceTiles = false;
                    tilesToReplace.clear();
                    playerMessage.setText("Submit a word: ");
                    playerSelectedTile.setText("Selected Tile: ");
                }
            	
                while (!ScrabbleBoard.getPlacedTiles().isEmpty()) {
                    Tile tileToRemove = ScrabbleBoard.getPlacedTiles().pop();
                    ScrabbleBoard.removeTileFromBoard(tileToRemove);
                    if (ScrabbleMain.player_turn % 2 == 0) {
                    	playerOne.getTileRack().add(tileToRemove);
                        System.out.println("Player One's Cleared Rack " + playerOne.getTileRack());
                    } else {
                    	playerTwo.getTileRack().add(tileToRemove);
                        System.out.println("Player Two's Cleared Rack " + playerTwo.getTileRack());
                        System.out.println("Player One's Rack " + playerOne.getTileRack());
                      }
                }
                
                if (savedBoardState != null) {
                    ScrabbleBoard.revertBoardState(savedBoardState);
                    for (int row = 0; row < BOARD_SIZE; row++) {
                        for (int col = 0; col < BOARD_SIZE; col++) {
                            if (savedBoardState[row][col] == null) {
                                board[row][col].setIcon(null);
                                drawScrabbleBoard(board[row][col], row, col);
                            } else {
                                TileGUI tileGUI = new TileGUI(savedBoardState[row][col]);
                                board[row][col].setIcon(tileGUI.getImageIcon());
                            }
                        }
                    }
                }
                
                selectedTile = null;
                ScrabbleBoard.printGameBoard();
                displayTiles(playerOne, playerTwo);
            }
        });
        
        gameOverButton = new JButton("Game Over");
        gameOverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = JOptionPane.showConfirmDialog(null, "Is game over?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    ScrabbleMain.gameOver = true;
                    if (ScrabbleMain.isGameOver()) {
                    	declareWinner();
                    }
                }
            }
        });
        
        backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(
                        null, "Are you sure you want to go back to the main menu?", "Confirm",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    // Return to the main menu
                    setVisible(false);
                    dispose();
                    try {
                        ScrabbleMain scrabbleMain = new ScrabbleMain();
                        scrabbleMain.resetGame();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        
        JPanel rightPanel = new JPanel(new GridLayout(0, 1));
        rightPanel.setPreferredSize(new Dimension(300, 0));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        rightPanel.add(playerOneScore);
        rightPanel.add(playerTwoScore);
        rightPanel.add(playerTurn);
        rightPanel.add(playerMessage);
        rightPanel.add(playerSelectedTile);
        rightPanel.add(tileCount);
        rightPanel.add(submitWordButton);
        rightPanel.add(replaceTilesButton);
        rightPanel.add(clearButton);
        rightPanel.add(gameOverButton);
        
        
        playerTurn.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
        playerMessage.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
        tileCount.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
        playerSelectedTile.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
        submitWordButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
        replaceTilesButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
        clearButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
        gameOverButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
        backButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));

        
        tileRackPanel = new JPanel(new GridLayout(1, 7));
        tilePanel = new JPanel();
        tilePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        tilePanel.setPreferredSize(new Dimension(350, 70));
        tileRackPanel.add(tilePanel);
        tileRackPanel.repaint();
        
        displayTiles(playerOne, playerTwo);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(tileRackPanel, BorderLayout.CENTER);

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(boardPanel, BorderLayout.CENTER);
        contentPane.add(rightPanel, BorderLayout.EAST);
        contentPane.add(tileRackPanel, BorderLayout.SOUTH);
        contentPane.add(backButton, BorderLayout.NORTH);
        
        gameOverButton.setEnabled(false);
        setVisible(true);
        
        System.out.println("Debugging Statement: Tile bag size = " + ScrabbleMain.tile_bag.size());
    }
    
    /*
     * Update playerTurn label after each go
     */
    
    private void updatePlayerTurnLabel() {
        if (ScrabbleMain.player_turn % 2 == 0) {
            playerTurn.setText(playerOne.getPlayerName().toString().concat("'s") + " turn");
        } else {
            playerTurn.setText(playerTwo.getPlayerName().toString().concat("'s") + " turn");
        }
    }
    
    /*
     * Update tileCount label after tile's are removed from the tile bag
     */
    
    public static void updateTileCount(ArrayList<Tile> tile_bag) {
        int remainingTiles = tile_bag.size();
        tileCount.setText("Tiles remaining: " + remainingTiles);
    }
    
    /*
     * Update playerMessage with the winner of the game 
     */
    
    private void declareWinner() {
    	if(ScrabbleMain.isGameOver() == true) {
    		if (playerOne.getPoints() > playerTwo.getPoints()) {
        		playerMessage.setText(playerOne.getPlayerName() + " wins!");
            } else if (playerOne.getPoints() < playerTwo.getPoints()) {
            	playerMessage.setText(playerTwo.getPlayerName() + " wins!");
            } else {
            	playerMessage.setText("It's a tie!");
            }
    	}	
    	playerTurn.setText("Return to main menu");
		playerSelectedTile.setText("");
		
    	submitWordButton.setEnabled(false);
    	replaceTilesButton.setEnabled(false);
    	clearButton.setEnabled(false);
    	gameOverButton.setEnabled(false);
    }
    
    /*
     * Display each player's tile rack 
     */

    public void displayTiles(Player playerOne, Player playerTwo) {
        tilePanel.removeAll();
        if (ScrabbleMain.player_turn % 2 == 0) {
            for (Tile tile : playerOne.getTileRack()) {
                if (!isTileAlreadyPlaced(tile)) {
                    TileGUI tileGUI = new TileGUI(tile);
                    TileMouseListener listener = new TileMouseListener(tileGUI, tile);
                    tileGUI.addMouseListener(listener);
                    tilePanel.add(tileGUI);
                }
            }
        } else {
            for (Tile tile : playerTwo.getTileRack()) {
                if (!isTileAlreadyPlaced(tile)) {
                    TileGUI tileGUI = new TileGUI(tile);
                    TileMouseListener listener = new TileMouseListener(tileGUI, tile);
                    tileGUI.addMouseListener(listener);
                    tilePanel.add(tileGUI);
                }
            }
        }
        tilePanel.revalidate();
        tilePanel.repaint();
    }
    
    /*
     * Place the tile from the rack onto the board
     */
    
    private void placeTile(int row, int col, Tile tile, Player currentPlayer) {
        if (isTileAlreadyPlaced(tile)) {
            return;
        }
 
        if (ScrabbleBoard.getPlacedTiles().isEmpty()) {
            savedBoardState = ScrabbleBoard.saveBoardState();
        }
        
        if (!ScrabbleBoard.placeTile(row, col, tile)) {
            System.out.println("Unable to place tile. A locked tile already exists at the given position.");
            return;
        }
        
        if (tile.isBlank()) {
            String input = JOptionPane.showInputDialog(null, "Enter a letter for the blank tile:", "Blank Tile", JOptionPane.QUESTION_MESSAGE);
            if (input != null && !input.isEmpty()) {
                String enteredLetter = input.substring(0, 1).toUpperCase();
                if (enteredLetter.matches("[A-Za-z]")) {
                    int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to use the letter '" + enteredLetter + "'?", "Confirm Letter", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        tile.setLetter(enteredLetter);
                    } else {
                        return;
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid letter. Please enter an alphabetic character.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                return;
            }
        }
        
        TileGUI tileGUI = new TileGUI(tile);
        JButton selectedSquare = board[row][col];
        selectedSquare.removeAll();
        selectedSquare.setIcon(tileGUI.getImageIcon());
        ScrabbleBoard.getPlacedTiles().push(tile);
        currentPlayer.removeTile(tile);
        boardPanel.repaint();
       
        displayTiles(playerOne, playerTwo);
    }
    
    /*
     * Tile can't be placed on board if true
     */

   
    private boolean isTileAlreadyPlaced(Tile tile) {
        return ScrabbleBoard.getPlacedTiles().contains(tile);
    }
    
    /*
     * Setup the Scrabble board
     */
    
    private void drawScrabbleBoard(JButton square, int row, int col) {
        square.setOpaque(true);
        JLabel letterScoreLabel = new JLabel("");
        letterScoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        letterScoreLabel.setVerticalAlignment(SwingConstants.CENTER);

        JPanel letterScorePanel = new JPanel(new BorderLayout());
        letterScorePanel.add(letterScoreLabel, BorderLayout.NORTH);
        square.add(letterScorePanel, BorderLayout.NORTH);

        if (row == 7 && col == 7) {
            square.setBackground(new Color(255, 192, 203));
            letterScoreLabel.setText("<html><div style='text-align: center; font-size: 8px; font-weight: bold;'>STAR</div></html>");
        } else if (isDoubleLetter(row, col)) {
            square.setBackground(new Color(204, 255, 255));
            letterScoreLabel.setText("<html><div style='text-align: center; font-size: 8px; font-weight: bold;'>DOUBLE<br>LETTER<br>SCORE</div></html>");
        } else if (isTripleLetter(row, col)) {
            square.setBackground(new Color(0, 128, 255));
            letterScoreLabel.setText("<html><div style='text-align: center; font-size: 8px; font-weight: bold;'>TRIPLE<br>LETTER<br>SCORE</div></html>");
        } else if (isDoubleWord(row, col)) {
            square.setBackground(new Color(255, 192, 203));
            letterScoreLabel.setText("<html><div style='text-align: center; font-size: 8px; font-weight: bold;'>DOUBLE<br>WORD<br>SCORE</div></html>");
        } else if (isTripleWord(row, col)) {
            square.setBackground(new Color(255, 51, 51));
            letterScoreLabel.setText("<html><div style='text-align: center; font-size: 8px; font-weight: bold;'>TRIPLE<br>WORD<br>SCORE</div></html>");
        } else {
            square.setBackground(new Color(220, 199, 178));
        }

        letterScorePanel.setBackground(square.getBackground());
        square.repaint();
        square.revalidate();
    }
    
    /*
     * Start next turn and update the GUI
     */
 
    public void startTurn() {
        savedBoardState = ScrabbleBoard.saveBoardState();
    }
    
    private void checkGameOver() {
        if (ScrabbleMain.isGameOver()) {
            declareWinner();
        }
        
        if (ScrabbleMain.tile_bag.isEmpty()) {
            gameOverButton.setEnabled(true);
        }
    }
        
    /*
     * MOUSE LISTENER CODE
     */
    
    private class TileMouseListener extends MouseAdapter {
        private final TileGUI tileGUI;
        private final Tile tile;

        public TileMouseListener(TileGUI tileGUI, Tile tile) {
            this.tileGUI = tileGUI;
            this.tile = tile;
        }

        
        @Override
        public void mousePressed(MouseEvent e) {
        	if (pvpBoard.replaceTiles) {
        	    if (tilesToReplace.contains(tile)) {
        	        tilesToReplace.remove(tile);
        	    } else {
        	        tilesToReplace.add(tile);
        	    }
        	   
        	    StringBuilder tile = new StringBuilder("Selected Tiles: ");
        	    for (int i = 0; i < tilesToReplace.size(); i++) {
        	        tile.append(tilesToReplace.get(i));
        	        if (i < tilesToReplace.size() - 1) {
        	            tile.append(", ");
        	        }
        	    }
        	    playerSelectedTile.setText(tile.toString());

            } else {
                selectedTile = tile;
                playerSelectedTile.setText("Selected Tile: " + selectedTile.getLetter());
                System.out.println(selectedTile);
            }
        }

    }
    
    private class SquareMouseListener extends MouseAdapter {
        private final int row;
        private final int col;

        public SquareMouseListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (selectedTile != null) {
                Player currentPlayer;
                if (ScrabbleMain.player_turn % 2 == 0) {
                    currentPlayer = playerOne;
                } else {
                    currentPlayer = playerTwo;
                }
                if (!isTileAlreadyPlaced(selectedTile)) {
                    placeTile(row, col, selectedTile, currentPlayer);
                    displayTiles(playerOne, playerTwo); 
                }
                selectedTile = null; 
            }
        }
    }
    
    /*
     * Check to see if it is a double letter spot
     */
    
    public static boolean isDoubleLetter(int row, int col) {
       if(row == 0 || row == 14) {	//Locations (0, 3) (0, 11) (14, 3) and (14, 11)
          if(col == 3 || col == 11)
             return true;
       } else if (row == 2 || row == 12) {	//Locations (2, 6) (2, 8) (12, 6) and (12, 8)
          if(col == 6 || col == 8)
             return true;
       } else if (row == 3 || row == 11) {	//Locations (3, 0) (3, 7) (3, 14) (11, 0) (11, 7) and (11, 14)
          if(col == 0 || col == 7 || col == 14)
             return true;
       } else if (row == 6 || row == 8) {	//Locations (6, 2) (6, 6) (6, 8) (6, 12) (8, 2) (8, 6) (8, 8) and (8, 12)
          if(col == 2 || col == 6 || col == 8 || col == 12)
             return true;
       } else if (row == 7) {	//Locations (7, 3) and (7, 11)
          if(col == 3 || col == 11)
             return true;
       }
       return false;	//Not a triple word spot
    }
    
    //Check to see if it is a triple letter spot
    public static boolean isTripleLetter(int row, int col) {
       if(row == 1 || row == 13) { //Locations (1, 5) (1, 9) (13, 5) and (13, 9)
          if(col == 5 || col == 9) 
             return true;
       } else if(row == 5 || row == 9) {	 //Locations (5, 1) (5, 5) (5, 9) (5, 13) (9, 1) (9, 5) (9, 9) and (9, 13)
          if(col == 1 || col == 5 || col == 9 || col == 13)
             return true;
       }
       return false;	//Not a triple letter spot
    }
  
    public static boolean isDoubleWord(int row, int col) {
        if(row == 1 || row == 2 || row == 3 || row == 4 || row == 10 || row == 11 || row == 12 || row == 13) {
           if(col == board.length - 1 - row || col == row) 
              return true;
        }
        return false;	
     }


    
    public static boolean isTripleWord(int row, int col) {
        if(row == 0 || row == 14) {  //Locations (0, 0) (0, 7) (0, 14) (14, 0) (14, 7) and (14, 14)
           if(col == 0 || col == 7 || col == 14)
              return true;
        } else if (row == 7) { //Locations (7, 0) and (7, 14)
           if(col == 0 || col == 14) 
              return true;
        }
        return false;	//Not a triple word spot
     }
}