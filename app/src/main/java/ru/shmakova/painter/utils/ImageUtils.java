package ru.shmakova.painter.utils;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import ru.shmakova.painter.R;
import timber.log.Timber;

/**
 * Created by shmakova on 03.08.16.
 */

public class ImageUtils {
    private static final String ALBUM_DIR = "Yamblz";
    private static final int STAMP_SIZE = 24;
    private Context context;

    public ImageUtils(Context context) {
        this.context = context;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static Bitmap getBitmap(VectorDrawable vectorDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }

    /**
     * Gets album storage dir
     *
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
     *
     * @param bitmap image
     */
    public void saveImageToFile(Bitmap bitmap) {
        File saveImagesDir = getAlbumStorageDir(ALBUM_DIR);
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
     *
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
     *
     * @param imageUri uri of image from gallery
     * @return bitmap
     */
    public Bitmap loadBitmapFromUri(Uri imageUri) {
        Bitmap bitmap = null;

        try {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            final int width = size.x;
            Bitmap rawBitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
            bitmap = Bitmap.createScaledBitmap(rawBitmap, width, width * rawBitmap.getHeight() / rawBitmap.getWidth(), false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    /**
     * Gets stamp from drawable
     *
     * @param id resource id
     * @return bitmap
     */
    public Bitmap getStampFromDrawable(int id) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeResource(context.getApplicationContext().getResources(), id, options);

        if (bitmap != null) {
            final float density = context.getResources().getDisplayMetrics().density;
            final int stampSize = (int) (STAMP_SIZE * density);
            return Bitmap.createScaledBitmap(bitmap, stampSize, stampSize, false);
        } else {
            VectorDrawable vectorDrawable = (VectorDrawable) ContextCompat.getDrawable(context, id);
            return getBitmap(vectorDrawable);
        }
    }
}
