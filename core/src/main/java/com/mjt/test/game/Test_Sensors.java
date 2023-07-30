// https://libgdx.com/wiki/input/gyroscope

package com.mjt.test.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.mjt.test.helper.Globals;

public class Test_Sensors implements Screen {

    boolean gyroscopeAvail = false;

    @Override
    public void show() {
        Globals.instance.curScreen = this;

        Globals.instance.font = new BitmapFont(Gdx.files.internal("fonts/arial-32.fnt"));
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);

        gyroscopeAvail = Gdx.input.isPeripheralAvailable(Input.Peripheral.Gyroscope);

        // This will turn the vibrator on for 200 milliseconds, then turn it off for 200 milliseconds then on again for another 200 milliseconds. The second parameter specifies that the pattern should not be repeated.
        //Gdx.input.vibrate(new long[] { 0, 200, 200, 200}, -1);
        Gdx.input.vibrate(1000);

    }

    @Override
    public void render(float delta) {
        Globals.visibleModels = 0;
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) && Gdx.app.getType() == Application.ApplicationType.Desktop) {
            Gdx.app.exit();
            return;
        }

        String txt = "";

        if (gyroscopeAvail) {
            txt = "GyroX: " + Gdx.input.getGyroscopeX() + "\n" +
                    "GyroY: " + Gdx.input.getGyroscopeY() + "\n" +
                    "GyroZ: " + Gdx.input.getGyroscopeZ() + "\n";
        } else txt = "Gyroscope not available.\n\n";

        txt += "Acc X: " + Gdx.input.getAccelerometerX() + "\n" +
                "Acc Y: " + Gdx.input.getAccelerometerY() + "\n" +
                "Acc Z: " + Gdx.input.getAccelerometerZ();

        Globals.instance.spriteBatch.begin();
        Globals.instance.font.setColor(1, 0, 0, 1);
        Globals.instance.font.draw(Globals.instance.spriteBatch, txt, 10, Gdx.graphics.getHeight() - 20);
        Globals.instance.spriteBatch.end();

    }

    @Override
    public void dispose() {
    }

    @Override
    public void resize(int width, int height) {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
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
