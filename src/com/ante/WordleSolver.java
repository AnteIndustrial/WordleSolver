package com.ante;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class WordleSolver {

    public static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    public static final int WORD_LENGTH = 5;
    public static final int NUMBER_OF_GUESSES = 6;

    private List<Map<String, Integer>> countAtPosition;
    public List<ScoredWord> scoredWords;

    public WordleSolver(){
        List<String> words = getWords();
        scoreWords(words);
        Collections.sort(scoredWords);
    }

    /**
     * Give every word a score based on how frequently its letters appear in the dictionary
     * @param words a list of dictionary words
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
    }

    /**
     * Count how often each letter appears in each position
     * @param words a list of dictionary words
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
     * read a dictionary for a list of words
     * @return a list of words
     */
    private List<String> getWords() {
        List<String> words = new ArrayList<>();

        try(Scanner sc = new Scanner(new File(("src/resources/smallwords.txt")))){
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

        System.out.println(solver.scoredWords.get(solver.scoredWords.size() - 1));
        System.out.println("Enter attempted word and results in the format 'word RRWWN'");
        System.out.println("Where R = letter in right position, W = letter in wrong position, and N = letter not in word");

        for(int i = 0; i < NUMBER_OF_GUESSES; i++) {
            String input = reader.nextLine();
            WordResult wordResult = solver.parseResult(input);
            solver.removeInvalidOptions(wordResult);
            System.out.println(solver.scoredWords);
            System.out.println(solver.scoredWords.get(solver.scoredWords.size() - 1));
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
        Collections.sort(scoredWords);
    }

    private WordResult parseResult(String input) {
        String[] split = input.split(" ");
        if(split.length != 2){
            throw new RuntimeException("invalid input format");
        }
        String attemptedWord = split[0];
        String results = split[1];
        if(results.length() != WORD_LENGTH){
            throw new RuntimeException("invalid result format");
        }
        List<LetterResult> letterResults = new ArrayList<>();
        for(int i = 0; i < WORD_LENGTH; i++){
            switch (results.charAt(i)) {
                case 'R' -> letterResults.add(LetterResult.RIGHT_POSITION);
                case 'W' -> letterResults.add(LetterResult.WRONG_POSITION);
                case 'N' -> letterResults.add(LetterResult.NOT_IN_WORD);
                default -> throw new RuntimeException("invalid char in results");
            }
        }
        return new WordResult(attemptedWord, letterResults);
    }
}
