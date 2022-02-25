package com.ante;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;

import static com.ante.WordleSolver.LOGGER;
import static org.junit.jupiter.api.Assertions.*;

class WordleSolverTest {

    private void setLoggingToFine() {
        LOGGER.setLevel(Level.FINE);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.FINER);
        LOGGER.addHandler(handler);
    }

    @Test
    void testGuessHasDuplicateAndTargetDoesntDuplicateIsMarkedNotInWord(){
        WordResult result = WordleSolver.guess("taunt", "yacht");

        List<LetterResult> letters = new ArrayList<>();
        letters.add(LetterResult.NOT_IN_WORD);
        letters.add(LetterResult.RIGHT_POSITION);
        letters.add(LetterResult.NOT_IN_WORD);
        letters.add(LetterResult.NOT_IN_WORD);
        letters.add(LetterResult.RIGHT_POSITION);
        WordResult expected = new WordResult("taunt", letters);

        assertEquals(expected, result);
    }

    @Test
    void testGuessHasDuplicateAndTargetDoesntDuplicateIsMarkedNotInWordButWithLettersInADifferentOrder(){
        WordResult result = WordleSolver.guess("taunt", "tachy");

        List<LetterResult> letters = new ArrayList<>();
        letters.add(LetterResult.RIGHT_POSITION);
        letters.add(LetterResult.RIGHT_POSITION);
        letters.add(LetterResult.NOT_IN_WORD);
        letters.add(LetterResult.NOT_IN_WORD);
        letters.add(LetterResult.NOT_IN_WORD);
        WordResult expected = new WordResult("taunt", letters);

        assertEquals(expected, result);
    }

    @Test
    void testGuessAllRight(){
        WordResult result = WordleSolver.guess("abcde", "abcde");

        List<LetterResult> letters = new ArrayList<>();
        letters.add(LetterResult.RIGHT_POSITION);
        letters.add(LetterResult.RIGHT_POSITION);
        letters.add(LetterResult.RIGHT_POSITION);
        letters.add(LetterResult.RIGHT_POSITION);
        letters.add(LetterResult.RIGHT_POSITION);
        WordResult expected = new WordResult("abcde", letters);

        assertEquals(expected, result);
    }

    @Test
    void testGuessAllLettersNotInWord(){
        WordResult result = WordleSolver.guess("abcde", "fghij");

        List<LetterResult> letters = new ArrayList<>();
        letters.add(LetterResult.NOT_IN_WORD);
        letters.add(LetterResult.NOT_IN_WORD);
        letters.add(LetterResult.NOT_IN_WORD);
        letters.add(LetterResult.NOT_IN_WORD);
        letters.add(LetterResult.NOT_IN_WORD);
        WordResult expected = new WordResult("abcde", letters);

        assertEquals(expected, result);
    }

    @Test
    void testGuessAllLettersInWrongPlace(){
        WordResult result = WordleSolver.guess("abcde", "bcdea");

        List<LetterResult> letters = new ArrayList<>();
        letters.add(LetterResult.WRONG_POSITION);
        letters.add(LetterResult.WRONG_POSITION);
        letters.add(LetterResult.WRONG_POSITION);
        letters.add(LetterResult.WRONG_POSITION);
        letters.add(LetterResult.WRONG_POSITION);
        WordResult expected = new WordResult("abcde", letters);

        assertEquals(expected, result);
    }

    @Test
    void testGuessSomeOfEverythingButNoDuplicateLetters(){
        WordResult result = WordleSolver.guess("abcde", "abefg");

        List<LetterResult> letters = new ArrayList<>();
        letters.add(LetterResult.RIGHT_POSITION);
        letters.add(LetterResult.RIGHT_POSITION);
        letters.add(LetterResult.NOT_IN_WORD);
        letters.add(LetterResult.NOT_IN_WORD);
        letters.add(LetterResult.WRONG_POSITION);
        WordResult expected = new WordResult("abcde", letters);

        assertEquals(expected, result);
    }

    @Test
    void testGuessTargetHasDuplicatesButGuessDoesnt(){
        WordResult result = WordleSolver.guess("abcde", "aaced");

        List<LetterResult> letters = new ArrayList<>();
        letters.add(LetterResult.RIGHT_POSITION);
        letters.add(LetterResult.NOT_IN_WORD);
        letters.add(LetterResult.RIGHT_POSITION);
        letters.add(LetterResult.WRONG_POSITION);
        letters.add(LetterResult.WRONG_POSITION);
        WordResult expected = new WordResult("abcde", letters);

        assertEquals(expected, result);
    }

    @Test
    void testGuessGuessHasDuplicatesButTargetDoesnt(){
        WordResult result = WordleSolver.guess("aacde", "abcdf");

        List<LetterResult> letters = new ArrayList<>();
        letters.add(LetterResult.RIGHT_POSITION);
        letters.add(LetterResult.NOT_IN_WORD);
        letters.add(LetterResult.RIGHT_POSITION);
        letters.add(LetterResult.RIGHT_POSITION);
        letters.add(LetterResult.NOT_IN_WORD);
        WordResult expected = new WordResult("aacde", letters);

        assertEquals(expected, result);
    }

    @Test
    void testGuessHasDuplicatesAndBothAreInTheWrongPosition(){
        WordResult result = WordleSolver.guess("trait", "wrath");

        List<LetterResult> letters = new ArrayList<>();
        letters.add(LetterResult.WRONG_POSITION);
        letters.add(LetterResult.RIGHT_POSITION);
        letters.add(LetterResult.RIGHT_POSITION);
        letters.add(LetterResult.NOT_IN_WORD);
        letters.add(LetterResult.NOT_IN_WORD);
        WordResult expected = new WordResult("trait", letters);

        assertEquals(expected, result);
    }

    @Test
    void testWhereTargetAndGuessHaveDuplicates(){
        WordResult result = WordleSolver.guess("bevel", "wheel");

        List<LetterResult> letters = new ArrayList<>();
        letters.add(LetterResult.NOT_IN_WORD);
        letters.add(LetterResult.WRONG_POSITION);
        letters.add(LetterResult.NOT_IN_WORD);
        letters.add(LetterResult.RIGHT_POSITION);
        letters.add(LetterResult.RIGHT_POSITION);
        WordResult expected = new WordResult("bevel", letters);

        assertEquals(expected, result);
    }
}