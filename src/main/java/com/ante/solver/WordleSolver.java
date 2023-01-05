package com.ante.solver;

import java.util.List;

public interface WordleSolver {

    String pickNextGuess(List<String> words);

    String pickNextGuessMulti(List<List<String>> words);
}
