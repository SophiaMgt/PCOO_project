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
    private Texture fishTexture; // Texture des poissons
    private Array<Rectangle> fishRectangles; // Liste des poissons

    private int score;
    private int death_count;// Score du joueur
    private BitmapFont font; // Police pour afficher le score

    private Texture gameOverTexture; // Texture pour l'image Game Over
    private boolean isGameOver = false; // Indique si le joueur est mort

    private Rectangle endGameZone; // Zone de fin
    private boolean endWin = false; // Indique si le joueur a gagné

    public GameScreen(KittyLostGame game) {
        this.game = game;

        batch = new SpriteBatch();
        map = new TmxMapLoader().load("long_map.tmx");
        renderer = new OrthogonalTiledMapRenderer(map);

        // Initialisation de la caméra
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Initialisation du joueur
        player = new Player("catracter.png", 100, 290, 200);

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

        // Charger la zone de fin
        for (MapObject object : map.getLayers().get("end_zone").getObjects()) {
            if (object instanceof RectangleMapObject) {
                endGameZone = ((RectangleMapObject) object).getRectangle();
                break;
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

        // Charger les poissons
        fishTexture = new Texture("fish.png"); // Texture du poisson
        fishRectangles = new Array<>();
        for (MapObject object : map.getLayers().get("poisson").getObjects()) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                fishRectangles.add(rect);
            }
        }

        // Initialiser le score et la police
        score = 0;
        death_count = 0;
        font = new BitmapFont(); // Utiliser une police par défaut
        font.setColor(Color.BLACK);
        font.getData().setScale(2); // Agrandir la police pour qu'elle soit lisible
    }

    // si le joueur meurt
    private void resetGame() {
        // Réinitialiser la position du joueur
        player = new Player("catracter.png", 100, 290, 200);
        isGameOver = false; // Réinitialiser l'état Game Over
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

            Rectangle playerRect = new Rectangle(player.getX(), player.getY(), player.getWidth(), player.getHeight());
            // Vérification de la zone de fin
            if (endGameZone != null && playerRect.overlaps(endGameZone)) {
                endWin = true;
            }

            // Collecte des poissons
            Array<Rectangle> collectedFishes = new Array<>();
            for (Rectangle fish : fishRectangles) {
                if (playerRect.overlaps(fish)) {
                    collectedFishes.add(fish); // Ajouter à la liste des poissons collectés
                    score += 1; // Incrémenter le score
                }
            }
            fishRectangles.removeAll(collectedFishes, true); // Supprimer les poissons collectés

            // Mettre à jour la position de la caméra
            camera.position.set(player.getX(), player.getY(), 0);
            float mapWidth = map.getProperties().get("width", Integer.class) * 32;
            float mapHeight = map.getProperties().get("height", Integer.class) * 32;
            camera.position.x = Math.max(camera.viewportWidth / 2, Math.min(camera.position.x, mapWidth - camera.viewportWidth / 2));
            camera.position.y = Math.max(camera.viewportHeight / 2, Math.min(camera.position.y, mapHeight - camera.viewportHeight / 2));
            camera.update();

            // Rendu de la carte
            batch.begin();
            renderer.setView(camera);
            renderer.render();

            // Dessiner les poissons
            for (Rectangle fish : fishRectangles) {
                batch.draw(fishTexture, fish.x, fish.y, fish.width, fish.height);
            }

            // Dessiner le joueur
            player.render(batch);

            // Afficher les scores
            font.draw(batch, "Score: " + score, camera.position.x + Gdx.graphics.getWidth() / 2 - 150, camera.position.y + Gdx.graphics.getHeight() / 2 - 20);
            font.draw(batch, "Mort : " + death_count, camera.position.x + Gdx.graphics.getWidth() / 2 - 600, camera.position.y + Gdx.graphics.getHeight() / 2 - 20);

            if (endWin) {
                font.draw(batch, "Vous avez gagné !", camera.position.x, camera.position.y); // Afficher "Win!" au centre
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
