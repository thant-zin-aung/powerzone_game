package com.game.catch_me_if_you_can_final.model.power;

import com.game.catch_me_if_you_can_final.model.AudioPlayer;

public interface Power {
    double energyFullSoundVolume = 0.3;
    AudioPlayer audioPlayer = new AudioPlayer();
    void use();

}
