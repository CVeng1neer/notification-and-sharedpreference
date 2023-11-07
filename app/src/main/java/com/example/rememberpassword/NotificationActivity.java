package com.example.rememberpassword;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.FormBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NotificationActivity extends Activity {
    private String TAG = "444";
    private TextView mTextView;
    private Retrofit mRetrofit;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_layout);

        Button get = (Button) findViewById(R.id.get);
        Button post = (Button) findViewById(R.id.post);
         mTextView = findViewById(R.id.textview);

         mRetrofit = new Retrofit.Builder()
                //设置网络请求BaseUrl地址
                .baseUrl("https://api.uomg.com/")
                //设置数据解析器
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getJsonData();
            }
        });
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postJsonData();
            }
        });
    }
    private void getJsonData() {
        // 步骤5:创建网络请求接口对象实例
        Api api = mRetrofit.create(Api.class);
        //步骤6：对发送请求进行封装，传入接口参数
        Call<Data<Info>> jsonDataCall = api.getJsonData("新歌榜", "json");

        //同步执行
//         Response<Data<Info>> execute = jsonDataCall.execute();

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
                mTextView.setText("返回的数据：" + "\n\n" + info.getName() + "\n" + info.getPicurl());
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
}
