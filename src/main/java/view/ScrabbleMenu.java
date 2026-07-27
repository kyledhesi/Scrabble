package view;

import java.awt.BorderLayout;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import controller.ScrabbleMain;
import model.Player;
import model.Tile;


/*
 * ScrabbleMenu class is the GUI for the main menu
 */

public class ScrabbleMenu extends JFrame {
    private static final int SCREEN_WIDTH = 500;
    private static final int SCREEN_HEIGHT = 600;
    private static final Font TITLE_FONT = new Font("Comic Sans MS", Font.BOLD, 60);
    private static final Font BUTTON_FONT = new Font("Poster Sans", Font.PLAIN, 24);

    private JButton pvpButton;
    private JButton pvcButton;

    public ScrabbleMenu() {
        setTitle("Scrabble");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        setLocationRelativeTo(null);

        // Create the title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT/4));

        // Add the title label
        JLabel titleLabel = new JLabel("SCRABBLE");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.BLACK);
        titlePanel.add(titleLabel);

        // Create the button panel
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 0, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT/2));

        // Add Player vs Player button
        pvpButton = new JButton("Player vs Player");
        pvpButton.setFont(BUTTON_FONT);
        pvpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    openAddPlayerNameScreen(false);
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });
        buttonPanel.add(pvpButton);

        // Add Player vs Computer button
        pvcButton = new JButton("Player vs Computer");
        pvcButton.setFont(BUTTON_FONT);
        pvcButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    openAddPlayerNameScreen(true);
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });
        buttonPanel.add(pvcButton);

        // Add Quit button
        JButton quitButton = new JButton("Quit");
        quitButton.setFont(BUTTON_FONT);
        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        buttonPanel.add(quitButton);

        // Add the title panel and button panel to the main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private void openAddPlayerNameScreen(boolean singlePlayer) throws IOException {
        ArrayList<Tile> tile_bag = ScrabbleMain.createTileBag();
        AddPlayerName addPlayerNameScreen = new AddPlayerName(ScrabbleMain.playerOne, ScrabbleMain.playerTwo, ScrabbleMain.AI, tile_bag, singlePlayer);
        addPlayerNameScreen.setVisible(true);
        
        pvpButton.setEnabled(false);
        pvcButton.setEnabled(false);
        addPlayerNameScreen.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                pvpButton.setEnabled(true);
                pvcButton.setEnabled(true);
            }
        });
    }
}
