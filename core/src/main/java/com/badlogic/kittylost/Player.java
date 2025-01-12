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
    private float x, y;  // Player's position
    private float speed;
    private float velocityY;  // Vertical vzlocity (gravity and jump)
    private float gravity = -600f;  // Gravity force
    private float jumpStrength = 300f;  // Jump strength
    private boolean isJumping = false;  // To know if the player is jumping
    private boolean isOnGround = false;  // Check if the player is on the ground
    private boolean isDead = false;

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

        ShapeRenderer shapeRenderer = new ShapeRenderer();

        sprite.setSize(55, 50);  // Taille du sprite
        sprite.setPosition(x, y);
    }

    public void update(float deltaTime, Array<Rectangle> collisionRectangles, Array<Polygon> trap) {
        float oldX = x, oldY = y;
        float newX = x , newY = y;

        // Horizontal move
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            newX += speed * deltaTime;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            newX -= speed * deltaTime;
        }

        // Gravity and jump
        if (!isOnGround) {
            velocityY += gravity * deltaTime; // Applies gravity if the player is not on the ground
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W) && isOnGround) {
            velocityY = jumpStrength; // Applies jump strength
            isJumping = true; // Say if the player is jumping or not
            isOnGround = false; // Player not on the ground anymore
        }

        newY += velocityY * deltaTime;

        // Check collision
        Rectangle playerRect = new Rectangle(
            newX,
            newY ,
            sprite.getWidth(),
            sprite.getHeight()
        );
        isOnGround = false; // Reset ground detection

        for (Rectangle rect : collisionRectangles) {
            if (playerRect.overlaps(rect)) {
                // Vertical collision
                if (oldY >= rect.y + rect.height) {
                    newY = rect.y + rect.height ; // Position above the platform
                    velocityY = 0; // Stop vertical movement
                    isOnGround = true; // The player is on the ground
                    isJumping = false; // The jump is finished
                } else if (oldY + sprite.getHeight() <= rect.y) {
                    newY = rect.y - sprite.getHeight();
                    velocityY = 0;
                }

                // Horizontal collision
                if (oldX + sprite.getWidth() <= rect.x) {
                    newX = rect.x - sprite.getWidth(); // Stuck to the left
                } else if (oldX >= rect.x + rect.width) {
                    newX = rect.x + rect.width; // Stuck to the right
                }
            }
        }

        // Checking interactions with traps
        for (Polygon traps : trap) {
            if (Intersector.overlapConvexPolygons(traps, new Polygon(new float[]{
                newX, newY,
                newX + sprite.getWidth(), newY,
                newX + sprite.getWidth(), newY + sprite.getHeight(),
                newX, newY + sprite.getHeight()
            }))) {
                System.out.println("Game Over!");
                isDead = true;
            }
        }

        // Applies new position
        x = newX;
        y = newY;

        // Update sprite position
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
