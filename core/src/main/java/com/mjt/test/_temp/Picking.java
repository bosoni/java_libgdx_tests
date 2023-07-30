package com.mjt.test._temp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

public class Picking {
    Ray pickRay;
    GameObject curGameObject = null;
    boolean found = false;

    public void setupRay(Camera cam) {
        pickRay = cam.getPickRay(Gdx.input.getX(), Gdx.input.getY(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void setupRay(Camera cam, float x, float y) {
        pickRay = cam.getPickRay(x, y, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public GameObject checkRay(Model3D model) {
        found = false;
        for (GameObject obj : model.getGameObjects()) {
            curGameObject = obj;
            for (Node node : obj.nodes) {
                checkCurrentInstance(node);
                if (found)
                    return curGameObject;
            }
        }
        return null;
    }

    public boolean checkRay(GameObject obj) {
        found = false;
        curGameObject = obj;
        for (Node node : obj.nodes)
            checkCurrentInstance(node);
        return found;
    }

    private void checkCurrentInstance(Node node) {
        if (found) return;

        if (node.getChildCount() > 0) {
            for (Node current : node.getChildren())
                checkCurrentInstance(current);
        } else if (node.parts.size > 0) {
            for (NodePart part : node.parts) {
                isMeshIntersected(pickRay, part.meshPart.mesh);
                if (found) return;
            }
        }
    }

    private void isMeshIntersected(Ray ray, Mesh mesh) {
        Vector3 intersection = new Vector3();
        float[] vertices = new float[mesh.getNumVertices() * 6];
        short[] indices = new short[mesh.getNumIndices()];
        mesh.getVertices(vertices);
        mesh.getIndices(indices);
        if ((ray != null) && (Intersector.intersectRayTriangles(ray, vertices, indices, 5, intersection))) {
            System.out.println("DEBUG: intersection: " + mesh + "   :: " + curGameObject.name);
            found = true;
        }
    }
}
