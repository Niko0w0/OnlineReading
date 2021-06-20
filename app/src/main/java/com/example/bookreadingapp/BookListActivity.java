package com.example.bookreadingapp;

import android.Manifest;
import android.app.Activity;
import android.app.AppComponentFactory;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class BookListActivity extends Activity {

    private static final String TAG = "BookListActivity";
    private List<BookListResult.Book> mBook =new ArrayList<>();
    private  AsyncHttpClient mClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        // 动态申请权限
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            //验证是否许可权限
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                }
            }
        }


         ListView mListView =(ListView) findViewById(R.id.book_list_view);

        String url = "http://www.imooc.com/api/teacher?type=10";
//        String url = "http://localhost:8080/book/allbooks";

        mClient = new AsyncHttpClient();
        mClient.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onStart(){
                super.onStart();
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
              final String result=new String(responseBody);

              Gson gson = new Gson();
              BookListResult bookListResult=gson.fromJson(result,BookListResult.class);

              mBook = bookListResult.getData();

              mListView.setAdapter(new BookListAdapter());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.i(TAG, "onFailure: ");
            }
            @Override
            public void onFinish(){
                super.onFinish();
            }
        });
    }

    public static void start(Context context){
        Intent intent =new Intent(context,BookListActivity.class);
        context.startActivity(intent);
    }



    public class BookListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mBook.size();
        }

        @Override
        public Object getItem(int i) {
            return mBook.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            BookListResult.Book book = mBook.get(position);

            ViewHolder viewHolder = new ViewHolder();
            if(view == null){
                LayoutInflater layoutInflater= (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = getLayoutInflater().inflate(R.layout.item_book_list,null);
                viewHolder.mNameTextView = (TextView) view.findViewById(R.id.name_text_view);
                viewHolder.mButton = (Button) view.findViewById(R.id.book_button);
                view.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) view.getTag();
            }

            viewHolder.mNameTextView.setText(book.getBookname());

//            final String path = Environment.getExternalStorageDirectory()+"/imooc/"+book.getBookname() + ".txt";
            final String path = "data/data/com.example.bookreadingapp/imooc/" + book.getBookname() + ".txt";
            final File file = new File(path);
            viewHolder.mButton.setText(file.exists() ? "开始阅读" : "点击下载");

            ViewHolder finalViewHolder = viewHolder;
            viewHolder.mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Log.e(TAG, "file是空不 "+ file );
                    Log.e(TAG, file.exists() ? "file存在" : "file不存在");
                    Log.e(TAG, "-----bookfile-----" + book.getBookfile());
                    // 文件下载代码
                    if(file.exists()){
                        BookActivity.start(BookListActivity.this, path);
//                        finalViewHolder.mNameTextView.setText("点击打开");
                    }else{
                        file.getParentFile().mkdir();
                        try {
                            //创建文件
                            file.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mClient.addHeader("Accept-Encoding", "identity");

                        mClient.get(book.getBookfile(), new FileAsyncHttpResponseHandler(file) {
                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {

                                Log.e(TAG, throwable.toString());
                                finalViewHolder.mButton.setText("下载失败");
                            }

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, File file) {
                                finalViewHolder.mButton.setText("开始阅读");
                            }


                            @Override
                            public void onProgress(long bytesWritten, long totalSize) {
                                super.onProgress(bytesWritten, totalSize);
                                finalViewHolder.mButton.setText(String.valueOf(bytesWritten*100/totalSize) + "%");
                            }
                        });
                    }



                }
            });
            return view;
        }

        class ViewHolder{
            public TextView mNameTextView;
            public Button mButton;
        }
    }

}
