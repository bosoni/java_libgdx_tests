package com.mjt.test.helper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DirectionalLightsAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.PointLightsAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.SpotLightsAttribute;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.environment.SpotLight;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.shaders.DepthShader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.RenderableSorter;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;

import net.mgsx.gltf.loaders.glb.GLBLoader;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.attributes.PBRCubemapAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRFloatAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import net.mgsx.gltf.scene3d.lights.DirectionalLightEx;
import net.mgsx.gltf.scene3d.lights.DirectionalShadowLight;
import net.mgsx.gltf.scene3d.lights.PointLightEx;
import net.mgsx.gltf.scene3d.lights.SpotLightEx;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import net.mgsx.gltf.scene3d.scene.SceneRenderableSorter;
import net.mgsx.gltf.scene3d.scene.SceneSkybox;
import net.mgsx.gltf.scene3d.scene.Updatable;
import net.mgsx.gltf.scene3d.shaders.PBRCommon;
import net.mgsx.gltf.scene3d.shaders.PBRShaderConfig;
import net.mgsx.gltf.scene3d.shaders.PBRShaderProvider;
import net.mgsx.gltf.scene3d.utils.EnvironmentCache;
import net.mgsx.gltf.scene3d.utils.EnvironmentUtil;
import net.mgsx.gltf.scene3d.utils.IBLBuilder;

public class SceneManagerEx implements Disposable {

    protected final EnvironmentCache computedEnvironement = new EnvironmentCache();
    private final Array<Model> models = new Array<>();
    private final Array<RenderableProvider> visibleList = new Array<>();
    public Node world = new Node();
    /**
     * Shouldn't be null.
     */
    public Environment environment = new Environment();
    public PerspectiveCamera camera;
    private Node[] camNodes = new Node[3]; // center, left, right
//TODO   koit Movablella  Noden tilal



    public Vector3 getStereoCameraPosition(boolean left) {

        // init
        if (camNodes[0] == null) {
            for (int q = 0; q < 3; q++)
                camNodes[q] = new Node();

            float D = 1f;
            camNodes[0].translation.set(camera.position);
            camNodes[1].translation.set(-D, 0, 0);
            camNodes[2].translation.set(D, 0, 0);

            camNodes[0].addChild(camNodes[1]); // add left to center
            camNodes[0].addChild(camNodes[2]); // add right to center

        }
        camNodes[0].translation.set( Globals.tempVector3);

        if (left)
            return camNodes[1].calculateWorldTransform().getTranslation(Globals.tempVector3_2);
        else
            return camNodes[2].calculateWorldTransform().getTranslation(Globals.tempVector3_2);
    }

    public FPSCameraController cameraController;
    private ModelBatchEx batch;
    private ModelBatchEx depthBatch;
    private SceneSkybox skybox;
    private Cubemap diffuseCubemap;
    private Cubemap environmentCubemap;
    private Cubemap specularCubemap;
    private Texture brdfLUT;
    private PhysicsManager physics = null;
    private final RenderableSorter renderableSorter;

    private final PointLightsAttribute pointLights = new PointLightsAttribute();
    private final SpotLightsAttribute spotLights = new SpotLightsAttribute();

    public SceneManagerEx() {
        this(24);
    }

    public SceneManagerEx(int maxBones) {
        this(PBRShaderProvider.createDefault(maxBones), PBRShaderProvider.createDefaultDepth(maxBones));
    }

    public SceneManagerEx(ShaderProvider shaderProvider, DepthShaderProvider depthShaderProvider) {
        this(shaderProvider, depthShaderProvider, new SceneRenderableSorter());
    }

    public SceneManagerEx(ShaderProvider shaderProvider, DepthShaderProvider depthShaderProvider, RenderableSorter renderableSorter) {
        this.renderableSorter = renderableSorter;

        batch = new ModelBatchEx(shaderProvider, renderableSorter);

        depthBatch = new ModelBatchEx(depthShaderProvider);

        float lum = 1f;
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, lum, lum, lum, 1));
    }

    public static SceneManagerEx createSceneManager(boolean usePBR, int bones, int dirlights, int pointlights, int spotlights) {
        if (usePBR) {
            Util.debug("createSceneManager pbr");
            PBRShaderConfig config = PBRShaderProvider.createDefaultConfig();
            config.numBones = bones;
            config.numDirectionalLights = dirlights;
            config.numPointLights = pointlights;
            config.numSpotLights = spotlights;
            DepthShader.Config depthConfig = new DepthShader.Config();
            depthConfig.numBones = config.numBones;
            return new SceneManagerEx(PBRShaderProvider.createDefault(config), PBRShaderProvider.createDefaultDepth(depthConfig));
        } else {
            Util.debug("createSceneManager default");
            DefaultShaderProvider config = new DefaultShaderProvider();
            config.config.numBones = bones;
            config.config.numDirectionalLights = dirlights;
            config.config.numPointLights = pointlights;
            config.config.numSpotLights = spotlights;
            DepthShaderProvider depthConfig = new DepthShaderProvider();
            depthConfig.config.numBones = config.config.numBones;
            return new SceneManagerEx(config, depthConfig);
        }
    }

    public static SceneManagerEx createSceneManager(String shaderName, String depthShaderName, int bones, int dirlights, int pointlights, int spotlights) {
        Util.debug("createSceneManager custom");
        PBRShaderConfig config = PBRShaderProvider.createDefaultConfig();
        config.numBones = bones;
        config.numDirectionalLights = dirlights;
        config.numPointLights = pointlights;
        config.numSpotLights = spotlights;
        // shader files should be located in core project resources (shaders folder)
        config.vertexShader = Gdx.files.classpath(shaderName + ".vs.glsl").readString();
        config.fragmentShader = Gdx.files.classpath(shaderName + "fs.glsl").readString();

        DepthShader.Config depthConfig = new DepthShader.Config();
        depthConfig.numBones = config.numBones;
        depthConfig.vertexShader = Gdx.files.classpath(depthShaderName + ".vs.glsl").readString();
        depthConfig.fragmentShader = Gdx.files.classpath(depthShaderName + ".fs.glsl").readString();

        return new SceneManagerEx(PBRShaderProvider.createDefault(config), PBRShaderProvider.createDefaultDepth(depthConfig));
    }

    public ModelBatch getBatch() {
        return batch;
    }

    public void setBatch(ModelBatchEx batch) {
        this.batch = batch;
    }

    public void initPhysics(int size) {
        physics = new PhysicsManager();
        physics.init(size, new Vector3(0, -10, 0));
    }

    public PhysicsManager getPhysics() {
        return physics;
    }

    public void setShaderProvider(ShaderProvider shaderProvider) {
        batch.dispose();
        batch = new ModelBatchEx(shaderProvider, renderableSorter);
    }

    public void setDepthShaderProvider(DepthShaderProvider depthShaderProvider) {
        depthBatch.dispose();
        depthBatch = new ModelBatchEx(depthShaderProvider);
    }

    public void add(Model scene) {
        addScene(scene, true);
        world.addChild(scene); // add node to world scenetree
    }

    public void addScene(Model model, boolean appendLights) {
        models.add(model);
        if (appendLights) {
            for (ObjectMap.Entry<Node, BaseLight> e : model.scene.lights) {
                environment.add(e.value);
            }
        }
    }

    /**
     * should be called in order to perform light culling, skybox update and animations.
     *
     * @param delta
     */
    public void update(float delta) {
        if (camera != null) {
            world.calculateTransforms(true);
            visibleList.clear();

            updateEnvironment();
            for (Model r : models) {
                if (r.scene instanceof Updatable) {
                    ((Updatable) r.scene).update(camera, delta);
                    visibleList.add(r.scene);
                }
            }
            if (skybox != null) skybox.update(camera, delta);

            if (physics != null) physics.update(delta);

            updateTransforms();
        }
    }

    private void updateTransforms() {
        for (Model m : models) {
            m.scene.modelInstance.transform.set(m.globalTransform);
        }
    }

    protected void updateEnvironment() {
        computedEnvironement.setCache(environment);
        pointLights.lights.clear();
        spotLights.lights.clear();
        if (environment != null) {
            for (Attribute a : environment) {
                if (a instanceof PointLightsAttribute) {
                    pointLights.lights.addAll(((PointLightsAttribute) a).lights);
                    computedEnvironement.replaceCache(pointLights);
                } else if (a instanceof SpotLightsAttribute) {
                    spotLights.lights.addAll(((SpotLightsAttribute) a).lights);
                    computedEnvironement.replaceCache(spotLights);
                } else {
                    computedEnvironement.set(a);
                }
            }
        }
        cullLights();
    }

    protected void cullLights() {
        PointLightsAttribute pla = environment.get(PointLightsAttribute.class, PointLightsAttribute.Type);
        if (pla != null) {
            for (PointLight light : pla.lights) {
                if (light instanceof PointLightEx) {
                    PointLightEx l = (PointLightEx) light;
                    if (l.range != null && !camera.frustum.sphereInFrustum(l.position, l.range)) {
                        pointLights.lights.removeValue(l, true);
                    }
                }
            }
        }
        SpotLightsAttribute sla = environment.get(SpotLightsAttribute.class, SpotLightsAttribute.Type);
        if (sla != null) {
            for (SpotLight light : sla.lights) {
                if (light instanceof SpotLightEx) {
                    SpotLightEx l = (SpotLightEx) light;
                    if (l.range != null && !camera.frustum.sphereInFrustum(l.position, l.range)) {
                        spotLights.lights.removeValue(l, true);
                    }
                }
            }
        }
    }

    /**
     * render all scenes.
     * because shadows use frame buffers, if you need to render scenes to a frame buffer, you should instead
     * first call {@link #renderShadows()}, bind your frame buffer and then call {@link #renderColors()}
     */
    public void render() {
        if (camera == null) return;

        renderShadows();

        Globals.visibleModels = 0;
        renderColors();
    }

    /**
     * Render shadows only to interal frame buffers.
     * (useful when you're using your own frame buffer to render scenes)
     */
    @SuppressWarnings("deprecation")
    public void renderShadows() {
        DirectionalLight light = getFirstDirectionalLight();
        if (light instanceof DirectionalShadowLight) {
            DirectionalShadowLight shadowLight = (DirectionalShadowLight) light;
            shadowLight.begin();
            renderDepth(shadowLight.getCamera());
            shadowLight.end();

            environment.shadowMap = shadowLight;
        } else {
            environment.shadowMap = null;
        }
    }

    /**
     * Render only depth (packed 32 bits), usefull for post processing effects.
     * You typically render it to a FBO with depth enabled.
     */
    public void renderDepth() {
        renderDepth(camera);
    }

    private void renderDepth(Camera camera) {
        depthBatch.begin(camera);
        depthBatch.render(visibleList);
        depthBatch.end();
    }

    /**
     * Render colors only. You should call {@link #renderShadows()} before.
     * (useful when you're using your own frame buffer to render scenes)
     */
    public void renderColors() {
        PBRCommon.enableSeamlessCubemaps();
        computedEnvironement.shadowMap = environment.shadowMap;
        batch.begin(camera);
        batch.render(visibleList, computedEnvironement);
        if (skybox != null) batch.render(skybox);
        batch.end();
    }

    public DirectionalLight getFirstDirectionalLight() {
        DirectionalLightsAttribute dla = environment.get(DirectionalLightsAttribute.class, DirectionalLightsAttribute.Type);
        if (dla != null) {
            for (DirectionalLight dl : dla.lights) {
                if (dl instanceof DirectionalLight) {
                    return dl;
                }
            }
        }
        return null;
    }

    public void setSkyBox(SceneSkybox skyBox) {
        this.skybox = skyBox;
    }

    public void setAmbientLight(float lum) {
        environment.get(ColorAttribute.class, ColorAttribute.AmbientLight).color.set(lum, lum, lum, 1);
    }

    public void setCamera(PerspectiveCamera camera) {
        this.camera = camera;
    }

    public void removeScene(Model model) {
        models.removeValue(model, true);
        for (ObjectMap.Entry<Node, BaseLight> e : model.scene.lights) {
            environment.remove(e.value);
        }
    }

    public void updateViewport(float width, float height) {
        if (camera != null) {
            camera.viewportWidth = width;
            camera.viewportHeight = height;
            camera.update(true);
        }
    }

    public int getActiveLightsCount() {
        return EnvironmentUtil.getLightCount(computedEnvironement);
    }

    public int getTotalLightsCount() {
        return EnvironmentUtil.getLightCount(environment);
    }

    public void setupSceneDefault(Vector3 camPos, float nearPlane, float farPlane, boolean useSkybox, boolean shadows) {
        Util.debug("create camera, light, ibl");
        // setup camera
        PerspectiveCamera camera = new PerspectiveCamera(45, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.near = nearPlane;
        camera.far = farPlane;
        camera.position.set(camPos);
        camera.lookAt(0, 0, 0);
        setCamera(camera);

        // setup light
        DirectionalLightEx light = new DirectionalLightEx();
        light.direction.set(-1, -2, 0).nor();
        light.color.set(Color.BLUE);

        if (shadows == false)
            environment.add(light);
        else {
            // setup light
            DirectionalShadowLight slight = new DirectionalShadowLight();
            slight.direction.set(light.direction);
            slight.color.set(light.color);

            BoundingBox bb = new BoundingBox();
            float V = 5;
            bb.ext(-V, -V, -V);
            bb.ext(V, V, V);
            //bb.min.set(-V, -V, -V);
            //bb.max.set(V, V, V);
            slight.setBounds(bb);

            environment.set(new PBRFloatAttribute(PBRFloatAttribute.ShadowBias, 0.01f));
            //PBRFloatAttribute shadowBias = sceneManager.environment.get(PBRFloatAttribute.class, PBRFloatAttribute.ShadowBias);
            //shadowBias.value = 0.2f;
            environment.add(slight);
        }

        // setup quick IBL (image based lighting)
        IBLBuilder iblBuilder = IBLBuilder.createOutdoor(light);
        environmentCubemap = iblBuilder.buildEnvMap(1024);
        diffuseCubemap = iblBuilder.buildIrradianceMap(256);
        specularCubemap = iblBuilder.buildRadianceMap(10);
        iblBuilder.dispose();
        brdfLUT = new Texture(Gdx.files.classpath("net/mgsx/gltf/shaders/brdfLUT.png"));

        setAmbientLight(1f);
        environment.set(new PBRTextureAttribute(PBRTextureAttribute.BRDFLUTTexture, brdfLUT));
        environment.set(PBRCubemapAttribute.createSpecularEnv(specularCubemap));
        environment.set(PBRCubemapAttribute.createDiffuseEnv(diffuseCubemap));

        // setup skybox
        if (useSkybox) {
            skybox = new SceneSkybox(environmentCubemap);
            setSkyBox(skybox);
        }

        Globals.instance.font = new BitmapFont(Gdx.files.internal("fonts/arial-15.fnt"));
    }

    @Override
    public void dispose() {
        Util.debug("SceneManager dispose");
        batch.dispose();
        depthBatch.dispose();

        if (environmentCubemap != null) {
            environmentCubemap.dispose();
            environmentCubemap = null;
        }
        if (diffuseCubemap != null) {
            diffuseCubemap.dispose();
            diffuseCubemap = null;
        }
        if (specularCubemap != null) {
            specularCubemap.dispose();
            specularCubemap = null;
        }
        if (brdfLUT != null) {
            brdfLUT.dispose();
            brdfLUT = null;
        }
        if (skybox != null) {
            skybox.dispose();
            skybox = null;
        }
        if (physics != null) {
            physics.dispose();
            physics = null;
        }

    }

    public void showTree(final Array<Node> nodes, int ind) {
        final int n = nodes.size;
        for (int i = 0; i < n; i++) {
            Node node = nodes.get(i);
            if (node != null) {
                for (int q = 0; q < ind; q++) System.out.print(" ");
                System.out.println("> " + node.id);
                showTree((Array<Node>) nodes.get(i).getChildren(), i);
            }
        }
    }

    public void showTree(Node node) {
        for (Model m : models) {
            Array<Node> nodetemp = new Array<>();
            for (Node n : m.scene.modelInstance.nodes) {
                nodetemp.add(n);
                //Util.debug(" found " + n.id);
            }
            showTree(nodetemp, 0);
        }
    }
}
