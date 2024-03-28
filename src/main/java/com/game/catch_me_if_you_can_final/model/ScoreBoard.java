package com.game.catch_me_if_you_can_final.model;

import com.game.catch_me_if_you_can_final.model.manipulator.TimeManipulator;

public class ScoreBoard {

    private int score;
    private long nanoValue = 1000000000;
    private TimeManipulator timeManipulator;

    public ScoreBoard() {
        timeManipulator = new TimeManipulator();
    }

    public void monitorScore() {

    }

    public void getScore() {

    }
}
