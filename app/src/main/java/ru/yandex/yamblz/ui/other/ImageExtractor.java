package ru.yandex.yamblz.ui.other;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by dSukmanova on 07.08.16.
 */

public class ImageExtractor {
    File extStorageDirectory = Environment.getExternalStorageDirectory();
    String fileName = "myPrettyImage";
    String imageSaved = "Image saved!";
    String imageLoaded = "Image loaded!";
    String errorMessage = "Error occurred!";

    public File saveBitmap(Bitmap bitmap) {
        OutputStream outStream = null;
        File file = createNewFile();
        if (file.exists()) {
            file.delete();
            file = createNewFile();
        }
        try {
            outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } finally {
            try {
                outStream.flush();
                outStream.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return file;

    }

    private File createNewFile() {
        return new File(extStorageDirectory.toString() + "/" + fileName + ".png");
    }

    public Bitmap loadBitmap() {
        Bitmap bitmap = null;
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(createNewFile());
            bitmap = BitmapFactory.decodeStream(fileInputStream);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return bitmap;
    }

    public String getImageLoaded() {
        return imageLoaded;
    }

    public String getImageSaved() {
        return imageSaved;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

}
