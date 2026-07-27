package model;

import java.util.HashMap;

public class LetterTreeNode {
    public boolean is_word;
    public HashMap<Character, LetterTreeNode> children;

    public LetterTreeNode(boolean is_word) {
        this.is_word = is_word;
        this.children = new HashMap<>();
    }
}
