package com.mjt.test._temp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.mjt.test.helper.Globals;

public class Scene {
    static Vector3 tmpVector3 = new Vector3();

    public final Array<GameObject> meshes = new Array<>();
    public PerspectiveCamera camera;
    public Environment environment;

    public Scene() {
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(0, 150, 0);
        camera.lookAt(Vector3.Zero);
        camera.near = 1f;
        camera.far = 1000;
        camera.update();
    }


    public void add(final GameObject ent) {
        meshes.add(ent);
    }

    public void add(final Model3D model) {
        for (GameObject e : model.getGameObjects()) {
            Gdx.app.log("D", "hoi1" + e.name);
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
        //Gdx.app.log("D", "scene render");
        Globals.instance.modelBatch.begin(camera);
        for (GameObject go : meshes) {
            go.render(camera, environment);
            //Gdx.app.log("D", "go "+go.name);
        }
        Globals.instance.modelBatch.end();
    }

    /**
     * Return index of the nearest model under screenX,screenY.
     * Checks ray->boundingbox or ray->boundingsphere.
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
