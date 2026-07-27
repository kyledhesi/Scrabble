package model;

import java.io.File;


import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/*
 * Creates dictionary for scrabble game and stores words from .txt file into ArrayList
 */

public class Dictionary {
    private static ArrayList<String> dictionary = new ArrayList<String>();
       
    public Dictionary(String filename) {
        try {
            dictionary = createDictionary(filename);
        } catch (IOException e) {
            System.err.println("Error reading dictionary file: " + e.getMessage());
        }
    }
    
    public static ArrayList<String> createDictionary(String filename) throws IOException {
        ArrayList<String> dictionary = new ArrayList(); 
        File file = new File(filename);         
        Scanner reader = new Scanner(new FileReader(file)); 
        while(reader.hasNext())                          
            dictionary.add(reader.next());                   
        reader.close();         
        
        return dictionary;
    }

    
    public ArrayList<String> getWords() {
        return dictionary;
    }
    
    public boolean contains(String word) {
        return dictionary.contains(word);
    }
}
