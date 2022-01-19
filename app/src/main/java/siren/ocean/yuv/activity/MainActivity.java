package siren.ocean.yuv.activity;

import android.Manifest;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

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
import siren.ocean.yuv.util.SpinnerCreator;
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
    private LinearLayout includeSheetLayout;
    private LinearLayout llGesture;
    private BottomSheetBehavior<LinearLayout> mSheetBehavior;

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
        ivPhoto = findViewById(R.id.iv_photo);
        mCameraView = findViewById(R.id.view_camera);
        includeSheetLayout = findViewById(R.id.include_bottom_sheet);
        mSheetBehavior = BottomSheetBehavior.from(includeSheetLayout);
        mSheetBehavior.setHideable(false);
        llGesture = (LinearLayout) findViewById(R.id.ll_gesture);
        llGesture.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                llGesture.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mSheetBehavior.setPeekHeight(llGesture.getMeasuredHeight());
            }
        });
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
        int position = parameter.getCameraId();
        new SpinnerCreator<Integer>().build(this, R.id.sp_camera_id, Arrays.asList(getCameraIds()), position, value -> {
            if (parameter.getCameraId() == value) return;
            parameter.setCameraId(value);
            updateParameter();
        });
    }

    private void initResolution() {
        int[] data = parameter.getResolution();
        int position = resolutionData.indexOf(data[0] + "X" + data[1]);
        new SpinnerCreator<String>().build(this, R.id.sp_resolution, resolutionData, position, value -> {
            String[] num = value.split("X");
            int[] resolution = new int[]{Integer.parseInt(num[0]), Integer.parseInt(num[1])};
            if (Arrays.equals(parameter.getResolution(), resolution)) return;
            parameter.setResolution(resolution);
            updateParameter();
        });
    }

    private void initOrientation() {
        int position = anglesData.indexOf(parameter.getOrientation());
        new SpinnerCreator<Integer>().build(this, R.id.sp_orientation, anglesData, position, value -> {
            if (parameter.getOrientation() == value) return;
            parameter.setOrientation(value);
            updateParameter();
        });
    }

    private void initRotation() {
        int position = anglesData.indexOf(parameter.getRotation());
        new SpinnerCreator<Integer>().build(this, R.id.sp_rotation, anglesData, position, value -> {
            if (parameter.getRotation() == value) return;
            parameter.setRotation(value);
            updateParameter();
        });
    }

    private void initMirror() {
        int position = mirrorData.indexOf(parameter.isMirror());
        new SpinnerCreator<Boolean>().build(this, R.id.sp_mirror, mirrorData, position, value -> {
            if (parameter.isMirror() == value) return;
            parameter.setMirror(value);
            updateParameter();
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