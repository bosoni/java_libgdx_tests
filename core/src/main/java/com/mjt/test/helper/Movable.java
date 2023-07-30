package com.mjt.test.helper;

import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class Movable extends Node {
    protected Vector3 rot = new Vector3();

    public void setPosition(final float x, final float y, final float z) {
        translation.set(x, y, z);
    }

    public void addToPosition(final float x, final float y, final float z) {
        translation.add(x, y, z);
    }

    public void setScale(final float x, final float y, final float z) {
        scale.set(x, y, z);
    }

    public void setRotation(final float x, final float y, final float z) {
        rot.set(x, y, z);
        rotation.setEulerAngles(x, y, z);
    }

    public void rotateX(final float v) {
        rot.x += v;
        rotation.setEulerAngles(rot.y, rot.x, rot.z);
        // NOTE:  x<->y swapped
    }

    public void rotateY(final float v) {
        rot.y += v;
        rotation.setEulerAngles(rot.y, rot.x, rot.z);
        // NOTE:  x<->y swapped
    }

    public void rotateZ(final float v) {
        rot.z += v;
        rotation.setEulerAngles(rot.y, rot.x, rot.z);
        // NOTE:  x<->y swapped
    }


    public void moveForwardXZ(final float f) {
        translation.add(-(MathUtils.sinDeg(rot.y) * f), 0, -(MathUtils.cosDeg(rot.y) * f));
    }

    public void strafeXZ(final float f) {
        translation.add((MathUtils.cosDeg(-rot.y) * f), 0, (MathUtils.sinDeg(-rot.y) * f));
    }

    // TODO TEST
    public void moveForwardXYZ(final float f) {
        translation.add(-MathUtils.sinDeg(rot.y) * MathUtils.cosDeg(-rot.x) * f,
                -MathUtils.sinDeg(-rot.x) * f,
                -MathUtils.cosDeg(rot.y) * MathUtils.cosDeg(-rot.x) * f);
    }

    // TODO TEST
    public void strafeXYZ(final float f) {
        translation.add(-MathUtils.sinDeg(rot.y - 90f) * f, 0, -MathUtils.cosDeg(rot.y - 90f) * f);
    }

}
