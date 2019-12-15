package com.example.beeon;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.nfc.FormatException;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.widget.Toast;

import com.example.beeon.qrScan.BarcodeReader;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import org.greenrobot.eventbus.EventBus;

public class QrScanActivity extends AppCompatActivity implements BarcodeReader.BarcodeReaderListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scan);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            // Let's read picked image data - its URI
            Uri pickedImage = data.getData();
            // Let's read picked image path using content resolver
            String[] filePath = {MediaStore.Images.Media.DATA};


            finish();

            Cursor cursor = getContentResolver().query(pickedImage, filePath, null, null, null);
            if (null != cursor ) {
                cursor.moveToFirst();
                String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bMap = BitmapFactory.decodeFile(imagePath, options);


                String contents = "";

                int[] intArray = new int[bMap.getWidth() * bMap.getHeight()];
                bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());

                LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

                MultiFormatReader reader = new MultiFormatReader();
                Result result = null;
                try {
                    result = reader.decode(bitmap);
                } catch (Resources.NotFoundException | NotFoundException e) {
                    e.printStackTrace();
                }

                if (null != result && null != result.getText()) {
                    contents = result.getText();
                }
                scannedText(contents);
                cursor.close();
            }

        }

    }



    private void scannedText(String contents) {
        //send data and finish
        Toast.makeText(this,"Navbat olindi iltimos kuting",Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onScanned(Barcode barcode) {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().post(new MessageEvent());

    }

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {
        finish();
    }

    @Override
    public void onScanError(String errorMessage) {

    }

    @Override
    public void onCameraPermissionDenied() {

    }
}
