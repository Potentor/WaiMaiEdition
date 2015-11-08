package com.mobileinternet.waimai.businessedition.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;


public class ClipImageView extends ImageView
        implements OnTouchListener, OnGlobalLayoutListener
{
    private final Matrix baseMatrix = new Matrix();
    private int borderHeight;
    private int borderWidth;
    private final RectF displayRect = new RectF();
    private final Matrix drawMatrix = new Matrix();
    private boolean isJusted=false;
    private final float[] matrixValues = new float[9];
    private float maxScale = 4.0F;
    private float midScale = 2.0F;
    private float minScale = 1.0F;
    private MultiGestureDetector multiGestureDetector;
    private final Matrix suppMatrix = new Matrix();

    public ClipImageView(Context paramContext)
    {
        this(paramContext, null);
        init(paramContext);
    }

    public ClipImageView(Context paramContext, AttributeSet paramAttributeSet)
    {
        this(paramContext, paramAttributeSet, 0);
        init(paramContext);
    }

    public ClipImageView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
    {
        super(paramContext, paramAttributeSet, paramInt);
        init(paramContext);
    }



    private void checkAndDisplayMatrix()
    {
        checkMatrixBounds();
        setImageMatrix(getDisplayMatrix());
    }


    /**
     * 检查是否过界，若是，则调整为合适的偏移，否，则按照用户的意思做
     * reback to study his logic algorythm
     */
    private void checkMatrixBounds()
    {
        RectF localRectF = getDisplayRect(getDisplayMatrix());
        if (localRectF == null)
            return;
        float width = getWidth();
        float heght = getHeight();

        //是否在剪辑框上边，true 是 ,false 非法
        boolean isTopOverBound = localRectF.top < (heght - this.borderHeight) / 2.0F;
        float f3 = 0.0F;
        if (!isTopOverBound)
            f3 = (heght - this.borderHeight) / 2.0F - localRectF.top;

        //若越过剪辑框下边，afford addmire
        if (localRectF.bottom < (heght + this.borderHeight) / 2.0F)
            f3 = (heght + this.borderHeight) / 2.0F - localRectF.bottom;

        //是否在剪辑框左边，true 是 false 非法
        boolean isLeftOverBound = localRectF.left < (width - this.borderWidth) / 2.0F;
        float f4 = 0.0F;
        if (!isLeftOverBound)
            f4 = (width - this.borderWidth) / 2.0F - localRectF.left;

        if (localRectF.right < (width + this.borderWidth) / 2.0F)
            f4 = (width + this.borderWidth) / 2.0F - localRectF.right;

        this.suppMatrix.postTranslate(f4, f3);
    }

    private void configPosition()
    {
        super.setScaleType(ScaleType.MATRIX);
        Drawable localDrawable = getDrawable();
        if (localDrawable == null)
            return;
        float width = getWidth();
        float height = getHeight();
        int drawableWidth = localDrawable.getIntrinsicWidth();
        int drawableHeight = localDrawable.getIntrinsicHeight();
        float scale = 1.0F;


        float widthScale=this.borderWidth*1.0f/drawableWidth;
        float heightScale=this.borderHeight*1.0f/drawableHeight;
        if(widthScale>=heightScale){

            //以图片高为标准缩小
            if(widthScale<=1){
                scale=widthScale;

            }else{//以图片宽为标准放大
                scale=widthScale;
            }

        }else{
            //以图片宽为标准缩小
            if(heightScale<=1){
                scale=heightScale;

            }else{//以图片高为标准放大
                scale=heightScale;
            }

        }

        this.baseMatrix.reset();
        this.baseMatrix.postScale(scale, scale);

        this.baseMatrix.postTranslate((width - scale * drawableWidth) / 2.0F, (height - scale * drawableHeight) / 2.0F);
        resetMatrix();
        this.isJusted = true;

    }


    /**
     * 返回经将要显示的矩阵处理过了的RectF对象，此RectF是显示时的矩形
     * @param paramMatrix
     * @return
     */
    private RectF getDisplayRect(Matrix paramMatrix)
    {
        Drawable localDrawable = getDrawable();
        if (localDrawable != null)
        {
            this.displayRect.set(0.0F, 0.0F, localDrawable.getIntrinsicWidth(), localDrawable.getIntrinsicHeight());
            paramMatrix.mapRect(this.displayRect);
            return this.displayRect;
        }
        return null;
    }

    @SuppressLint({"ClickableViewAccessibility"})
    private void init(Context paramContext)
    {
        super.setScaleType(ScaleType.MATRIX);
        setOnTouchListener(this);
        this.multiGestureDetector = new MultiGestureDetector(paramContext);
    }


    /**
     * 执行动画
     * @param paramView
     * @param paramRunnable
     */
    @TargetApi(16)
    private void postOnAnimation(View paramView, Runnable paramRunnable)
    {
        if (VERSION.SDK_INT >= 16)
        {
            paramView.postOnAnimation(paramRunnable);
            return;
        }
        paramView.postDelayed(paramRunnable, 16L);
    }


    /**
     * 恢复最开始的显示，并重置suppMatrix矩阵
     */
    private void resetMatrix()
    {
        if (this.suppMatrix == null)
            return;

        //重置矩阵
        this.suppMatrix.reset();
        setImageMatrix(getDisplayMatrix());
    }


    /**
     * 生成对应尺寸的图片
     * @return
     */
    public Bitmap clip()
    {
        //只获得ImageView显示出来的部分图像
        Bitmap localBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);
       //将显示出来的部分图像
        draw(new Canvas(localBitmap));
        //生成剪辑框范围内的图像部分
        return Bitmap.createBitmap(localBitmap, (getWidth() - this.borderWidth) / 2, (getHeight() - this.borderHeight) / 2, this.borderWidth, this.borderHeight);
    }


    /**
     * 获得最终要显示的Matrix
     * @return
     */
    protected Matrix getDisplayMatrix()
    {
        this.drawMatrix.set(this.baseMatrix);
        this.drawMatrix.postConcat(this.suppMatrix);
        return this.drawMatrix;
    }


    /**
     * 存储suppMatrix到数组后，返回尺寸值
     * @return
     */
    public final float getScale()
    {
        this.suppMatrix.getValues(this.matrixValues);
        return this.matrixValues[0];
    }

    protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeGlobalOnLayoutListener(this);
    }

    public void onGlobalLayout()
    {
        if (this.isJusted)
            return;
        Log.i("yy","Global");
        configPosition();
    }


    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
    {
        //将触屏事件交给Gesture类的OnTouch方法处理
        return this.multiGestureDetector.onTouchEvent(paramMotionEvent);
    }

    /**
     * 设置剪辑框高
     * @param paramInt
     */
    public void setBorderHeight(int paramInt)
    {
        this.borderHeight = paramInt;
    }


    /**
     * 设置剪辑框宽
     * @param paramInt
     */
    public void setBorderWidth(int paramInt)
    {
        this.borderWidth = paramInt;
    }


    /**
     *  缩放动画Runnable
     */
    private class AnimatedZoomRunnable implements Runnable
    {
//        static final float ANIMATION_SCALE_PER_ITERATION_IN = 1.07F;
//        static final float ANIMATION_SCALE_PER_ITERATION_OUT = 0.93F;
        private final float deltaScale;//起始尺寸
        private final float focalX;//支点X
        private final float focalY;//支点Y
        private final float targetZoom;//目标尺寸


        public AnimatedZoomRunnable(float deltaScale, float targetZoom, float focalX, float focalY)
        {
            this.targetZoom = targetZoom;
            this.focalX = focalX;
            this.focalY = focalY;

            if (deltaScale < targetZoom)
            {
                this.deltaScale = 1.07F;
                return;
            }
            this.deltaScale = 0.93F;
        }

        public void run()
        {
            //Can Not understand！
            ClipImageView.this.suppMatrix.postScale(this.deltaScale, this.deltaScale, this.focalX, this.focalY);
            ClipImageView.this.checkAndDisplayMatrix();
            float f1 = ClipImageView.this.getScale();
            if (((this.deltaScale > 1.0F) && (f1 < this.targetZoom)) || ((this.deltaScale < 1.0F) && (this.targetZoom < f1)))
            {
                ClipImageView.this.postOnAnimation(ClipImageView.this, this);
                return;
            }
            float f2 = this.targetZoom / f1;
            ClipImageView.this.suppMatrix.postScale(f2, f2, this.focalX, this.focalY);
            ClipImageView.this.checkAndDisplayMatrix();
        }
    }



    private class MultiGestureDetector extends SimpleOnGestureListener
            implements OnScaleGestureListener
    {
        private static final String tag = "ClipImageView.MultiGestureDetector";
        private final GestureDetector gestureDetector;
        private final ScaleGestureDetector scaleGestureDetector;
        private boolean isDragging=true;
//        private final float scaledTouchSlop;
//        private VelocityTracker velocityTracker;
//        private long oldTime;
        Context localContext;

        public MultiGestureDetector(Context context)
        {
            this.localContext=context;
            this.scaleGestureDetector = new ScaleGestureDetector(localContext, this);
            this.gestureDetector = new GestureDetector(localContext, this);
            this.gestureDetector.setOnDoubleTapListener(MultiGestureDetector.this);

           // this.scaledTouchSlop = ViewConfiguration.get(localContext).getScaledTouchSlop();
        }


        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            //对图像添加平移Matrix变换
            ClipImageView.this.suppMatrix.postTranslate(-distanceX,-distanceY);
            ClipImageView.this.checkAndDisplayMatrix();
            return true;
        }

        public boolean onDoubleTap(MotionEvent paramMotionEvent)
        {

            float oldScale;
            float width;
            float height;
            try
            {
                oldScale = ClipImageView.this.getScale();
                width = ClipImageView.this.getWidth() / 2;
                height = ClipImageView.this.getHeight() / 2;

                if (oldScale < ClipImageView.this.midScale) {
                    ClipImageView.this.post(new AnimatedZoomRunnable(oldScale, ClipImageView.this.midScale, width, height));
                }else if (oldScale < ClipImageView.this.maxScale){
                    ClipImageView.this.post(new AnimatedZoomRunnable(oldScale, ClipImageView.this.maxScale, width, height));
                }else{
                    ClipImageView.this.post(new AnimatedZoomRunnable(oldScale, ClipImageView.this.minScale, width, height));
                }

            }
            catch (Exception localException)
            {
                localException.printStackTrace();
            }

            return true;
        }

        public boolean onScale(ScaleGestureDetector paramScaleGestureDetector)
        {

            float targetScale = paramScaleGestureDetector.getScaleFactor();
            float oldScale = ClipImageView.this.getScale();
            //1.判断有没有图片 2.是否在限制尺寸最大值最小值范围内满足放大、缩小之一
            if ((ClipImageView.this.getDrawable() != null) && (((oldScale < ClipImageView.this.maxScale) && (targetScale > 1.0F)) || ((oldScale > ClipImageView.this.minScale) && (targetScale < 1.0F))))
            {
                //如果缩放的比例小于最小比例，则将缩放的比例置为最小比例进行缩放
                if (targetScale * oldScale < ClipImageView.this.minScale)
                    targetScale = ClipImageView.this.minScale / oldScale;

                //如果缩放的比例大于最大的比例，则将缩放的比例置为最大比例进行缩放
                if (targetScale * oldScale > ClipImageView.this.maxScale)
                    targetScale = ClipImageView.this.maxScale / oldScale;

                //将缩放比例转为Matrix,合并到图像当前的Matrix
                ClipImageView.this.suppMatrix.postScale(targetScale, targetScale, ClipImageView.this.getWidth() / 2, ClipImageView.this.getHeight() / 2);
               //检查缩放后是否越界，并对原始图像进行Matrix变换
                ClipImageView.this.checkAndDisplayMatrix();
            }
            return true;
        }

        public boolean onScaleBegin(ScaleGestureDetector paramScaleGestureDetector)
        {
            return true;
        }

        public void onScaleEnd(ScaleGestureDetector paramScaleGestureDetector)
        {
        }

        public boolean onTouchEvent(MotionEvent event)
        {

            /**
             * isDragging设置的目的是为了防止：由于缩放后手指抬起导致的图片突然移动
             * 当进行缩放操作后，将手指抬起的事件同样交给scaleGestureDetector处理
             */

            /**
             * gestureDetector可以识别 双连击、单手指滑动的事件。并在onDoubleTap、onScroll
             * 中进行回调
             * scaleGestureDetector可以识别手指的缩放事件，并在onScale中进行回调
             */

            switch (event.getAction())
            {
                case MotionEvent.ACTION_MOVE:
                    //判断几个手指按下，若超过了两个，则默认是进行缩放操作，
                    // 将事件交给scaleGestureDetector处理
                    if (event.getPointerCount() >= 2) {
                        scaleGestureDetector.onTouchEvent(event);
                        isDragging=false;
                    }

                    //若果不是缩放操作，则交给 gestureDetector处理全部触屏事件操作
                    if(isDragging)
                        gestureDetector.onTouchEvent(event);
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:

                    if (isDragging){
                        gestureDetector.onTouchEvent(event);
                    }else{
                        scaleGestureDetector.onTouchEvent(event);
                        isDragging=true;
                    }
                    break;
                default:
                    if(isDragging){
                        gestureDetector.onTouchEvent(event);
                    }
            }
            return true;

        }


    }
}