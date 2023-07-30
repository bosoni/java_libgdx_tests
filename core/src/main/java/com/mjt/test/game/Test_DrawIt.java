/**
 * DrawIt
 * by mjt, 2014
 * <p>
 * voi piirtää polygoneja hiirellä.
 * piirtovaiheessa käytetään ShapeRenderer luokan lineä.
 * jokainen hiiren koordinaatti laitetaan listaan.
 * kun hiiren irrottaa, vedetään viiva viimesestä vertexistä ekaan.
 * optimoidaan viivat, poistetaan turhat vertexit.
 * polygoni laitetaan toiseen listaan.
 * luodaan lopuista viivoista polygoni (pitäis saada myös UV:t mutta eka vaik värillä).
 * pitää luoda Mesh että saa uv:t.
 * assetmanager     https://github.com/libgdx/libgdx/wiki/Managing-your-assets
 * tsekataan meneekö joku viiva toisen yli; jos menee, hylätään se poly
 * <p>
 * TODO:
 */
package com.mjt.test.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.utils.ShortArray;
import com.mjt.test.helper.Globals;
import com.mjt.test.helper.Util;

import java.util.ArrayList;
import java.util.List;

public class Test_DrawIt implements Screen {
    public static boolean DEBUG = false;

    List<Polygon> polys = new ArrayList<>();
    Polygon poly = new Polygon();
    //com.badlogic.gdx.scenes.scene2d.ui.List list;

    SpriteBatch batch;
    ShapeRenderer shapeRenderer;

    boolean mousePressed = false;

    @Override
    public void show() {
        Globals.instance.curScreen = this;

        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();

        Globals.instance.font = new BitmapFont(Gdx.files.internal("fonts/arial-15.fnt"));
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);

    }

    @Override
    public void render(float d) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) && Gdx.app.getType() == Application.ApplicationType.Desktop) {
            Gdx.app.exit();
            return;
        }

        Globals.instance.spriteBatch.begin();
        Globals.instance.font.setColor(0.2f, 0.5f, 1.0f, 1.0f);
        Globals.instance.font.draw(Globals.instance.spriteBatch,
                "num of polys: " + polys.size(),
                5, Gdx.graphics.getHeight() - 20);
        Globals.instance.spriteBatch.end();

        int x = Gdx.input.getX(), y = Gdx.graphics.getHeight() - Gdx.input.getY();

        if (Gdx.input.isButtonPressed(Buttons.LEFT)) {
            mousePressed = true;

            if (poly.points.size() > 0) {
                Vertex v1 = poly.points.get(poly.points.size() - 1);
                Vertex v2 = new Vertex(x, y);

                // jos vertexit ei ihan päällekäin niin lisää polyyn
                if (v1.len(v2) > 20) {
                    poly.points.add(new Vertex(x, y)); // lisää koordinaatit polyyn
                }
            } else {
                poly.points.add(new Vertex(x, y)); // lisää koordinaatit polyyn
            }
        } else {
            // jos juuri irrotettu hiiren vasen nappi, optimoidaan poly ja lisätään listaan
            if (mousePressed) {
                Util.debug("D: size:" + poly.points.size());
                if (poly.optimize(true)) // jos poly ok 
                {
                    try {
                        poly.createTriangles();
                    } catch (Exception ex) {
                        Util.debug(":: " + ex);
                    }
                    Util.debug("  =>  opt_size:" + poly.points.size());
                    polys.add(poly);
                }

                // luo uusi poly
                poly = new Polygon();
            }
            mousePressed = false;
        }

        shapeRenderer.begin(ShapeType.Line);
        shapeRenderer.setColor(0, 1, 0, 1);
        for (Polygon poly1 : polys) {
            poly1.render(shapeRenderer, batch, false); // rendaa polyt, ei outlineä
        }
        poly.render(shapeRenderer, batch, true); // piirrä pelkkä outline

        shapeRenderer.end();

    }

    @Override
    public void resize(int width, int height) {
        Gdx.gl.glViewport(0, 0, width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

}

class Vertex {
    public int x, y;

    public Vertex(int x_, int y_) {
        x = x_;
        y = y_;
    }

    public float len(Vertex v2) {
        int xl = x - v2.x;
        int yl = y - v2.y;
        return Math.abs(xl * xl + yl * yl);
    }

}

class Polygon {
    private static final EarClippingTriangulator triangulator = new EarClippingTriangulator();
    public static int OPTLEN = 100; // jos vertexien etäisyys pienempi kuin tämä, poistetaan vertex
    private static int _count = 0;
    public String name = "";
    public List<Vertex> points = new ArrayList<>();
    private short[] indices = null;
    private Mesh mesh = null;
    private Texture texture;
    private ShaderProgram shader;

    public boolean optimize(boolean close) {
        List<Vertex> temp = new ArrayList<>();

        int i = 0;
        temp.add(points.get(0));

        for (int q = 0; q < points.size() - 1; q++) {
            if (temp.get(i).len(points.get(q)) > OPTLEN) {
                temp.add(points.get(q));
                i++;
            }
        }
        points = temp;

        if (close) {
            points.add(points.get(0));
        }

        // tarkista meneekö joku viiva toisen päältä
        for (int q = 0; q < points.size() - 1; q++) {
            for (int w = q + 2; w < points.size() - 2; w++) {
                int x1 = points.get(q).x;
                int y1 = points.get(q).y;
                int x2 = points.get(q + 1).x;
                int y2 = points.get(q + 1).y;
                int x3 = points.get(w).x;
                int y3 = points.get(w).y;
                int x4 = points.get(w + 1).x;
                int y4 = points.get(w + 1).y;
                if (Intersector.intersectSegments(x1, y1, x2, y2, x3, y3, x4, y4, null) == true) {
                    points.clear();
                    return false;
                }
            }
        }
        return true;
    }

    public void createTriangles() throws Exception {
        //Color color = Color.WHITE;
        Color color = new Color((float) Math.random(), (float) Math.random(), (float) Math.random(), 1);
        float REPEAT = 4f + (float) Math.random() * 2;
        float offsetX = (float) Math.random(), offsetY = (float) Math.random();
        String texName = "textures/drawit/" + (int) ((Math.random() * 5) + 1) + ".png";

        Globals.instance.assets.load(texName, Texture.class);
        Globals.instance.assets.finishLoading();
        texture = Globals.instance.assets.get(texName, Texture.class);

        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        float[] verts = new float[points.size() * 2]; // xy, xy, xy ..
        for (int q = 0; q < points.size(); q++) {
            verts[2 * q] = points.get(q).x;
            verts[2 * q + 1] = points.get(q).y;
        }

        ShortArray arr = triangulator.computeTriangles(verts);
        indices = arr.toArray();

        mesh = new Mesh(true, true, points.size(), indices.length,
                new VertexAttributes(
                        new VertexAttribute(Usage.Position, 3, "a_position"),
                        new VertexAttribute(Usage.ColorPacked, 4, "a_color"),
                        new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoord0")
                ));

        //build vertex data array
        float[] vverts = new float[points.size() * 6]; // xyzcuv
        int vert = 0;
        for (int i = 0; i < points.size() * 6; i += 6) {
            vverts[i] = verts[vert];             // X
            vverts[i + 1] = verts[vert + 1];     // Y
            vverts[i + 2] = 0;                   // Z
            vverts[i + 3] = color.toFloatBits(); // C
            vverts[i + 4] = verts[vert] / (float) texture.getWidth() * REPEAT + offsetX;       // U
            vverts[i + 5] = -verts[vert + 1] / (float) texture.getHeight() * REPEAT + offsetY; // V
            vert += 2;
        }

        mesh.setVertices(vverts);
        mesh.setIndices(indices);

        String vertexShader = "attribute vec4 a_position;    \n"
                + "attribute vec4 a_color;\n"
                + "attribute vec2 a_texCoord0;\n"
                + "uniform mat4 u_worldView;\n"
                + "varying vec4 v_color;"
                + "varying vec2 v_texCoords;"
                + "void main()                  \n"
                + "{                            \n"
                + "   v_color = a_color; \n"
                + "   v_texCoords = a_texCoord0; \n"
                + "   gl_Position =  u_worldView * a_position;  \n"
                + "}                            \n";
        String fragmentShader = "#ifdef GL_ES\n"
                + "precision mediump float;\n"
                + "#endif\n"
                + "varying vec4 v_color;\n"
                + "varying vec2 v_texCoords;\n"
                + "uniform sampler2D u_texture;\n"
                + "void main()                                  \n"
                + "{                                            \n"
                + "  gl_FragColor = v_color * texture2D(u_texture, v_texCoords);\n"
                + "}";

        shader = new ShaderProgram(vertexShader, fragmentShader);
        if (shader.isCompiled() == false) {
            throw new Exception("Failed to compile shaders!");
        }

        name = "Poly" + _count++;
    }

    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch, boolean drawOutline) {
        // rendaa triangle viivoilla //debug
        if (Test_DrawIt.DEBUG) {
            if (indices != null) {
                shapeRenderer.setColor(1, 0, 0, 1);
                for (int q = 0; q < indices.length; q += 3) {
                    shapeRenderer.triangle(
                            points.get(indices[q]).x, points.get(indices[q]).y,
                            points.get(indices[q + 1]).x, points.get(indices[q + 1]).y,
                            points.get(indices[q + 2]).x, points.get(indices[q + 2]).y);
                }
            }
        }

        if (drawOutline) {
            if (points.size() >= 2) {
                shapeRenderer.setColor(0, 0, 1, 1);
                for (int q = 0; q < points.size() - 1; q++) {
                    int x = points.get(q).x, y = points.get(q).y;
                    int x2 = points.get(q + 1).x, y2 = points.get(q + 1).y;
                    shapeRenderer.line(x, y, x2, y2);
                }
            }
        } else if (mesh != null) {
            texture.bind();
            shader.bind();
            mesh.bind(shader);
            shader.setUniformi("u_texture", 0);
            shader.setUniformMatrix("u_worldView", batch.getProjectionMatrix().cpy().mul(batch.getTransformMatrix()));
            mesh.render(shader, GL20.GL_TRIANGLES);
        }

    }

}
