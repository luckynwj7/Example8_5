package com.example.arcore.chapter8.example8_5;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.google.ar.core.Frame;
import com.google.ar.core.Plane;
import com.google.ar.core.PointCloud;
import com.google.ar.core.Session;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import de.javagl.obj.ObjData;

public class MainRenderer implements GLSurfaceView.Renderer {
    private static final String TAG = MainRenderer.class.getSimpleName();

    private boolean mViewportChanged;
    private int mViewportWidth;
    private int mViewportHeight;

    private CameraRenderer mCamera;
    private PointCloudRenderer mPointCloud;
    private PlaneRenderer mPlane;

    private ObjRenderer mTable;
    private ObjRenderer mChair;
    private ObjRenderer mBed;

    private boolean mIsDrawTable = false;
    private boolean mIsDrawChair = false;
    private boolean mIsDrawBed = false;

    private RenderCallback mRenderCallback;

    public interface RenderCallback {
        void preRender();
    }

    public MainRenderer(Context context, RenderCallback callback) {
        mCamera = new CameraRenderer();

        mPointCloud = new PointCloudRenderer();

        mPlane = new PlaneRenderer(Color.GRAY, 0.5f);

        mTable = new ObjRenderer(context, "table.obj", "table.jpg");
        mChair = new ObjRenderer(context, "chair.obj", "chair.jpg");
        mBed = new ObjRenderer(context, "bed.obj", "bed.jpg");

        mRenderCallback = callback;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glClearColor(1.0f, 1.0f, 0.0f, 1.0f);

        mCamera.init();

        mPointCloud.init();

        mPlane.init();

        mTable.init();
        mChair.init();
        mBed.init();
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        mViewportChanged = true;
        mViewportWidth = width;
        mViewportHeight = height;
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        mRenderCallback.preRender();

        GLES20.glDepthMask(false);
        mCamera.draw();
        GLES20.glDepthMask(true);

        mPointCloud.draw();

        mPlane.draw();

        if (mIsDrawTable) {
            mTable.draw();
        }
        if (mIsDrawChair) {
            mChair.draw();
        }
        if (mIsDrawBed) {
            mBed.draw();
        }
    }

    public int getTextureId() {
        return mCamera == null ? -1 : mCamera.getTextureId();
    }

    public void onDisplayChanged() {
        mViewportChanged = true;
    }

    public boolean isViewportChanged() {
        return mViewportChanged;
    }

    public int getWidth() {
        return mViewportWidth;
    }

    public int getHeight() {
        return mViewportHeight;
    }

    public void updateSession(Session session, int displayRotation) {
        if (mViewportChanged) {
            session.setDisplayGeometry(displayRotation, mViewportWidth, mViewportHeight);
            mViewportChanged = false;
        }
    }

    public void transformDisplayGeometry(Frame frame) {
        mCamera.transformDisplayGeometry(frame);
    }

    public void updatePointCloud(PointCloud pointCloud) {
        mPointCloud.update(pointCloud);
    }

    public void updatePlane(Plane plane) {
        mPlane.update(plane);
    }

    public void setTableModelMatrix(float[] matrix) {
        mTable.setModelMatrix(matrix);
    }

    public void setChairModelMatrix(float[] matrix) {
        mChair.setModelMatrix(matrix);
    }

    public void setBedModelMatrix(float[] matrix) {
        mBed.setModelMatrix(matrix);
    }

    public void setProjectionMatrix(float[] matrix) {
        mPointCloud.setProjectionMatrix(matrix);
        mPlane.setProjectionMatrix(matrix);
        mTable.setProjectionMatrix(matrix);
        mChair.setProjectionMatrix(matrix);
        mBed.setProjectionMatrix(matrix);
    }

    public void updateViewMatrix(float[] matrix) {
        mPointCloud.setViewMatrix(matrix);
        mPlane.setViewMatrix(matrix);
        mTable.setViewMatrix(matrix);
        mChair.setViewMatrix(matrix);
        mBed.setViewMatrix(matrix);
    }

    public void updateTableViewMatrix(float[] matrix) {
        mTable.setViewMatrix(matrix);
    }

    public void updateChairViewMatrix(float[] matrix) {
        mChair.setViewMatrix(matrix);
    }

    public void updateBedViewMatrix(float[] matrix) {
        mBed.setViewMatrix(matrix);
    }

    public void setModelDraw(boolean table, boolean chair, boolean bed) {
        mIsDrawTable = table;
        mIsDrawChair = chair;
        mIsDrawBed = bed;
    }
}
