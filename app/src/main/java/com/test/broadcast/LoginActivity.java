package com.test.broadcast;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import static com.test.broadcast.R.*;

public class LoginActivity extends BaseActivity {
    private static final String TAG = "LoginActivity";
    private EditText account, password;
    private Button login;
    public SharedPreferences.Editor editor;
    public SharedPreferences pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_login);
        editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        pm = PreferenceManager.getDefaultSharedPreferences(this);

        //文件存储判断
        if (isStartMain()) {
            Log.d(TAG, "onCreate: startMainActivity");
            startMainActivity();
            //开发中要finish登录界面，现在为了测试，注释掉，为了直观感受
            //finish();
        } else {
            Log.d(TAG, "onCreate: initView");
            initView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //当back是会调用此方法，因为打开apk是，当数据正确时，界面不会初始化，直接startMainActivity
        //所以当我们back时，要重新初始化界面
        initView();
    }

    private void initView() {

        account = (EditText) findViewById(id.account);
        password = (EditText) findViewById(id.password);
        login = (Button) findViewById(id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String account_get = conversion(account.getText().toString());
                String password_get = conversion(password.getText().toString());
                Log.d(TAG, account_get + ":" + password_get);
                if (account_get.equals("admin") && password_get.equals("123456")) {
                    Log.d(TAG, "onClick: save database");
                    save(account_get);
                    save(account_get);
                    editor.putString("username", account_get);
                    editor.putString("password", password_get);
                    editor.apply();
                    startMainActivity();
                    finish();
                } else {
                    Log.d(TAG, "error username or password");
                    Toast.makeText(LoginActivity.this, "account or password is error",
                            Toast.LENGTH_SHORT).show();
                    editor.clear();
                }
            }
        });
    }

    //判断数据库中你的用户名和密码是否正确
    public Boolean isStartMain() {
        String login_file = load();
        String username = pm.getString("username", "null");
        String password = pm.getString("password", "null");
        Log.d(TAG, "initView: " + username + ":" + password);
        //文件中存储的
        if (login_file.contains("admin123456")) {
            //SharedPreferences中存储的
            if (username.equals("admin") && password.equals("123456")) {
                return true;
            }
            return false;
        }
        return false;
    }

    //启动主页面
    public void startMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }

    //将数据保存到文件中
    public void save(String inputText) {
        FileOutputStream out = null;
        BufferedWriter writer = null;
        try {
            //文件保存在data/data/pacakgename/file/file_data
            out = openFileOutput("file_data", Context.MODE_APPEND);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(inputText);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //将数据从文件中读出来
    public String load() {
        FileInputStream in = null;
        BufferedReader reader = null;
        StringBuilder content = new StringBuilder();

        try {
            in = openFileInput("file_data");
            reader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return content.toString();
    }

    //将值为null的数据转为"null"
    public String conversion(String str) {
        if (str == null) {
            return "null";
        }
        return str;
    }
}
