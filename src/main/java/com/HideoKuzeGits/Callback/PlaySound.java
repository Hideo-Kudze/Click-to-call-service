package com.HideoKuzeGits.Callback;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.BufferedInputStream;
import java.io.FileInputStream;

/**
 * Created by root on 22.07.14.
 */
@Component
public class PlaySound implements ApplicationListener<ContextRefreshedEvent> {


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            String fileName = "/home/hideo/beep_end.wav";
            Clip clip = AudioSystem.getClip();
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(new FileInputStream(fileName)));
            clip.open(inputStream);
            clip.loop(3);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
