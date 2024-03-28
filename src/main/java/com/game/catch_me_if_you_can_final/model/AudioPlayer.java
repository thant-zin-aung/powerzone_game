package com.game.catch_me_if_you_can_final.model;

import com.game.catch_me_if_you_can_final.Main;
import javafx.application.Platform;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AudioPlayer {

    Map<String,String> audioMap = new HashMap<>();
    AudioClip audioClip;
    String audioName;
    public AudioPlayer() {
        audioMap.put("selectionSound",getURIAudioPath("selection1.wav"));
        audioMap.put("startGame",getURIAudioPath("start-game.wav"));
        audioMap.put("fullEnergy",getURIAudioPath("full-energy.wav"));
        audioMap.put("blueBunny",getURIAudioPath("blue-bunny.mp3"));
        audioMap.put("flash",getURIAudioPath("blue-bunny.wav"));
        audioMap.put("pinkBunny",getURIAudioPath("pink-bunny.wav"));
        audioMap.put("turtle",getURIAudioPath("turtle.wav"));
        audioMap.put("superTurtle",getURIAudioPath("super-turtle.wav"));
        audioMap.put("turtleRun",getURIAudioPath("turtle-run.mp3"));
        audioMap.put("pig",getURIAudioPath("pig.wav"));
        audioMap.put("zPanda",getURIAudioPath("z-panda.wav"));
        audioMap.put("dog",getURIAudioPath("dog.wav"));
        audioMap.put("happyDog",getURIAudioPath("happy-dog.wav"));
        audioMap.put("happyDog2",getURIAudioPath("happy-dog2.wav"));
        audioMap.put("cat",getURIAudioPath("cat.wav"));
        audioMap.put("mouse",getURIAudioPath("mouse.wav"));
        audioMap.put("blackMonkeyThrow",getURIAudioPath("black-monkey-throw.wav"));
        audioMap.put("blackMonkeyReload",getURIAudioPath("black-monkey-reload.mp3"));
        audioMap.put("superCatNinja1",getURIAudioPath("super-cat-ninja-1.mp3"));
        audioMap.put("superCatNinja2",getURIAudioPath("super-cat-ninja-2.mp3"));
        audioMap.put("superCatNinja3",getURIAudioPath("super-cat-ninja-3.mp3"));
        audioMap.put("superCatNinja4",getURIAudioPath("super-cat-ninja-4.mp3"));
        audioMap.put("superCatNinja5",getURIAudioPath("super-cat-ninja-5.mp3"));
        audioMap.put("superCatNinja6",getURIAudioPath("super-cat-ninja-6.mp3"));
        audioMap.put("superCatNinja7",getURIAudioPath("super-cat-ninja-7.mp3"));
        audioMap.put("superCatDuplicate",getURIAudioPath("super-cat-duplicate.wav"));
        audioMap.put("owl",getURIAudioPath("owl.wav"));
        audioMap.put("die",getURIAudioPath("die.wav"));
        audioMap.put("blueBird",getURIAudioPath("blue-bird.wav"));
        audioMap.put("loveBird1",getURIAudioPath("love-bird-1.wav"));
        audioMap.put("loveBird2",getURIAudioPath("love-bird-2.wav"));
        audioMap.put("loveBird3",getURIAudioPath("love-bird-3.wav"));

        audioClip = new AudioClip(audioMap.get("selectionSound"));

    }

    private String getURIAudioPath(String audioName) {
        return Objects.requireNonNull(Main.class.getResource("/audios/"+audioName)).toString();
    }

    public AudioClip getAudioClip() {
        return audioClip;
    }

    public void play(String audioName,double rate,double volume) {
        this.audioName = audioName;
        audioClip = new AudioClip(audioMap.get(audioName));
        audioClip.setRate(rate);
        audioClip.setVolume(volume);
        audioClip.play();
    }
    public void stop() {
        audioClip.stop();
    }
}
