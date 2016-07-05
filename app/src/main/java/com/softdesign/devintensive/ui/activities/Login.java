package com.softdesign.devintensive.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.utils.ConstantManager;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Masha on 03.07.2016.
 */
public class Login extends AppCompatActivity {

    public static final String TAG = ConstantManager.TAG_PREFIX + "Login";

    @BindView(R.id.login_email_et) EditText mEmail;
    @BindView(R.id.login_password_et) EditText mPassword;
    @BindView(R.id.forgot_pass_tv) TextView mForgotPassword;
    //private EditText mEmail;
    //private EditText mPasword;
    private Button mEnter;
    //private TextView mForgotPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        ButterKnife.bind(this);
    }

    public void onEnterClick(View view){
        String mail = "Oleg@mail.ru".toLowerCase();
        String inp_mail = mEmail.getText().toString().toLowerCase();
        String pswrd = "assa";
        String inp_pswrd = mPassword.getText().toString();
        if(inp_mail.equals(mail) && inp_pswrd.equals(pswrd)){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            this.finish();
            return;
        }else{
            Toast.makeText(this, "Не правильно введённый Логин или Пароль\r\nПовторите попытку", Toast.LENGTH_LONG).show();
        }
    }

    public void onForgotPasswordClick(View v) {
        Toast.makeText(this, "E-mail: Oleg@mail.ru\r\nПароль: assa", Toast.LENGTH_LONG).show();
    }
}
