package com.mobileinternet.waimai.businessedition.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

/**
 * Created by 海鸥2012 on 2015/7/24.
 */
public class MScrollView extends ScrollView {



    private boolean ifLayoutHasExecuted=false;
    private OnLayoutListener mOnLayoutListener;


    public MScrollView(Context context) {
        super(context);
    }

    public MScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public MScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }




    @Override
    public int computeHorizontalScrollRange() {
        return 0;
    }

    @Override
    public int computeHorizontalScrollOffset() {
        return 0;
    }

    @Override
    public int computeHorizontalScrollExtent() {
        return 0;
    }

    @Override
    public int computeVerticalScrollRange() {
        return 0;
    }

    @Override
    public int computeVerticalScrollOffset() {
        return 0;
    }

    @Override
    public int computeVerticalScrollExtent() {
        return 0;
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {

//        if (isOk) {
//            if (childViewGroup.getChildAt(1).getTop() == getTop()) {
//                Log.i("detector", "boss do not alow");
//                return false;
//            }
//            Log.i("detector", "boss child top2:"+childViewGroup.getChildAt(1).getTop());
//        }
//
//        Log.i("detector", "boss move");

        return super.onTouchEvent(ev);
    }



    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (!ifLayoutHasExecuted){



            ViewGroup viewGroup= (ViewGroup)getChildAt(0);

            ifLayoutHasExecuted=true;

            View thirdView=viewGroup.getChildAt(2);
            ViewGroup.LayoutParams params=thirdView.getLayoutParams();
            params.height=getHeight()-viewGroup.getChildAt(1).getBottom()+viewGroup.getChildAt(0).getBottom();

            if (mOnLayoutListener!=null){

                mOnLayoutListener.onLayout(getHeight(),
                        viewGroup.getChildAt(0).getBottom(),
                        viewGroup.getChildAt(1).getBottom());


            }
        }

    }


    public void setOnLayoutListener(OnLayoutListener onLayoutListener){
        this.mOnLayoutListener=onLayoutListener;
    }

    public interface OnLayoutListener {

        public void onLayout(int totalHeight,int firstItemHeight,int secondItemHeight);
    }



}
