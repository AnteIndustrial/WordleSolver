package com.ante;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WordleSolver {

    static final Logger LOGGER = Logger.getLogger(WordleSolver.class.getName());

    public static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    public static final int WORD_LENGTH = 5;
    public static final int NUMBER_OF_GUESSES = 6;

    private List<Map<String, Integer>> countAtPosition;
    public List<ScoredWord> scoredWords;

    public WordleSolver(){
        List<String> words = getWords();
        scoreWords(words);
    }

    /**
     * Give every word a score based on how frequently its letters appear in the dictionary
     * @param words a list of dictionary words.txt
     */
    private void scoreWords(List<String> words) {
        countLettersAtPositions(words);
        scoredWords = new ArrayList<>(words.size());

        for(String word: words){
            int score = 0;
            List<Character> previousCharacters = new ArrayList<>(5);
            for(int i = 0; i < WORD_LENGTH; i++){
                char c = word.charAt(i);
                //for every letter in the word
                //count how often the letter is in this position
                score += countAtPosition.get(i).get(Character.toString(c));
                if(!previousCharacters.contains(c)) {
                    for (int j = 0; j < WORD_LENGTH; j++) {
                        //count how often the letter is in any position
                        //this recounts the previous value, which doubles the letter's score for being in the right position
                        score += countAtPosition.get(j).get(Character.toString(c));
                    }
                }
                previousCharacters.add(c);
            }

            scoredWords.add(new ScoredWord(word, score));
        }
        Collections.sort(scoredWords);
    }

    /**
     * Count how often each letter appears in each position
     * @param words a list of dictionary words.txt
     */
    private void countLettersAtPositions(List<String> words) {
        initialiseCountAtPositionList();
        for(String word : words){
            for(int i = 0; i < WORD_LENGTH; i++) {
                String c = Character.toString(word.charAt(i));
                int previousCount = countAtPosition.get(i).get(c);
                countAtPosition.get(i).put(c, previousCount + 1);
            }
        }
    }

    /**
     * Set up a List of empty Maps
     */
    private void initialiseCountAtPositionList() {
        countAtPosition = new ArrayList<>();
        for(int i = 0; i < WORD_LENGTH; i++) {
            countAtPosition.add(new HashMap<>());
            for(int j = 0; j < ALPHABET.length(); j++){
                countAtPosition.get(i).put(Character.toString(ALPHABET.charAt(j)), 0);
            }
        }
    }

    /**
     * read a dictionary for a list of words.txt
     * @return a list of words.txt
     */
    private List<String> getWords() {
        List<String> words = new ArrayList<>();

        try(Scanner sc = new Scanner(new File(("src/main/resources/smallwords.txt")))){
            while(sc.hasNextLine()){
                words.add(sc.nextLine());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return words;
    }

    public static void main(String[] args) {
        WordleSolver solver = new WordleSolver();
        Scanner reader = new Scanner(System.in);
        System.out.println("1: manual solver");
        System.out.println("2: auto solver");
        System.out.println("3: bulk solver");
        //or d1, d2, d3 to turn on debug logging

        String input = reader.nextLine();
        if(input.length() == 2 && input.charAt(0) == 'd'){
            solver.setDebug();
            input = input.substring(1);
        }
        switch (input) {
            case "1" -> solver.manualSolver(reader);
            case "2" -> solver.autoSolver(reader);
            case "3" -> solver.bulkSolver();
            default -> System.out.println("Invalid input");
        }
    }

    private void setDebug() {
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.FINER);
        LOGGER.setLevel(Level.FINER);
        LOGGER.addHandler(handler);
    }

    private void bulkSolver() {
        List<String> allWords = getWords();

        Map<String, Integer> guessesPerWord = new HashMap<>(allWords.size());
        for(String target : allWords){
            LOGGER.fine(String.format("Target: %s", target));
            int guesses = 0;
            ScoredWord currentGuess;
            do{
                if(scoredWords.size() == 0){
                    LOGGER.severe(String.format("Failed on %s", target));
                    guesses = 50;
                    break;
                }
                currentGuess = scoredWords.get(scoredWords.size() - 1);
                LOGGER.fine(String.format("Guessing: %s", currentGuess));
                WordResult result = guess(currentGuess.getWord(), target);

                removeInvalidOptions(result);
                guesses++;
                if(guesses == 20){
                    LOGGER.severe("breaking");
                    break;
                }
            } while((!currentGuess.getWord().equals(target)));
            guessesPerWord.put(target, guesses);
            scoreWords(allWords);
        }
        Map<Integer, Integer> totalGuesses = new HashMap<>();
        for(String key : guessesPerWord.keySet()){
            Integer guess = guessesPerWord.get(key);
            if(guess > 5){
                System.out.printf("%s took %s guesses%n", key, guess);
            }
            if(totalGuesses.containsKey(guess)){
                totalGuesses.put(guess, totalGuesses.get(guess) + 1);
            } else {
                totalGuesses.put(guess, 1);
            }
        }
        System.out.println(totalGuesses);
    }

    private void autoSolver(Scanner reader) {
        System.out.println("Enter target word");
        String target = reader.nextLine();
        int guesses = 0;
        ScoredWord currentGuess;
        do{
            currentGuess = scoredWords.get(scoredWords.size() - 1);
            System.out.printf("Guessing %s%n", currentGuess.getWord());

            WordResult result = guess(currentGuess.getWord(), target);

            LOGGER.fine(result.toString());

            removeInvalidOptions(result);
            guesses++;
            if(guesses == 20){
                System.out.println("breaking");
                break;
            }
        } while((!currentGuess.getWord().equals(target)));

        System.out.printf("Solved in %s guesses%n", guesses);
    }

    static WordResult guess(String currentGuess, String target) {
        List<LetterResult> letterResults = new ArrayList<>();
        Map<Character, Integer> numOfCharsInTarget = WordResult.calculateNumOfCharsInAWord(target);
        Map<Character, Integer> numOfCharsInGuess = WordResult.calculateNumOfCharsInAWord(currentGuess);
        Map<Character, Integer> duplicates = new HashMap<>();

        for(int i = 0; i < WORD_LENGTH; i++){
            char currentChar = currentGuess.charAt(i);
            if(currentChar == target.charAt(i)){
                LOGGER.fine(String.format("Right position: %s", currentChar));
                if(duplicates.containsKey(currentChar)){
                    duplicates.put(currentChar, duplicates.get(currentChar) + 1);
                } else {
                    duplicates.put(currentChar, 1);
                }
                letterResults.add(LetterResult.RIGHT_POSITION);

            } else if(target.indexOf(currentChar) == -1){
                LOGGER.fine(String.format("Not in word: %s", currentChar));
                letterResults.add(LetterResult.NOT_IN_WORD);
            }
        }
        for(int i = 0; i < WORD_LENGTH; i++){
            char currentChar = currentGuess.charAt(i);
             if(!(currentChar == target.charAt(i)) && !(target.indexOf(currentChar) == -1)) {
                if(numOfCharsInTarget.get(currentChar) >= numOfCharsInGuess.get(currentChar)){
                    LOGGER.fine(String.format("Wrong position: %s", currentChar));
                    letterResults.add(i, LetterResult.WRONG_POSITION);
                } else {
                    if(duplicates.containsKey(currentChar)){
                        duplicates.put(currentChar, duplicates.get(currentChar) + 1);
                    } else {
                        duplicates.put(currentChar, 1);
                    }
                    if(numOfCharsInTarget.get(currentChar)  >= duplicates.get(currentChar)){
                        LOGGER.fine(String.format("Wrong position: %s", currentChar));
                        letterResults.add(i, LetterResult.WRONG_POSITION);
                    } else {
                        LOGGER.fine(String.format("Duplicate, Not in word: %s", currentChar));
                        letterResults.add(i, LetterResult.NOT_IN_WORD);
                    }
                }
            }
        }

        return new WordResult(currentGuess, letterResults);
    }

    private void manualSolver(Scanner reader) {
        System.out.println(scoredWords.get(scoredWords.size() - 1));
        System.out.println("Enter attempted word and results in the format 'word RRWWN'");
        System.out.println("Where R = letter in right position, W = letter in wrong position, and N = letter not in word");

        for(int i = 0; i < NUMBER_OF_GUESSES; i++) {
            WordResult wordResult;
            do {
                String input = reader.nextLine();
                wordResult = parseResult(input);
                if(wordResult == null){
                    System.out.println("Invalid input, try again");
                }
            } while(wordResult == null);

            removeInvalidOptions(wordResult);
            System.out.println(scoredWords);
            System.out.println(scoredWords.get(scoredWords.size() - 1));
        }
    }

    private void removeInvalidOptions(WordResult wordResult) {
        List<String> reducedList = new ArrayList<>();
        for(ScoredWord word : scoredWords){
            if(wordResult.isValid(word.getWord())){
                reducedList.add(word.getWord());
            }
        }
        scoreWords(reducedList);
    }

    /**
     * Returns null if the string is invalid.
     */
    private WordResult parseResult(String input) {
        String[] split = input.split(" ");
        if(split.length != 2){
            return null;
        }
        String attemptedWord = split[0].toLowerCase();
        String results = split[1].toUpperCase();
        if(results.length() != WORD_LENGTH){
            return null;
        }
        List<LetterResult> letterResults = new ArrayList<>();
        for(char c : results.toCharArray()){
            switch (c) {
                case 'R' -> letterResults.add(LetterResult.RIGHT_POSITION);
                case 'W' -> letterResults.add(LetterResult.WRONG_POSITION);
                case 'N' -> letterResults.add(LetterResult.NOT_IN_WORD);
                default -> {
                    return null;
                }
            }
        }
        return new WordResult(attemptedWord, letterResults);
    }
}
