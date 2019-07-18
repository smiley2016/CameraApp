package com.example.android.camera2basic;

import android.Manifest;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Camera1Fragment extends Fragment implements View.OnClickListener, TextureView.SurfaceTextureListener, Camera.AutoFocusCallback, Camera.PictureCallback {

    private static final String TAG = CameraActivity.class.getSimpleName();

    private TextureView mTextureView;

    private Camera mCamera;

    private boolean cameraFront = true;

    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    public static final String KEY_IMAGE = "image";

    protected static final int MIN_WIDTH = 512;

    private ImageButton preview;
    private File mFile;

    public static String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_camera2_basic, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mTextureView = view.findViewById(R.id.texture);
        view.findViewById(R.id.picture).setOnClickListener(this);
        preview = view.findViewById(R.id.preview);

        ActivityCompat.requestPermissions(getActivity(),PERMISSIONS , MY_CAMERA_PERMISSION_CODE);
    }

    @Override
    public void onAutoFocus(boolean b, Camera camera) {
        camera.takePicture(null, null, this);
    }

    @Override
    public void onPictureTaken(byte[] bytes, Camera camera){
        FileOutputStream fos = null;
        try {
            mFile = new File(getActivity().getExternalFilesDir(null), "pic.jpg");
            fos = new FileOutputStream(mFile);
            fos.write(bytes);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        int rotation = 0;
        switch (display.getRotation()) {
            case Surface.ROTATION_0: // This is display orientation
                rotation = 90;
                break;
            case Surface.ROTATION_90:
                rotation = 0;
                break;
            case Surface.ROTATION_180:
                rotation = 270;
                break;
            case Surface.ROTATION_270:
                rotation = 180;
                break;
        }
        Glide.with(getActivity()).load(mFile).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(preview).getView().setRotation(rotation);

    }

    @Override
    public void onResume() {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        int rotation = 0;
        switch (display.getRotation()) {
            case Surface.ROTATION_0: // This is display orientation
                rotation = 90;
                break;
            case Surface.ROTATION_90:
                rotation = 0;
                break;
            case Surface.ROTATION_180:
                rotation = 270;
                break;
            case Surface.ROTATION_270:
                rotation = 180;
                break;
        }
        mCamera = Camera.open();
        mCamera.setDisplayOrientation(rotation);
        if (mTextureView.isAvailable()) {
            onSurfaceTextureAvailable(mTextureView.getSurfaceTexture(), 0, 0);
        } else {
            mTextureView.setSurfaceTextureListener(this);
        }
        super.onResume();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        try {
            Camera.Parameters parameters = mCamera.getParameters();

            Camera.Size bestSize = parameters.getPictureSize();
            for (Camera.Size size : parameters.getSupportedPictureSizes()) {
                int min = Math.min(size.height, size.width);
                if (min >= MIN_WIDTH && min < Math.min(bestSize.height, bestSize.width)) {
                    bestSize = size;
                }
            }
            parameters.setPictureSize(bestSize.width, bestSize.height);

            if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }

            mCamera.setParameters(parameters);

            mCamera.setPreviewTexture(surfaceTexture);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.w(TAG, e);
        }
 }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }

    @Override
    public void onClick(View view) {
        mCamera.autoFocus(this);
    }
}
