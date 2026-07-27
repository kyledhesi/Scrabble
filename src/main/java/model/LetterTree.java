package model;

import java.io.File;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/*
 * LetterTree class to represent a trie structure for words and their prefixes
 */

public class LetterTree {
	
	public LetterTreeNode root;	// The root node of the tree
	
	/*
	 *  Constructor to create a tree from an array of words
	 */
	 
	 public LetterTree(String[] words) {
	    
	     this.root = new LetterTreeNode(false);	// Initialise root node as not a word
	    
	     for (String word : words) {	// Iterate through each word in the parameter array
	         LetterTreeNode current_node = this.root;	// Start from the root node
	         for (char letter : word.toCharArray()) {	// Iterate through each letter in the word
	             if (!current_node.children.containsKey(letter)) {
	                 current_node.children.put(letter, new LetterTreeNode(false)); // If the letter is not in the current node's children, add a new child node
	             }  
	             current_node = current_node.children.get(letter);	// Move to the next node in the tree
	         }
	         current_node.is_word = true;	// Mark the last node as a complete word
	     }
	 }
	
	 /*
	  *  Look up a node in the tree corresponding to a word
	  */
	 
	  public LetterTreeNode lookup(String word) { 
		  LetterTreeNode current_node = this.root;	// Start from the root node
		 
		  // Iterate through each letter in the word
		  for (char letter : word.toCharArray()) {
			  if (!current_node.children.containsKey(letter)) {
				  return null;	// If the letter is not in the current node's children, return null (word not found)
			  }
		      current_node = current_node.children.get(letter);	// Move to the next node in the tree
		  }
		  return current_node;	// Return the node corresponding to the word
	  }
	 
	 /*
	  *  Check if a string is a valid word in the tree
	  */
	
	 public boolean is_word(String word) {
	     LetterTreeNode word_node = this.lookup(word);  // Look up the node corresponding to the word
	     if (word_node == null) {
	         return false;	// If the node is not found, the word is not valid
	     }
	     return word_node.is_word; // If the node is found, return whether it's a complete word
	 }
	
	 /*
	  * Create a LetterTree from a dictionary file 
	  */
	 
	 public static LetterTree basic_english() throws Exception {
	     Scanner file = new Scanner(new File("dictionary.txt")); // Read the dictionary file
	     ArrayList<String> words = new ArrayList<>(); // Initialise a list to store the words
	     // Iterate through each word in the file
	     while (file.hasNextLine()) {
	         String word = file.nextLine().trim();
	         words.add(word);
	     }
	     // Convert the list of words into an array
	     String[] word_array = new String[words.size()];
	     word_array = words.toArray(word_array);
	     return new LetterTree(word_array);	// Create a new LetterTree using the array of words
	 }
	 
	 /*
	  * Generate all possible words from tiles - not used by AI - redundant method
	  */
    
	 public Set<String> allWords(List<Tile> availableTiles) {
		 Set<String> words = new HashSet<>();	// Initialise a set to store the words
         List<String> availableLetters = tilesToLetters(availableTiles);	// Convert available tiles to available letters
         allWordsHelper("", availableLetters, words);	// Recursive helper function to generate all possible words
         return words;	// Return the set of generated words
	 }
	 
     /*
      * Recursive helper function to generate all possible words - not used by AI - redundant method
      */

     private void allWordsHelper(String prefix, List<String> availableLetters, Set<String> words) {
 
    	 if (!prefix.isEmpty() && is_word(prefix)) {	// If the prefix is not empty and is a valid word
    	 	 words.add(prefix);	// Add the prefix to the set of words
    	 }

         if (availableLetters.isEmpty()) {	// If there are no available letters left
        	 return;	// Return and stop the recursion
         }
         
         for (int i = 0; i < availableLetters.size(); i++) {	// Iterate through each letter in the list of available letters
        	 String letter = availableLetters.get(i);	// Get the current letter
             List<String> remainingLetters = new ArrayList<>(availableLetters);	 // Create a new list of remaining letters
             remainingLetters.remove(i);	// Remove the current letter from the remaining letters list
             allWordsHelper(prefix + letter, remainingLetters, words);	 // Recursively call the helper function with the updated prefix and remaining letters
         }
     }
     
     /*
	  * Convert tiles to letters
	  */

     private List<String> tilesToLetters(List<Tile> availableTiles) {
        List<String> availableLetters = new ArrayList<>();	// Initialise a list to store the letters
        for (Tile tile : availableTiles) {	// Iterate through each tile in the list of available tiles
            availableLetters.add(String.valueOf(tile.getLetter()));	// Add the letter of the tile to the list of available letters
        }
        return availableLetters;	// Return the list of available letters
     }
     
     /*
      * Generate all possible words containing locked tiles on the board and available tiles
      */
    
     public Set<String> wordsContainingLetter(List<Tile> availableTiles, String lockedLetter) {
    	 Set<String> words = new HashSet<>();	// Initialise a set to store the words
         List<String> availableLetters = tilesToLetters(availableTiles);	// Convert available tiles to available letters
         wordsContainingLetterHelper("", availableLetters, words, lockedLetter);	// Recursive helper function to generate all possible words containing the locked letter
         return words;	// Return the set of words
     }
     
     /*
      * Recursive helper function to generate all words containing locked tiles by generating prefixes 
      */

     private void wordsContainingLetterHelper(String prefix, List<String> availableLetters, Set<String> words, String lockedLetter) {
    	 if (!prefix.isEmpty() && is_word(prefix) && prefix.contains(lockedLetter)) {	// If the prefix is not empty, is a valid word, and contains the locked letter
    		 words.add(prefix);	// Add the prefix to the set of words
         }
         if (availableLetters.isEmpty()) {	// If there are no available letters left
        	 return;	// Return and stop the recursion
         }
         for (int i = 0; i < availableLetters.size(); i++) {	// Iterate through each letter in the list of available letters
        	 String letter = availableLetters.get(i);	// Get the current letter
        	 List<String> remainingLetters = new ArrayList<>(availableLetters);	// Create a new list of remaining letters
             remainingLetters.remove(i);	// Remove the current letter from the remaining letters list
             wordsContainingLetterHelper(prefix + letter, remainingLetters, words, lockedLetter);	// Recursively call the helper function with the updated prefix and remaining letters
         }
     }
}
