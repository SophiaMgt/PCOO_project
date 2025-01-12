package com.badlogic.kittylost;

import com.badlogic.gdx.Game;

public class KittyLostGame extends Game {
    @Override
    public void create() {
        this.setScreen(new MainMenuScreen(this)); // Starts with main menu screen
    }

    @Override
    public void render() {
        super.render(); // Calls the `render` method of the current screen
    }

    @Override
    public void dispose() {
        getScreen().dispose(); // Frees up current screen resources
    }
}
