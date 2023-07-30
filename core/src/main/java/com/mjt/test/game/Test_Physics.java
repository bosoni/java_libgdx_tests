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
import com.mjt.test.helper.PhysicsManager;
import com.mjt.test.helper.SceneManagerEx;
import com.mjt.test.helper.Util;

import net.mgsx.gltf.scene3d.scene.Scene;

public class Test_Physics implements Screen {
    public static Model debug1, debug2;
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

        sceneManager.initPhysics(100);

        Model mdl = new Model("models/test.glb");
        mdl.setScale(5, 5, 5);
        sceneManager.add(mdl);
        for (int meshNum = 0; meshNum < 3; meshNum++) {
            //sceneManager.getPhysics().add(mdl, meshNum, PhysicsManager.TRIMESH, 0, null);
            //sceneManager.getPhysics().add(mdl, meshNum, PhysicsManager.BOX, 0, null);
            sceneManager.getPhysics().add(mdl, meshNum, PhysicsManager.SPHERE, 0, new Vector3(1, 1, 1));
        }

        //sceneManager.getPhysics().add(mdl, PhysicsManager.BOX, 0);

/*
        debug1 = new Model("models/mushroom1.glb");
        debug2 = new Model("models/mushroom1.glb");

        sceneManager.add(debug1);
        sceneManager.add(debug2);
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

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            spawn();
        }

        sceneManager.cameraController.update();
        sceneManager.update(delta);
        sceneManager.render();

        //sceneManager.getPhysics().printDebug(); // DEBUG
        //sceneManager.getPhysics().drawDebug();

        Util.showInfo();

    }

    void spawn() {
        float x = (float) Math.random() * 20 - 10;
        float y = 30;
        float z = (float) Math.random() * 20 - 10;
        float mass = 0.1f + (float) Math.random();

        Model mdl = new Model("models/rock1.glb");
        mdl.setPosition(x, y, z);
        sceneManager.add(mdl);
        float S = 0.5f;
        //sceneManager.getPhysics().add(mdl, 0, PhysicsManager.SPHERE, mass, new Vector3(S,S,S));
        //sceneManager.getPhysics().add(mdl, 0, PhysicsManager.BOX, mass, null); //, new Vector3(S,S,S));
        sceneManager.getPhysics().add(mdl, 0, PhysicsManager.SPHERE, mass, null);

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

