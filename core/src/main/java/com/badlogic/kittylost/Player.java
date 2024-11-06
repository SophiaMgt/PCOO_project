package com.badlogic.kittylost;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class Player {
    private Texture texture;
    private Sprite sprite;
    private float x, y;  // Position du joueur
    private float speed;
    private float velocityY;  // Vitesse verticale (pour la gravité et le saut)
    private float gravity = -800f;  // Force de gravité, plus la valeur est petite, plus la gravité est forte
    private float jumpStrength = 300f;  // La force du saut
    private boolean isJumping = false;  // Pour savoir si le joueur est en train de sauter
    private boolean isOnGround = false;  // Vérifie si le joueur touche le sol

    // Initialisation du joueur
    public Player(String texturePath, float startX, float startY, float speed) {
        this.texture = new Texture(texturePath);
        this.sprite = new Sprite(texture);
        this.x = startX;
        this.y = startY;
        this.speed = speed;

        sprite.setSize(64, 64);  // Taille du sprite
        sprite.setPosition(x, y);
    }

    // Mise à jour du joueur (gestion du déplacement et du saut)
    public void update(float deltaTime) {
        // Déplacement du joueur
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            x += speed * deltaTime;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            x -= speed * deltaTime;
        }

        // Saut
        if (Gdx.input.isKeyPressed(Input.Keys.W) && isOnGround && !isJumping) {
            isJumping = true;
            velocityY = jumpStrength;  // Applique la force de saut
            isOnGround = false;  // Le joueur n'est plus au sol
        }

        // Application de la gravité
        if (!isOnGround) {
            velocityY += gravity * deltaTime;  // La gravité fait diminuer la vitesse verticale
        }

        // Mise à jour de la position verticale
        y += velocityY * deltaTime;

        // Vérifier si le joueur est au sol
        if (y <= 0) {  // Supposons que le sol soit à y = 0
            y = 0;
            isOnGround = true;
            isJumping = false;
            velocityY = 0;
        }

        // Mettre à jour la position du sprite
        sprite.setPosition(x, y);
    }

    // Rendu du joueur
    public void render(SpriteBatch batch) {
        sprite.draw(batch);
    }

    // Getter pour la position X
    public float getX() {
        return x;
    }

    // Getter pour la position Y
    public float getY() {
        return y;
    }

    // Méthode de nettoyage pour libérer les ressources
    public void dispose() {
        texture.dispose();
    }
}
