package com.ante;

import java.util.ArrayList;
import java.util.List;

public record WordResult(String word, List<LetterResult> results) {

    public String getLetter(int index) {
        return Character.toString(word.charAt(index));
    }

    public LetterResult getResult(int index) {
        return results.get(index);
    }

    public boolean isValid(String inputWord) {
        //System.out.println("comparing " + inputWord + " to previous result " + this)
        for (int i = 0; i < WordleSolver.WORD_LENGTH; i++) {
            switch (getResult(i)) {
                case RIGHT_POSITION:
                    if (!Character.toString(inputWord.charAt(i)).equals(getLetter(i))) {
                        //System.out.println("right position invalid at index " + i);
                        return false;
                    }
                    break;
                case WRONG_POSITION:
                    if (Character.toString(inputWord.charAt(i)).equals(getLetter(i))) {
                        //System.out.println("wrong position invalid at index " + i);
                        return false;
                    } else {
                        if (!inputWord.contains(getLetter(i))) {
                            //System.out.println("word doesn't contain letter at index " + i);
                            return false;
                        }
                    }
                    break;
                case NOT_IN_WORD:
                    if (inputWord.contains(getLetter(i))) {
                        boolean maybe = false;
                        for(int j = 0; j < WordleSolver.WORD_LENGTH; j++){
                            if(i != j && getLetter(i).equals(Character.toString(inputWord.charAt(j))) && inputWord.charAt(j) == word.charAt(j) && getResult(j) == LetterResult.RIGHT_POSITION){
                                //System.out.println("here");
                                maybe = true;
                            }
                        }
                        //System.out.println("word contains invalid letter at index " + i);
                        if(!maybe){
                            return false;
                        }
                    }
                    break;
            }
            //System.out.println("results valid at index " + i);

        }
        return true;
    }
}
