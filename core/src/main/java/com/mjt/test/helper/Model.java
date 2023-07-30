package com.mjt.test.helper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.utils.Disposable;
import com.bulletphysics.dynamics.RigidBody;

import net.mgsx.gltf.loaders.glb.GLBLoader;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

public class Model extends Movable implements Disposable {
    public Scene scene = null;
    protected AnimationController animation = null;
    private final RigidBody body = null;

    public Model() {
    }

    public Model(String name) {
        load(name);
    }

    public RigidBody getRigidBody() {
        return body;
    }

    public void load(String name) {
        Util.debug("load " + name);

        SceneAsset sceneAsset = null;
        if (name.contains(".gltf"))
            sceneAsset = new GLTFLoader().load(Gdx.files.internal(name));
        else
            sceneAsset = new GLBLoader().load(Gdx.files.internal(name));
        scene = new Scene(sceneAsset.scene);

        animation = scene.animationController;
        for (int q = 0; q < scene.modelInstance.animations.size; q++) {
            Util.debug(" anim " + q + ": " + scene.modelInstance.animations.get(q).id);
        }

        for (Node n : scene.modelInstance.nodes)
            for (NodePart np : n.parts)
                np.meshPart.update();
    }

    public void setAnimation(final String name, float speed) {
        if (animation != null) {
            animation.animate(name, -1, speed, null, 0.2f);
        }
    }

    public void updateAnimation(final float delta) {
        if (animation != null) {
            animation.update(delta);
        }
    }

    @Override
    public void dispose() {
    }

}
