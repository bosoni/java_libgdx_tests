package com.mjt.test;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Logger;

// oldies
import com.mjt.test._temp.Test_Scene;
import com.mjt.test._temp.Test_ShadowSystem;
import com.mjt.test._temp._TestShadowSystem_orig_;

// newer tests
import com.mjt.test.game.Test_Anim;
import com.mjt.test.game.Test_Blending;
import com.mjt.test.game.Test_DrawIt;
import com.mjt.test.game.Test_Drawing;
import com.mjt.test.game.Test_Hierarchy;
import com.mjt.test.game.Test_MiniGui;
import com.mjt.test.game.Test_Physics;
import com.mjt.test.game.Test_Sensors;
import com.mjt.test.game.Test_SplitScreen;
import com.mjt.test.helper.Globals;
import com.mjt.test.helper.Util;

public class Main extends Game {
    public Main main = null;

    @Override
    public void create() {
        main = this;
        Globals.instance.game = this;

        Globals.instance.assets.getLogger().setLevel(Logger.DEBUG);
        Util.debug("Testtt  v0.1.230730"); // year month day

        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glClearColor(0.0f, 0.0f, 1.0f, 1);

        Globals.screenWidth = Gdx.graphics.getWidth();
        Globals.screenHeight = Gdx.graphics.getHeight();

        // TESTS: -------------------------------------------------------
        //Test_Scene test = new Test_Scene(); // old
        //Test_ShadowSystem test = new Test_ShadowSystem(); // old
        //_TestShadowSystem_orig_ test = new _TestShadowSystem_orig_(); // old

        
        Test_Blending test=new Test_Blending();
        //Test_DrawIt test = new Test_DrawIt();
        //Test_Drawing test = new Test_Drawing(); // drawing prog
        //Test_MiniGui test = new Test_MiniGui();

        ////Test_Anim test = new Test_Anim();
        //Test_Hierarchy test = new Test_Hierarchy();

        //Test_Sensors test = new Test_Sensors(); // sensors and vibrator

        //Test_Physics test=new Test_Physics(); // bugs
        //Test_SplitScreen test = new Test_SplitScreen(); // bugs

        setScreen(test);

    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        Util.debug("Main dispose");

        if (Globals.instance.curScreen != null) Globals.instance.curScreen.dispose();
        Globals.instance.dispose();
    }

}
