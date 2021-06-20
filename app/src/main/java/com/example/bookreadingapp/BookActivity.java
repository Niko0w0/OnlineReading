package com.example.bookreadingapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.StaleDataException;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.text.style.LineHeightSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookreadingapp.view.BookPageBezierHelper;
import com.example.bookreadingapp.view.BookPageView;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BookActivity extends AppCompatActivity {


    private  BookPageView mbookpageview;
    private TextView mprogessTextView;
    private static String FILE_PATH = "fileName";
    private static final String TAG = "BookActivity";
    private int mCurrentLength;
    private View mSettingView;
    private RecyclerView mRecyclerView;
    public static final String BOOKMARK = "bookmark";
    private BookPageBezierHelper mHelper;
    private String mFilePath;
    private int mWidth;
    private int mHeight;
    private int mLastLength;
    private TextToSpeech mTTS;
    private SeekBar mSeekBar;
    private int mTotalLength;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //fullscreen
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            // 上面的状态栏和下面的操作条
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }


        setContentView(R.layout.activity_book);

        if(getIntent() != null){
            String filepath = getIntent().getStringExtra(FILE_PATH);
            mFilePath = filepath;
            if(!TextUtils.isEmpty(mFilePath)){
                 mTotalLength = (int)new File(mFilePath).length();
            }
            Log.e(TAG, "----filepath:" + filepath);


        }else
        {
            // 找不到这本书
        }

        //初始化视图
        mbookpageview =(BookPageView) findViewById(R.id.book_page_view);
        mprogessTextView = (TextView) findViewById(R.id.progress_text_view);
         mSettingView = findViewById(R.id.setting_view);
        mRecyclerView = findViewById(R.id.settingRecyclerView);
        //SeekBar
        mSeekBar = (SeekBar)findViewById(R.id.seekbar);



        //get Size
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mWidth = displayMetrics.widthPixels;
         mHeight = displayMetrics.heightPixels;
        openBookProgress(R.drawable.shuimo,0);
        Log.e(TAG, "---- width:" + mWidth + " height:" + mHeight);

        //设置进度
        mHelper.setOnProgressChangedListener(new BookPageBezierHelper.OnProgressChangedListener() {
            @Override
            public void setProgress(int currentLength, int totalLength) {
                mCurrentLength = currentLength;
                float progress;

                if(totalLength == 0) {
                    progress = 0;
                }else{
                    progress =mCurrentLength * 100 / totalLength;
                }
                mprogessTextView.setText(String.format("%s%%",progress));
            }

            @Override
            public void setProgress(String progess) {

            }

        });


                //set user setting view listener
                mbookpageview.setOnUserNeedSettingListener(new BookPageView.OnUserNeedSettingListener() {
                    @Override
                    public void onUserNeedSetting() {
                        mSettingView.setVisibility(mSettingView.getVisibility() == View.VISIBLE
                                ? View.GONE : View.VISIBLE);
                    }
                });

        //设置循环器视图数据
        List<String> settingList = new ArrayList<>();
        settingList.add("添加书签");
        settingList.add("读取书签");
        settingList.add("设置背景");
        settingList.add("语音朗读");
        settingList.add("跳转进度");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(new HorizontalAdapter(this,settingList));

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                    openBookProgress(R.drawable.shuimo,seekBar.getProgress()*mTotalLength/100);
            }
        });


    }

    private void openBookProgress(int backgroundResourceID,int progress) {

        //设置book helper
        mHelper = new BookPageBezierHelper(mWidth,mHeight,progress);
        // helper.setTextColor(Color.BLUE);
        Log.e(TAG, "----progress : " + mHelper.getProgress());
        mbookpageview.setBookPageBezierHelper(mHelper);

        //当前页面，下一页
        Bitmap currentPageBitmap = Bitmap.createBitmap(mWidth,mHeight,Bitmap.Config.ARGB_8888);
        Bitmap nextPageBitmap = Bitmap.createBitmap(mWidth,mHeight,Bitmap.Config.ARGB_8888);
        mbookpageview.setBitmaps(currentPageBitmap, nextPageBitmap);

        //设置背景图片
        mHelper.setBackground(this,backgroundResourceID);

        Log.e(TAG, "----- filepath ----" + mFilePath);
        //打开书籍
        if(!TextUtils.isEmpty(mFilePath)){
            Log.e(TAG, "---- 进入if语句文件不为空 ----");
            try {
                mHelper.openBook(mFilePath);
                Canvas canvas = new Canvas(currentPageBitmap);
                mHelper.draw(canvas);
                mbookpageview.invalidate();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
        }

    }

    public static void start(Context context,String filepath){
        Intent intent =new Intent(context,BookActivity.class);
        intent.putExtra(FILE_PATH,filepath);
        context.startActivity(intent);
    }

    private class HorizontalAdapter extends RecyclerView.Adapter {

        private Context mContext;
        private List<String> mData = new ArrayList<>();
        public HorizontalAdapter(Context context, List<String> list) {
            mContext = context;
            mData = list;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            TextView itemView = new TextView(mContext);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
                TextView textView = (TextView) holder.itemView;
                textView.setWidth(240);
                textView.setHeight(180);
                textView.setTextSize(22);
                textView.setTextColor(Color.WHITE);
                textView.setGravity(Gravity.CENTER);
                textView.setText(mData.get(position));


            SharedPreferences sharedPreferences =
                    mContext.getSharedPreferences("book_preference",MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (position){
                        case 0:
                            //添加书签
                            //获取进度
                            //存进度
                            editor.putInt(BOOKMARK,mCurrentLength);
                            editor.apply();
                            break;
                        case 1:
                            //得到书签
                            //重新加载书籍到该进度
                            mLastLength = sharedPreferences.getInt(BOOKMARK,0);
                            openBookProgress(R.drawable.shuimo,mLastLength);
                            break;
                        case 2:
                            //更换背景
                            openBookProgress(R.drawable.menglong,mLastLength);
                            break;
                        case 3:
                            //语音朗读
                            if(mTTS == null){
                                mTTS = new TextToSpeech(mContext, new TextToSpeech.OnInitListener() {
                                    @Override
                                    public void onInit(int status) {
                                    if(status == TextToSpeech.SUCCESS){
                                        int result = mTTS.setLanguage(Locale.CHINA);
                                        if(result == TextToSpeech.LANG_MISSING_DATA ||result == TextToSpeech.LANG_NOT_SUPPORTED){
                                            Log.e(TAG,"不支持这种语言");
                                            //下载一个讯飞
                                            Uri uri = Uri.parse("http://acj2.pc6.com/pc6_soure/2017-6/com.iflytek.vflynote_208.apk");
                                            Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                                            startActivity(intent);
                                        }else {
                                            Log.i(TAG, "onInit: success.");
                                            //朗读这一页所有文字
                                            mTTS.speak(mHelper.getCurrentPageContent(),TextToSpeech.QUEUE_FLUSH,null);

                                        }
                                    }else {
                                        Log.e(TAG,"onInit:error");
                                    }
                                    }
                                });
                            }else {
                                if(mTTS.isSpeaking()){
                                    mTTS.stop();
                                }else {
                                    mTTS.speak(mHelper.getCurrentPageContent(),TextToSpeech.QUEUE_FLUSH,null);
                                }
                            }

                            break;
                        case 4:
                            //跳转进度
                            mSeekBar.setVisibility(View.VISIBLE);


                            break;
                    }
                }
            });
        }



        @Override
        public int getItemCount() {
            return mData.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder{

            private TextView mTextView;
            public ViewHolder(TextView itemView){
                super(itemView);
                mTextView = itemView;
            }

        }

    }
      protected void onDestory(){
        super.onDestroy();
        if(mTTS != null){
            mTTS.shutdown();
        }
    }
}
