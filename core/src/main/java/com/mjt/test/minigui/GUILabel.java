package com.mjt.test.minigui;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class GUILabel extends GUIComponent {
    private String text;

    public GUILabel() {
        backColor.set(0.8f, 0.8f, 0.8f, 1.0f);
    }

    public void setText(String text_) {
        text = text_;
        _update = true;
    }

    @Override
    protected void render(SpriteBatch batch, TextureAtlas atlas, BitmapFont font) {
        if (_update) {
            layout.setText(font, text);
            txtWidth = (int) layout.width;
            txtHeight = (int) layout.height;
            if (width == 0) {
                width = txtWidth;
                height = txtHeight;
            }
            _update = false;
        }
        font.draw(batch, text, 0, layout.height);
    }
}
