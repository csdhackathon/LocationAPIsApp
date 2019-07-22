package com.pb.locationapis.tilesource;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.DisplayMetrics;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.CopyrightOverlay;
import org.osmdroid.views.overlay.Overlay;

/**
 * Class used to Create the Map Attributions. This class will add the Pitney Bowes Logo
 * as well as @OpenStreetMap attribution on top of Map.
 * @link https://locate.pitneybowes.com/docs/location-intelligence/v1/en/index.html#Appendix/appendix_o_mapattribution.html#appendixj
 */

public class PBGeoMapCopyrightOverlay extends Overlay {
    private Paint paint;
    int xOffset = 10;
    int yOffset = 10;
    protected boolean alignBottom = true;
    protected boolean alignRight = true;
    final DisplayMetrics dm;
    private String copyRight=null;
    private Bitmap logo=null;
    // Constructor

    public PBGeoMapCopyrightOverlay(Context context) {
        super();

        // Get the string
        Resources resources = context.getResources();

        // Get the display metrics
        dm = resources.getDisplayMetrics();

        // Get paint
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(dm.density * 12);
    }

    public void setCopyRightText(String copyright){this.copyRight=copyright;}

    public void setLogo(Bitmap _logo){this.logo=_logo;}

    public void setTextSize(int fontSize) {
        paint.setTextSize(dm.density * fontSize);
    }

    public void setTextColor(int color) {
        paint.setColor(color);
    }
    // Set alignBottom

    /**
     * Sets the screen offset. Values are in real pixels, not dip
     *
     * @param x horizontal screen offset, if aligh right is set, the offset is from the right, otherwise lift
     * @param y vertical screen offset, if align bottom is set, the offset is pixels from the bottom (not the top)
     */
    public void setOffset(final int x, final int y) {
        xOffset = x;
        yOffset = y;
    }

    @Override
    public void draw(Canvas canvas, MapView map, boolean shadow) {
        if (shadow) return;
        if (map.isAnimating()) {
            return;
        }

        if (copyRight == null ||  copyRight.length() == 0)
            return;

        int width = canvas.getWidth();
        int height = canvas.getHeight();

        float x = 0;
        float y = 0;

        if (alignRight) {
            x = width - xOffset;
            paint.setTextAlign(Paint.Align.RIGHT);
        } else {
            x = xOffset;
            paint.setTextAlign(Paint.Align.LEFT);
        }

        if (alignBottom)
            y = height - yOffset;
        else
            y = paint.getTextSize() + yOffset;

        // Draw the text
        canvas.save();
        canvas.concat(map.getProjection().getInvertedScaleRotateCanvasMatrix());
        canvas.drawText(copyRight, x, y, paint);

        x = xOffset;
        y=height - 50;
        paint.setTextAlign(Paint.Align.LEFT);

        canvas.drawBitmap(this.logo,x,y,paint);

        canvas.restore();
    }

}
