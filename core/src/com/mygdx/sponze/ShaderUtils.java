package com.mygdx.sponze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * Created by ian on 7/15/17.
 */
public class ShaderUtils {

    public ShaderProgram setupShader(String shader, String version){
        return setupShader(version,"",shader);
    }


    public ShaderProgram setupShader(String version, String prefix,String shader){
        version = version + " \n";
        prefix = version + prefix;
        String vert = Gdx.files.internal(shader + "_v.glsl").readString();
        String frag = Gdx.files.internal(shader + "_f.glsl").readString();
        String geom;
        ShaderProgram program;
        program = new ShaderProgram(prefix + vert,prefix + frag);

        program.pedantic = true;

        if (!program.isCompiled()){
            System.err.println("Error with shader " + program.getLog());
            System.exit(1);
        }else{
            Gdx.app.log("init 2", "Shader " + " compilled " + program.getLog());
        }
        return program;
    }



}

