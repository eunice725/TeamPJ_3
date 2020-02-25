package com.example.teampj_1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class LockPasswordActivity extends AppCompatActivity {

    TextView tvPhrase;
    ImageView imgTwo1, imgTwo2, imgTwo3, imgTwo4;
    Button btnNum1, btnNum2, btnNum3, btnNum4, btnNum5, btnNum6, btnNum7, btnNum8, btnNum9, btnNum0;
    ImageButton imgBtnDel;
    int width, height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_password);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.hide(); // 타이틀바 없애기
        setTitle("암호 설정");

        tvPhrase = (TextView)findViewById(R.id.tvPhrase);
        imgTwo1 = (ImageView)findViewById(R.id.imgTwo1);
        imgTwo2 = (ImageView)findViewById(R.id.imgTwo2);
        imgTwo3 = (ImageView)findViewById(R.id.imgTwo3);
        imgTwo4 = (ImageView)findViewById(R.id.imgTwo4);
        btnNum0 = (Button)findViewById(R.id.btnNum0);
        btnNum1 = (Button)findViewById(R.id.btnNum1);
        btnNum2 = (Button)findViewById(R.id.btnNum2);
        btnNum3 = (Button)findViewById(R.id.btnNum3);
        btnNum4 = (Button)findViewById(R.id.btnNum4);
        btnNum5 = (Button)findViewById(R.id.btnNum5);
        btnNum6 = (Button)findViewById(R.id.btnNum6);
        btnNum7 = (Button)findViewById(R.id.btnNum7);
        btnNum8 = (Button)findViewById(R.id.btnNum8);
        btnNum9 = (Button)findViewById(R.id.btnNum9);
        imgBtnDel = (ImageButton)findViewById(R.id.btnDel);


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch ((item.getItemId())) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
