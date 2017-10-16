package com.mygdx.sponze;
import java.util.ArrayList;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader.Config;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.g3d.shaders.DepthShader;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.math.Matrix4;


public class MyShader extends DefaultShaderProvider {
    private ShaderProgram program;
    private String shader;

    public MyShader(String shader) {
        this.shader = shader;
    }

    public void setUniformi(String var, int i) {
        program.begin();
        program.setUniformi(var, i); //; //GL_TEXTURE1
        program.end();
    }

    public void dispose() {

    }

    @Override
    public DefaultShader createShader(Renderable renderable) {
        Config config = new DefaultShader.Config();
        String prefix = DefaultShader.createPrefix(renderable, config);
        ShaderUtils sUtils = new ShaderUtils();
        ShaderProgram program = sUtils.setupShader(prefix, shader);
        DefaultShader shader = new DefaultShader(renderable, config, program);
        program = shader.program;
        program.pedantic = true;
        return shader;

    }

}

