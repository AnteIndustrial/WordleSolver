package com.ante.solver;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordleSolverRandomTest {

    //this will fail about 1 in a million attempts. oh well.
    @Test
    void basicFunctionalityCheck(){
        WordleSolverRandom solver = new WordleSolverRandom();
        List<String> words = new ArrayList<>();
        words.add("a");
        words.add("b");

        List<String> results = new ArrayList<>();
        results.add(solver.pickNextGuess(words));
        results.add(solver.pickNextGuess(words));
        results.add(solver.pickNextGuess(words));
        results.add(solver.pickNextGuess(words));
        results.add(solver.pickNextGuess(words));
        results.add(solver.pickNextGuess(words));
        results.add(solver.pickNextGuess(words));
        results.add(solver.pickNextGuess(words));
        results.add(solver.pickNextGuess(words));
        results.add(solver.pickNextGuess(words));
        results.add(solver.pickNextGuess(words));
        results.add(solver.pickNextGuess(words));
        results.add(solver.pickNextGuess(words));
        results.add(solver.pickNextGuess(words));
        results.add(solver.pickNextGuess(words));
        results.add(solver.pickNextGuess(words));
        results.add(solver.pickNextGuess(words));

        assertTrue(results.contains("a"));
        assertTrue(results.contains("b"));
    }

}