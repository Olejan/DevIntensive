package com.softdesign.devintensive.data.managers;

import android.content.Context;

import com.softdesign.devintensive.data.network.PicassoCach;
import com.softdesign.devintensive.data.network.RestService;
import com.softdesign.devintensive.data.network.ServiceGenerator;
import com.softdesign.devintensive.data.network.req.UserLoginReq;
import com.softdesign.devintensive.data.network.res.UserListRes;
import com.softdesign.devintensive.data.network.res.UserModelRes;
import com.softdesign.devintensive.data.storage.models.DaoSession;
import com.softdesign.devintensive.data.storage.models.User;
import com.softdesign.devintensive.data.storage.models.UserDao;
import com.softdesign.devintensive.utils.DevintensiveApplication;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class DataManager {
    private static DataManager INSTANCE = null;
    private Picasso mPicasso;

    private Context mContext;
    private PreferencesManager mPreferencesManager;
    private RestService mRestService;

    private DaoSession mDaoSession;

    public DataManager() {
        this.mPreferencesManager = new PreferencesManager();
        this.mContext = DevintensiveApplication.getContext();
        this.mRestService = ServiceGenerator.createService(RestService.class);
        this.mPicasso = new PicassoCach(mContext).getPicassoInstance();
        this.mDaoSession = DevintensiveApplication.getsDaoSession();
    }

    public static DataManager getInstance(){
        if(INSTANCE == null){
            INSTANCE = new DataManager();
        }
        return INSTANCE;
    }

    public PreferencesManager getPreferencesManager(){
        return mPreferencesManager;
    }

    public Context getContext(){
        return mContext;
    }

    public Picasso getmPicasso() {
        return mPicasso;
    }

    //region ================ Network =================
    public Call<UserModelRes> loginUser(UserLoginReq userLoginReq){
        return  mRestService.loginUser(userLoginReq);
    }

    public Call<UserListRes> getUserListFromNetwork() {
        return mRestService.getUserList();
    }
    //endregion

    //region ================ Database =================


    public DaoSession getmDaoSession() {
        return mDaoSession;
    }

    public List<User> getUserListFromDb(){
        List<User> userList = new ArrayList<>();
        try{
            userList = mDaoSession.queryBuilder(User.class)
                    .where(UserDao.Properties.CodeLines.gt(0))
                    .orderDesc(UserDao.Properties.CodeLines)
                    .build()
                    .list();
        }catch (Exception e){
            e.printStackTrace();
        }

        return userList;
    }

    public List<User> getUserListByName(String query){
        List<User> userList = new ArrayList<>();
        try{
            userList = mDaoSession.queryBuilder(User.class)
                    .where(UserDao.Properties.Rating.gt(0), UserDao.Properties.SearchName.like("%" + query.toUpperCase() + "%"))
                    .orderDesc(UserDao.Properties.CodeLines)
                    .build()
                    .list();
        }catch (Exception e){
            e.printStackTrace();
        }
        return userList;
    }
    //endregion
}
