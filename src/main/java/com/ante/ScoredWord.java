package com.ante;

public record ScoredWord(String word, int score) implements Comparable<ScoredWord> {

    public String getWord() {
        return word;
    }

    @Override
    public int compareTo(ScoredWord other) {
        return Integer.compare(this.score, other.score);
    }
}
