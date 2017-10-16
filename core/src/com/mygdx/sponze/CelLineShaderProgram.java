package com.mygdx.sponze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import java.util.logging.FileHandler;

/**
 * 2D shader program. Draws cel lines based on differences in depth.
 */
public class CelLineShaderProgram extends ShaderProgram {


	public CelLineShaderProgram() {
		super(Gdx.files.internal("default_v.glsl").readString(),Gdx.files.internal("default_f.glsl").readString());
//		super("hey","there");
//		super(Gdx.files.internal("assets/cel.line.vertex.glsl").readString(), Gdx.files.internal("cel.line.fragment.glsl").readString());
//		super(Gdx.files.internal("default_v.glsl").readString(), Gdx.files.internal("default_f.glsl").readString());
	}
	public CelLineShaderProgram(String vert, String frag){
		super(vert,frag);
	}
	/**
	 * Sets the u_size uniform. This should be configurable instead of relying on Gdx.graphics all the time.
	 */
	@Override
	public void begin() {
		super.begin();
		setUniformf("u_size", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}
}
