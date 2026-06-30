package es.epycus.app.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;

/**
 * Recorte "superior": escala la imagen para llenar el ancho del destino y la alinea arriba,
 * recortando la parte inferior. Para los personajes de cuerpo entero deja visible la cabeza
 * y el cuello (en vez de {@code centerCrop}, que muestra el torso).
 */
public class TopCropTransformation extends BitmapTransformation {

    private static final String ID = "es.epycus.app.util.TopCropTransformation.v1";
    private static final byte[] ID_BYTES = ID.getBytes(CHARSET);

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform,
                               int outWidth, int outHeight) {
        if (toTransform.getWidth() == outWidth && toTransform.getHeight() == outHeight) {
            return toTransform;
        }

        // Escala para cubrir todo el destino (la mayor de las dos escalas).
        float scaleX = (float) outWidth / toTransform.getWidth();
        float scaleY = (float) outHeight / toTransform.getHeight();
        float scale = Math.max(scaleX, scaleY);

        float scaledWidth = toTransform.getWidth() * scale;
        // Centrado horizontal, alineado arriba (dy = 0) -> conserva la cabeza.
        float dx = (outWidth - scaledWidth) * 0.5f;

        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        matrix.postTranslate(dx, 0f);

        Bitmap.Config config = toTransform.getConfig() != null
                ? toTransform.getConfig() : Bitmap.Config.ARGB_8888;
        Bitmap result = pool.get(outWidth, outHeight, config);
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(toTransform, matrix, new Paint(Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG));
        return result;
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update(ID_BYTES);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof TopCropTransformation;
    }

    @Override
    public int hashCode() {
        return ID.hashCode();
    }
}
