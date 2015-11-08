package com.mobileinternet.waimai.businessedition.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mobileinternet.waimai.businessedition.R;
import com.mobileinternet.waimai.businessedition.util.BitMapUtils;
import com.mobileinternet.waimai.businessedition.util.CodeUtil;
import com.mobileinternet.waimai.businessedition.util.UUIDUtil;
import com.mobileinternet.waimai.businessedition.view.ClipImageView;
import com.mobileinternet.waimai.businessedition.view.MClipView;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;

public class ClipImageActivity extends ActionBarActivity {

    public static final int CAMARE = 1;// 拍照
    public static final int IMAGBOX = 2; // 相册

    private ClipImageView mClipImageView;
    private int width;
    private int height;
    private RelativeLayout layout;
    public static final int CLIP_FAIL = 1;
    private String mFileName;
    private String myFilePath;
    private Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clipeimage);

        ActionBar mActionBar=getSupportActionBar();
        mActionBar.setTitle("图片裁剪");




        WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();

        height=width/5<<2;


        int type = getIntent().getIntExtra("type", ClipImageActivity.CAMARE);

        if (type == ClipImageActivity.CAMARE) {

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "temp.jpg")));
            startActivityForResult(intent, ClipImageActivity.CAMARE);
        } else {
            startActivityForResult(new Intent("android.intent.action.PICK", MediaStore.Images.Media.EXTERNAL_CONTENT_URI), ClipImageActivity.IMAGBOX);
        }



    }

    private void initView() {

        layout = (RelativeLayout) findViewById(R.id.selectpicture_rl_rl1);
        mClipImageView = new ClipImageView(this);
        mClipImageView.setBorderWidth(width);
        mClipImageView.setBorderHeight(height);

        MClipView mClipView = new MClipView(this);
        mClipView.setBorderWidth(width);
        mClipView.setBorderHeight(height);


        layout.addView(mClipImageView, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        layout.addView(mClipView, 1, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }


    public void startClip(View v) {

        clipAndSave();
    }


    public void cancel(View v) {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED) {
            finish();
            return;
        }


        initView();

        Uri uri = null;

        if (requestCode == ClipImageActivity.CAMARE) {
            uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "temp.jpg"));
        }

        if (requestCode == ClipImageActivity.IMAGBOX) {
            uri = data.getData();
        }


        if (uri == null) {
            CodeUtil.toast(this, "获取图片失败");
            finish();
            return;
        }

        initImage(uri);


//
//        if ( data == null) {
//            CodeUtil.toast(this,"获取图片失败");
//            setResult(CLIP_FAIL, null);
//            finish();
//            return;
//        }
//
//
//        Uri localUri = data.getData();
//        if (localUri == null) {CodeUtil.toast(this, "获取图片失败");
//            finish();
//            return;
//        }


    }


    /**
     * 开始剪辑图片
     */
    private void clipAndSave() {


        //检测sd卡的状态
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "sd卡异常，请检查sd卡", Toast.LENGTH_LONG).show();
            return;
        }
        //检测SD卡中是否有图片储存的路径
        //1.无建立
        String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        myFilePath = rootPath + "/page/imas";
        File mFile = new File(myFilePath);
        if (!mFile.exists()) {
            boolean ifSuccssful = mFile.mkdirs();
            if (!ifSuccssful) {
                Toast.makeText(this, "裁剪失败", Toast.LENGTH_LONG).show();
                return;
            }
        }


        //生成路径中没有出现过的文件名字
        mFileName = UUIDUtil.getUUID() + ".jpg";


        this.mBitmap = null;
        this.mBitmap = this.mClipImageView.clip();
        saveToSDcard();

    }

    private void saveToSDcard() {
        //保存
        try {

            boolean result = BitMapUtils.saveBitmap(myFilePath, mFileName, mBitmap, Bitmap.CompressFormat.JPEG);
            if (!result) {
                Toast.makeText(this, "图片存入sd卡过程失败", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = getIntent();
            intent.putExtra("path", myFilePath + "/" + mFileName);
            setResult(200, intent);
            finish();


        } catch (OutOfMemoryError localOutOfMemoryError) {

            Toast.makeText(this, "可用内存不多了，这次不能继续添加了", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
        } finally {
            if ((this.mBitmap != null) && (!this.mBitmap.isRecycled()))
                this.mBitmap.recycle();
            System.gc();
        }
    }


    /**
     * 初始化图片
     *
     * @param localUri
     */
    private void initImage(Uri localUri) {

        try {
            // int i = CommonUtils.getOrientation(getApplicationContext(), localUri);
            Matrix localMatrix = new Matrix();
            //  localMatrix.postRotate(70);
            BitmapFactory.Options localOptions = new BitmapFactory.Options();
            localOptions.inSampleSize = 1;
            localOptions.inJustDecodeBounds = true;
            FileDescriptor localFileDescriptor = getContentResolver().openFileDescriptor(localUri, "r").getFileDescriptor();
            BitmapFactory.decodeFileDescriptor(localFileDescriptor, null, localOptions);

            if ((!localOptions.mCancel) && (localOptions.outWidth != -1)) {
                if (localOptions.outHeight == -1)
                    return;

                localOptions.inSampleSize = computeSampleSize(localOptions, -1, this.width * this.height);


                localOptions.inJustDecodeBounds = false;
                localOptions.inDither = false;
                localOptions.inPurgeable = true;
                localOptions.inInputShareable = true;
                localOptions.inTempStorage = new byte[32768];
                Bitmap localBitmap = BitmapFactory.decodeFileDescriptor(localFileDescriptor, null, localOptions);
                //  if (i != 0)
                localBitmap = Bitmap.createBitmap(localBitmap, 0, 0, localBitmap.getWidth(), localBitmap.getHeight(), localMatrix, true);
                if (localBitmap != null) {
                    this.mClipImageView.setImageBitmap(localBitmap);
                    return;
                }
            }
        } catch (FileNotFoundException localFileNotFoundException) {
            // LogUtils.e("SelectPicActivity", localFileNotFoundException.getMessage(), localFileNotFoundException);
            Toast.makeText(getApplicationContext(), "提取图片失败", Toast.LENGTH_SHORT).show();
            finish();
            return;
        } catch (OutOfMemoryError localOutOfMemoryError) {
        }

    }

    private int computeSampleSize(BitmapFactory.Options options,
                                  int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    private int computeInitialSampleSize(BitmapFactory.Options options,
                                         int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 :
                (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 :
                (int) Math.min(Math.floor(w / minSideLength),
                        Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) &&
                (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    /**
     * Try to return the absolute file path from the given Uri
     *
     * @param context
     * @param uri
     * @return the file path or null
     */
    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }


}
