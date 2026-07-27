package view;

import java.awt.Font;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import controller.ScrabbleMain;
import model.Player;
import model.Tile;

import javax.swing.*;
import java.awt.*;

public class AddPlayerName extends JFrame {
    private static final int SCREEN_WIDTH = 400;
    private static final int SCREEN_HEIGHT = 300;
    private static final Font LABEL_FONT = new Font("Comic Sans MS", Font.PLAIN, 20);

    private JTextField playerOneNameField;
    private JTextField playerTwoNameField;
    
    public AddPlayerName(Player playerOne, Player playerTwo,  Player aiPlayer, ArrayList<Tile> tile_bag, boolean singlePlayer) {
    	setTitle("Add Player Names");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        setLocationRelativeTo(null);
        setResizable(false);

        setLayout(new GridLayout(5, 1));

        JLabel playerOneLabel = new JLabel("Player 1 name:");
        playerOneLabel.setHorizontalAlignment(SwingConstants.CENTER);
        playerOneLabel.setFont(LABEL_FONT);
        add(playerOneLabel);
        
        playerOneNameField = new JTextField();
        playerOneNameField.setHorizontalAlignment(SwingConstants.CENTER);
        playerOneNameField.setFont(LABEL_FONT);
        add(playerOneNameField);

        JLabel playerTwoLabel = new JLabel("Player 2 name:");
        playerTwoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        playerTwoLabel.setFont(LABEL_FONT);
        add(playerTwoLabel);
        
        playerTwoNameField = new JTextField();
        playerTwoNameField.setHorizontalAlignment(SwingConstants.CENTER);
        playerTwoNameField.setFont(LABEL_FONT);
        add(playerTwoNameField);
        
        playerTwoLabel.setVisible(!singlePlayer);
        playerTwoNameField.setVisible(!singlePlayer);

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String playerOneName = playerOneNameField.getText().trim();
                String playerTwoName = singlePlayer ? "AI" : playerTwoNameField.getText().trim();

                if (!playerOneName.isEmpty() && (!singlePlayer || singlePlayer && !playerTwoName.isEmpty())) {
                    playerOne.setPlayerName(playerOneName);

                    if (singlePlayer) {
                        ScrabbleMain.AI.setPlayerName(playerTwoName);
                        pvcBoard pvcBoard = null;

                        try {
                            pvcBoard = new pvcBoard(playerOne, ScrabbleMain.AI); 
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }

                        pvcBoard.setVisible(true);

                    } else {
                        playerTwo.setPlayerName(playerTwoName);
                        pvpBoard pvpBoard = null;
                        try {
                            pvpBoard = new pvpBoard(playerOne, playerTwo);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                        pvpBoard.setVisible(true);
                    }

                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Please enter player name", "Missing Player Name", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        
        JPanel buttonPanel = new JPanel(); 
        buttonPanel.add(submitButton);
        add(buttonPanel); 
    }
}