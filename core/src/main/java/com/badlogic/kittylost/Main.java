package com.badlogic.kittylost;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private OrthoCachedTiledMapRenderer renderer;
    private TiledMap map;
    private OrthographicCamera camera;
    private Player player;

    @Override
    public void create() {
        batch = new SpriteBatch();
        map = new TmxMapLoader().load("map2.tmx");
        renderer = new OrthoCachedTiledMapRenderer(map);

        // Initialisation de la caméra
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Initialisation du joueur
        player = new Player("tile002.png", 100, 260, 200);
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        // Mise à jour du joueur
        player.update(Gdx.graphics.getDeltaTime());

        // Mettre à jour la position de la caméra pour suivre le joueur
        camera.position.set(player.getX(), player.getY(), 0);

        // Vous pouvez ajouter des limites pour éviter que la caméra sorte de la carte
        float mapWidth = map.getProperties().get("width", Integer.class) * 32;  // 32 est la taille d'une tuile
        float mapHeight = map.getProperties().get("height", Integer.class) * 32;

        // Limiter la caméra pour ne pas sortir de la carte
        camera.position.x = Math.max(camera.viewportWidth / 2, Math.min(camera.position.x, mapWidth - camera.viewportWidth / 2));
        camera.position.y = Math.max(camera.viewportHeight / 2, Math.min(camera.position.y, mapHeight - camera.viewportHeight / 2));

        // Mettre à jour la caméra
        camera.update();

        // Rendu de la carte et du joueur
        batch.begin();
        renderer.setView(camera);
        renderer.render();
        player.render(batch);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        player.dispose();
    }
}
