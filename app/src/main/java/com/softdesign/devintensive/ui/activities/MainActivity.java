package com.softdesign.devintensive.ui.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.UserDataTextWatcher;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = ConstantManager.TAG_PREFIX + "Main Activity";

    private DataManager mDataManager;
    private int mCurrentEditMode=0;

    @BindView(R.id.call_img) ImageView mCallImg;
    @BindView(R.id.send_msg_iv) ImageView mSendMsgImg;
    @BindView(R.id.goto_vk_iv) ImageView mGotoVkImg;
    @BindView(R.id.goto_git_iv) ImageView mGotoGitImg;
    @BindView(R.id.main_coordinator_container) CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.navigation_drawer) DrawerLayout mNavigationDrawer;
    @BindView(R.id.fab) FloatingActionButton mFab;
    @BindView(R.id.profile_placeholder) RelativeLayout mProfilePlaceholder;
    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout mCollapsingToolbar;
    @BindView(R.id.appbar_layout) AppBarLayout mAppBarLayout;
    @BindView(R.id.user_photo_img) ImageView mProfileImage;
    @BindView(R.id.phone_et) EditText mUserPhone;
    @BindView(R.id.mail_et) EditText mUserMail;
    @BindView(R.id.vk_profile_et) EditText mUserVk;
    @BindView(R.id.git_repo_et) EditText mUserGit;
    @BindView(R.id.about_et) EditText mUserBio;
    @BindViews({R.id.phone_et, R.id.mail_et, R.id.vk_profile_et, R.id.git_repo_et, R.id.about_et}) List<EditText> mUserInfoViews;

    @BindView(R.id.rating_tv) TextView mUserValueRating;
    @BindView(R.id.strings_tv) TextView mUserValueCodeLines;
    @BindView(R.id.projects_tv) TextView mUserValueProjects;
    @BindViews({R.id.rating_tv, R.id.strings_tv, R.id.projects_tv}) List<TextView> mUserValueViews;

    @BindView(R.id.user_email_txt) TextView mUserEmail;
    @BindView(R.id.user_name_txt) TextView mUserName;
    @BindView(R.id.user_avatar) ImageView mUserAvatar;/**/

    private AppBarLayout.LayoutParams mAppBarParams = null;

    private File mPhotoFile = null;
    private Uri mSelectedImage = null;

    private enum EditType {Phone, Email, Vk, Git};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");

        ButterKnife.bind(this);
        mDataManager = DataManager.getInstance();
        mProfilePlaceholder.setOnClickListener(this);
        mFab.setOnClickListener(this);
        mCallImg.setOnClickListener(this);
        mSendMsgImg.setOnClickListener(this);
        mGotoVkImg.setOnClickListener(this);
        mGotoGitImg.setOnClickListener(this);

        setupToolbar();
        setupDrawer();
        loadUserFields();
        initUserInfoValue();

        //устанавливаю валидаторы
        UserDataTextWatcher udtw1 = new UserDataTextWatcher(mUserVk, UserDataTextWatcher.Type.Vk, 0, getString(R.string.user_profile_uri_vk).length());
        mUserVk.addTextChangedListener(udtw1);
        UserDataTextWatcher udtw2 = new UserDataTextWatcher(mUserGit, UserDataTextWatcher.Type.Git, 0, getString(R.string.user_profile_uri_git).length());
        mUserGit.addTextChangedListener(udtw2);
        UserDataTextWatcher udtw3 = new UserDataTextWatcher(mUserPhone, UserDataTextWatcher.Type.Phone, 11, 20);
        mUserPhone.addTextChangedListener(udtw3);

        //здесь тоже
        mUserPhone.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                textValidate(mUserPhone, EditType.Phone);
                return false;
            }
        });

        mUserMail.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                textValidate(mUserMail, EditType.Email);
                return false;
            }
        });

        //List<String> test = mDataManager.getPreferencesManager().loadUserProfileData();
        Uri uri = mDataManager.getPreferencesManager().loadUserPhoto();
        Picasso.with(this)
                .load(uri)
                .placeholder(R.drawable.header_bg) //// TODO: 02.07.2016 сделать плейсхолдер и transform + crop
                .into(mProfileImage);

        if(savedInstanceState == null){
            //запускается впервые
            //showSnackBar("Активити запускается впервые");
            //showToast("Активити запускается впервые");
        } else {
            //уже запускалось
            //showSnackBar("Активити уже запускалось");
            //showToast("Активити уже запускалось");
            mCurrentEditMode = savedInstanceState.getInt(ConstantManager.CURRENT_EDIT_MODE, 0);
            changeEditMode(mCurrentEditMode);
        }
    }

    /*
     * Валидация данных пользователя. Если не правильно - текст красным цветом
     */
    public void textValidate(EditText et, EditType type){
        if(et == null)
            return;
        boolean result = true;
        String str = et.getText().toString();
        if(str.isEmpty())
            return;
        switch(type){
            case Phone:
                result = phoneValidate(str);
                break;
            case Email:
                result = emailValidate(str);
                break;
        }
        if(result == false)
            et.setTextColor(getResources().getColor(R.color.red));
        else
            et.setTextColor(getResources().getColor(R.color.black));
    }
/*
 * Валидация телефона
 */
    private boolean phoneValidate(String str){//проверка номера телефона
        if(str.length() < 11 || str.length() > 20)
            return false;
        Pattern p = Pattern.compile(getString(R.string.phone_regexp));
        Matcher m = p.matcher(str);
        return m.matches();
    }
    /*
    * Валидация почты
    */
    private boolean emailValidate(String str){//проверка email
        char[] chars = str.trim().toCharArray();
        String[] sd = str.split("@");
        if(sd.length != 2)
            return false;
        if(sd[0].length() < 3)//имя < 3
            return false;
        String[] sp = sd[1].split("\\.");
        if(sp.length < 2)
            return false;
        if(sp[0].length() < 2 && sp.length == 2 || sp[sp.length - 1].length() < 2)//если всего 2 части и первая < 2 или домен < 2
            return false;
        /*Pattern p = Pattern.compile(getString(R.string.email_regexp));
        Matcher m = p.matcher(str);
        return m.matches();*/
        return true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        showToast("MainActivity restarted");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            mNavigationDrawer.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(mNavigationDrawer.isDrawerOpen(GravityCompat.START)){
            mNavigationDrawer.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }
    /*
     * Обрабатываем нажатия кнопок
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                //showSnackBar("На кнопку Fab click");
                if(mCurrentEditMode == 0){
                    mCurrentEditMode = 1;
                    changeEditMode(1);
                } else {
                    mCurrentEditMode = 0;
                    changeEditMode(0);
                }
                break;
            case R.id.profile_placeholder:
                //// TODO: 30.06.2016 сделать выбор, откуда загружать фото
                showDialog(ConstantManager.LOAD_PROFILE_PHOTO);
                break;
            case R.id.call_img: {
                String phone = mUserPhone.getText().toString();
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(getString(R.string.uri_header_tel) + phone));
                startActivity(intent);
                break;
            }
            case R.id.send_msg_iv: {
                String addr = mUserMail.getText().toString();
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(getString(R.string.uri_header_mailto)+addr));
                startActivity(Intent.createChooser(intent, getString(R.string.text_send_email)));
                break;
            }
            case R.id.goto_vk_iv: {
                String s = mUserVk.getText().toString();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.uri_header_https) + s));
                startActivity(intent);
                break;
            }
            case R.id.goto_git_iv: {
                String s = mUserGit.getText().toString();
                    if(!s.isEmpty()) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.uri_header_https)+s));
                    startActivity(intent);
                }else{
                    showSnackBar(getString(R.string.text_feel_data));
                }
                break;
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");
        outState.putInt(ConstantManager.CURRENT_EDIT_MODE, mCurrentEditMode);
    }

    public void showSnackBar(String message){
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }
    /*private void runWithDelay(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideProgress();
            }
        }, 3000);
    }*/
    private  void setupToolbar(){
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();

        mAppBarParams = (AppBarLayout.LayoutParams) mCollapsingToolbar.getLayoutParams();
        if (actionBar!=null){
            actionBar.setHomeAsUpIndicator(R.drawable.ic_dehaze_black_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupDrawer(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                showSnackBar(item.getTitle().toString());
                item.setChecked(true);
                mNavigationDrawer.closeDrawer(GravityCompat.START);
                return false;
            }
        });
    }

    /**
     * Получение результата из другой Activity (фото из камеры или галереи)
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case ConstantManager.REQUEST_GALARY_PICTURE:
                if (resultCode == RESULT_OK && data != null){
                    mSelectedImage = data.getData();

                    insertProfileImage(mSelectedImage);
                }
                break;
            case ConstantManager.REQUEST_CAMERA_PICTURE:
                if (resultCode == RESULT_OK && mPhotoFile != null) {
                    mSelectedImage = Uri.fromFile(mPhotoFile);
                    insertProfileImage(mSelectedImage);
                }
                break;
        }
    }

    /**
     * Переключает режим редактирования
     * @param mode если 1 - режим редактирования, если 0 - режим просмотра
     */
    private  void changeEditMode(int mode){
        if(mCurrentEditMode == 1) {
            for (EditText userValue : mUserInfoViews) {
                userValue.setEnabled(true);
                userValue.setFocusable(true);
                userValue.setFocusableInTouchMode(true);
                mFab.setImageResource(R.drawable.ic_check_black_24dp);

                showProfilePlaceholder();
               // lockToolbar();
                //mCollapsingToolbar.setExpandedTitleColor(Color.TRANSPARENT);

                mUserPhone.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
            mUserPhone.setFocusableInTouchMode(true);
        }else{
            for (EditText userValue : mUserInfoViews) {
                userValue.setEnabled(false);
                userValue.setFocusable(false);
                userValue.setFocusableInTouchMode(false);
                mFab.setImageResource(R.drawable.ic_create_black_24dp);

                hideProfilePlaceholder();
               // unlockToolbar();

                saveUserFields();
                mCollapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.white));
            }
        }
    }
    /*
     * Загружаю пользовательские анные
     */
    private void loadUserFields(){
        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileData();
        for (int i = 0; i < userData.size(); i++){
            mUserInfoViews.get(i).setText(userData.get(i));
        }
        /*List<String> names = mDataManager.getPreferencesManager().loadUserProfileName();
        String name = names.get(0) + " " + names.get(1);
        mUserName.setText(name);*/
    }
    private void initUserInfoValue(){
        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileValues();
        for(int i = 0; i < userData.size(); i++){
            mUserValueViews.get(i).setText(userData.get(i));
        }
    }
    /*
     * сохраняю пользовательские данные
     */
    private void saveUserFields(){
        List<String> userData = new ArrayList<>();
        for (EditText userFieldView : mUserInfoViews) {
            userData.add(userFieldView.getText().toString());
        }
        mDataManager.getPreferencesManager().saveUserProfileData(userData);
    }

    private void loadPhotoFromGalary(){
        Intent takeGalaryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        takeGalaryIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(takeGalaryIntent, getString(R.string.user_profile_chose_message)), ConstantManager.REQUEST_GALARY_PICTURE);
    }

    private void loadPhotoFromCamera(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent takeCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                mPhotoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
                // TODO: 30.06.2016 сделать обработку ошибок
            }
            if (mPhotoFile != null) {
                // TODO: 30.06.2016 передать фотофайл в интент
                takeCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
                startActivityForResult(takeCaptureIntent, ConstantManager.REQUEST_CAMERA_PICTURE);
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, ConstantManager.CAMERA_REQUEST_PERMISSION_CODE
            );

            Snackbar.make(mCoordinatorLayout, getString(R.string.text_give_allow), Snackbar.LENGTH_LONG)
                    .setAction("Разрешить", new View.OnClickListener(){
                        @Override
                        public void onClick(View v){
                            openApplicationSettings();
                        }
                    }).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == ConstantManager.CAMERA_REQUEST_PERMISSION_CODE && grantResults.length == 2){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // TODO: 02.07.2016 тут обрабатываем разрешение (разрешение получено)
                // например, вывести сообщение или обработать какой-то логикой, если нужно
            }
        }
        if(grantResults[1] == PackageManager.PERMISSION_GRANTED){
            // TODO: 02.07.2016 тут обрабатываем разрешение (разрешение получено)
            //
        }
    }

    private void hideProfilePlaceholder(){
        mProfilePlaceholder.setVisibility(View.GONE);
    }

    private void showProfilePlaceholder(){
        mProfilePlaceholder.setVisibility(View.VISIBLE);
    }

    private void lockToolbar(){
        mAppBarLayout.setExpanded(true, true);
        mAppBarParams.setScrollFlags(0);
        mCollapsingToolbar.setLayoutParams(mAppBarParams);
    }

    private void unlockToolbar(){
        mAppBarParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
        mCollapsingToolbar.setLayoutParams(mAppBarParams);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id){
            case ConstantManager.LOAD_PROFILE_PHOTO:
                String[] selectItems = {getString(R.string.user_profile_dialog_galary),
                        getString(R.string.user_profile_dialog_camera),
                        getString(R.string.user_profile_dialog_cancel)};

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.user_profile_dialog_title));
                builder.setItems(selectItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int choiceItem) {
                        switch (choiceItem){
                            case 0:
                                //// TODO: 30.06.2016 загрузить из галереи
                                loadPhotoFromGalary();
                                //showSnackBar("загрузить из галереи");
                                break;
                            case 1:
                                //// TODO: 30.06.2016 сделать снимок
                                loadPhotoFromCamera();
                                //showSnackBar("сделать снимок");
                                break;
                            case 2:
                                //// TODO: 30.06.2016 отмена
                                dialog.cancel();
                                //showSnackBar("отмена");
                                break;
                        }
                    }
                });
                return builder.create();
            default:
                return null;
        }
    }

    private File createImageFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_"+timeStamp+"_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, image.getAbsolutePath());

        this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        return image;
    }

    private void insertProfileImage(Uri selectedImage) {
        Picasso.with(this)
                .load(selectedImage)
                .into(mProfileImage);
        //// TODO: 02.07.2016 сделать плейсхолдер и transform + crop
        mDataManager.getPreferencesManager().saveUserPhoto(selectedImage);
    }

    public void openApplicationSettings(){
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"+getPackageName()));
        startActivityForResult(appSettingsIntent, ConstantManager.PERMISSION_REQUEST_SETTINGS_CODE);
    }
}
