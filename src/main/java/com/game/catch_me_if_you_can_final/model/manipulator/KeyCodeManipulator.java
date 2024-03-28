package com.game.catch_me_if_you_can_final.model.manipulator;

import javafx.scene.input.KeyCode;

import java.util.HashMap;
import java.util.Map;

public class KeyCodeManipulator {

    private final Map<String, KeyCode> keyMap1;
    private final Map<String, KeyCode> keyMap2;
    private final Map<String, KeyCode> keyMap3;
    private final Map<String, KeyCode> keyMap4;

    public KeyCodeManipulator() {
        keyMap1 = new HashMap<>();
        keyMap2 = new HashMap<>();
        keyMap3 = new HashMap<>();
        keyMap4 = new HashMap<>();

        keyMap1.put("up",KeyCode.W);
        keyMap1.put("down",KeyCode.S);
        keyMap1.put("left",KeyCode.A);
        keyMap1.put("right",KeyCode.D);
        keyMap1.put("power",KeyCode.SHIFT);

        keyMap2.put("up",KeyCode.U);
        keyMap2.put("down",KeyCode.J);
        keyMap2.put("left",KeyCode.H);
        keyMap2.put("right",KeyCode.K);
        keyMap2.put("power",KeyCode.SPACE);

        keyMap3.put("up",KeyCode.UP);
        keyMap3.put("down",KeyCode.DOWN);
        keyMap3.put("left",KeyCode.LEFT);
        keyMap3.put("right",KeyCode.RIGHT);
        keyMap3.put("power",KeyCode.NUMPAD0);

        keyMap4.put("up",KeyCode.NUMPAD8);
        keyMap4.put("down",KeyCode.NUMPAD5);
        keyMap4.put("left",KeyCode.NUMPAD4);
        keyMap4.put("right",KeyCode.NUMPAD6);
        keyMap4.put("power",KeyCode.ADD);
    }

    public Map<String, KeyCode> getKeyMap1() {
        return keyMap1;
    }

    public Map<String, KeyCode> getKeyMap2() {
        return keyMap2;
    }

    public Map<String, KeyCode> getKeyMap3() {
        return keyMap3;
    }

    public Map<String, KeyCode> getKeyMap4() {
        return keyMap4;
    }
}
