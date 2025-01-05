package com.badlogic.kittylost;

import com.badlogic.gdx.Game;

public class KittyLostGame extends Game {
    @Override
    public void create() {
        this.setScreen(new MainMenuScreen(this)); // Démarre avec l'écran du menu principal
    }

    @Override
    public void render() {
        super.render(); // Appelle la méthode `render` de l'écran actuel
    }

    @Override
    public void dispose() {
        getScreen().dispose(); // Libère les ressources de l'écran actuel
    }
}
