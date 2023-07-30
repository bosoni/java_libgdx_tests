package com.mjt.test.minigui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;

import java.util.ArrayList;
import java.util.List;

public class GUITable extends GUIComponent {
    private static final String GUI_DIR = "ui/";
    private final List<GUIComponent> components;
    private final SpriteBatch batch;
    private final TextureAtlas atlas;
    private final BitmapFont font;
    private final Matrix4 matrix = new Matrix4();
    private final OrthographicCamera camera;

    public GUITable(String atlasName, String fontName, String name_) {
        name = name_; // tablen nimi
        this.components = new ArrayList<GUIComponent>();
        batch = new SpriteBatch();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
        camera.update();

        atlas = new TextureAtlas(Gdx.files.internal(GUI_DIR + atlasName), Gdx.files.internal(GUI_DIR));

        TextureRegion fontReg = atlas.findRegion(fontName);
        BitmapFont.BitmapFontData bfd = new BitmapFont.BitmapFontData(Gdx.files.internal(GUI_DIR + fontName), false);
        font = new BitmapFont(bfd, fontReg, true);

        font.setColor(fontColor);
        backColor.set(0.9f, 0.9f, 0.9f, 1.0f);
    }

    public void add(GUIComponent component_, String name_) {
        component_.name = name_; // komponentin nimi
        components.add(component_);
    }

    public void remove(String name) {
        for (int q = 0; q < components.size(); q++) {
            if (components.get(q).name.equals(name)) {
                components.remove(q);
                break;
            }
        }
    }

    public void dispose() {
        batch.dispose();
    }

    public void render() {
        batch.setProjectionMatrix(camera.combined);
        matrix.idt();
        matrix.setTranslation(x, y, 0); // aseta ekan tablen paikka
        batch.setTransformMatrix(matrix);
        ScissorStack.calculateScissors(camera, batch.getTransformMatrix(), clipBounds, scissors);
        ScissorStack.pushScissors(scissors); // ei rendata tablen ulkopuolelle

        Gdx.gl.glClearColor(backColor.r, backColor.g, backColor.b, backColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        render(batch, atlas, font);
        batch.end();
        ScissorStack.popScissors();
    }

    @Override
    protected void render(SpriteBatch batch, TextureAtlas atlas, BitmapFont font) {
        for (GUIComponent c : components) {
            matrix.translate(c.x, c.y, 0); // liikuta kompontti paikoilleen
            batch.setTransformMatrix(matrix);
            matrix.getTranslation(_pos);
            c.update();

            ScissorStack.calculateScissors(camera, batch.getTransformMatrix(), c.clipBounds, scissors);
            boolean cut = ScissorStack.pushScissors(scissors);
            //System.out.println("> " + c.name + ": " + c.clipBounds.toString());

            if (cut) {
                if (c.backColor.a != 1) {
                    // blend
                }

                Gdx.gl.glClearColor(c.backColor.r, c.backColor.g, c.backColor.b, c.backColor.a);
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            }

            c.render(batch, atlas, font);
            //batch.flush();

            if (cut) {
                ScissorStack.popScissors();
            }

            matrix.translate(-c.x, -c.y, 0); // takas
        }
    }
}
