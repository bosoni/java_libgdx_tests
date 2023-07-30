package com.mjt.test.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.mjt.test.helper.Globals;

public class Test_Blending implements Screen {
    Texture[] tex = new Texture[10];
    FrameBuffer fbo;
    Color colorTint = new Color(0, 0, 0, 1);

    int x, y;

    @Override
    public void show() {
        Globals.instance.curScreen = this;

        String texName = "textures/bg1.png";
        Globals.instance.assets.load(texName, Texture.class);
        Globals.instance.assets.finishLoading();
        tex[0] = Globals.instance.assets.get(texName, Texture.class);

        texName = "textures/head.png";
        Globals.instance.assets.load(texName, Texture.class);
        Globals.instance.assets.finishLoading();
        tex[1] = Globals.instance.assets.get(texName, Texture.class);

        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.app.getGraphics().getWidth(), Gdx.app.getGraphics().getHeight(), false);
        fbo.begin();
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Globals.instance.spriteBatch.begin();
        Globals.instance.spriteBatch.draw(tex[0], 0, 0);
        Globals.instance.spriteBatch.end();
        fbo.end();

    }

    @Override
    public void render(float delta) {
        Globals.visibleModels = 0;
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) && Gdx.app.getType() == Application.ApplicationType.Desktop) {
            Gdx.app.exit();
            return;
        }

        colorTint = new Color((float) Math.random(),
                (float) Math.random(),
                (float) Math.random(),
                (float) Math.random());  //1);
        


        x = (int)(Math.random() * 1000);
        y = (int)(Math.random() * 1000);

        fbo.begin();
        Globals.instance.spriteBatch.begin();

        /*
        Globals.instance.spriteBatch.enableBlending();
        //Globals.instance.spriteBatch.setBlendFunction(Gdx.gl20.GL_SRC_ALPHA, Gdx.gl20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl20.glBlendFuncSeparate(
                Gdx.gl20.GL_SRC_ALPHA, Gdx.gl20.GL_ONE_MINUS_SRC_ALPHA,
                Gdx.gl20.GL_ONE, Gdx.gl20.GL_ONE);
        */

        //Globals.instance.spriteBatch.draw(tex[0], 0, 0);

        Globals.instance.spriteBatch.setColor(colorTint);
        Globals.instance.spriteBatch.draw(tex[1], x, y);
        Globals.instance.spriteBatch.setColor(1, 1, 1, 1);

        Globals.instance.spriteBatch.end();

        fbo.end();

        Globals.instance.spriteBatch.begin();
        Globals.instance.spriteBatch.draw(fbo.getColorBufferTexture(), 0, 0, fbo.getWidth(), fbo.getHeight(), 0, 0, 1, 1);
        Globals.instance.spriteBatch.end();

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
