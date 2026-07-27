package view;

import java.awt.Color;


import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import model.Tile;

/*
 * Designs the graphical representation of a Tile
 */

public class TileGUI extends JButton {
    private final int TILE_SIZE = 42;
    private final Color TILE_COLOUR = new Color(230, 190, 138); // Light Oak
    private final String letter;	// Instance variable for the Tile letter
    private final int score;	// Instance variable for the Tile score
    private final Font LETTER_FONT = new Font("Poster Sans", Font.BOLD, 18);	// Font for displaying the letter 
    private final Font SCORE_FONT = new Font("Poster Sans", Font.BOLD, 8);	// Font for displaying the score
    private ImageIcon image;	// ImageIcon for the Tile
    private Tile tile;	// Reference to the Tile object
    
    /*
     * Constructor for the class
     */
    
    public TileGUI(Tile tile) {
        this.tile = tile;
        this.letter = tile.getLetter();
        this.score = tile.getScore();
        setPreferredSize(new Dimension(TILE_SIZE, TILE_SIZE)); // Set the preferred size for the Tile 
        image = createImageIcon();	// Create its image
        setIcon(image);
    }
    
    /*
     * Customise the appearance of the Tile
     */
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Call superclass's paintComponent method to ensure proper rendering

        // Set the color for the Tile and fill the whole area with that color
        g.setColor(TILE_COLOUR);
        g.fillRect(0, 0, TILE_SIZE, TILE_SIZE); 

        // Set the color and font for the letter
        g.setColor(Color.BLACK);
        g.setFont(LETTER_FONT);

        // Calculate the letter's x and y position to center it within the Tile
        FontMetrics letterMetrics = g.getFontMetrics(LETTER_FONT);
        int letterX = TILE_SIZE/2 - letterMetrics.stringWidth(letter)/2;
        int letterY = TILE_SIZE/2 + letterMetrics.getHeight()/2 - 6;

        // Draw the letter at the calculated position
        g.drawString(letter, letterX, letterY);

        // Set the font for the score
        g.setFont(SCORE_FONT);

        // Convert the score to a string for drawing
        String scoreString = Integer.toString(score);

        // Calculate the score's x and y position to place it at the bottom right corner of the Tile
        FontMetrics scoreMetrics = g.getFontMetrics(SCORE_FONT);
        int scoreX = TILE_SIZE - scoreMetrics.stringWidth(scoreString) - 5;
        int scoreY = TILE_SIZE - scoreMetrics.getHeight() + scoreMetrics.getAscent() - 5;

        // Draw the score at the calculated position
        g.drawString(scoreString, scoreX, scoreY);
    }
    
    private ImageIcon createImageIcon() {
        BufferedImage bufImage = new BufferedImage(TILE_SIZE, TILE_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bufImage.createGraphics();
        paintComponent(g);
        g.dispose();
        return new ImageIcon(bufImage);
    }
    
    public ImageIcon getImageIcon() {
        return image;
    }
}
