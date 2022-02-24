package com.ante;

public class Range
{
    private int low;
    private int high;

    public Range(int low, int high){
        this.low = low;
        this.high = high;
    }

    public Range(int low, int high, Range other){
        this.low = Integer.max(low, other.low);
        this.high = Integer.min(high, other.high);
    }

    public void newMax(int newMax){
        this.high = newMax;
    }

    public void newMin(int newMin){
        this.low = newMin;
    }

    public void intersect(Range other){
        this.low = Integer.max(this.low, other.low);
        this.high = Integer.min(this.high, other.high);
    }

    public boolean contains(int number){
        return (number >= low && number <= high);
    }

    @Override
    public String toString(){
        return "[" + low + " - " + high + "]";
    }
}
