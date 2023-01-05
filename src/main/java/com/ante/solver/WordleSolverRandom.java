package com.ante.solver;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class WordleSolverRandom implements WordleSolver {
    @Override
    public String pickNextGuess(List<String> words) {
        return words.get(ThreadLocalRandom.current().nextInt(0, words.size()));
    }

    @Override
    public String pickNextGuessMulti(List<List<String>> words) {
        return words.get(ThreadLocalRandom.current().nextInt(0, words.size())).get(ThreadLocalRandom.current().nextInt(0, words.size()));
    }
}
/* bulk solver results
{1=1, 2=79, 3=583, 4=941, 5=517, 6=153, 7=27, 8=13, 9=1}
Average guesses: 4.0937
Failed: 41 (0.02%)
Time taken: 5237 ms

large list:
{2=125, 3=888, 4=1932, 5=1591, 6=712, 7=311, 8=123, 9=58, 10=11, 11=2, 12=3, 13=1}
Average guesses: 4.6436
Failed: 509 (0.09%)
Time taken: 30591 ms
 */