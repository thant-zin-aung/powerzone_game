package com.game.catch_me_if_you_can_final.model.power;

public class NoPower implements Power {
    private final  int energyFillTime = 0;

    @Override
    public void use() {
        System.out.println("This player has no power...");
    }
}
