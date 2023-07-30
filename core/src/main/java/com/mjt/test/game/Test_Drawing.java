/*
simple drawing prog
by mjt, 2023

keys:
LEFT, RIGHT     change brush
UP, DOWN        rotation

NUMPAD +        add scale
NUMPAD -        sub scale

NUMBAD *        add step
NUMBAD /        sub step

C               change color

 */
package com.mjt.test.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.Array;
import com.mjt.test.helper.Globals;

public class Test_Drawing implements Screen {
    SpriteBatch batch;
    FrameBuffer fbo;

    Array<Texture> brush = new Array<>();
    int curBrush = 0, step = 1;
    float scale = 1, rot = 0;

    int lastX = -1, lastY = -1;
    boolean drag = false;

    Color colorTint = new Color(0.5f, 0.5f, 0.5f, 1);

    @Override
    public void show() {
        Globals.instance.curScreen = this;

        batch = new SpriteBatch();

        Globals.instance.font = new BitmapFont(Gdx.files.internal("fonts/arial-15.fnt"));
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);

        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.app.getGraphics().getWidth(), Gdx.app.getGraphics().getHeight(), false);
        fbo.begin();
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        fbo.end();

        String[] bnames = {"pix_b", "cross5_b", "line5_b", "c5_b", "../head", "../lightimg", "../grass", "../fire"};
        for (int q = 0; q < bnames.length; q++) {
            String texName = "textures/drawit/" + bnames[q] + ".png";
            Globals.instance.assets.load(texName, Texture.class);
            Globals.instance.assets.finishLoading();
            Texture texture = Globals.instance.assets.get(texName, Texture.class);
            brush.add(texture);
        }
    }

    @Override
    public void render(float delta) {

        Globals.visibleModels = 0;
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) && Gdx.app.getType() == Application.ApplicationType.Desktop) {
            Gdx.app.exit();
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            if (curBrush > 0) curBrush--;
            scale = 1;
            rot = 0;
            step = 1;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            if (curBrush < brush.size - 1) curBrush++;
            scale = 1;
            rot = 0;
            step = 1;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.UP))
            rot++;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
            rot--;

        if (Gdx.input.isKeyPressed(Input.Keys.NUMPAD_ADD))
            scale += 0.1f;
        if (Gdx.input.isKeyPressed(Input.Keys.NUMPAD_SUBTRACT))
            if (scale >= 0.11) scale -= 0.1f;

        if (Gdx.input.isKeyPressed(Input.Keys.NUMPAD_DIVIDE))
            if (step > 1) step--;
        if (Gdx.input.isKeyPressed(Input.Keys.NUMPAD_MULTIPLY))
            step++;

        if (Gdx.input.isKeyPressed(Input.Keys.C))
            colorTint = new Color((float) Math.random(),
                    (float) Math.random(),
                    (float) Math.random(),
                    1);


        fbo.begin();
        batch.begin();

        //batch.enableBlending();
        //batch.setBlendFunction(Gdx.gl20.GL_SRC_ALPHA, Gdx.gl20.GL_ONE_MINUS_SRC_ALPHA);

        int q = 0;
        {
            //for (int q = 0; q < Gdx.input.getMaxPointers(); q++) {
            if (Gdx.input.isTouched(q)) {
                drag = true;
                int xx = Gdx.input.getX(q);
                int yy = Gdx.graphics.getHeight() - Gdx.input.getY(q);

                batch.setColor(colorTint);
                drawLine(brush.get(curBrush), xx, yy, lastX, lastY);
                lastX = xx;
                lastY = yy;

                batch.setColor(1, 1, 1, 1);

            } else {
                drag = false;
                lastX = lastY = -1;
            }

        }
        batch.end();
        fbo.end();

        batch.begin();
        batch.draw(fbo.getColorBufferTexture(), 0, 0, fbo.getWidth(), fbo.getHeight(), 0, 0, 1, 1);

        batch.setColor(colorTint);
        batch.draw(brush.get(curBrush), 5, Gdx.graphics.getHeight() - brush.get(curBrush).getHeight() - 20);
        /*int w = (int) (brush.get(curBrush).getWidth() * scale);
        int h = (int) (brush.get(curBrush).getHeight() * scale);
        batch.draw(brush.get(curBrush), 5 * scale, Gdx.graphics.getHeight() - h - 20 * scale, w / 2, h / 2, w, h, scale, scale, rot, 0, 0,
                w, h, false, false);
        */
        Globals.instance.font.setColor(0, 0.3f, 0.3f, 1);
        Globals.instance.font.draw(batch, "Current Brush     scale: " + scale + "   rotation: " + rot % 360 + "   step: " + step, 3, Gdx.graphics.getHeight() - 3);
        batch.setColor(1, 1, 1, 1);

        batch.end();

    }

    void drawLine(Texture tex, float x0, float y0, float x1, float y1) {
        if (lastX == -1 && lastY == -1) {
            batch.draw(tex, x0 - tex.getWidth() / 2, y0 - tex.getHeight() / 2, 0, 0, tex.getWidth(), tex.getHeight(), scale, scale, rot, 0, 0, tex.getWidth(), tex.getHeight(), false, false);
            return;
        }
        if (x0 == x1 && y0 == y1)
            return;

        float x = x1 - x0;
        float y = y1 - y0;

        float max = Math.max(Math.abs(x), Math.abs(y));
        x /= max;
        y /= max;

        for (int n = 0; n < max; n++) {
            if (n % step == 0)
                batch.draw(tex, x0 - tex.getWidth() / 2, y0 - tex.getHeight() / 2, 0, 0, tex.getWidth(), tex.getHeight(), scale, scale, rot, 0, 0, tex.getWidth(), tex.getHeight(), false, false);
            x0 += x;
            y0 += y;
        }

    }


    @Override
    public void dispose() {
    }

    @Override
    public void resize(int width, int height) {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
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

}
