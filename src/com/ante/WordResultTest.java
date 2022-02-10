package com.ante;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WordResultTest {

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