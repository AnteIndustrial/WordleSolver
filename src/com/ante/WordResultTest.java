package com.ante;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WordResultTest {

    @Test
    void isValidMultipleCases(){
        List<LetterResult> list = new ArrayList<>();
        list.add(LetterResult.NOT_IN_WORD);
        list.add(LetterResult.RIGHT_POSITION);
        list.add(LetterResult.RIGHT_POSITION);
        list.add(LetterResult.RIGHT_POSITION);
        list.add(LetterResult.RIGHT_POSITION);
        WordResult result = new WordResult("cause", list);
        assertTrue(result.isValid("pause"));
        assertTrue(result.isValid("mause"));
        assertFalse(result.isValid("cause"));
        assertFalse(result.isValid("aaaaa"));
        assertFalse(result.isValid("pouse"));
        assertFalse(result.isValid("paose"));
        assertFalse(result.isValid("pauoe"));
        assertFalse(result.isValid("pauso"));
    }

    @Test
    void isValidReturnsTrueWhenResultSaysDoubleAndWordContainsDouble(){
        List<LetterResult> list = new ArrayList<>();
        list.add(LetterResult.RIGHT_POSITION);
        list.add(LetterResult.RIGHT_POSITION);
        list.add(LetterResult.RIGHT_POSITION);
        list.add(LetterResult.RIGHT_POSITION);
        list.add(LetterResult.NOT_IN_WORD);
        WordResult result = new WordResult("proof", list);
        assertTrue(result.isValid("proom"));
    }

    @Test
    void isValidReturnsFalseWhenResultSaysDoubleAndWordDoesNotContainDouble(){
        List<LetterResult> list = new ArrayList<>();
        list.add(LetterResult.RIGHT_POSITION);
        list.add(LetterResult.RIGHT_POSITION);
        list.add(LetterResult.RIGHT_POSITION);
        list.add(LetterResult.RIGHT_POSITION);
        list.add(LetterResult.NOT_IN_WORD);
        WordResult result = new WordResult("proof", list);
        assertFalse(result.isValid("promp"));
    }

    @Test
    void isValidReturnsTrueWhenResultsSayDoubleInWrongPlaceAndWordContainsDoubleInNewPlace(){
        List<LetterResult> list = new ArrayList<>();
        list.add(LetterResult.NOT_IN_WORD);
        list.add(LetterResult.NOT_IN_WORD);
        list.add(LetterResult.WRONG_POSITION);
        list.add(LetterResult.WRONG_POSITION);
        list.add(LetterResult.NOT_IN_WORD);
        WordResult result = new WordResult("proof", list);
        assertTrue(result.isValid("ooabc"));
    }

    @Test
    void isValidReturnsFalseWhenResultsSayDoubleInWrongPlaceAndWordHasNoDouble(){
        List<LetterResult> list = new ArrayList<>();
        list.add(LetterResult.NOT_IN_WORD);
        list.add(LetterResult.NOT_IN_WORD);
        list.add(LetterResult.WRONG_POSITION);
        list.add(LetterResult.WRONG_POSITION);
        list.add(LetterResult.NOT_IN_WORD);
        WordResult result = new WordResult("proof", list);
        assertFalse(result.isValid("oabcd"));
    }

    @Test
    void isValidReturnsFalseWhenResultSaysNoDoublesAndWordContainsDouble() {

        List<LetterResult> list = new ArrayList<>();
        list.add(LetterResult.RIGHT_POSITION);
        list.add(LetterResult.RIGHT_POSITION);
        list.add(LetterResult.RIGHT_POSITION);
        list.add(LetterResult.NOT_IN_WORD);
        list.add(LetterResult.NOT_IN_WORD);
        WordResult result = new WordResult("proof", list);
        assertFalse(result.isValid("promo"));
    }
}