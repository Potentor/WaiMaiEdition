package com.mobileinternet.waimai.businessedition.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class MClipView extends View
{
    private int borderHeight;
    private int borderWidth;
    private Paint mPaint = new Paint();

    public MClipView(Context paramContext)
    {
        this(paramContext, null);
    }

    public MClipView(Context paramContext, AttributeSet paramAttributeSet)
    {
        this(paramContext, paramAttributeSet, 0);
    }

    public MClipView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
    {
        super(paramContext, paramAttributeSet, paramInt);
    }

    protected void onDraw(Canvas paramCanvas)
    {
        super.onDraw(paramCanvas);
        int width = getWidth();
        int height = getHeight();
        this.mPaint.setColor(Color.BLACK);
        this.mPaint.setAlpha(200);
        int left = (width - this.borderWidth) / 2;
        paramCanvas.drawRect(0.0F, 0.0F, width, (height - this.borderHeight) / 2, this.mPaint);
        paramCanvas.drawRect(0.0F, (height + this.borderHeight) / 2, width, height, this.mPaint);
        paramCanvas.drawRect(0.0F, (height - this.borderHeight) / 2, left, (height + this.borderHeight) / 2, this.mPaint);
        paramCanvas.drawRect((width + this.borderWidth) / 2, (height - this.borderHeight) / 2, width, (height + this.borderHeight) / 2, this.mPaint);

        this.mPaint.setColor(Color.DKGRAY);
        this.mPaint.setStrokeWidth(1.0F);
        paramCanvas.drawLine(left, (height - this.borderHeight) / 2, width - left, (height - this.borderHeight) / 2, this.mPaint);
        paramCanvas.drawLine(left, (height + this.borderHeight) / 2, width - left, (height + this.borderHeight) / 2, this.mPaint);
        paramCanvas.drawLine(left, (height - this.borderHeight) / 2, left, (height + this.borderHeight) / 2, this.mPaint);
        paramCanvas.drawLine(width - left, (height - this.borderHeight) / 2, width - left, (height + this.borderHeight) / 2, this.mPaint);
    }

    public void setBorderHeight(int paramInt)
    {
        this.borderHeight = paramInt;
    }

    public void setBorderWidth(int paramInt)
    {
        this.borderWidth = paramInt;
    }
}