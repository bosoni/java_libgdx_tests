package com.mjt.test.helper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IntIntMap;

public class FPSCameraController extends InputAdapter implements Disposable {
    public static int FORWARD = Keys.W;
    public static int BACKWARD = Keys.S;
    public static int STRAFE_LEFT = Keys.A;
    public static int STRAFE_RIGHT = Keys.D;
    public static float speed = 0.1f, vaaka = 0.2f, pysty = 0.01f;
    private final Vector3 tmpV = new Vector3(), tmpV2 = new Vector3();
    private final Camera camera;
    private final IntIntMap keys = new IntIntMap();

    public FPSCameraController(Camera camera) {
        this.camera = camera;
        Gdx.input.setCursorPosition(0, 0);

    }

    @Override
    public boolean keyDown(int keycode) {
        keys.put(keycode, keycode);
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        keys.remove(keycode, 0);
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        float x = -Gdx.input.getDeltaX() * vaaka;
        float y = -Gdx.input.getDeltaY() * pysty;
        Vector3 direction = camera.direction.cpy();
        camera.rotate(Vector3.Y, x);
        camera.direction.y += Math.sin(y);
        camera.update();
        return true;
    }

    public void update() {
        float spd = speed;
        if (keys.containsKey(Keys.SHIFT_LEFT))
            spd *= 3;

        if (keys.containsKey(FORWARD)) {
            Vector3 v = camera.direction.cpy().scl(spd);
            camera.translate(v);
            camera.update();
        }

        if (keys.containsKey(BACKWARD)) {
            Vector3 v = camera.direction.cpy().scl(spd);
            v.x = -v.x;
            v.y = -v.y;
            v.z = -v.z;
            camera.translate(v);
            camera.update();
        }

        if (keys.containsKey(STRAFE_LEFT)) {
            Vector3 v = camera.direction.cpy().scl(spd);
            v.y = 0f;
            v.rotate(Vector3.Y, 90);
            camera.translate(v);
            camera.update();
        }

        if (keys.containsKey(STRAFE_RIGHT)) {
            Vector3 v = camera.direction.cpy().scl(spd);
            v.y = 0f;
            v.rotate(Vector3.Y, -90);
            camera.translate(v);
            camera.update();
        }

    }

    @Override
    public void dispose() {
    }
}
