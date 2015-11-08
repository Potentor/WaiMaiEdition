package com.mobileinternet.waimai.businessedition.activity.Acount;


import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.view.View;

import com.mobileinternet.waimai.businessedition.R;
import com.mobileinternet.waimai.businessedition.activity.BaseTabActivity;
import com.mobileinternet.waimai.businessedition.app.Share;
import com.mobileinternet.waimai.businessedition.fragment.AcountCenter.RefundHandleFragment;
import com.mobileinternet.waimai.businessedition.fragment.AcountCenter.RefundNotHandleFragment;

import java.util.ArrayList;

public class RefundActivity extends BaseTabActivity {


    @Override
    protected void extendYourNeed() {

        ls_title=new ArrayList<>();
        ls_fragment=new ArrayList<>();

        ls_fragment.add(RefundNotHandleFragment.newInstance());
        ls_fragment.add(RefundHandleFragment.newInstance());

        ls_title.add("待退款");
        ls_title.add("退款记录");

        mViewPager.setOffscreenPageLimit(2);

        ActionBar mActionBar=getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle("退款申请");

       final SharedPreferences preferences=getSharedPreferences(Share.share_prefrence_name,Activity.MODE_PRIVATE);
        boolean hasClicked=preferences.getBoolean(Share.refund_notice,false);

        if (!hasClicked) {
            View view=findViewById(R.id.base_fragment_notice);
            view.setVisibility(View.VISIBLE);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.setVisibility(View.GONE);
                    SharedPreferences.Editor editor=preferences.edit();
                   editor.putBoolean(Share.refund_notice,true);
                    editor.commit();

                }
            });

        }

    }


}
