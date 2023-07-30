package com.mjt.test.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector3;
import com.mjt.test.helper.FPSCameraController;
import com.mjt.test.helper.Globals;
import com.mjt.test.helper.Model;
import com.mjt.test.helper.SceneManagerEx;
import com.mjt.test.helper.Util;

import net.mgsx.gltf.scene3d.attributes.PBRColorAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import net.mgsx.gltf.scene3d.scene.Scene;

public class Test_SplitScreen implements Screen {
    private SceneManagerEx sceneManager;
    private Scene scene;

    FrameBuffer[] fbo = new FrameBuffer[2];

    @Override
    public void show() {
        Globals.instance.curScreen = this;

        sceneManager = SceneManagerEx.createSceneManager(true, 32, 1, 0, 0);
        sceneManager.setupSceneDefault(new Vector3(0, 3, 15), 0.1f, 1000.0f, true, true);
        //sceneManager.setupSceneDefault(new Vector3(0, 0, 0), 0.1f, 1000.0f, true, true);

        sceneManager.cameraController = new FPSCameraController(sceneManager.camera);
        Gdx.input.setInputProcessor(sceneManager.cameraController);
        Gdx.input.setCursorCatched(true);
        Gdx.input.setCursorPosition(0, 0);

        Model mdl = new Model("models/test.glb");
        sceneManager.add(mdl);

        System.out.println("ShowTree: ");
        sceneManager.showTree(mdl.scene.modelInstance.nodes, 0);

        for (int q = 0; q < 2; q++) {
            fbo[q] = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.app.getGraphics().getWidth() / 2, Gdx.app.getGraphics().getHeight(), true);
            fbo[q].begin();
            Gdx.gl.glClearColor(0, 0, 0, 0);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
            fbo[q].end();
        }

        /*
        String texName = "textures/earth.png";
        Globals.instance.assets.load(texName, Texture.class);
        Globals.instance.assets.finishLoading();
        Texture texture = Globals.instance.assets.get(texName, Texture.class);
        PBRTextureAttribute texAttr = new PBRTextureAttribute(TextureAttribute.Diffuse, texture);
        mdl.scene.modelInstance.materials.get(0).set(texAttr);

        mdl.scene.modelInstance.materials.get(0)
                .set(PBRColorAttribute.createBaseColorFactor(new Color(1,0,0,1)));

         */

    }

    @Override
    public void resize(int width, int height) {
        sceneManager.updateViewport(width, height);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) && Gdx.app.getType() == Application.ApplicationType.Desktop) {
            Gdx.app.exit();
            return;
        }

        sceneManager.cameraController.update();

        Globals.tempVector3.set(sceneManager.camera.position); // save orig cam pos

        //sceneManager.renderShadows();

//        TODO   koit Movablella  Noden tilal

        fbo[0].begin();
        sceneManager.camera.position.set(sceneManager.getStereoCameraPosition(true));
        sceneManager.update(delta);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Globals.visibleModels = 0;
        sceneManager.renderColors();
        fbo[0].end();

        fbo[1].begin();
        //sceneManager.camera.position.set(sceneManager.getStereoCameraPosition(false));
        sceneManager.update(delta);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Globals.visibleModels = 0;
        sceneManager.renderColors();
        fbo[1].end();

        Globals.instance.spriteBatch.begin();

        Globals.instance.spriteBatch.setColor(1, 0.5f, 0.5f, 1);
        Globals.instance.spriteBatch.draw(fbo[0].getColorBufferTexture(), 0, 0,
            fbo[0].getWidth(), fbo[0].getHeight(), 0.25f, 0, 0.75f, 1);
        //fbo[0].getWidth(), fbo[0].getHeight(), 0, 0, 1, 1);

        Globals.instance.spriteBatch.setColor(1, 1, 1, 1);
        Globals.instance.spriteBatch.draw(fbo[1].getColorBufferTexture(), fbo[0].getWidth(), 0,
            fbo[1].getWidth(), fbo[0].getHeight(), 0.25f, 0, 0.75f, 1);
        //fbo[1].getWidth(), fbo[1].getHeight(), 0, 0, 1, 1);

        Globals.instance.spriteBatch.end();

        sceneManager.camera.position.set(Globals.tempVector3); // restore orig cam pos

        Util.showInfo();
    }

    @Override
    public void dispose() {
        sceneManager.dispose();
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
