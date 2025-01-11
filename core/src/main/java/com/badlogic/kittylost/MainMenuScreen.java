package com.badlogic.kittylost;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MainMenuScreen implements Screen {
    private final KittyLostGame game;
    private SpriteBatch batch;
    private Texture menuImage;
    private OrthographicCamera camera;

    public MainMenuScreen(KittyLostGame game) {
        this.game = game;
        batch = new SpriteBatch();
        menuImage = new Texture("Menu_screen.png"); // image d'accueil
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        // Dessiner l'image en pleine largeur et hauteur de l'écran
        batch.draw(menuImage, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        batch.end();

        // Gérer les clics
        if (Gdx.input.isTouched()) {
            // Passer à l'écran de jeu
            game.setScreen(new GameScreen(game));
            dispose(); // Libérer les ressources de l'écran actuel
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        batch.dispose();
        menuImage.dispose();
    }
}
