package com.mjt.test._temp;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.SpotLight;
import com.badlogic.gdx.utils.Array;
import com.mjt.test.helper.Globals;

public class Test_ShadowSystem implements Screen {
    Model3D obj;
    private Scene_ShadowSystem skene;

    @Override
    public void show() {
        Globals.instance.curScreen = this;

        skene = new Scene_ShadowSystem();
        skene.initShadowSystem(true);
        skene.camera.position.set(0, 800, 500);
        skene.camera.lookAt(0, 0, 0);
        skene.camera.far = 10000;
        skene.camera.update();

        obj = Model3D.load("models/objects.g3db");
        obj.get(0).transform.setToScaling(15, 15, 15); // scale floor
        skene.add(obj);

        // change material
        Array<Texture> mat = new Array<Texture>(3);
        mat.add(new Texture(Gdx.files.internal("textures/wood/diffuse.png")));
        mat.add(new Texture(Gdx.files.internal("textures/wood/normal.png")));
        mat.add(new Texture(Gdx.files.internal("textures/wood/specular.png")));
        for (int q = 0; q < 1; q++) {
            obj.get(q).materials.get(0).remove(TextureAttribute.Diffuse);

            Material material = new Material(TextureAttribute.createDiffuse(mat.get(0)), TextureAttribute.createNormal(mat.get(1)),
                    TextureAttribute.createSpecular(mat.get(2)));

            obj.get(q).materials.get(0).set(material);
        }

        // todo fix  noi arvot ei toimi tässä skenessä
        SpotLight sl = new SpotLight().setPosition(0, 50, -6).setColor(0.8f, 0.3f, 0.3f, 1).setDirection(0, -0.57346237f, 0.8192319f).setIntensity(20).setCutoffAngle(60).setExponent(60);
        skene.addLight(sl);

        DirectionalLight dl = new DirectionalLight().setColor(0.5f, 0.5f, 0.5f, 1).setDirection(-1, -1f, -1);
        skene.addLight(dl);

        // init after lights
        skene.initLights();
    }

    @Override
    public void render(float delta) {
        Globals.visibleModels = 0;
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        // exit
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) && Gdx.app.getType() == Application.ApplicationType.Desktop)
            exitApp();

        skene.renderWithShadows();

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
    public void dispose() {
    }


    @Override
    public void resume() {
    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {
    }
}
