package com.mjt.test.minigui;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class GUIButton extends GUIComponent {
    private String text;
    private Sprite sprite = null, spritePressed;

    public void setText(String text_) {
        text = text_;
        _update = true;
    }

    @Override
    protected void render(SpriteBatch batch, TextureAtlas atlas, BitmapFont font) {
        if (sprite == null) {
            TextureRegion reg, regPressed;
            reg = atlas.findRegion("default-round-large");
            regPressed = atlas.findRegion("default-round-down");

            sprite = new Sprite(reg);
            spritePressed = new Sprite(regPressed);
        }
        if (_update) {
            layout.setText(font, text);
            txtWidth = (int) layout.width;
            txtHeight = (int) layout.height;
            if (width == 0) {
                width = txtWidth;
                height = txtHeight;
            }
            _update = false;

            sprite.setSize(layout.width + 2 * ADD, layout.height + 2 * ADD);
            spritePressed.setSize(layout.width + 2 * ADD, layout.height + 2 * ADD);
        }

        if (mousePressed) {
            if (mouseInArea) {
                spritePressed.draw(batch);
            } else {
                sprite.draw(batch);
            }
        } else {
            sprite.draw(batch);
        }

        font.draw(batch, text, ADD, ADD + layout.height);
    }
}
