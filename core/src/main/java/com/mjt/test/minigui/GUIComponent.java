package com.mjt.test.minigui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class GUIComponent {
    protected static final int ADD = 4;
    protected static final Vector3 _pos = new Vector3();
    protected static final Rectangle scissors = new Rectangle();
    protected final GlyphLayout layout = new GlyphLayout();
    protected final Rectangle clipBounds = new Rectangle();
    protected String name;
    protected int x, y, width = 0, height = 0;
    protected GUIListener listener = null;
    protected boolean mousePressed = false, mouseEnter = false, mouseMoving = false, mouseInArea = false;
    protected int mouseWheel = 0;
    protected boolean _update = false;
    protected int txtWidth, txtHeight;
    protected Color backColor = new Color(), fontColor = new Color();

    public GUIComponent() {
        backColor.set(1, 1, 1, 1);
        fontColor.set(0, 0, 0, 1);
    }

    public void setBackColor(Color color) {
        backColor.set(color);
    }

    public void setFontColor(Color color) {
        fontColor.set(color);
    }

    public void setPosition(int x_, int y_) {
        x = x_;
        y = y_;
        _update = true;
    }

    public void setSize(int width_, int height_) {
        width = width_;
        height = height_;

        clipBounds.set(0, 0, width_, height_);
    }

    protected void render(SpriteBatch batch, TextureAtlas atlas, BitmapFont font) {
    }

    public void addListener(GUIListener listener_) {
        listener = listener_;
    }

    // position otettu matriisista (siihen on laskettu parenttien ja childien positionit)
    protected void setPos(Vector3 pos) {
        _pos.set(pos);
    }

    protected void update() {
        int mx = Gdx.input.getX(), my = Gdx.graphics.getHeight() - Gdx.input.getY();
        int tx = (int) _pos.x, ty = (int) _pos.y;

        // jos hiiri komponentin päällä
        if (mx >= tx && mx < tx + 2 * ADD + width && my >= ty && my < ty + 2 * ADD + height) {
            mouseInArea = true;

            // jos juuri tullaan komponentin päälle
            if (mouseEnter == false) {
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) // jos nappi pohjassa, poistu
                {
                    return;
                }
                mouseEnter = true;
            }

            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                if (listener != null) {
                    listener.pressed();
                }
                mousePressed = true;
            } else if (mousePressed) {
                if (listener != null) {
                    listener.released();
                }
                mousePressed = false;
            }

            if (Gdx.input.getDeltaX() != 0 || Gdx.input.getDeltaY() != 0) {
                if (listener != null) {
                    listener.move();
                }
                mouseMoving = true;
            } else {
                mouseMoving = false;
            }

        } else {
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) == false) {
                mousePressed = false;
            }
            mouseEnter = false;
            mouseInArea = false;
        }
    }
}
