package com.game.catch_me_if_you_can_final.model;


public class NumberGenerator {
    public static int generate(int start,int end) {
        return (int)(Math.random()*(end-start))+start;
    }
}
