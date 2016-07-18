package com.softdesign.devintensive.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.network.req.UserLoginReq;
import com.softdesign.devintensive.data.network.res.UserModelRes;
import com.softdesign.devintensive.utils.NetworkStatusChecker;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AuthActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.login_btn)
    Button mSignIn;
    @BindView(R.id.remember_txt)
    TextView mRememberPassword;
    @BindView(R.id.login_email_et)
    EditText mLogin;
    @BindView(R.id.login_password_et)
    EditText mPassword;
    @BindView(R.id.main_coordinator_container)
    CoordinatorLayout mCoordinatorLayout;

    private DataManager mDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        mDataManager = DataManager.getInstance();

        ButterKnife.bind(this);
        mRememberPassword.setOnClickListener(this);
        mSignIn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_btn:
                signIn();
                break;
            case R.id.remember_txt:
                rememberPassword();
                break;
        }
    }

    private  void showSnackBar(String message){
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG);
        showToast(message);
    }

    private void rememberPassword(){
        Intent remembrIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://devintensive.softdesign-apps.ru/forgotpass"));
        startActivity(remembrIntent);
    }

    private void loginSuccess(UserModelRes userModel){
        //showToast(userModel.getData().getToken());
        mDataManager.getPreferencesManager().saveAuthToken(userModel.getData().getToken());
        mDataManager.getPreferencesManager().saveUserId(userModel.getData().getUser().getId());
        saveUserValues(userModel);
        saveUserData(userModel);

        Intent loginIntent = new Intent(this, MainActivity.class);
        startActivity(loginIntent);
    }

    private void signIn(){
        if(NetworkStatusChecker.isNetworkAvailable(this)) {
            Call<UserModelRes> call = mDataManager.loginUser(new UserLoginReq(mLogin.getText().toString(), mPassword.getText().toString()));
            call.enqueue(new Callback<UserModelRes>() {
                @Override
                public void onResponse(Call<UserModelRes> call, Response<UserModelRes> response) {
                    if (response.code() == 200) {
                        loginSuccess(response.body());
                    } else if (response.code() == 404) {
                        showToast("Неверный логин или пароль");
                    } else {
                        showToast("Всё пропало, шеф!!!");
                    }
                }

                @Override
                public void onFailure(Call<UserModelRes> call, Throwable t) {
                    ///// TODO: 11.07.2016 обработать ошибки
                }
            });
        } else {
            showToast("Сеть на данный момент не доступна, попробуйте позже");
        }
    }

    private void saveUserValues(UserModelRes userModel){
        int[] userValues = {
                userModel.getData().getUser().getProfileValues().getRating(),
                userModel.getData().getUser().getProfileValues().getLinesCode(),
                userModel.getData().getUser().getProfileValues().getProjects()
        };

        mDataManager.getPreferencesManager().saveUserProfileValues(userValues);
    }

    private void saveUserData(UserModelRes userModel){
        String name = userModel.getData().getUser().getFirstName();
        String family = userModel.getData().getUser().getSecondName();
        mDataManager.getPreferencesManager().saveUserProfileName(name, family);
        List<String> userData = new ArrayList<>();
        userData.add(userModel.getData().getUser().getContacts().getPhone());
        userData.add(userModel.getData().getUser().getContacts().getEmail());
        userData.add(userModel.getData().getUser().getContacts().getVk());
        List<UserModelRes.Repo> repositories = userModel.getData().getUser().getRepositories().getRepo();
        String git = "git";
        if(repositories.size() > 0){
            git = repositories.get(0).getGit();
        }
        userData.add(git);
        userData.add(userModel.getData().getUser().getPublicInfo().getBio());
        mDataManager.getPreferencesManager().saveUserProfileData(userData);
        String photo = userModel.getData().getUser().getPublicInfo().getPhoto();
        String avatar = userModel.getData().getUser().getPublicInfo().getAvatar();
        mDataManager.getPreferencesManager().saveUserPhoto(Uri.parse(photo));
        mDataManager.getPreferencesManager().saveUserAvatar(Uri.parse(avatar));
    }
}
