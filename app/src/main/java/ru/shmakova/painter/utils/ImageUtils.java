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

public final class ImageUtils {
    private static final String ALBUM_DIR = "painter";
    private static final int STAMP_SIZE = 24;

    private ImageUtils() {
        // No op
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

    private static File getAlbumStorageDir(String albumName) {
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Timber.e("Directory not created");
        }
        return file;
    }

    public static void saveImageToFile(Context context, Bitmap bitmap) throws IOException {
        File saveImagesDir = getAlbumStorageDir(ALBUM_DIR);
        String fileName = "img" + System.currentTimeMillis() + ".jpg";
        File file = new File(saveImagesDir, fileName);

        try (FileOutputStream stream = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, stream);
            addImageToGallery(context, file.getAbsolutePath());
            Toast.makeText(context, R.string.saved, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(context, R.string.image_saving_error, Toast.LENGTH_SHORT).show();
            Timber.e(e);
        }
    }

    private static Uri addImageToGallery(Context context, String filepath) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, filepath);

        return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    public static Bitmap loadBitmapFromUri(Context context, Uri imageUri) {
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
            Timber.e(e);
        }

        return bitmap;
    }

    public static Bitmap getStampFromDrawable(Context context, int id) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeResource(context.getApplicationContext().getResources(), id, options);

        if (bitmap == null) {
            VectorDrawable vectorDrawable = (VectorDrawable) ContextCompat.getDrawable(context, id);
            return getBitmap(vectorDrawable);
        } else {
            final float density = context.getResources().getDisplayMetrics().density;
            final int stampSize = (int) (STAMP_SIZE * density);
            return Bitmap.createScaledBitmap(bitmap, stampSize, stampSize, false);
        }
    }
}
