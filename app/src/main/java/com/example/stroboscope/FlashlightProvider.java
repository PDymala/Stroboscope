package com.example.stroboscope;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class FlashlightProvider {

    private static final String TAG = FlashlightProvider.class.getSimpleName();
    private static Camera mCamera;
    private Camera.Parameters parameters;
    private CameraManager camManager;
    private Context context;

    public FlashlightProvider(Context context) {
        this.context = context;
    }


    public boolean hasFlash() {
        if (mCamera == null) {
            return false;
        }

        Camera.Parameters parameters;
        try {
            parameters = mCamera.getParameters();
        } catch (RuntimeException ignored)  {
            return false;
        }

        if (parameters.getFlashMode() == null) {
            return false;
        }

        List<String> supportedFlashModes = parameters.getSupportedFlashModes();
        if (supportedFlashModes == null || supportedFlashModes.isEmpty() || supportedFlashModes.size() == 1 && supportedFlashModes.get(0).equals(Camera.Parameters.FLASH_MODE_OFF)) {
            return false;
        }

        return true;
    }





    public void turnFlashlightOn() {


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {
                    camManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
                    String cameraId = null;
                    if (camManager != null) {
                        cameraId = camManager.getCameraIdList()[0];
                        camManager.setTorchMode(cameraId, true);
                    }
                } catch (CameraAccessException e) {
                    Log.e(TAG, e.toString());
                }
            } else {
                mCamera = Camera.open();
                parameters = mCamera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                mCamera.setParameters(parameters);
                mCamera.startPreview();
            }




    }

    public void turnFlashlightOff() {





        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                String cameraId;
                camManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
                if (camManager != null) {
                    cameraId = camManager.getCameraIdList()[0]; // Usually front camera is at 0 position.
                    camManager.setTorchMode(cameraId, false);
                }
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        } else {
            mCamera = Camera.open();
            parameters = mCamera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(parameters);
            mCamera.stopPreview();
        }


        }
    }
