package com.mygdx.sponze.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.sponze.LoadModelsTest;

public class DesktopLauncher {

    public static void main (String[] arg) {

        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Sponza";
        config.width = 960;//1280 1080 is cornell
        config.height = 540;
        //config.width = 1024;
        //config.height = 576;
        config.samples = 0;
        config.useGL30 = true;
        config.gles30ContextMajorVersion = 3;
        config.gles30ContextMinorVersion = 2;

        config.vSyncEnabled = true; // Setting to false disables vertical sync
//        config.foregroundFPS = 60; // Setting to 0 disables foreground fps throttling
//        config.backgroundFPS = 30;
        LwjglApplication app = new LwjglApplication(new LoadModelsTest(), config );
    }
}

