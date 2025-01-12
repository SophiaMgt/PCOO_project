package com.badlogic.kittylost;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
    private Texture fishTexture; // Fish texture
    private Array<Rectangle> fishRectangles; // Fish List

    private int score;
    private int death_count;// Player score
    private BitmapFont font; // Font for displaying text

    private Texture gameOverTexture; // Texture for Game Over image
    private boolean isGameOver = false; // Indicates whether the player is dead

    private Rectangle endGameZone; // End zone
    private boolean endWin = false; // Indicates whether the player has won

    public GameScreen(KittyLostGame game) {
        this.game = game;

        batch = new SpriteBatch();
        map = new TmxMapLoader().load("long_map.tmx");
        renderer = new OrthogonalTiledMapRenderer(map);

        // Camera initialization
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Player initialization
        player = new Player("catracter.png", 100, 290, 200);

        // Game Over
        gameOverTexture = new Texture("game_over.jpg");

        // Load collision rectangles
        collisionRectangles = new Array<>();
        for (MapObject object : map.getLayers().get("collision").getObjects()) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                collisionRectangles.add(rect);
            }
        }

        // Load the end zone
        for (MapObject object : map.getLayers().get("end_zone").getObjects()) {
            if (object instanceof RectangleMapObject) {
                endGameZone = ((RectangleMapObject) object).getRectangle();
                break;
            }
        }

        // Load traps
        trap = new Array<>();
        for (MapObject object : map.getLayers().get("pièges").getObjects()) {
            if (object instanceof PolygonMapObject) {
                Polygon traps = ((PolygonMapObject) object).getPolygon();
                trap.add(traps);
            }
        }

        // // Load fishs
        fishTexture = new Texture("fish.png");
        fishRectangles = new Array<>();
        for (MapObject object : map.getLayers().get("poisson").getObjects()) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                fishRectangles.add(rect);
            }
        }

        // Initialize the score and the font
        score = 0;
        death_count = 0;
        font = new BitmapFont();
        font.setColor(new Color(205 / 255f, 170 / 255f, 75 / 255f, 1));
        font.getData().setScale(2);
    }

    // if the player (cat) die
    private void resetGame() {
        // Reset th player's position
        player = new Player("catracter.png", 100, 290, 200);
        isGameOver = false; // Reset Game over
        death_count += 1;
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        if (isGameOver) {
            batch.begin();

            // Print Game over image full screen
            batch.draw(gameOverTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

            batch.end();

            // clic to start
            if (Gdx.input.isTouched()) {
                resetGame(); // Reset the game
            }
        } else {
            // Update of the player and collisions
            player.update(delta, collisionRectangles, trap);

            // Check if the player is dead
            if (player.isDead()) {
                isGameOver = true;
            }

            Rectangle playerRect = new Rectangle(player.getX(), player.getY(), player.getWidth(), player.getHeight());
            // Check the end zone
            if (endGameZone != null && playerRect.overlaps(endGameZone)) {
                endWin = true;
            }

            // Check score (fish gain)
            Array<Rectangle> collectedFishes = new Array<>();
            for (Rectangle fish : fishRectangles) {
                if (playerRect.overlaps(fish)) {
                    collectedFishes.add(fish); // Add the collected fish list
                    score += 1; // Increment score
                }
            }
            fishRectangles.removeAll(collectedFishes, true); // delete collected fishs

            // Update camera position
            camera.position.set(player.getX(), player.getY(), 0);
            float mapWidth = map.getProperties().get("width", Integer.class) * 32;
            float mapHeight = map.getProperties().get("height", Integer.class) * 32;
            camera.position.x = Math.max(camera.viewportWidth / 2, Math.min(camera.position.x, mapWidth - camera.viewportWidth / 2));
            camera.position.y = Math.max(camera.viewportHeight / 2, Math.min(camera.position.y, mapHeight - camera.viewportHeight / 2));
            camera.update();

            // map
            batch.begin();
            renderer.setView(camera);
            renderer.render();

            // Draw fish
            for (Rectangle fish : fishRectangles) {
                batch.draw(fishTexture, fish.x, fish.y, fish.width, fish.height);
            }

            // Draw player
            player.render(batch);

            // Display the score and the number of death
            font.draw(batch, "Score: " + score, camera.position.x + Gdx.graphics.getWidth() / 2 - 150, camera.position.y + Gdx.graphics.getHeight() / 2 - 20);
            font.draw(batch, "Dead : " + death_count, camera.position.x + Gdx.graphics.getWidth() / 2 - 600, camera.position.y + Gdx.graphics.getHeight() / 2 - 20);

            if (endWin) {
                font.draw(batch, "You've won !", camera.position.x, camera.position.y);
            }

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
        fishTexture.dispose();
        font.dispose();
    }
}
