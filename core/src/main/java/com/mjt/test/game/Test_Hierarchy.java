package com.mjt.test.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector3;
import com.mjt.test.helper.FPSCameraController;
import com.mjt.test.helper.Globals;
import com.mjt.test.helper.Model;
import com.mjt.test.helper.SceneManagerEx;
import com.mjt.test.helper.Util;

import net.mgsx.gltf.scene3d.scene.Scene;

public class Test_Hierarchy implements Screen {
    Model sun, earth, moon, ufo;
    float x = 0;
    private SceneManagerEx sceneManager;
    private Scene scene;

    @Override
    public void show() {
        Globals.instance.curScreen = this;

        sceneManager = SceneManagerEx.createSceneManager(true, 32, 1, 0, 0);
        sceneManager.setupSceneDefault(new Vector3(0, 3, 15), 0.1f, 1000.0f, true, true);

        sceneManager.cameraController = new FPSCameraController(sceneManager.camera);
        Gdx.input.setInputProcessor(sceneManager.cameraController);
        Gdx.input.setCursorCatched(true);

        sun = new Model("models/mushroom1.glb");
        earth = new Model("models/wallcube.glb");
        moon = new Model("models/rock1.glb");
        ufo = new Model("models/car.glb");

        earth.setPosition(-10, 0, 0);
        moon.setPosition(-5, 0, 0);
        ufo.setPosition(-20, 0, 0);

        sceneManager.add(sun);
        sceneManager.add(earth);
        sceneManager.add(moon);
        sceneManager.add(ufo);

        // create hierarchy
        sun.addChild(earth);
        earth.addChild(moon);
        sun.addChild(ufo);


        System.out.println("ShowTree:");
        sceneManager.showTree(sun.scene.modelInstance.nodes, 0);

        System.out.println("ShowTree: ---");
        sceneManager.showTree(sceneManager.world);
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

        sun.rotateY(delta * 50);
        earth.rotateY(-delta * 120);
        moon.rotateX(delta * 80);
        ufo.rotateZ(delta * 100);

        sceneManager.cameraController.update();
        sceneManager.update(delta);
        sceneManager.render();

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
