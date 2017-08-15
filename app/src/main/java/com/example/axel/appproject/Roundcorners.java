package com.example.axel.appproject;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by Viktor on 2015-05-05.
 */
public class Roundcorners {

    Bitmap output;
    Canvas canvas;

    public Roundcorners(Bitmap bitmap) {
        output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect1 = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect1);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);

        float width = (float) bitmap.getWidth();
        float height = (float) bitmap.getHeight();
        float radius;

        if (width > height){
            radius = height/2;
        } else {
            radius = width/2;
        }

        final RectF rect = new RectF();
        float center_x, center_y;

        center_x = width;
        center_y = height;

        rect.set(0,0,center_x,center_y);

        canvas.drawRoundRect(rect, 100, 100, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, 0, 0, paint);
    }

    public Bitmap getRoundBitmap() {
        return output;
    }
}
