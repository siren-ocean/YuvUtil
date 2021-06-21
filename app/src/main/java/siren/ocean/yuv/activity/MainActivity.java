package siren.ocean.yuv.activity;

import android.Manifest;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import pub.devrel.easypermissions.EasyPermissions;
import siren.ocean.yuv.R;
import siren.ocean.yuv.YuvUtil;
import siren.ocean.yuv.entity.CameraParameter;
import siren.ocean.yuv.util.PhotoUtils;
import siren.ocean.yuv.util.PreferencesUtility;
import siren.ocean.yuv.util.ThreadUtil;
import siren.ocean.yuv.widget.CameraView;

/**
 * 主页
 * Created by Siren on 2021/6/17.
 */
public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private CameraView mCameraView;
    private ImageView ivPhoto;
    private final List<String> resolutionData = new ArrayList<>(Arrays.asList("640X480", "1280X720", "1280X960"));
    private final List<Integer> anglesData = new ArrayList<>(Arrays.asList(0, 90, 180, 270));
    private final List<Boolean> mirrorData = new ArrayList<>(Arrays.asList(true, false));
    private final CameraParameter parameter = PreferencesUtility.getCameraParameter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initPreview();
        initCameraId();
        initResolution();
        initOrientation();
        initRotation();
        initMirror();
        requestPermission();
    }

    private void initView() {
        mCameraView = findViewById(R.id.view_camera);
        ivPhoto = findViewById(R.id.iv_photo);
    }

    private void initPreview() {
        mCameraView.setParameter(parameter.getCameraId(), parameter.getResolution(), parameter.getOrientation());
        mCameraView.setPreviewCallback((data, camera) -> {
            mCameraView.addCallbackBuffer();
            byte[] dstData = YuvUtil.nv21RotateMirror(data, parameter.getResolution()[0], parameter.getResolution()[1], parameter.getRotation(), parameter.isMirror(), 1);
            int w, h;
            //如果流数据做了直角旋转，则必然导致宽高互换
            if (parameter.getRotation() == 90 || parameter.getRotation() == 270) {
                w = mCameraView.mPreviewHeight;
                h = mCameraView.mPreviewWidth;
            } else {
                w = mCameraView.mPreviewWidth;
                h = mCameraView.mPreviewHeight;
            }
            runOnUiThread(() -> ivPhoto.setImageBitmap(PhotoUtils.nv21ToBitmap(this, dstData, w, h)));
        });
    }

    private void initCameraId() {
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, android.R.id.text1, getCameraIds());
        Spinner spinner = findViewById(R.id.sp_camera_id);
        spinner.setAdapter(adapter);
        spinner.setSelection(parameter.getCameraId());
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                int cameraId = adapter.getItem(position);
                if (parameter.getCameraId() == cameraId) return;
                parameter.setCameraId(cameraId);
                updateParameter();
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void initResolution() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, android.R.id.text1, resolutionData);
        Spinner spinner = findViewById(R.id.sp_resolution);
        spinner.setAdapter(adapter);
        int[] data = parameter.getResolution();
        spinner.setSelection(resolutionData.indexOf(data[0] + "X" + data[1]));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String[] data = adapter.getItem(position).split("X");
                int[] resolution = new int[]{Integer.parseInt(data[0]), Integer.parseInt(data[1])};
                if (Arrays.equals(parameter.getResolution(), resolution)) return;
                parameter.setResolution(resolution);
                updateParameter();
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void initOrientation() {
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, android.R.id.text1, anglesData);
        Spinner spinner = findViewById(R.id.sp_orientation);
        spinner.setAdapter(adapter);
        spinner.setSelection(anglesData.indexOf(parameter.getOrientation()));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                int orientation = adapter.getItem(position);
                if (parameter.getOrientation() == orientation) return;
                parameter.setOrientation(adapter.getItem(position));
                updateParameter();
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void initRotation() {
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, android.R.id.text1, anglesData);
        Spinner spinner = findViewById(R.id.sp_rotation);
        spinner.setAdapter(adapter);
        spinner.setSelection(anglesData.indexOf(parameter.getRotation()));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                int rotation = adapter.getItem(position);
                if (parameter.getRotation() == rotation) return;
                parameter.setRotation(adapter.getItem(position));
                updateParameter();
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void initMirror() {
        ArrayAdapter<Boolean> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, android.R.id.text1, mirrorData);
        Spinner spinner = findViewById(R.id.sp_mirror);
        spinner.setAdapter(adapter);
        spinner.setSelection(mirrorData.indexOf(parameter.isMirror()));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                boolean isMirror = adapter.getItem(position);
                if (parameter.isMirror() == isMirror) return;
                parameter.setMirror(isMirror);
                updateParameter();
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void requestPermission() {
        String[] perms = {Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this, perms)) {
            ThreadUtil.runOnMainThreadDelayed(() -> mCameraView.openCamera(), 300);
        } else {
            EasyPermissions.requestPermissions(this, "The app must have the permission of cameras", 0, perms);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        mCameraView.openCamera();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onResume() {
        mCameraView.openCamera();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mCameraView.releaseCamera();
        super.onPause();
    }

    private void updateParameter() {
        PreferencesUtility.setCameraParameter(parameter);
        mCameraView.setParameter(parameter.getCameraId(), parameter.getResolution(), parameter.getOrientation());
    }

    public Integer[] getCameraIds() {
        int number = Camera.getNumberOfCameras();
        if (number > 0) {
            Integer[] data = new Integer[number];
            for (int i = 0; i < number; i++) {
                data[i] = i;
            }
            return data;
        }
        return new Integer[1];
    }
}