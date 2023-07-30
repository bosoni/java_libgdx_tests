package com.mjt.test.helper;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import com.badlogic.gdx.graphics.g3d.particles.batches.BillboardParticleBatch;
import com.badlogic.gdx.graphics.g3d.particles.batches.PointSpriteParticleBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class Globals {
    public static final Globals instance = new Globals();

    public static final Vector3 tempVector3 = new Vector3();
    public static final Vector3 tempVector3_2 = new Vector3();
    public static final Matrix4 tempMatrix4 = new Matrix4();
    public static final Quaternion tempQuat = new Quaternion();

    public static int screenWidth = 1200, screenHeight = 800;
    public static int visibleModels = 0;

    public Game game = null;
    public Screen curScreen = null;

    public AssetManager assets = new AssetManager();
    public ModelBatch modelBatch = new ModelBatch();
    public SpriteBatch spriteBatch = new SpriteBatch();
    public ParticleSystem particleSystem = new ParticleSystem();
    public PointSpriteParticleBatch pointSpriteBatch = new PointSpriteParticleBatch();
    public BillboardParticleBatch billboardParticleBatch = new BillboardParticleBatch();
    public DecalBatch decalBatch;
    public BitmapFont font;

    public void createDecalBatch(Camera camera) {
        instance.decalBatch = new DecalBatch(new CameraGroupStrategy(camera));
    }

    public void dispose() {
        if (instance.assets != null) {
            instance.assets.dispose();
            instance.assets = null;
        }
        if (instance.modelBatch != null) {
            instance.modelBatch.dispose();
            instance.modelBatch = null;
        }
        if (instance.spriteBatch != null) {
            instance.spriteBatch.dispose();
            instance.spriteBatch = null;
        }
        if (instance.decalBatch != null) {
            instance.decalBatch.dispose();
            instance.decalBatch = null;
        }

        if (font != null) {
            font.dispose();
            font = null;
        }

        instance.billboardParticleBatch = null;
    }

}
