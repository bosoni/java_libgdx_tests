/*
Blenderin käyttö editorina:
* blenderillä luodaan skene, objektien nimissä parametreja

 NIMISSÄ PARAMETREJA:
   jos joku seuraavista, luodaan collision area ohjelmallisesti:
     _CB (collision box) lasketaan modelin bbox ja asetetaan bullettiin
	 _CS (collision sphere) lasketaan modelin bsphere ja asetetaan bullettiin
	 _CC (collision capsule) btCapsuleShape (3d ellipse)
	 _CM (collision mesh shape)  btBvhTriangleMeshShape

  animoidun ukon bonet voi ehkä ottaa  getChild(string)  metodilla, niihi voi sit luoda omat collision spheret tai mitä vaa

 */
package com.mjt.test.helper;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.bulletphysics.collision.broadphase.AxisSweep3;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.BvhTriangleMeshShape;
import com.bulletphysics.collision.shapes.CapsuleShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.HeightfieldTerrainShape;
import com.bulletphysics.collision.shapes.IndexedMesh;
import com.bulletphysics.collision.shapes.ScalarType;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.collision.shapes.TriangleIndexVertexArray;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.util.ObjectArrayList;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public class PhysicsManager implements Disposable {
    public final static int BOX = 1;
    public final static int SPHERE = 2;
    public final static int CAPSULE = 3;
    public final static int TRIMESH = 4;
    public final static int TERRAIN = 5;
    static int objs = 0;
    static Vector3f tmpVector3f = new Vector3f();
    static Quat4f tmpQuat4f = new Quat4f();
    DiscreteDynamicsWorld dynamicsWorld;
    // keep track of the shapes, we release memory at exit.
    // make sure to re-use collision shapes among rigid bodies whenever possible!
    ObjectArrayList<CollisionShape> collisionShapes = new ObjectArrayList<>();

    static Vector3f toVector3f(Vector3 v) {
        tmpVector3f.x = v.x;
        tmpVector3f.y = v.y;
        tmpVector3f.z = v.z;
        return tmpVector3f;
    }

    static Quat4f toQuat4f(Quaternion q) {
        tmpQuat4f.x = q.x;
        tmpQuat4f.y = q.y;
        tmpQuat4f.z = q.z;
        tmpQuat4f.w = q.w;
        return tmpQuat4f;
    }

    public static ByteBuffer getVertexBuffer(float[] vertices) {
        ByteBuffer buf = ByteBuffer.allocateDirect(vertices.length * 4).order(ByteOrder.nativeOrder());
        for (int i = 0; i < vertices.length; i++) {
            buf.putFloat(vertices[i]);
        }
        buf.flip();
        return buf;
    }

    public static ByteBuffer getIndexBuffer(short[] indices) {
        ByteBuffer buf = ByteBuffer.allocateDirect(indices.length * 2).order(ByteOrder.nativeOrder());
        for (int i = 0; i < indices.length; i++) {
            buf.putShort(indices[i]);
        }
        buf.flip();
        return buf;
    }

    public void init(float size, Vector3 gravity) {

        // collision configuration contains default setup for memory, collision
        // setup. Advanced users can create their own configuration.
        CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();

        // use the default collision dispatcher. For parallel processing you
        // can use a diffent dispatcher (see Extras/BulletMultiThreaded)
        CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
        // the maximum size of the collision world. Make sure objects stay
        // within these boundaries
        // Don't make the world AABB size too large, it will harm simulation
        // quality and performance
        Vector3f worldAabbMin = new Vector3f(-size, -size, -size);
        Vector3f worldAabbMax = new Vector3f(size, size, size);
        int maxProxies = 1024;
        AxisSweep3 overlappingPairCache = new AxisSweep3(worldAabbMin, worldAabbMax, maxProxies);
        //BroadphaseInterface overlappingPairCache = new SimpleBroadphase(maxProxies);

        // the default constraint solver. For parallel processing you can use a
        // different solver (see Extras/BulletMultiThreaded)
        SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();

        dynamicsWorld = new DiscreteDynamicsWorld(
                dispatcher, overlappingPairCache, solver,
                collisionConfiguration);

        dynamicsWorld.setGravity(toVector3f(gravity));
    }

    public void addTerrain(byte[] data, int gridSize, Vector3 scale) {
        HeightfieldTerrainShape shape = new HeightfieldTerrainShape(gridSize, gridSize,
                data, 255, 1, false, false);
        shape.setLocalScaling(toVector3f(scale));

        collisionShapes.add(shape);
        Transform tr = new Transform();
        tr.setIdentity();
        tr.origin.set(0, 0, 0);

        Vector3 localInertia = new Vector3(0, 0, 0);

        float mass = 0.0f;
        DefaultMotionState myMotionState = new DefaultMotionState(tr);
        RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(mass, myMotionState, shape, toVector3f(localInertia));
        RigidBody body = new RigidBody(rbInfo);

        // add the body to the dynamics world
        dynamicsWorld.addRigidBody(body);

    }

    public void add(Model model, int meshNum, int shapeId, float mass, Vector3 size) {
        CollisionShape shape = null;

        switch (shapeId) {
            case BOX:
                if (size == null) {
                    final Node node = model.scene.modelInstance.nodes.get(meshNum);
                    Globals.tempVector3.set(node.parts.get(0).meshPart.halfExtents);
                    Globals.tempVector3.mulAdd(Globals.tempVector3, model.scale);
                    shape = new BoxShape(toVector3f(Globals.tempVector3));
                } else
                    shape = new BoxShape(toVector3f(size));
                break;

            case SPHERE:
                if (size == null) {
                    final Node node = model.scene.modelInstance.nodes.get(meshNum);
                    shape = new SphereShape(node.parts.get(0).meshPart.radius * model.scale.x);

                    Util.debug("r = " + node.parts.get(0).meshPart.radius);
                } else
                    shape = new SphereShape(size.x); // size.x = radius
                break;

            case CAPSULE:
                shape = new CapsuleShape(size.x, size.y); // radius, height
                break;

            case TRIMESH:
                final Mesh mesh = model.scene.modelInstance.model.meshes.get(meshNum);
                IndexedMesh indexedMesh = new IndexedMesh();
                indexedMesh.numTriangles = mesh.getNumIndices() / 3;
                indexedMesh.numVertices = mesh.getNumVertices();

                float[] vert = new float[mesh.getNumVertices() * mesh.getVertexSize() / 4];
                mesh.getVertices(vert);
                indexedMesh.vertexStride = mesh.getVertexSize();
                //indexedMesh.vertexStride = 12; // 3 verts * 4 bytes each
                indexedMesh.vertexBase = getVertexBuffer(vert);

                indexedMesh.indexType = ScalarType.SHORT;
                indexedMesh.triangleIndexStride = 12; // 3 index entries * 4 bytes each
                short[] ind = new short[mesh.getNumIndices()];
                mesh.getIndices(ind);
                indexedMesh.triangleIndexBase = getIndexBuffer(ind);

                TriangleIndexVertexArray tiv = new TriangleIndexVertexArray();
                tiv.addIndexedMesh(indexedMesh);
                shape = new BvhTriangleMeshShape(tiv, true);
                break;
        }

        shape.setLocalScaling(toVector3f(model.scale));
        collisionShapes.add(shape);

        Transform transform = new Transform();
        transform.setIdentity();

        final Node node = model.scene.modelInstance.nodes.get(meshNum);
        node.localTransform.getTranslation(Globals.tempVector3);
        Globals.tempVector3.add(model.translation);
        transform.origin.set(toVector3f(Globals.tempVector3));
        Util.debug("origin>> " + Globals.tempVector3);

        node.localTransform.getRotation(Globals.tempQuat);
        Globals.tempQuat.add(model.rotation);
        transform.setRotation(toQuat4f(Globals.tempQuat));


        // rigidbody is dynamic if and only if mass is non zero, otherwise static
        boolean isDynamic = (mass != 0f);

        Vector3 localInertia = new Vector3(0, 0, 0);
        if (isDynamic) {
            shape.calculateLocalInertia(mass, toVector3f(localInertia));
        }

        // using motionstate is recommended, it provides interpolation
        // capabilities, and only synchronizes 'active' objects
        DefaultMotionState myMotionState = new DefaultMotionState(transform);
        RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(mass, myMotionState, shape, toVector3f(localInertia));
        RigidBody body = new RigidBody(rbInfo);
        body.setUserPointer(model);

        // add the body to the dynamics world
        dynamicsWorld.addRigidBody(body);

    }

    public void update(float d) {
        //dynamicsWorld.stepSimulation(1.f / 60.f, 5);

        final float delta = Math.min(1f / 60f, d);
        dynamicsWorld.stepSimulation(delta, 5);
        updateNodes();
    }

    public void drawDebug() {
        if (dynamicsWorld != null) {
            dynamicsWorld.debugDrawWorld();
        }
    }

    private void updateNodes() {
        Transform trans = new Transform();
        for (int j = dynamicsWorld.getNumCollisionObjects() - 1; j >= 0; j--) {
            CollisionObject obj = dynamicsWorld.getCollisionObjectArray().getQuick(j);
            RigidBody body = RigidBody.upcast(obj);
            if (body != null && body.getMotionState() != null) {
                Model mdl = (Model) body.getUserPointer();
                if (mdl != null) {
                    body.getMotionState().getWorldTransform(trans);
                    mdl.setPosition(trans.origin.x, trans.origin.y, trans.origin.z);

                    trans.getRotation(tmpQuat4f);
                    Globals.tempQuat.x = tmpQuat4f.x;
                    Globals.tempQuat.y = tmpQuat4f.y;
                    Globals.tempQuat.z = tmpQuat4f.z;
                    Globals.tempQuat.w = tmpQuat4f.w;
                    mdl.rotation.set(Globals.tempQuat);
                }
            }
        }
    }

    public void printDebug() {
        // print positions of all objects
        for (int j = dynamicsWorld.getNumCollisionObjects() - 1; j >= 0; j--) {
            CollisionObject obj = dynamicsWorld.getCollisionObjectArray().getQuick(j);
            RigidBody body = RigidBody.upcast(obj);
            if (body != null && body.getMotionState() != null) {
                Transform trans = new Transform();
                body.getMotionState().getWorldTransform(trans);
                System.out.printf(
                        ((Model) body.getUserPointer()).scene.modelInstance.nodes.get(0).id +
                                "> worldPos = %f,%f,%f\n", trans.origin.x, trans.origin.y, trans.origin.z);
            }
        }
    }

    @Override
    public void dispose() {
        Util.debug("Physics dispose");
        dynamicsWorld.destroy();
    }
}
