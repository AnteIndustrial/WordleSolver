package com.ante;

import com.ante.solver.WordleSolver;
import com.ante.solver.WordleSolverMinimax;
import com.ante.ui.WordleInterface;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WordleDriver {

    static final Logger LOGGER = Logger.getLogger(WordleDriver.class.getName());

    public static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    public static final int WORD_LENGTH = 5;
    public static final int NUMBER_OF_GUESSES = 6;

    private final WordleInterface ui;
    private WordleSolver solver;

    public WordleDriver(WordleInterface ui, WordleSolver solver){
        this.ui = ui;
        this.solver = solver;
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
            //this is not recoverable
            throw new RuntimeException(e);
        }
        return words;
    }



    public void setDebug() {
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.FINER);
        LOGGER.setLevel(Level.FINER);
        LOGGER.addHandler(handler);
    }

    /**
     * Solve for every word in the dictionary
     * @return {1: number of words solved in 1 guess; 2: number of words solved in 2 guesses, etc.}
     */
    public Map<Integer, Integer> bulkSolver() {
        long startTime = System.nanoTime();
        List<String> allWords = getWords();

        Map<String, Integer> guessesPerWord = new HashMap<>(allWords.size());
        for(String target : allWords){
            LOGGER.fine(String.format("Target: %s", target));
            int guesses = 0;
            String currentGuess;
            List<String> validWords = new ArrayList<>(allWords);
            do{
                currentGuess = solver.pickNextGuess(validWords);
                if(currentGuess == null){
                    LOGGER.severe(String.format("Failed on %s", target));
                    guesses = 50;
                    break;
                }
                LOGGER.fine(String.format("Guessing: %s", currentGuess));
                WordResult result = guess(currentGuess, target);

                validWords = removeInvalidOptions(result, validWords);
                guesses++;
                if(guesses == 20){
                    LOGGER.severe("breaking");
                    break;
                }
            } while((!currentGuess.equals(target)));
            guessesPerWord.put(target, guesses);
        }
        Map<Integer, Integer> totalGuesses = new HashMap<>();
        for(String key : guessesPerWord.keySet()){
            Integer guess = guessesPerWord.get(key);
            if(guess > 5){
                ui.print(String.format("%s took %s guesses", key, guess));
            }
            if(totalGuesses.containsKey(guess)){
                totalGuesses.put(guess, totalGuesses.get(guess) + 1);
            } else {
                totalGuesses.put(guess, 1);
            }
        }
        long endTime = System.nanoTime();
        ui.print(totalGuesses.toString());
        printStats(totalGuesses, endTime - startTime);
        return totalGuesses;
    }

    private void printStats(Map<Integer, Integer> totalGuesses, long time){
        int total = 0;
        int sum = 0;
        int fail = 0;
        for(Integer key : totalGuesses.keySet()){
            total += totalGuesses.get(key);
            sum += key * totalGuesses.get(key);
            if(key > 6){
                fail += totalGuesses.get(key);
            }
        }
        ui.print(String.format("Average guesses: %.4f", (double) sum / total));
        ui.print(String.format("Failed: %s (%.2f%%)", fail, (double) fail / total));
        ui.print(String.format("Time taken: %s ms", TimeUnit.NANOSECONDS.toMillis(time)));
    }

    /**
     * See how many guesses, and which guesses, it takes to find the target word
     * @param target the target word
     * @return the number of guesses
     */
    public int autoSolver(String target) {
        int guesses = 0;
        String currentGuess;
        List<String> validWords = getWords();
        do{
            currentGuess = solver.pickNextGuess(validWords);
            ui.print(String.format("Guessing %s", currentGuess));

            WordResult result = guess(currentGuess, target);

            LOGGER.fine(result.toString());

            validWords = removeInvalidOptions(result, validWords);
            guesses++;
            if(guesses == 20){
                ui.print("breaking");
                break;
            }
        } while((!currentGuess.equals(target)));

        ui.print(String.format("Solved in %s guesses", guesses));

        return guesses;
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

    public void manualSolver() {
        List<String> validWords = getWords();
        ui.print(String.format("Suggested word: %s", solver.pickNextGuess(validWords)));
        ui.print("Enter attempted word and results in the format 'word RRWWN'");
        ui.print("Where R = letter in right position, W = letter in wrong position, and N = letter not in word");

        while(true){ //turned this into an infinite loop, I felt it was useful to ignore the 6 guess limit here.
            WordResult wordResult;
            do {
                String input = ui.readLine();
                wordResult = parseResult(input);
                if(wordResult == null){
                    ui.print("Invalid input, try again");
                }
            } while(wordResult == null);

            validWords = removeInvalidOptions(wordResult, validWords);
            ui.print(validWords);
            ui.print(solver.pickNextGuess(validWords));
        }
    }

    private List<String> removeInvalidOptions(WordResult wordResult, List<String> words) {
        List<String> reducedList = new ArrayList<>();
        for(String word : words){
            if(wordResult.isValid(word)){
                reducedList.add(word);
            }
        }
        return reducedList;
    }

    /**
     * Turns a user-input String into a WordResult
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

    public void minimax() {
        List<String> allWords = getWords();
        solver = new WordleSolverMinimax();
        solver.pickNextGuess(allWords);
    }

    public void multiManualSolver(int numTargets) {
        List<String> allWords = getWords();
        List<List<String>> validWords = new ArrayList<>(numTargets);
        for(int i = 0; i < numTargets; i++){
            validWords.add(new ArrayList<>(allWords));
        }

        ui.print(String.format("Suggested word: %s", solver.pickNextGuessMulti(validWords)));
        ui.print("Enter attempted word and results in the format 'word RRWWN NNNRN XXXXX...'");
        ui.print("Where R = letter in right position, W = letter in wrong position, N = letter not in word, XXXXX = word previously solved");

        while(true){
            List<WordResult> wordResults = new ArrayList<>();
            boolean valid = true;
            do {
                String input = ui.readLine();
                String[] strings = input.split(" ");
                if(strings.length != numTargets + 1){
                    ui.print("Invalid input, try again");
                    valid = false;
                }
                for(int i = 1; i <= numTargets; i++){
                    wordResults.add(parseResult(strings[0] + " " + strings[i]));
                }
                for(WordResult result : wordResults){
                    if(result == null){
                        ui.print("Invalid input, try again");
                        valid = false;
                    }
                }

            } while(!valid);

            for(int i  = 0; i < wordResults.size(); i++){
                WordResult result = wordResults.get(i);
                boolean solved = true;
                for(LetterResult letterResult : result.results){
                    if(letterResult != LetterResult.RIGHT_POSITION){
                        solved = false;
                    }
                }
                if(solved){
                    validWords.set(i, new ArrayList<>());
                    continue;
                }

                validWords.set(i, removeInvalidOptions(wordResults.get(i), validWords.get(i)));
                LOGGER.fine("Valid words: " + validWords.get(i));
            }
            ui.print(validWords);
            ui.print(solver.pickNextGuessMulti(validWords));
        }
    }

    public int multiAutoSolver(List<String> targets) {
        //todo
        int guesses = 0;
        String currentGuess;
        List<String> validWords = getWords();
        do{
            currentGuess = solver.pickNextGuess(validWords);
            ui.print(String.format("Guessing %s", currentGuess));

            WordResult result = guess(currentGuess, targets.get(0));

            LOGGER.fine(result.toString());

            validWords = removeInvalidOptions(result, validWords);
            guesses++;
            if(guesses == 20){
                ui.print("breaking");
                break;
            }
        } while((!currentGuess.equals(targets.get(0))));

        ui.print(String.format("Solved in %s guesses", guesses));

        return guesses;
    }
}
/*

o1
bossy nnnnn nnnnn wnnnr nnnnn rnnnr nnnnn nnwnn nwnnn
loyal nnnwn nnnwn nnwwn nnnnn wnwwn nnnwn nnnnn nwnwn
thick wnrnn wnnnn nnnwn wnwnn nnnnn rnwnn wnrnn nwnwn
irate wnwwn nnwwn nnwnn wnnwn nnwnn wrrwn wnnrr nnwnn
cabby nrnnn nrnnn rrrrr nnnnn nrwnr nwnnn nnnnn wrnnn
gaunt nrnrr nrrrr rrrrr nnwwr nrnnn nwnnr nnnnw nrnnn
unfit nwwwr wwnnr rrrrr rrrrr nnnnn nnnrr nnnww nnnnn
faint rrrrr nrnrr rrrrr rrrrr nrnnn nwwnr nnrnw nrnnn
trait rrrrr nnwnr rrrrr rrrrr nnwnn rrrrr wnnwn nnwnn
 */