package com.example.facerecognitionimages;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.facerecognitionimages.face_recognition.FaceClassifier;
import com.example.facerecognitionimages.face_recognition.TFLiteFaceRecognition;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import android.graphics.RenderEffect;
import android.graphics.Shader;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
//import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import fi.iki.elonen.NanoHTTPD;


public class RecognitionActivity extends AppCompatActivity {

    CardView galleryCard,cameraCard;
    ImageView imageView;
    Uri image_uri;

    public static final int PERMISSION_CODE = 100;
    //private ImageView imageView;
    private TextView tvResult;

    private final String esp32Ip = "http://172.20.10.5"; // IP จริงของ ESP32

    private Bitmap latestBitmap;
    private String latestImageName;

    private final Handler handler = new Handler();


    private final int intervalMs = 60000; // 1 นาที

    //TODO declare face detector
// High-accuracy landmark detection and face classification


    //TODO declare face recognizer



    //Temporary
    //TODO get the image from gallery and display it
    ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        image_uri = result.getData().getData();
                        Bitmap inputImage = uriToBitmap(image_uri);
                        Bitmap rotated = rotateBitmap(inputImage);
                        imageView.setImageBitmap(rotated);
                        performFaceDetection(rotated);
                    }
                }
            });

    //TODO capture the image using camera and display it
//    ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            new ActivityResultCallback<ActivityResult>() {
//                @Override
//                public void onActivityResult(ActivityResult result) {
//                    if (result.getResultCode() == Activity.RESULT_OK) {
//                        Bitmap inputImage = uriToBitmap(image_uri);
//                        Bitmap rotated = rotateBitmap(inputImage);
//                        imageView.setImageBitmap(rotated);
//                        performFaceDetection(rotated);
//                    }
//                }
//            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognition);

        imageView = findViewById(R.id.imageView2);
        tvResult = findViewById(R.id.tvResult);


        //TODO handling permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED){
                String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permission, PERMISSION_CODE);
            }
        }
//        //TODO initialize views
        //Temporary
        galleryCard = findViewById(R.id.gallerycard);
//        cameraCard = findViewById(R.id.cameracard);
//        imageView = findViewById(R.id.imageView2);

        //Temporary
        //TODO code for choosing images from gallery
        galleryCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                galleryActivityResultLauncher.launch(galleryIntent);
            }
        });
//
//        //TODO code for capturing images using camera
//        cameraCard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//                    if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                            == PackageManager.PERMISSION_DENIED){
//                        String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
//                        requestPermissions(permission, PERMISSION_CODE);
//                    }
//                    else {
//                        openCamera();
//                    }
//                }
//
//                else {
//                    openCamera();
//                }
//            }
//        });

        //TODO initialize face detector


        //TODO initialize face recognition model


    }

//    //TODO opens camera so that user can capture image
//    private void openCamera() {
//        ContentValues values = new ContentValues();
//        values.put(MediaStore.Images.Media.TITLE, "New Picture");
//        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
//        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
//        cameraActivityResultLauncher.launch(cameraIntent);
//    }

    //Temporary
    //TODO takes URI of the image and returns bitmap
    private Bitmap uriToBitmap(Uri selectedFileUri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    getContentResolver().openFileDescriptor(selectedFileUri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);

            parcelFileDescriptor.close();
            return image;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  null;
    }

    //TODO rotate image if image captured on samsung devices
    //TODO Most phone cameras are landscape, meaning if you take the photo in portrait, the resulting photos will be rotated 90 degrees.
    @SuppressLint("Range")
    public Bitmap rotateBitmap(Bitmap input){
        String[] orientationColumn = {MediaStore.Images.Media.ORIENTATION};
        Cursor cur = getContentResolver().query(image_uri, orientationColumn, null, null, null);
        int orientation = -1;
        if (cur != null && cur.moveToFirst()) {
            orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]));
        }
        Log.d("tryOrientation",orientation+"");
        Matrix rotationMatrix = new Matrix();
        rotationMatrix.setRotate(orientation);
        Bitmap cropped = Bitmap.createBitmap(input,0,0, input.getWidth(), input.getHeight(), rotationMatrix, true);
        return cropped;
    }

//    // Blur faces
//    private void blurFaceRegion(Bitmap bitmap, Rect faceBounds) {
//        // แปลง Bitmap เป็น Mat
//        Mat imgMat = new Mat();
//        Utils.bitmapToMat(bitmap, imgMat);
//
//        // จำกัดขอบเขตให้อยู่ในภาพ
//        int left = Math.max(faceBounds.left, 0);
//        int top = Math.max(faceBounds.top, 0);
//        int right = Math.min(faceBounds.right, bitmap.getWidth());
//        int bottom = Math.min(faceBounds.bottom, bitmap.getHeight());
//
//        org.opencv.core.Rect roi = new org.opencv.core.Rect(left, top, right - left, bottom - top);
//
//        // คัดเฉพาะส่วนใบหน้ามา blur
//        Mat faceROI = imgMat.submat(roi);
//        Imgproc.GaussianBlur(faceROI, faceROI, new Size(195, 195), 0);
//
//        // กลับมาเป็น Bitmap
//        Utils.matToBitmap(imgMat, bitmap);
//    }

    //TODO perform face detection



    //TODO perform face recognition



    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
