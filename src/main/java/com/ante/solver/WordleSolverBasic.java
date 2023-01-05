package com.ante.solver;

import com.ante.ScoredWord;
import com.ante.WordleDriver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordleSolverBasic implements WordleSolver{

    /**
     * Choose the next word to guess
     * @param words a list of words to choose from
     * @return the chosen word
     */
    @Override
    public String pickNextGuess(List<String> words){
        List<ScoredWord> scoredWords = scoreWords(words);
        if(scoredWords.size() == 0){
            return null;
        }
        return scoredWords.get(scoredWords.size() - 1).getWord();

    }

    @Override
    public String pickNextGuessMulti(List<List<String>> words) {
        List<List<ScoredWord>> listOfScores = new ArrayList<>();
        for(List<String> list : words) {
            List<ScoredWord> scoredWords = scoreWords(list);

            listOfScores.add(scoredWords);
        }
        String bestWord = null;
        int bestScore = 0;

        for(List<ScoredWord> list : listOfScores) {
            if(list.size() == 1){
                return list.get(0).getWord();
            }
            if(list.size() == 0){
                continue;
            }
            ScoredWord best = list.get(list.size() - 1);
            if(best.score() > bestScore){
                bestScore = best.score();
                bestWord = best.getWord();
            }
        }
        return bestWord;
    }


    /**
     * Give every word a score based on how frequently its letters appear in the dictionary
     * @param words a list of dictionary words
     * @return those words, with a score, and sorted low to high
     */
    private List<ScoredWord> scoreWords(List<String> words) {
        List<Map<String, Integer>> countAtPosition = countLettersAtPositions(words);
        List<ScoredWord> scoredWords = new ArrayList<>(words.size());

        for(String word: words){
            int score = 0;
            List<Character> previousCharacters = new ArrayList<>(5);
            for(int i = 0; i < WordleDriver.WORD_LENGTH; i++){
                char c = word.charAt(i);
                //for every letter in the word
                //count how often the letter is in this position
                score += countAtPosition.get(i).get(Character.toString(c));
                if(!previousCharacters.contains(c)) {
                    for (int j = 0; j < WordleDriver.WORD_LENGTH; j++) {
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
        return scoredWords;
    }

    /**
     * Count how often each letter appears in each position
     * @param words a list of dictionary words.txt
     * @return a list of how often each letter appears in each position
     */
    private List<Map<String, Integer>> countLettersAtPositions(List<String> words) {
        List<Map<String, Integer>> countAtPosition = initialiseCountAtPositionList();
        for(String word : words){
            for(int i = 0; i < WordleDriver.WORD_LENGTH; i++) {
                String c = Character.toString(word.charAt(i));
                int previousCount = countAtPosition.get(i).get(c);
                countAtPosition.get(i).put(c, previousCount + 1);
            }
        }
        return countAtPosition;
    }

    /**
     * Set up a List of empty Maps
     * @return a List of empty Maps
     */
    private List<Map<String, Integer>> initialiseCountAtPositionList() {
        List<Map<String, Integer>> countAtPosition = new ArrayList<>();
        for(int i = 0; i < WordleDriver.WORD_LENGTH; i++) {
            countAtPosition.add(new HashMap<>());
            for(int j = 0; j < WordleDriver.ALPHABET.length(); j++){
                countAtPosition.get(i).put(Character.toString(WordleDriver.ALPHABET.charAt(j)), 0);
            }
        }
        return countAtPosition;
    }
}
/* bulk solver results:
{1=1, 2=132, 3=941, 4=968, 5=217, 6=41, 7=11, 8=3, 9=1}
Average guesses: 3.6289
Failed: 15
Time taken: 8700 ms

large list:
{1=1, 2=187, 3=1553, 4=2255, 5=1054, 6=407, 7=166, 8=81, 9=31, 10=14, 11=5, 12=2, 13=1}
Average guesses: 4.1839
Failed: 300 (0.05%)
Time taken: 51827 ms
 */