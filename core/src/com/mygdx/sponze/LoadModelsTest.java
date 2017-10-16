/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.mygdx.sponze;


import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.shaders.CelDepthShader;
//import com.badlogic.gdx.graphics.g3d.shaders.CelLineShaderProgram;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.bitfire.postprocessing.PostProcessor;
import com.bitfire.postprocessing.effects.Bloom;
import com.bitfire.postprocessing.effects.Fxaa;
import com.bitfire.postprocessing.effects.Vignette;

/**
 * See: http://blog.xoppa.com/loading-models-using-libgdx/
 * @author Xoppa
 */
public class LoadModelsTest implements ApplicationListener {
    public CameraInputController camController;
    public ModelBatch modelBatch;
    public AssetManager assets;
    public Array<ModelInstance> instances = new Array<ModelInstance>();
    public boolean loading;
    private PerspectiveCamera camera;
    private AssetManager assetManager = new AssetManager();

    private FrameBuffer fbo;
    private TextureRegion textureRegion;
//    private CelLineShaderProgram lineShader = new CelLineShaderProgram();// can't init this sstuff early
    private String vert;
    private String frag;
    private ShaderProgram lineShader;// = new ShaderProgram(vert,frag);

    private SpriteBatch spriteBatch;
//    private ModelBatch modelBatch = new ModelBatch(Gdx.files.classpath("com/badlogic/gdx/graphics/g3d/shaders/default.vertex.glsl").readString(), Gdx.files.internal("shaders/cel.main.fragment.glsl").readString());
    private ModelBatch depthBatch;// = new ModelBatch(new MyCelShader());
//    private Environment environment = new Environment();
    private Environment environment;
    private com.bitfire.postprocessing.PostProcessor postProcessor;
    String modelName = "mapped_grave_stone" + ".g3db";


    @Override
    public void create () {
        vert = Gdx.files.internal("cel.line.vertex.glsl").readString();
        frag = Gdx.files.internal("cel.line.fragment.glsl").readString();
        lineShader = new CelLineShaderProgram(vert,frag);
        depthBatch = new ModelBatch(new MyCelShader());
        spriteBatch = new SpriteBatch();
        postProcessor = new PostProcessor(true, true, true);
        Fxaa fxaa = new Fxaa(((int) (Gdx.graphics.getWidth())), ((int) (Gdx.graphics.getHeight())));
        Vignette vig = new Vignette(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),false);
        postProcessor.addEffect(vig);
        postProcessor.addEffect(fxaa);
        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        modelBatch = new ModelBatch(Gdx.files.internal("cel.main_v.glsl").readString(),Gdx.files.internal("cel.main_f.glsl").readString());
        environment = new Environment();
//        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

//        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(7f, 7f, 7f);
        camera.lookAt(0,0,0);
        camera.near = 1f;
        camera.far = 300f;
        camera.update();

        camController = new CameraInputController(camera);
        Gdx.input.setInputProcessor(camController);
         
        assets = new AssetManager();
//        assets.load("mapped_grave_stone.g3db", Model.class);
        assets.load(modelName, Model.class);
        loading = true;
    }
 
    private void doneLoading() {
        Model ship = assets.get(modelName, Model.class);
        float xx = 10f;
        for (float x = -xx; x <= xx; x += 2f) {
            for (float z = -xx; z <= xx; z += 2f) {
                ModelInstance shipInstance = new ModelInstance(ship);
                shipInstance.transform.setToTranslation(x/1.5f, 0, z);
                instances.add(shipInstance);
            }
        }
        loading = false;
    }

    @Override
    public void render () {
        if (loading && assets.update())
            doneLoading();
        camController.update();
         
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        captureDepth();

        prepTextureRegion();
        postProcessor.capture();
        float r = 0.9f;
        float g = 0.4f;
        float b = 0.4f;
        Gdx.gl.glClearColor(r, g, b, 0.0f);
        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_COLOR_BUFFER_BIT);
        renderScene();
        drawOutlines();
        postProcessor.render();

    }

    protected void drawOutlines() {
        spriteBatch.setShader(lineShader);
        lineShader.setUniformf("u_size", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        spriteBatch.begin();
        spriteBatch.draw(textureRegion, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        spriteBatch.end();
        spriteBatch.setShader(null);
    }
    /*
     * Stores fbo texture in a TextureRegion and flips it vertically.
     */
    protected void prepTextureRegion() {
        textureRegion = new TextureRegion(fbo.getColorBufferTexture());
        textureRegion.flip(false, true);
    }
    /*
     * Draws the depth pass to an fbo, using a ModelBatch created with CelDepthShaderProvider()
     */
    protected void captureDepth() {
        fbo.begin();
        float r = 0.9f;//
        float g = 0.5f;//
        float b = 0.5f;//
        Gdx.gl.glClearColor(r, g, b, 0.0f);
        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_COLOR_BUFFER_BIT);
        depthBatch.begin(camera);
        depthBatch.render(instances);
        depthBatch.end();
        fbo.end();
    }
    /*
     * Renders the scene.
     */
    protected void renderScene() {
        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_COLOR_BUFFER_BIT);
        modelBatch.begin(camera);
        modelBatch.render(instances, environment);
        modelBatch.end();
    }
     
    @Override
    public void dispose () {
        modelBatch.dispose();
        instances.clear();
        assets.dispose();
    }

	@Override
	public void resize(int width, int height) {
        if (fbo != null) fbo.dispose();
        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}



}