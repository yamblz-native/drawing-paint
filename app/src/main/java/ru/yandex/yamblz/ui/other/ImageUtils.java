package ru.yandex.yamblz.ui.other;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import ru.yandex.yamblz.R;
import timber.log.Timber;

/**
 * Created by shmakova on 03.08.16.
 */

public class ImageUtils {
    private static final String ALBUMS_DIR = "Yamblz";
    private Context context;

    public ImageUtils(Context context) {
        this.context = context;
    }

    /**
     * Gets album storage dir
     * @param albumName name of album
     * @return album's dir
     */
    private File getAlbumStorageDir(String albumName) {
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Timber.e("Directory not created");
        }
        return file;
    }

    /**
     * Saves image to file
     * @param bitmap image
     */
    public void saveImageToFile(Bitmap bitmap) {
        File saveImagesDir = getAlbumStorageDir(ALBUMS_DIR);
        String fileName = "img" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        File file = new File(saveImagesDir, fileName);

        try {
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, stream);
            stream.flush();
            stream.close();
            addImageToGallery(file.getAbsolutePath());
            Toast.makeText(context, R.string.saved, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(context, R.string.image_saving_error, Toast.LENGTH_SHORT).show();
            Timber.e(e.getMessage());
        }
    }

    /**
     * Adds image to gallery
     * @param filepath absolute path of image
     * @return uri of image in gallery
     */
    private Uri addImageToGallery(String filepath) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, filepath);

        return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    /**
     * Loads bitmap from Uri
     * @param imageUri uri of image from gallery
     * @return bitmap
     */
    public Bitmap loadBitmapFromUri(Uri imageUri) {
        Bitmap bitmap = null;

        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }
}
