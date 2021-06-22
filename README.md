# YuvUtil

该项目依赖于Google的libyuv，实现摄像头nv21流数据的旋转、镜像和缩放功能。
并简单封装了下Camera1的摄像头，实现摄像头Id选取，分辨率选择和方向预览。

---
camera获取到原始nv21数据之后，通过nv21RotateMirror进行处理。


```
/**
 * @param src      源数据
 * @param width    宽度
 * @param height   长度
 * @param rotation 旋转角度
 * @param isMirror 是否镜像
 * @param ratio    缩放值 0~1之间小数
 * @return
 */
public static native byte[] nv21RotateMirror(byte[] src, int width, int height, int rotation, boolean isMirror, float ratio);

```


Nv21预览，需要配置renderscript

```
defaultConfig {
        ...
        renderscriptTargetApi 18
        renderscriptSupportModeEnabled true
        ...
}
```

Nv21转Bitmap方法

```
public static Bitmap nv21ToBitmap(Context context, byte[] data, int width, int height) {
        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicYuvToRGB yuvToRgbIntrinsic = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs));

        Type.Builder yuvType = new Type.Builder(rs, Element.U8(rs)).setX(data.length);
        Allocation in = Allocation.createTyped(rs, yuvType.create(), Allocation.USAGE_SCRIPT);
        Type.Builder rgbaType = new Type.Builder(rs, Element.RGBA_8888(rs)).setX(width).setY(height);
        Allocation out = Allocation.createTyped(rs, rgbaType.create(), Allocation.USAGE_SCRIPT);

        in.copyFrom(data);
        yuvToRgbIntrinsic.setInput(in);
        yuvToRgbIntrinsic.forEach(out);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        out.copyTo(bitmap);
        return bitmap;
    }
```

### 参考
https://github.com/hzl123456/LibyuvDemo
