package pv256.fi.muni.cz.moviotk.uco409735.helpers;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.squareup.picasso.Transformation;

/**
 * Class that is used to make images circular.
 */
public class AvatarTransformation implements Transformation {

    private static final String AVATAR_KEY = "avatarTransformKey";

    @Override
    public Bitmap transform(Bitmap source) {

        int size = Math.min(source.getWidth(), source.getHeight());

        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap != source) {
            source.recycle();
        }

        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);

        float radius = size / 2f - 1;
        canvas.drawCircle(radius, radius, radius, paint);

        squaredBitmap.recycle();
        return bitmap;
    }

    @Override
    public String key() {
        return AVATAR_KEY;
    }
}
