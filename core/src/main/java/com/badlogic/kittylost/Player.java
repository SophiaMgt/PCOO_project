package com.badlogic.kittylost;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.utils.Array;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Player {
    private Texture texture;
    private Sprite sprite;
    private float x, y;  // Position du joueur
    private float speed;
    private float velocityY;  // Vitesse verticale (pour la gravité et le saut)
    private float gravity = -600f;  // Force de gravité
    private float jumpStrength = 300f;  // La force du saut
    private boolean isJumping = false;  // Pour savoir si le joueur est en train de sauter
    private boolean isOnGround = false;  // Vérifie si le joueur touche le sol
    private boolean isDead = false;

    private ShapeRenderer shapeRenderer;

    public float getWidth() {
        return sprite.getWidth();
    }

    public float getHeight() {
        return sprite.getHeight();
    }

    public Player(String texturePath, float startX, float startY, float speed) {
        this.texture = new Texture(texturePath);
        this.sprite = new Sprite(texture);
        this.x = startX;
        this.y = startY;
        this.speed = speed;

        shapeRenderer = new ShapeRenderer();

        sprite.setSize(55, 50);  // Taille du sprite
        sprite.setPosition(x, y);
    }

    public void update(float deltaTime, Array<Rectangle> collisionRectangles, Array<Polygon> trap) {
        float oldX = x, oldY = y; // Sauvegarder la position précédente
        float newX = x , newY = y;

        // Déplacement horizontal
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            newX += speed * deltaTime;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            newX -= speed * deltaTime;
        }

        // Gestion de la gravité et du saut
        if (!isOnGround) {
            velocityY += gravity * deltaTime; // Applique la gravité si le joueur n'est pas au sol
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W) && isOnGround) {
            velocityY = jumpStrength; // Applique la force de saut
            isJumping = true; // Déclare que le joueur est en train de sauter
            isOnGround = false; // Le joueur n'est plus au sol
        }

        newY += velocityY * deltaTime;

        // Vérification des collisions
        //Rectangle playerRect = new Rectangle(newX, newY, sprite.getWidth(), sprite.getHeight());
        Rectangle playerRect = new Rectangle(
            newX,
            newY , // Ajuster de 10 pixels vers le bas
            sprite.getWidth(),
            sprite.getHeight()  // Réduire la hauteur de la collision
        );
        isOnGround = false; // Réinitialiser la détection au sol pour cette mise à jour

        for (Rectangle rect : collisionRectangles) {
            if (playerRect.overlaps(rect)) {
                // Collision verticale
                if (oldY >= rect.y + rect.height) {
                    newY = rect.y + rect.height ; // Positionner au-dessus de la plateforme
                    velocityY = 0; // Stopper le mouvement vertical
                    isOnGround = true; // Le joueur est sur le sol
                    isJumping = false; // Le saut est terminé
                } else if (oldY + sprite.getHeight() <= rect.y) {
                    newY = rect.y - sprite.getHeight(); // Bloquer en dessous
                    velocityY = 0;
                }

                // Collision horizontale
                if (oldX + sprite.getWidth() <= rect.x) {
                    newX = rect.x - sprite.getWidth(); // Bloquer à gauche
                } else if (oldX >= rect.x + rect.width) {
                    newX = rect.x + rect.width; // Bloquer à droite
                }
            }
        }

        // Vérification des collisions avec les pièges
        for (Polygon traps : trap) {
            if (Intersector.overlapConvexPolygons(traps, new Polygon(new float[]{
                newX, newY,
                newX + sprite.getWidth(), newY,
                newX + sprite.getWidth(), newY + sprite.getHeight(),
                newX, newY + sprite.getHeight()
            }))) {
                System.out.println("Game Over!"); // Affiche Game Over dans la console
                isDead = true; // permet de réinitialiser la partie et afficher un écran de Game Over
            }
        }

        // Appliquer la nouvelle position
        x = newX;
        y = newY;

        // Mettre à jour la position du sprite
        sprite.setPosition(x, y);
    }

    public boolean isDead() {
        return isDead;
    }

    public void render(SpriteBatch batch) {
        sprite.draw(batch);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void dispose() {
        texture.dispose();
    }
}
