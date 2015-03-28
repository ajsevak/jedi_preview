package com.example.jz5k21.myapplication;

import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.view.SurfaceHolder;
import android.widget.ImageView;

import com.googlecode.javacv.cpp.opencv_core;
import com.googlecode.javacv.cpp.opencv_core.*;
import com.googlecode.javacv.*;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.IOException;



import static org.opencv.imgproc.Imgproc.cvtColor;

public class CameraPreview implements SurfaceHolder.Callback, Camera.PreviewCallback
{
    private Camera mCamera = null;
    private ImageView MyCameraPreview = null;
    private Bitmap bitmap = null;
    private int[] pixels = null;
    private byte[] frame = null;
    private int imageFormat;
    private int PreviewSizeWidth;
    private int PreviewSizeHeight;
    private boolean bProcessing = true;

    Handler mHandler = new Handler(Looper.getMainLooper());

    // TODO: complete function, use this link as a guide:
    // http://softwarependula.blogspot.com/2013/03/face-and-hand-detection-using-javacv.html
  /*
  private static void findAndMarkObjects(
    CvHaarClassifierCascade classifier,
    CvMemStorage storage,
    CvScalar colour,
    Mat inImage,
    Mat outImage) {
    CvSeq faces = cvHaarDetectObjects(inImage, classifier,
      storage, 1.1, 3, CV_HAAR_DO_CANNY_PRUNING);
    int totalFaces = faces.total();
    for (int i = 0; i < totalFaces; i++) {
      CvRect r = new CvRect(cvGetSeqElem(faces, i));
      int x = r.x(), y = r.y(), w = r.width(), h = r.height();
      cvRectangle(outImage, cvPoint(x, y), cvPoint(x + w, y + h),
        colour, 1, CV_AA, 0);
    }
  }
  */

    private Runnable DoImageProcessing = new Runnable()
    {
        public void run()
        {
//            bProcessing = true;
//
//            // create a grayscale and an rgba opencv image
//            Mat mgray = new Mat(PreviewSizeHeight, PreviewSizeWidth, CvType.CV_8UC1);
//            Mat mrgba = new Mat(PreviewSizeHeight + PreviewSizeHeight/2, PreviewSizeWidth, CvType.CV_8UC1);
//            mgray.put(0, 0, frame);
//            mrgba.put(0, 0, frame);
//            // colorspace conversion from yuv420 nv21 to rgba
//            cvtColor(mrgba, mrgba, 96);
//
//            // detect faces w/ grayscale img, draw rects to rgba img
//            // TODO: render rects over current frame
//            opencv_core.CvMemStorage storage = opencv_core.CvMemStorage.create();
//
//            // TODO: uncomment when function is complete
//            //findAndMarkObjects(faceClassifier, faceStorage, CvScalar.GREEN, mgray, mrgba);
//
//            //MyCameraPreview.setImageBitmap(bitmap);
//            bProcessing = false;
        }
    };

    public CameraPreview(int PreviewlayoutWidth, int PreviewlayoutHeight,
                         ImageView CameraPreview)
    {
        PreviewSizeWidth = PreviewlayoutWidth;
        PreviewSizeHeight = PreviewlayoutHeight;
        MyCameraPreview = CameraPreview;
        bitmap = Bitmap.createBitmap(PreviewSizeWidth, PreviewSizeHeight, Bitmap.Config.ARGB_8888);
        pixels = new int[PreviewSizeWidth * PreviewSizeHeight];
    }

    @Override
    public void onPreviewFrame(byte[] arg0, Camera arg1)
    {
        // At preview mode, the frame data will push to here.
        if (imageFormat == ImageFormat.NV21)
        {
            //We only accept the NV21(YUV420) format.
            if (!bProcessing)
            {
                frame = arg0;
                mHandler.post(DoImageProcessing);
            }
        }
    }

    public void onPause()
    {
        mCamera.stopPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3)
    {
        Camera.Parameters parameters;

        parameters = mCamera.getParameters();
        // Set the camera preview size
        parameters.setPreviewSize(PreviewSizeWidth, PreviewSizeHeight);

        imageFormat = parameters.getPreviewFormat();

        mCamera.setParameters(parameters);

        mCamera.startPreview();
    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0)
    {
        mCamera = Camera.open();
        try
        {
            // If did not set the SurfaceHolder, the preview area will be black.
            mCamera.setPreviewDisplay(arg0);
            mCamera.setPreviewCallback(this);
        }
        catch (IOException e)
        {
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0)
    {
        mCamera.setPreviewCallback(null);
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }
}