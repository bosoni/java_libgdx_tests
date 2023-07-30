package com.mjt.test.helper;

import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.RenderableSorter;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;
import com.badlogic.gdx.utils.Pool;

public class ModelBatchEx extends ModelBatch {

    public ModelBatchEx() {
    }

    public ModelBatchEx(ShaderProvider shaderProvider, RenderableSorter renderableSorter) {
        super(shaderProvider, renderableSorter);
    }

    public ModelBatchEx(DepthShaderProvider depthShaderProvider) {
        super(depthShaderProvider);
    }

    public Pool getPool() {
        return renderablesPool;
    }

    @Override
    public void flush() {
        sorter.sort(camera, renderables);
        Shader currentShader = null;
        for (int i = 0; i < renderables.size; i++) {
            final Renderable renderable = renderables.get(i);

            // pitää tsekata onko renderable (eli mesh) näkyvissä
            renderable.worldTransform.getScale(Globals.tempVector3_2);
            float max = Globals.tempVector3_2.x;
            if (Globals.tempVector3_2.y > max) max = Globals.tempVector3_2.y;
            if (Globals.tempVector3_2.z > max) max = Globals.tempVector3_2.z;

            renderable.worldTransform.getTranslation(Globals.tempVector3);
            if (camera.frustum.sphereInFrustum(Globals.tempVector3, renderable.meshPart.radius * max)) {
                //Util.debug(">>>("+i+" "+renderable.meshPart.id+"):\t"+renderable.meshPart.radius +"\t\t"+ max);
                if (currentShader != renderable.shader) {
                    if (currentShader != null) currentShader.end();
                    currentShader = renderable.shader;
                    currentShader.begin(camera, context);
                }
                currentShader.render(renderable);
                Globals.visibleModels++;
            }
        }
        if (currentShader != null) currentShader.end();
        renderablesPool.flush();
        renderables.clear();

        //Util.debug("--------------------------------------");
    }

}
