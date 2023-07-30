/*
 Mini-GUI
 by mjt, 2014

 ** Table
 ** Label
 ** Button
 ** List
 ** fontit ladataan myös gui atlaksesta
 ** listan scrollaus rullalla ja draggaamalla

 TODO:
 * Scrollbars
 * Checkbox

 */
package com.mjt.test.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.mjt.test.helper.Globals;
import com.mjt.test.minigui.GUIButton;
import com.mjt.test.minigui.GUILabel;
import com.mjt.test.minigui.GUIList;
import com.mjt.test.minigui.GUIListener;
import com.mjt.test.minigui.GUITable;

public class Test_MiniGui implements Screen {
    public int _count = 0;

    GUITable table;
    GUILabel fpslabel;
    GUIButton button;
    GUIList lst;

    InputMultiplexer input;
    MouseInput mouseInput = new MouseInput();

    @Override
    public void show() {
        Globals.instance.curScreen = this;

        input = new InputMultiplexer(Gdx.input.getInputProcessor());
        input.addProcessor(0, mouseInput);
        Gdx.input.setInputProcessor(input);

        table = new GUITable("uiskin.atlas", "default.fnt", "mytable");
        table.setPosition(10, 10);
        table.setSize(300, 300);

        fpslabel = new GUILabel();
        fpslabel.setText("FPS=");
        fpslabel.setPosition(1, 1);
        table.add(fpslabel, "fpslabel");

        // buttons
        {
            button = new GUIButton();
            button.setText(" Add ");
            button.setPosition(10, 100);
            button.addListener(new GUIListener() {
                @Override
                public void pressed() {
                    System.out.println(" add button pressed");
                }

                @Override
                public void released() {
                    System.out.println(" add button released");

                    lst.add("Added " + _count++);
                }

                @Override
                public void move() {
                    System.out.println(" add button move");
                }
            });
            table.add(button, "addbutton");

            button = new GUIButton();
            button.setText(" Delete ");
            button.setPosition(10, 50);
            button.addListener(new GUIListener() {
                @Override
                public void pressed() {
                    System.out.println(" delete button pressed");
                }

                @Override
                public void released() {
                    System.out.println(" delete button released");

                    //lst.remove(0);
                    lst.remove(lst.getSelected());
                }

                @Override
                public void move() {
                    System.out.println(" delete button move");
                }

            });
            table.add(button, "deletebutton");
        }

        lst = new GUIList();
        lst.setPosition(100, 50);
        lst.setSize(80, 100);
        lst.add("Line 1");
        lst.add("Testing");
        table.add(lst, "Blah");

    }

    @Override
    public void render(float delta) {
        Globals.visibleModels = 0;
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        // exit
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) && Gdx.app.getType() == Application.ApplicationType.Desktop) {
            Gdx.app.exit();
            return;
        }

        String txt = "   selected: " + lst.getSelected();
        fpslabel.setText("FPS=" + Gdx.graphics.getFramesPerSecond() + txt);
        table.render();

        //DEBUG
        if (Gdx.input.isButtonPressed(Buttons.RIGHT)) {
            table.setPosition(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
        }

        // HACK, but works
        lst.scroll((int) mouseInput.wheelY * 10);
        mouseInput.reset(); // must call last
    }

    @Override
    public void dispose() {
        table.dispose();
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

class MouseInput implements InputProcessor {
    public float wheelX, wheelY;

    public void reset() {
        wheelX = wheelY = 0;
    }

    @Override
    public boolean keyDown(int keycode) {
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return true;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        wheelX = amountX;
        wheelY = amountY;
        return true;
    }
}
