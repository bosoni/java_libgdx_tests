package com.mjt.test._temp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.mjt.test.helper.Globals;

public class GameObject extends ModelInstance {
    static Vector3 tmpVector3 = new Vector3();

    public final Vector3 center = new Vector3();
    public final Vector3 dimensions = new Vector3();
    public final float radius;
    public final BoundingBox bounds = new BoundingBox();
    public final Vector3 rotation = new Vector3();
    public String name = "";
    /**
     * If true, check if boundingbox is visible.
     * If false, check if boundingsphere is visible.
     */
    public boolean checkBB = true;

    public AnimationController animation = null;

    /**
     * Create model (not animated)
     */
    public GameObject(final Model model, final String name, String rootNode, final boolean mergeTransform) {
        super(model, rootNode);
        this.name = name;
        calculateBoundingBox(bounds);
        bounds.getCenter(center);
        bounds.getDimensions(dimensions);
        radius = dimensions.len() / 2f;
    }

    /**
     * Create animated model.
     * If animName==null, set first animation.
     */
    public GameObject(final Model model, final String name, final String animName) {
        super(model); // NOTE: no rootNode for animated models
        this.name = name;
        calculateBoundingBox(bounds);
        bounds.getCenter(center);
        bounds.getDimensions(dimensions);
        radius = dimensions.len() / 2f;

        this.animation = new AnimationController(this);
        for (int q = 0; q < this.animations.size; q++) {
            Gdx.app.log("DEBUG", "  Anim " + q + ": " + this.animations.get(q).id);
        }

        if (animName != null) {
            this.animation.animate(animName, -1, 1f, null, 0.2f);
        } else {
            this.animation.animate(this.animations.get(0).id, -1, 1f, null, 0.2f);
        }

    }

    public void setShader(MyShaderProvider.ShaderTypes shaderName) {
        this.userData = shaderName;
    }

    public void render(final Camera camera, final Environment environment) {
        // if bbox or bsphere is in frustum, render
        if ((checkBB && isVisibleBB(camera)) || (!checkBB && isVisibleSphere(camera))) {
            Globals.instance.modelBatch.render(this, environment);
            Globals.visibleModels++;
        }
    }

    public boolean isVisibleBB(final Camera cam) {
        transform.getTranslation(tmpVector3);
        tmpVector3.add(center);
        return cam.frustum.boundsInFrustum(tmpVector3, dimensions);
    }

    public boolean isVisibleSphere(final Camera cam) {
        transform.getTranslation(tmpVector3);
        tmpVector3.add(center);
        return cam.frustum.sphereInFrustum(tmpVector3, radius);
    }

    public void setAnimation(final String name) {
        if (animation != null) {
            animation.animate(name, -1, 1f, null, 0.2f);
        }
    }

    public void updateAnimation(final float delta) {
        if (animation != null) {
            animation.update(delta);
        }
    }

    public void rotate(final float x, final float y, final float z, final float degrees) {
        transform.rotate(x, y, z, degrees);
        rotation.add(x * degrees, y * degrees, z * degrees);
    }

    public void move(final float x, final float y, final float z) {
        transform.trn(x, y, z);
    }


    public void moveForwardXZ(final float f) {
        transform.trn(-(MathUtils.sinDeg(rotation.y) * f), 0, -(MathUtils.cosDeg(rotation.y) * f));
    }

    public void strafeXZ(final float f) {
        transform.trn((MathUtils.cos(-rotation.y) * f), 0, (MathUtils.sin(-rotation.y) * f));
    }

    public void moveForwardXYZ(float f) {
        transform.trn(-MathUtils.sinDeg(rotation.y) * MathUtils.cosDeg(-rotation.x) * f, -MathUtils.sinDeg(-rotation.x) * f, -MathUtils.cosDeg(rotation.y) * MathUtils.cosDeg(-rotation.x) * f);
    }

    public void strafeXYZ(float f) {
        transform.trn(-MathUtils.sinDeg(rotation.y - 90f) * f, 0, -MathUtils.cosDeg(rotation.y - 90f) * f);
    }

}
