package com.softdesign.devintensive.ui.activities;

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
    @BindView(R.id.remember_txt) TextView mForgotPassword;
    //@BindView(R.id.login_ll) LinearLayout mLl;
    //private EditText mEmail;
    //private EditText mPasword;
    private Button mEnter;
    //private TextView mForgotPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        ButterKnife.bind(this);
        //mLl.bringToFront();

        if(savedInstanceState != null){

        }
    }

    boolean DEBUG = false;
    public void onEnterClick(View view){
        if(DEBUG == true){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            this.finish();
            return;
        }
        String mail = getString(R.string.login_email).toLowerCase();
        String inp_mail = mEmail.getText().toString().toLowerCase();
        String pswrd = getString(R.string.login_password);
        String inp_pswrd = mPassword.getText().toString();
        if(inp_mail.equals(mail) && inp_pswrd.equals(pswrd)){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            this.finish();
            return;
        }else{
            Toast.makeText(this, getString(R.string.text_wrong_password), Toast.LENGTH_LONG).show();
        }
    }

    public void onForgotPasswordClick(View v) {
        Toast.makeText(this, getString(R.string.text_login_hint), Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //Toast.makeText(this, "Login restarted", Toast.LENGTH_LONG);
    }
}
