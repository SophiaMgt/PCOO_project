package com.badlogic.kittylost;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.Polygon;

public class GameScreen implements Screen {
    private final KittyLostGame game;
    private SpriteBatch batch;
    private OrthogonalTiledMapRenderer renderer;
    private TiledMap map;
    private OrthographicCamera camera;
    private Player player;
    private Array<Rectangle> collisionRectangles;
    private Array<Polygon> trap;

    private Texture gameOverTexture; // Texture pour l'image Game Over
    private boolean isGameOver = false; // Indique si le joueur est mort

    public GameScreen(KittyLostGame game) {
        this.game = game;

        batch = new SpriteBatch();
        map = new TmxMapLoader().load("long_map.tmx");
        renderer = new OrthogonalTiledMapRenderer(map);

        // Initialisation de la caméra
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Initialisation du joueur
        player = new Player("tile002.png", 100, 290, 200);

        // Game Over
        gameOverTexture = new Texture("game_over.jpg");

        // Charger les rectangles de collision
        collisionRectangles = new Array<>();
        for (MapObject object : map.getLayers().get("collision").getObjects()) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                collisionRectangles.add(rect);
            }
        }

        // Charger les pièges
        trap = new Array<>();
        for (MapObject object : map.getLayers().get("pièges").getObjects()) {
            if (object instanceof PolygonMapObject) {
                Polygon traps = ((PolygonMapObject) object).getPolygon();
                trap.add(traps);
            }
        }
    }

    // si le joueur meurt
    private void resetGame() {
        // Réinitialiser la position du joueur
        player = new Player("tile002.png", 100, 290, 200);
        isGameOver = false; // Réinitialiser l'état Game Over
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        if (isGameOver) {
            batch.begin();

            // Dessiner l'image Game Over sur tout l'écran
            batch.draw(gameOverTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

            batch.end();

            // Gérer le clic pour redémarrer
            if (Gdx.input.isTouched()) {
                resetGame(); // Réinitialiser la partie
            }
        } else {
            // Mise à jour du joueur et des collisions
            player.update(delta, collisionRectangles, trap);

            // Vérifiez si le joueur est mort
            if (player.isDead()) {
                isGameOver = true;
            }

            // Mettre à jour la position de la caméra
            camera.position.set(player.getX(), player.getY(), 0);
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
        gameOverTexture.dispose();
    }
}
