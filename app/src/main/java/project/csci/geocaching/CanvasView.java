package project.csci.geocaching;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

public class CanvasView extends View {

    //Animation constants
    public static final int SPRITE_WIDTH = 160;
    public static final int SPRITE_HEIGHT = 160;

    public static final int TOTAL_WIDTH = 1280;
    public static final int TOTAL_HEIGHT = 320;

    public static final int SPEED = 100;

    private Bitmap spriteSheetBitmap;

    private Handler handler;
    private Runnable animationThread = new Runnable() {
        @Override
        public void run() {
            // delay (calculate based on FPS)
            invalidate();
        }
    };

    private int row = 0;
    private int col = 0;

    public CanvasView(Context ctx) {
        super(ctx);
        prepareAnimation();
    }

    public CanvasView(Context ctx, AttributeSet attr) {
        super(ctx, attr);
        prepareAnimation();
    }

    private void prepareAnimation() {
        spriteSheetBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.globetwo);

        // un-scale the bitmap (compensate for density scaling)
        spriteSheetBitmap = Bitmap.createScaledBitmap(spriteSheetBitmap, TOTAL_WIDTH, TOTAL_HEIGHT, false);

        handler = new Handler();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        // fill the background grey
        canvas.drawRGB(200, 200, 200);

        // draw the current frame
        drawFrame(canvas);

        // update the frame
        nextFrame();

        // schedule the next frame draw
        handler.postDelayed(animationThread, SPEED);
    }

    private void nextFrame() {
        col++;

        if (col >= 8) {
            col = 0;
            row++;

            if (row >= 2) {
                row = 0;
            }
        }
    }

    private void drawFrame(Canvas canvas) {
        int left = col * SPRITE_WIDTH;
        int top = row * SPRITE_HEIGHT;
        Rect sourceRect = new Rect(left, top, left + SPRITE_WIDTH, top + SPRITE_HEIGHT);
        RectF destRect = new RectF(0, 0, SPRITE_WIDTH * 2, SPRITE_HEIGHT * 2);
        canvas.drawBitmap(spriteSheetBitmap, sourceRect, destRect, null);
    }
}