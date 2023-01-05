package com.ante;

import com.ante.solver.WordleSolverBasic;
import com.ante.util.NullInterface;
import com.ante.util.NullSolver;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WordleDriverTest {

    @Test
    void getRightResultWhereGuessHasDuplicateAndTargetDoesntDuplicateIsMarkedNotInWord(){
        WordResult result = WordleDriver.guess("taunt", "yacht");

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
    void getRightResultWhereGuessHasDuplicateAndTargetDoesntDuplicateIsMarkedNotInWordButWithLettersInADifferentOrder(){
        WordResult result = WordleDriver.guess("taunt", "tachy");

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
    void getRightResultWithAllRight(){
        WordResult result = WordleDriver.guess("abcde", "abcde");

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
    void getRightResultWithAllLettersNotInWord(){
        WordResult result = WordleDriver.guess("abcde", "fghij");

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
    void getRightResultWithAllLettersInWrongPlace(){
        WordResult result = WordleDriver.guess("abcde", "bcdea");

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
    void getRightResultWithSomeOfEverythingButNoDuplicateLetters(){
        WordResult result = WordleDriver.guess("abcde", "abefg");

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
    void getRightResultWhereTargetHasDuplicatesButGuessDoesnt(){
        WordResult result = WordleDriver.guess("abcde", "aaced");

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
    void getRightResultWhereGuessHasDuplicatesButTargetDoesnt(){
        WordResult result = WordleDriver.guess("aacde", "abcdf");

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
    void getRightResultWhereGuessHasDuplicatesAndBothAreInTheWrongPosition(){
        WordResult result = WordleDriver.guess("trait", "wrath");

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
    void getRightResultWhereTargetAndGuessHaveDuplicates(){
        WordResult result = WordleDriver.guess("bevel", "wheel");

        List<LetterResult> letters = new ArrayList<>();
        letters.add(LetterResult.NOT_IN_WORD);
        letters.add(LetterResult.WRONG_POSITION);
        letters.add(LetterResult.NOT_IN_WORD);
        letters.add(LetterResult.RIGHT_POSITION);
        letters.add(LetterResult.RIGHT_POSITION);
        WordResult expected = new WordResult("bevel", letters);

        assertEquals(expected, result);
    }

    //todo this should be an integration test of Solver + Driver
    @Test
    void autoSolverCanSolveAWord(){
        WordleDriver solver = new WordleDriver(new NullInterface(), new WordleSolverBasic());
        int guesses = solver.autoSolver("bloke");
        assertTrue(guesses > 0);
        assertTrue(guesses < 10);
    }

    //todo this should be an integration test of Solver + Driver
    //This test takes >10 seconds, don't want it running all the time
    @Test
    @Disabled
    void bulkTestSolvesAllWords(){
        WordleDriver solver = new WordleDriver(new NullInterface(), new WordleSolverBasic());
        Map<Integer, Integer> guessMap = solver.bulkSolver();

        assertNotNull(guessMap);
        assertTrue(guessMap.get(2) > 0);
        assertFalse(guessMap.containsKey(20));
        assertFalse(guessMap.containsKey(50));
    }
}