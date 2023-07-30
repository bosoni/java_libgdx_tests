/*
scene test

 */
package com.mjt.test._temp;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.mjt.test.helper.Globals;

public class Test_Scene implements Screen {
    public MyShaderProvider myShaderProvider = new MyShaderProvider();
    Model3D obj;
    private Scene skene;

    @Override
    public void show() {
        Globals.instance.curScreen = this;
        //Globals.instance.setMyShaderProvider();
        Globals.instance.modelBatch = new ModelBatch(myShaderProvider);

        skene = new Scene();
        skene.camera.position.set(0, 800, 500);
        skene.camera.lookAt(0, 0, 0);
        skene.camera.far = 10000;
        skene.camera.update();

        obj = Model3D.load("models/objects.g3db");
        // scale floor
        obj.get(0).transform.setToScaling(15, 15, 15);

        obj.setShader(MyShaderProvider.ShaderTypes.lighting);
        //obj.setShader(ShaderTypes.toon);

        skene.add(obj);
    }

    @Override
    public void render(float delta) {
        Globals.visibleModels = 0;
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        // exit
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) && Gdx.app.getType() == ApplicationType.Desktop)
            exitApp();

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            obj.get(1).rotate(0, 1, 0, delta * 50);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            obj.get(1).rotate(0, 1, 0, -delta * 50);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            obj.get(1).moveForwardXZ(delta * 100);
        }

        skene.render();

    }

    void exitApp() {
        dispose();
        Globals.instance.dispose();
        Gdx.app.exit();
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

    @Override
    public void dispose() {
    }
}
