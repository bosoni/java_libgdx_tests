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

public class Test_Anim implements Screen {
    Model player;
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

        Model mdl = new Model("models/test.glb");
        sceneManager.add(mdl);

        player = new Model("anim/ugly1.glb");
        player.setPosition(-2, 0.1f, 0);
        player.setScale(0.2f, 0.2f, 0.2f);
        sceneManager.add(player);

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

        //------------------------------------------
        String animName = "Idle";

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            animName = "Walk";
            player.moveForwardXZ(delta * 5);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            animName = "Walk";
            player.moveForwardXZ(-delta * 5);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
            player.rotateY(delta * 200);
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            player.rotateY(-delta * 200);

        player.setAnimation(animName, 0.4f);
        player.updateAnimation(delta);
        //------------------------------------------

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
