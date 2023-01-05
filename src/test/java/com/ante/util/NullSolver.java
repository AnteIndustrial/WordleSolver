package com.ante.util;

import com.ante.solver.WordleSolver;

import java.util.List;

public class NullSolver implements WordleSolver {
    @Override
    public String pickNextGuess(List<String> words) {
        return null;
    }

    @Override
    public String pickNextGuessMulti(List<List<String>> words) {
        return null;
    }
}
