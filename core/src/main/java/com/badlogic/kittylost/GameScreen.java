package com.badlogic.kittylost;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class GameScreen implements Screen {
    private final KittyLostGame game;
    private SpriteBatch batch;
    private OrthoCachedTiledMapRenderer renderer;
    private TiledMap map;
    private OrthographicCamera camera;
    private Player player;
    private Array<Rectangle> collisionRectangles;

    public GameScreen(KittyLostGame game) {
        this.game = game;

        batch = new SpriteBatch();
        map = new TmxMapLoader().load("map2.tmx");
        renderer = new OrthoCachedTiledMapRenderer(map);

        // Initialisation de la caméra
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Initialisation du joueur
        player = new Player("tile002.png", 100, 290, 200);

        // Charger les rectangles de collision
        collisionRectangles = new Array<>();
        for (MapObject object : map.getLayers().get("collision").getObjects()) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                collisionRectangles.add(rect);
            }
        }
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        // Mise à jour du joueur avec les collisions
        player.update(delta, collisionRectangles);

        // Mettre à jour la position de la caméra pour suivre le joueur
        camera.position.set(player.getX(), player.getY(), 0);

        // Limiter la caméra pour qu'elle reste dans les limites de la carte
        float mapWidth = map.getProperties().get("width", Integer.class) * 32;
        float mapHeight = map.getProperties().get("height", Integer.class) * 32;
        camera.position.x = Math.max(camera.viewportWidth / 2, Math.min(camera.position.x, mapWidth - camera.viewportWidth / 2));
        camera.position.y = Math.max(camera.viewportHeight / 2, Math.min(camera.position.y, mapHeight - camera.viewportHeight / 2));

        camera.update();

        // Rendu de la carte et du joueur
        batch.begin();
        renderer.setView(camera);
        renderer.render();
        player.render(batch);
        batch.end();
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
        player.dispose();
    }
}
