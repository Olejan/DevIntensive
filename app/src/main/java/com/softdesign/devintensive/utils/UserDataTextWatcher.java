package com.softdesign.devintensive.utils;

import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.softdesign.devintensive.R;

/**
 * Created by Masha on 07.07.2016.
 */
public class UserDataTextWatcher implements TextWatcher {
    public enum Type {Phone, Email, Vk, Git};
    public EditText mEditText;
    public Type mType;
    public int mMin; // минимальное количество символов
    public int mMax; // максимальное количество символов
    private int mAdded = 0;
    private String mBefore;
    private boolean mEditedFromCode;
    public UserDataTextWatcher(EditText et, Type type, int min, int max){
        super();
        mEditText = et;
        mType = type;
        if(min<=max) {// проверка на соответствие порядку min и max
            mMin = min;
            mMax = max;
        }else{
            mMin = max;
            mMax = min;
        }
    }
    public void afterTextChanged(Editable s){
        String str = mEditText.getText().toString();
        int len = str.length();
        int tp = mEditText.getSelectionStart();
        switch (mType) {
            case Phone: {
                if (/*str.length() < 11 && */mMax > 0 && len > mMax) {
                    mEditText.setText(str.subSequence(0, str.length() - 1));
                    if (tp == len) {
                        mEditText.setSelection(tp - 1);
                    } else {
                        mEditText.setSelection(tp);
                    }
                }
                break;
            }
            case Email:{

                break;
            }
            case Vk://vk.com/
            case Git:{//github.com/
                if(mEditedFromCode){
                    mEditedFromCode = false;
                    break;
                }
                if (mAdded > 0){
                    if (tp > 0 && tp <= mMax/*7*/) {
                        if (mBefore.length() < s.length()) {
                            mEditedFromCode = true;
                            mEditText.setText(mBefore);
                            //mEditedFromCode = true;
                            //mEditText.setSelection(7, 7);
                        }
                    }
                } else if (mAdded < 0){//vk.com/
                    //if(tp <= 6){
                    if(tp < mMax){
                        mEditedFromCode = true;
                        mEditText.setText(mBefore);
                        //mEditedFromCode = true;
                        //mEditText.setSelection(7, 7);
                    }
                }
                break;
            }
        }
    }
    public void beforeTextChanged(CharSequence s, int start, int count, int after){
        mAdded = after - count;
        mBefore = s.toString();
    }
    public void onTextChanged(CharSequence s, int start, int before, int count){
    }
}
