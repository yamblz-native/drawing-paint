package ru.yandex.yamblz;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Aleksandra on 02/08/16.
 */
public class FileManager {
    public static final String DEBUG_TAG = FileManager.class.getName();

    public boolean saveBitmapToExternalStorage(Context context, Bitmap bitmap, Uri uri) {
        OutputStream outputStream = null;
        try {
            outputStream = context.getContentResolver().openOutputStream(uri);
            if (outputStream != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }


    public Bitmap loadBitmapFromExternalStorage(Context context, Uri uri) {
        Log.d(DEBUG_TAG, "In loadBitmap");
        ParcelFileDescriptor parcelFileDescriptor = null;
        Bitmap image = null;
        try {
            parcelFileDescriptor = context.getContentResolver().openFileDescriptor(uri, "rw");
            FileDescriptor fileDescriptor = null;
            if (parcelFileDescriptor != null) {
                fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                Log.d(DEBUG_TAG, " IsMutable = " + image.isMutable());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (parcelFileDescriptor != null) {
                    parcelFileDescriptor.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return image;
    }

}
