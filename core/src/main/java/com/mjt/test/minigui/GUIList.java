package com.mjt.test.minigui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import java.util.ArrayList;
import java.util.List;

public class GUIList extends GUIComponent {
    private final List<String> strings;
    private final Color selectedFontColor = new Color();
    private String selected = "";
    private int startY = 0;

    public GUIList() {
        this.strings = new ArrayList<String>();
        backColor.set(0.8f, 0.8f, 0.8f, 1.0f);
        selectedFontColor.set(1, 0, 0, 1);
    }

    public void setSelectedFontColor(Color color) {
        selectedFontColor.set(color);
    }

    public String getSelected() {
        return selected;
    }

    public void add(String text) {
        strings.add(text);
    }

    public void remove(String text) {
        for (int q = 0; q < strings.size(); q++) {
            if (strings.get(q).equals(text)) {
                strings.remove(q);
                break;
            }
        }
    }

    public void remove(int line) {
        if (strings.size() > 0) {
            if (strings.get(line).equals(selected)) {
                selected = "";
            }
            strings.remove(line);
        }
    }

    public void clear() {
        strings.clear();
    }

    public void scroll(int amount) {
        startY += amount;

    }

    @Override
    protected void render(SpriteBatch batch, TextureAtlas atlas, BitmapFont font) {
        if (_update) {
            layout.setText(font, "ABCDEFG");
            txtWidth = (int) layout.width;
            txtHeight = (int) layout.height;
        }

        if (mousePressed && mouseMoving) {
            startY += Gdx.input.getDeltaY();
        }

        int mx = Gdx.input.getX(), my = Gdx.graphics.getHeight() - Gdx.input.getY();
        int tx = (int) _pos.x, ty = (int) _pos.y + height - txtHeight + startY;
        int txtY = startY + height;

        for (String str : strings) {
            if (mousePressed) {
                if (mx > tx && mx < tx + width && my > ty && my < ty + txtHeight) {
                    font.setColor(selectedFontColor);
                    selected = str;
                } else {
                    font.setColor(fontColor);
                }
            } else {
                if (str.equals(selected)) {
                    font.setColor(selectedFontColor);
                } else {
                    font.setColor(fontColor);
                }
            }

            font.draw(batch, str, 0, txtY);
            ty -= txtHeight;
            txtY -= txtHeight;
        }

        font.setColor(fontColor);
    }

}
