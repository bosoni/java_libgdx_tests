// http://stackoverflow.com/questions/28590802/libgdx-assigning-a-specific-shader-to-a-modelinstance
package com.mjt.test._temp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;

public class MyShaderProvider extends DefaultShaderProvider {
    public final DefaultShader.Config config;

    public MyShaderProvider(final DefaultShader.Config config) {
        this.config = (config == null) ? new DefaultShader.Config() : config;
    }

    public MyShaderProvider(final String vertexShader, final String fragmentShader) {
        this(new DefaultShader.Config(vertexShader, fragmentShader));
    }

    public MyShaderProvider(final FileHandle vertexShader, final FileHandle fragmentShader) {
        this(vertexShader.readString(), fragmentShader.readString());
    }

    public MyShaderProvider() {
        this(null);
    }

    public void testListShader(Renderable instance) {
        for (Shader shader : shaders) {
            Gdx.app.log("MyShaderProvider", "shader=" + shader.getClass().getName());
            Gdx.app.log("MyShaderProvider", "can render=" + shader.canRender(instance));
        }
    }

    @Override
    protected Shader createShader(final Renderable renderable) {
        // pick shader based on renderables userdata?
        ShaderTypes shaderEnum = (ShaderTypes) renderable.userData;

        if (shaderEnum == null) {
            return super.createShader(renderable);
        }
        Gdx.app.log("MyShaderProvider", "shaderenum=" + shaderEnum);

        switch (shaderEnum) {
            case lighting: {
                String vert = Gdx.files.internal("shaders/lighting.vertex.glsl").readString();
                String frag = Gdx.files.internal("shaders/lighting.fragment.glsl").readString();
                return new DefaultShader(renderable, new DefaultShader.Config(vert, frag));
            }
            case toon: {
                String vert = Gdx.files.internal("shaders/toon.vertex.glsl").readString();
                String frag = Gdx.files.internal("shaders/toon.fragment.glsl").readString();
                return new DefaultShader(renderable, new DefaultShader.Config(vert, frag));
            }
            case fur: {
                String vert = Gdx.files.internal("shaders/fur.vertex.glsl").readString();
                String frag = Gdx.files.internal("shaders/fur.fragment.glsl").readString();
                return new DefaultShader(renderable, new DefaultShader.Config(vert, frag));
            }

            default:
                return super.createShader(renderable);

        }
        // return new DefaultShader(renderable, new DefaultShader.Config());
    }

    public Shader getShader(Renderable renderable) {
        Shader suggestedShader = renderable.shader;
        if (suggestedShader != null && suggestedShader.canRender(renderable)) {
            return suggestedShader;
        }
        for (Shader shader : shaders) {
            if (shader.canRender(renderable)) {
                return shader;
            }
        }
        final Shader shader = createShader(renderable);
        shader.init();
        shaders.add(shader);
        return shader;
    }

    // shaders
    public enum ShaderTypes {
        lighting, toon, fur
    }

}
