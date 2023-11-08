package com.example.rememberpassword;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStream;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NotificationActivity extends Activity {
    private String TAG = "444";
    private TextView mTextView;
    private ImageView mImageView;
    private Spinner spinner;
    private Retrofit mRetrofit;
    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_layout);

        Button get = (Button) findViewById(R.id.get);
        Button post = (Button) findViewById(R.id.post);
        mImageView = findViewById(R.id.imageview);
        spinner = findViewById(R.id.spinner);
        //mTextView = findViewById(R.id.textview);

        // 初始化 Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sort_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadImage();
            }
        });
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void loadImage() {
        // 获取 Spinner 选中的值
        String sort = spinner.getSelectedItem().toString();
        //步骤4:创建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.uomg.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // 步骤5:创建网络请求接口对象实例
        Api apiService = retrofit.create(Api.class);
        //步骤6：对发送请求进行封装，传入接口参数
        Call<ImageResponse> call = apiService.fetchImage(sort, "json");
        //步骤7:发送网络请求(异步)
        Log.e(TAG, "get == url：" + call.request().url());
        call.enqueue(new Callback<ImageResponse>() {
            @Override
            public void onResponse(Call<ImageResponse> call, Response<ImageResponse> response) {
                if (response.isSuccessful()) {
                    List<String> imageUrls = response.body().getImgurl();
                    if (!imageUrls.isEmpty()) {
                        String imageUrl = imageUrls.get(0);
                        // 使用 Glide 加载图片
                        Glide.with(NotificationActivity.this).load(imageUrl).into(mImageView);
                    }
                }
            }
            @Override
            public void onFailure(Call<ImageResponse> call, Throwable t) {
                Log.e(TAG, "get回调失败：" + t.getMessage() + "," + t.toString());
                Toast.makeText(NotificationActivity.this, "get回调失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //下面是以前的调用api显示文本
    /**
    private void getJsonData() {
        // 步骤5:创建网络请求接口对象实例
        Api api = mRetrofit.create(Api.class);
        //步骤6：对发送请求进行封装，传入接口参数
        Call<Data<Info>> jsonDataCall = api.getJsonData("男", "images");

        //步骤7:发送网络请求(异步)
        Log.e(TAG, "get == url：" + jsonDataCall.request().url());
        jsonDataCall.enqueue(new Callback<Data<Info>>() {
            @Override
            public void onResponse(Call<Data<Info>> call, Response<Data<Info>> response) {
                //步骤8：请求处理,输出结果
                Toast.makeText(NotificationActivity.this, "get回调成功:异步执行", Toast.LENGTH_SHORT).show();
                Data<Info> body = response.body();
                if (body == null) return;
                Info info = body.getData();
                if (info == null) return;
                //mTextView.setText("返回的数据：" + "\n\n" + info.getName() + "\n" + info.getPicurl());
//                Glide.with(NotificationActivity.this)
//                        .load(info.getPicurl())
//                        .into(mImageView);
            }

            @Override
            public void onFailure(Call<Data<Info>> call, Throwable t) {
                Log.e(TAG, "get回调失败：" + t.getMessage() + "," + t.toString());
                Toast.makeText(NotificationActivity.this, "get回调失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void postJsonData() {
        //步骤4:创建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.uomg.com/") // 设置网络请求baseUrl
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析
                .build();

        // 步骤5:创建网络请求接口的实例
        Api request = retrofit.create(Api.class);
        //步骤6：对发送请求进行封装:传入参数
        Call<Object> call = request.postDataCall("JSON");

        //步骤7:发送网络请求(异步)

        //请求地址
        Log.e(TAG, "post == url：" + call.request().url());

        //请求参数
        StringBuilder sb = new StringBuilder();
        if (call.request().body() instanceof FormBody) {
            FormBody body = (FormBody) call.request().body();
            for (int i = 0; i < body.size(); i++) {
                sb.append(body.encodedName(i))
                        .append(" = ")
                        .append(body.encodedValue(i))
                        .append(",");
            }
            sb.delete(sb.length() - 1, sb.length());
            Log.e(TAG, "| RequestParams:{" + sb.toString() + "}");
        }

        call.enqueue(new Callback<Object>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                //步骤8：请求处理,输出结果
                Object body = response.body();
                if (body == null) return;
                mTextView.setText("返回的数据：" + "\n\n" + response.body().toString());
                Toast.makeText(NotificationActivity.this, "post回调成功:异步执行", Toast.LENGTH_SHORT).show();
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<Object> call, Throwable throwable) {
                Log.e(TAG, "post回调失败：" + throwable.getMessage() + "," + throwable.toString());
                Toast.makeText(NotificationActivity.this, "post回调失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
     **/
}
