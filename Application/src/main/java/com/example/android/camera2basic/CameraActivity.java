/*
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.camera2basic;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;

import static com.example.android.camera2basic.Camera1Fragment.PERMISSIONS;

public class CameraActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        boolean legacy = false;
        CameraManager manager = (CameraManager) getBaseContext().getSystemService(Context.CAMERA_SERVICE);
        try {
            if (manager != null) {
                for (String cameraId : manager.getCameraIdList()) {
                    CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                    Integer deviceLevel = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
                    if (deviceLevel != null && deviceLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY) {
                        legacy = true;
                    }
                }
            }
        } catch (CameraAccessException | NullPointerException e) {
            Log.w(CameraActivity.class.getName(), e);
        }

        if (legacy) {
            ViewGroup newLayout = findViewById(R.id.container);
            if (newLayout != null) {

                Camera1Fragment newFragment = new Camera1Fragment();

                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(newLayout.getId(), newFragment,
                        Camera1Fragment.class.getName());
                fragmentTransaction.addToBackStack("old_fragment");
                fragmentTransaction.commit();

            }
        } else {
            ViewGroup newLayout = findViewById(R.id.container);
            if (newLayout != null) {

                Camera2Fragment newFragment = new Camera2Fragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(newLayout.getId(), newFragment,
                        Camera2Fragment.class.getName());
                fragmentTransaction.addToBackStack("old_fragment");
                fragmentTransaction.commit();
            }
        }

    }
}
