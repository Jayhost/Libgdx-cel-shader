package com.mygdx.sponze;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.shaders.CelDepthShader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;

public class MyCelShader extends DefaultShaderProvider {
    CelDepthShader shader;

    public CelDepthShader createShader(Renderable renderable) {
        CelDepthShader.Config config = new CelDepthShader.Config();
        config.numBones = 16;
        shader = new CelDepthShader(renderable);
        return shader;
    }
}