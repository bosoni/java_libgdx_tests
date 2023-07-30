package com.mjt.test._temp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.SpotLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.tests.g3d.shadows.system.ShadowSystem;
import com.badlogic.gdx.tests.g3d.shadows.system.realistic.RealisticShadowSystem;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class _TestShadowSystem_orig_ implements Screen {
    public Array<ModelBatch> passBatches = new Array<ModelBatch>();
    public ModelBatch mainBatch;

    PerspectiveCamera cam;
    CameraInputController inputController;

    Model model;
    ModelInstance instance;
    Environment environment;

    Model axesModel;
    ModelInstance axesInstance;

    SpotLight sl;
    SpotLight sl2;
    SpotLight sl3;
    DirectionalLight dl;
    float radius = 1f;
    Vector3 center = new Vector3(), transformedCenter = new Vector3(), tmpV = new Vector3();

    Stage stage;
    //Label label;

    ShadowSystem currSystem;

    Array<ModelInstance> instances = new Array<ModelInstance>();
    long lastTime;

    @Override
    public void show() {
        environment = new Environment();

        sl = new SpotLight().setPosition(0, 10, -6).setColor(0.8f, 0.3f, 0.3f, 1).setDirection(0, -0.57346237f, 0.8192319f).setIntensity(20).setCutoffAngle(60).setExponent(60);

        sl2 = new SpotLight().setPosition(0, 7, 5).setColor(0.7f, 0.7f, 0.7f, 1).setDirection(new Vector3(0, -1f, -0.06f).nor()).setIntensity(10).setCutoffAngle(60).setExponent(20);

        sl3 = new SpotLight().setPosition(0, 9, 6).setColor(0.3f, 0.3f, 0.8f, 1).setDirection(new Vector3(0, -1f, -0.06f).nor()).setIntensity(20).setCutoffAngle(60).setExponent(60);

        dl = new DirectionalLight().setColor(0.5f, 0.5f, 0.5f, 1).setDirection(0, -1f, 0);

        //        environment.add(sl);
        environment.add(sl2);
        //        environment.add(sl3);
        //        environment.add(dl);

        // The user camera
        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0f, 7f, 10f);
        cam.lookAt(0, 0, 0);
        cam.near = 1f;
        cam.far = 25f;
        cam.up.set(0, 1, 0);
        cam.update();

        // Load texture
        Array<Texture> wood = new Array<Texture>(3);
        wood.add(new Texture(Gdx.files.internal("textures/wood/diffuse.png")));
        wood.add(new Texture(Gdx.files.internal("textures/wood/normal.png")));
        wood.add(new Texture(Gdx.files.internal("textures/wood/specular.png")));

        Array<Texture> earth = new Array<Texture>(3);
        earth.add(new Texture(Gdx.files.internal("textures/earth/diffuse.png")));
        earth.add(new Texture(Gdx.files.internal("textures/earth/normal.png")));
        earth.add(new Texture(Gdx.files.internal("textures/earth/specular.png")));
        earth.get(0).setFilter(TextureFilter.Linear, TextureFilter.Linear);
        earth.get(1).setFilter(TextureFilter.Linear, TextureFilter.Linear);
        earth.get(2).setFilter(TextureFilter.Linear, TextureFilter.Linear);

        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        MeshPartBuilder mpb = modelBuilder.part("ground", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates, new Material(TextureAttribute.createDiffuse(wood.get(0)), TextureAttribute.createNormal(wood.get(1)), TextureAttribute.createSpecular(wood.get(2))));
        mpb.box(0, -1.5f, 0, 10, 1, 10);

        mpb = modelBuilder.part("ball", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates, new Material(TextureAttribute.createDiffuse(earth.get(0)), TextureAttribute.createNormal(earth.get(1)), TextureAttribute.createSpecular(earth.get(2))));
        mpb.sphere(2f, 2f, 2f, 20, 20);

        model = modelBuilder.end();

  /*      Model3D obj;
        obj = Model3D.load("objects.g3db");
        obj.get(0).transform.setToScaling(15, 15, 15); // scale floor
        //skene.add(obj);

        instance = obj.get(0);
*/
        instance = new ModelInstance(model);

        instances.add(instance);

        // Shadow system init
        passBatches = new Array<ModelBatch>();
        //currSystem = new ClassicalShadowSystem();
        currSystem = new RealisticShadowSystem();

        //        currSystem.addLight(sl);
        currSystem.addLight(sl2);
        //        currSystem.addLight(sl3);
        //        currSystem.addLight(dl);

        currSystem.init();
        for (int i = 0; i < currSystem.getPassQuantity(); i++) {
            passBatches.add(new ModelBatch(currSystem.getPassShaderProvider(i)));
        }
        mainBatch = new ModelBatch(currSystem.getShaderProvider());

        createAxes();
        Gdx.input.setInputProcessor(inputController = new CameraInputController(cam));

        stage = new Stage(new ScreenViewport());
/*        label = new Label("", new Skin(Gdx.files.internal("ui/uiskin.json")));
        stage.addActor(label);
        label.setX(100);
        label.setY(Gdx.graphics.getHeight() - 30);
*/
    }

    private void createAxes() {
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        MeshPartBuilder builder = modelBuilder.part("axes", GL20.GL_LINES, Usage.Position | Usage.ColorUnpacked, new Material());

        float v1 = 0, v2 = 0, v3 = 100;
        // RED = X
        builder.setColor(Color.RED);
        builder.line(0, 0, 0, v1, v2, v3);

        // GREEN = Y
        builder.setColor(Color.GREEN);
        builder.line(0, 0, 0, v3, v1, v2);

        // BLUE = Z
        builder.setColor(Color.BLUE);
        builder.line(0, 0, 0, v2, v3, v1);

        axesModel = modelBuilder.end();
        axesInstance = new ModelInstance(axesModel);
    }

    @Override
    public void render(float delta) {

        if (TimeUtils.timeSinceMillis(lastTime) > 3 * 1000) {
            //            label.setText(currSystem.toString());
            lastTime = TimeUtils.millis();
        }

        sl.position.rotate(Vector3.Y, -delta * 20f);
        sl.position.rotate(Vector3.X, -delta * 30f);
        sl.position.rotate(Vector3.Z, -delta * 10f);
        sl.direction.set(Vector3.Zero.cpy().sub(sl.position));

        sl2.position.rotate(Vector3.Y, delta * 10f);
        //        sl2.position.rotate(Vector3.X, delta * 20f);
        //        sl2.position.rotate(Vector3.Z, delta * 30f);
        sl2.direction.set(Vector3.Zero.cpy().sub(sl2.position));

        sl3.position.rotate(Vector3.Y, delta * 30f);
        sl3.position.rotate(Vector3.X, delta * 10f);
        sl3.position.rotate(Vector3.Z, delta * 20f);
        sl3.direction.set(Vector3.Zero.cpy().sub(sl3.position));

        dl.direction.rotate(Vector3.X, delta * 10f);

        currSystem.begin(cam, instances);

        // Update shadow map
        currSystem.update();
        for (int i = 0; i < currSystem.getPassQuantity(); i++) {
            currSystem.begin(i);
            Camera camera;
            while ((camera = currSystem.next()) != null) {
                passBatches.get(i).begin(camera);
                passBatches.get(i).render(instance, environment);
                passBatches.get(i).end();
            }
            camera = null;
            currSystem.end(i);
        }
        currSystem.end();

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        mainBatch.begin(cam);
        mainBatch.render(axesInstance);
        mainBatch.render(instance, environment);
        mainBatch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        model.dispose();
    }


    @Override
    public void resume() {
    }

    @Override
    public void hide() {

    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }
}
