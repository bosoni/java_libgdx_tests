package com.mjt.test._temp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.environment.SpotLight;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.tests.g3d.shadows.system.ShadowSystem;
import com.badlogic.gdx.tests.g3d.shadows.system.classical.ClassicalShadowSystem;
import com.badlogic.gdx.tests.g3d.shadows.system.realistic.RealisticShadowSystem;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class Scene_ShadowSystem {
    static Vector3 tmpVector3 = new Vector3();

    public final Array<GameObject> meshes = new Array<GameObject>();
    public PerspectiveCamera camera;
    public Environment environment;

    public Array<ModelBatch> passBatches = new Array<ModelBatch>();
    public ModelBatch mainBatch;
    ShadowSystem currSystem;

    public Scene_ShadowSystem() {
        environment = new Environment();
        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(0, 150, 0);
        camera.lookAt(Vector3.Zero);
        camera.near = 1f;
        camera.far = 1000;
        camera.update();
    }

    /**
     * Init shadow system before adding lights.
     *
     * @param classicalShadowSystem Choose classicalShadowSystem or RealisticShadowSystem.
     */
    public void initShadowSystem(boolean classicalShadowSystem) {
        // Shadow system init
        passBatches = new Array<ModelBatch>();
        if (classicalShadowSystem)
            currSystem = new ClassicalShadowSystem();
        else
            currSystem = new RealisticShadowSystem();
    }

    /**
     * Init system after adding lights.
     */
    public void initLights() {
        currSystem.init();
        for (int i = 0; i < currSystem.getPassQuantity(); i++) {
            passBatches.add(new ModelBatch(currSystem.getPassShaderProvider(i)));
        }
        mainBatch = new ModelBatch(currSystem.getShaderProvider());

    }

    public void addLight(BaseLight light) {
        environment.add(light);
        if (light instanceof DirectionalLight) {
            currSystem.addLight((DirectionalLight) light);
        } else if (light instanceof PointLight) {
            currSystem.addLight((PointLight) light);
        } else if (light instanceof SpotLight) {
            currSystem.addLight((SpotLight) light);
        } else
            throw new GdxRuntimeException("Unknown light type");
    }

    public void add(final GameObject mesh) {
        meshes.add(mesh);
    }

    public void add(final Model3D model) {
        for (GameObject e : model.getGameObjects()) {
            meshes.add(e);
        }
    }

    public GameObject get(final int index) {
        return meshes.get(index);
    }

    public GameObject get(final String name) {
        for (int q = 0; q < meshes.size; q++) {
            if (meshes.get(q).name.equals(name)) {
                return meshes.get(q);
            }
        }
        return null;
    }

    public void remove(final String name) {
        for (int q = 0; q < meshes.size; q++) {
            if (meshes.get(q).name.equals(name)) {
                meshes.removeIndex(q);
                break;
            }
        }
    }

    public void render() {
        for (GameObject go : meshes) {
            go.render(camera, environment);
        }
    }

    public void renderWithShadows() {
        currSystem.begin(camera, meshes);

        // Update shadow map
        currSystem.update();
        for (int i = 0; i < currSystem.getPassQuantity(); i++) {
            currSystem.begin(i);
            Camera camera;
            while ((camera = currSystem.next()) != null) {
                passBatches.get(i).begin(camera);
                passBatches.get(i).render(meshes, environment);
                passBatches.get(i).end();
            }
            camera = null;
            currSystem.end(i);
        }
        currSystem.end();

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        mainBatch.begin(camera);
        mainBatch.render(meshes, environment);
        mainBatch.end();
    }


    /**
     * Return index of the nearest model under screenX,screenY.
     * Checks ray->boundingbox or ray->bounding sphere.
     */
    public int getNearestModelIndex(int screenX, int screenY) {
        Ray ray = camera.getPickRay(screenX, screenY);
        int result = -1;
        float len = 1000000;
        int i = 0;
        for (GameObject go : meshes) {
            if ((go.checkBB && Intersector.intersectRayBounds(ray, go.bounds, tmpVector3))
                    || (!go.checkBB && Intersector.intersectRaySphere(ray, go.center, go.radius, tmpVector3))) {
                float len2 = tmpVector3.len2();
                if (len2 < len) {
                    len = len2;
                    result = i;
                }
            }
            i++;

        }
        return result;
    }

}
