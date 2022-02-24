package com.ante;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RangeTest {

    @Test
    void testConstructor(){
        Range one = new Range(0,3);
        assertFalse(one.contains(-1));
        assertTrue(one.contains(0));
        assertTrue(one.contains(1));
        assertTrue(one.contains(2));
        assertTrue(one.contains(3));
        assertFalse(one.contains(4));
    }

    @Test
    void testConstructorPassingInAnotherRange(){
        Range one = new Range(0,5);
        Range two = new Range(1,6, one);

        assertFalse(two.contains(-1));
        assertFalse(two.contains(0));
        assertTrue(two.contains(1));
        assertTrue(two.contains(2));
        assertTrue(two.contains(3));
        assertTrue(two.contains(4));
        assertTrue(two.contains(5));
        assertFalse(two.contains(6));
        assertFalse(two.contains(7));
    }
}