package com.mjt.test._temp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.utils.Array;
import com.mjt.test._temp.MyShaderProvider.ShaderTypes;
import com.mjt.test.helper.Globals;

public class Model3D {
    private Array<GameObject> gameObjects;

    public static Model3D load(final String fileName) {
        Gdx.app.log("DEBUG", "Model3D.load " + fileName);

        Model3D newModel = new Model3D();
        newModel.gameObjects = new Array<GameObject>();

        Globals.instance.assets.load(fileName, Model.class);
        Globals.instance.assets.finishLoading();
        Model model = Globals.instance.assets.get(fileName, Model.class);

        for (int i = 0; i < model.nodes.size; i++) {
            String id = model.nodes.get(i).id;
            GameObject instance = new GameObject(model, id, id, false);
            newModel.gameObjects.add(instance);

            Gdx.app.log(" DEBUG", "  " + i + ": " + id);
        }
        return newModel;
    }

    public static Model3D loadAnimated(final String fileName, final String name, final String animName) {
        Gdx.app.log("DEBUG", "Model3D.loadAnimated " + fileName);

        Model3D newModel = new Model3D();
        newModel.gameObjects = new Array<GameObject>();

        Globals.instance.assets.load(fileName, Model.class);
        Globals.instance.assets.finishLoading();
        Model model = Globals.instance.assets.get(fileName, Model.class);
        GameObject instance = new GameObject(model, name, animName);
        newModel.gameObjects.add(instance);

        return newModel;
    }

    public void setShader(ShaderTypes shaderType) {
        for (GameObject o : gameObjects) {
            o.setShader(shaderType);
        }
    }

    public void render(final Camera camera, final Environment environment) {
        Globals.instance.modelBatch.begin(camera);

        for (GameObject o : gameObjects) {
            o.render(camera, environment);
        }
        Globals.instance.modelBatch.end();
    }

    public void setAnimation(final String name) {
        gameObjects.get(0).setAnimation(name);
    }

    public void updateAnimation(final float delta) {
        gameObjects.get(0).updateAnimation(delta);
    }

    public GameObject get(final int index) {
        return gameObjects.get(index);
    }

    public Array<GameObject> getGameObjects() {
        return gameObjects;
    }

    public GameObject find(final String name) {
        for (GameObject o : gameObjects) {
            if (o.name.equals(name)) {
                return o;
            }
        }
        return null;
    }

}
