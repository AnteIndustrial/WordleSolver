package com.ante;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WordResult {

    static final Logger LOGGER = Logger.getLogger(WordResult.class.getName());
    String word;
    List<LetterResult> results;
    Map<Character, Range> numOfCharsInTargetWord;

    public WordResult(String word, List<LetterResult> results){
        this.word = word;
        this.results = results;
        calculateNumOfCharsInTargetWord(word, results);
    }

    protected static Map<Character, Integer> calculateNumOfCharsInAWord(String input){
        Map<Character, Integer> result = new HashMap<>();
        for(char c : input.toCharArray()){
            if(result.containsKey(c)){
                result.put(c, result.get(c) + 1);
            } else {
                result.put(c, 1);
            }
        }
        return result;
    }

    private void calculateNumOfCharsInTargetWord(String word, List<LetterResult> results) {
        numOfCharsInTargetWord = new HashMap<>();
        for(char c : WordleSolver.ALPHABET.toCharArray()){
            numOfCharsInTargetWord.put(c, new Range(0,WordleSolver.WORD_LENGTH));
        }

        Map<Character, List<LetterResult>> lettersToResult = new HashMap<>();
        for(int i = 0; i < word.length(); i++){
            if(lettersToResult.containsKey(word.charAt(i))){
                lettersToResult.get(word.charAt(i)).add(results.get(i));
            } else {
                lettersToResult.put(word.charAt(i), new ArrayList<>());
                lettersToResult.get(word.charAt(i)).add(results.get(i));
            }
        }

        LOGGER.log(Level.FINE, lettersToResult.toString());

        for(char c : lettersToResult.keySet()){
            //if letter is only grey
            if(lettersToResult.get(c).contains(LetterResult.NOT_IN_WORD) &&
                    !lettersToResult.get(c).contains(LetterResult.RIGHT_POSITION) &&
                    !lettersToResult.get(c).contains(LetterResult.WRONG_POSITION)){
                numOfCharsInTargetWord.get(c).intersect(new Range(0,0));
            }
            //letter is in somewhere, no grey
            if((lettersToResult.get(c).contains(LetterResult.RIGHT_POSITION) ||
                    lettersToResult.get(c).contains(LetterResult.WRONG_POSITION)) &&
                    !lettersToResult.get(c).contains(LetterResult.NOT_IN_WORD)){
                numOfCharsInTargetWord.get(c).intersect(new Range(lettersToResult.get(c).size(),WordleSolver.WORD_LENGTH));
            }
            //letter is in it, and also there's a grey
            if(lettersToResult.get(c).contains(LetterResult.NOT_IN_WORD) &&
                    (lettersToResult.get(c).contains(LetterResult.WRONG_POSITION) ||
                    lettersToResult.get(c).contains(LetterResult.RIGHT_POSITION))){
                int exactNumOfLetters = 0;
                for(LetterResult result : lettersToResult.get(c)){
                    if(result.equals(LetterResult.RIGHT_POSITION) || result.equals(LetterResult.WRONG_POSITION)){
                        exactNumOfLetters++;
                    }
                }
                numOfCharsInTargetWord.get(c).intersect(new Range(exactNumOfLetters, exactNumOfLetters));
            }
        }

        LOGGER.log(Level.FINE, numOfCharsInTargetWord.toString());
    }

    public String getLetter(int index) {
        return Character.toString(word.charAt(index));
    }

    public LetterResult getResult(int index) {
        return results.get(index);
    }

    public boolean isValid(String inputWord) {
        LOGGER.fine(String.format("comparing %s to previous result %s", inputWord, this));

        Map<Character, Integer> numOfCharsInInputWord = calculateNumOfCharsInAWord(inputWord);
        for(char c : inputWord.toCharArray()){
            if(!numOfCharsInTargetWord.get(c).contains(numOfCharsInInputWord.get(c))){
                return false;
            }
        }

        for (int i = 0; i < WordleSolver.WORD_LENGTH; i++) {
            switch (getResult(i)) {
                case RIGHT_POSITION:
                    if (!Character.toString(inputWord.charAt(i)).equals(getLetter(i))) {
                        LOGGER.fine(String.format("right position invalid at index %s", i));
                        return false;
                    }
                    break;
                case WRONG_POSITION:
                    if (Character.toString(inputWord.charAt(i)).equals(getLetter(i))) {
                        LOGGER.fine(String.format("wrong position invalid at index %s", i));
                        return false;
                    } else {
                        if (!inputWord.contains(getLetter(i))) {
                            LOGGER.fine(String.format("word doesn't contain letter at index %s", i));
                            return false;
                        }
                    }
                    break;
            }
            LOGGER.fine(String.format("results valid at index %s", i));

        }
        return true;
    }

    @Override
    public String toString(){
        return String.format("[%s, %s]", word, results);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WordResult that = (WordResult) o;
        return word.equals(that.word) && results.equals(that.results);
    }

    @Override
    public int hashCode() {
        return Objects.hash(word, results);
    }
}
