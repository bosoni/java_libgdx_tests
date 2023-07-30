/*
extrude, lathe

TODO

 */
package com.mjt.test.helper;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class ModelCreator {

    /**
     * lathe
     * vertices xz plane (z=0).
     */
    public static void lathe(Vector3[] vert, int segments) {
        int numPoints = vert.length;
        if (segments < 3) {
            Util.debug("lathe: segment==" + segments + " (min 3)");
            return;
        }
        Vector3[][] vrt = new Vector3[numPoints][segments];

        int q = 0, w = 0;

        float step = 360 / (float) segments;
        float ang = 0;

        // calc vertices
        for (q = 0; q < numPoints; q++) {
            for (w = 0; w < segments; w++) {
                float angrad = ang * (float) Math.PI / 180;
                ang += step;
                float x = (float) Math.sin(angrad) * vert[q].x;
                float z = (float) Math.cos(angrad) * vert[q].x;
                float y = vert[q].y;
                vrt[q][w] = new Vector3(x, y, z);
            }
        }

        // create triangles
        Array<Vector3> v = new Array<>();
        for (q = 0; q < numPoints - 1; q++) {
            for (w = 0; w < segments; w++) {
                int i = (w + 1) % segments;
                v.add(vrt[q][w]);
                v.add(vrt[q][i]);
                v.add(vrt[q + 1][i]);

                v.add(vrt[q][w]);
                v.add(vrt[q + 1][i]);
                v.add(vrt[q + 1][w]);
            }
        }

        /*
        Object3D obj = new Object3D("latheobj");
        Mesh mesh = new Mesh();
        // face indexit --
        int faceIndex = 0;
        mesh.faces = new int[v.size / 3][3];
        for (q = 0; q < v.size; q += 3) {
            mesh.faces[faceIndex][0] = q;
            mesh.faces[faceIndex][2] = q + 1;
            mesh.faces[faceIndex][1] = q + 2;
            faceIndex++;
        }

        mesh.vertex = new Vector3[v.size];
        for (q = 0; q < v.size; q++) {
            mesh.vertex[q] = v.get(q);
        }

        mesh.vertexNormals = new Vector3[v.size];
        Geometry.calcNormals(mesh.vertex, mesh.faces, mesh.vertexNormals, false);

        mesh.boundings.setup(mesh.vertex, mesh.faces, BoundingArea.SPHERE);
        mesh.createBuffers(mesh.vertex, mesh.faces, mesh.vertexNormals, null, true);

        obj.addMesh(mesh);

        return obj;
         */
    }

    /**
     * extrude
     * poly must be convex
     */
    public static void extrude(Vector3[] vert, float len, boolean caps) {
        //Object3D obj = new Object3D("extobj");

        int numVerts = vert.length;
        if (numVerts < 3) {
            Util.debug("extrude: numverts=" + numVerts + " (min 3)");
            return;
        }

        Array<Vector3> v = new Array<>();
        int faceIndex = 0;

        // create triangles
        if (caps) {
            int a = 1, b;
            for (int q = 0; q < numVerts - 2; q++) {
                // vertex 0
                v.add(new Vector3(vert[0]));
                // 2.vertex
                for (b = a + 1; b >= a; b--) {
                    v.add(new Vector3(vert[b]));
                }
                // close
                v.add(new Vector3(vert[0]));
                v.get(v.size - 1).z = len;
                for (b = a; b < a + 2; b++) {
                    v.add(new Vector3(vert[b]));
                    v.get(v.size - 1).z = len;
                }
                a++;
            }
        }

        //
        for (int q = 0; q < numVerts; q++) {
            int w = (q + 1) % numVerts;

            // 1.triangle
            v.add(new Vector3(vert[q]));
            v.add(new Vector3(vert[w]));
            v.add(new Vector3(vert[w]));
            v.get(v.size - 1).z = len;

            // 2.triangle
            v.add(new Vector3(vert[q]));
            v.add(new Vector3(vert[w]));
            v.get(v.size - 1).z = len;
            v.add(new Vector3(vert[q]));
            v.get(v.size - 1).z = len;
        }

        /*
        // face indices
        mesh.faces = new int[v.size / 3][3];
        for (int q = 0; q < v.size; q += 3) {
            mesh.faces[faceIndex][0] = q;
            mesh.faces[faceIndex][2] = q + 1;
            mesh.faces[faceIndex][1] = q + 2;
            faceIndex++;
        }

        mesh.vertex = new Vector3[v.size];
        for (int q = 0; q < v.size; q++) {
            mesh.vertex[q] = v.get(q);
        }

        /*
        mesh.vertexNormals = new Vector3[v.size];
        Geometry.calcNormals(mesh.vertex, mesh.faces, mesh.vertexNormals, false);

        mesh.boundings.setup(mesh.vertex, mesh.faces, BoundingArea.SPHERE);
        mesh.createBuffers(mesh.vertex, mesh.faces, mesh.vertexNormals, null, true);
        obj.addMesh(mesh);

        return obj;
         */
    }

    public static void box(Vector3 size) {
        float sx = size.x * 0.5f;
        float sy = size.y * 0.5f;
        float sz = size.z * 0.5f;
        if (sy != 0) {
            float t = sy;
            sy = sz;
            sz = t;
        }
        Vector3[] vert = new Vector3[4];
        vert[3] = new Vector3(-sx, -sy, -sz);
        vert[2] = new Vector3(sx, -sy, -sz);
        vert[1] = new Vector3(sx, sy, -sz);
        vert[0] = new Vector3(-sx, sy, -sz);
        extrude(vert, sz, true);
    }

    public static void sphere(Vector3 size, int u, int v) {
        // calc half circle
        float step = 180 / (float) v + 2;
        float ang = 0;
        Vector3[] vr = new Vector3[v];
        for (int q = 0; q < v; q++) {
            float angrad = ang * (float) Math.PI / 180;
            ang += step;

            float x = (float) Math.sin(angrad) * size.x;
            float y = (float) Math.cos(angrad) * size.y;

            vr[q] = new Vector3(x, y, 0);
        }
        lathe(vr, u);
    }
}
