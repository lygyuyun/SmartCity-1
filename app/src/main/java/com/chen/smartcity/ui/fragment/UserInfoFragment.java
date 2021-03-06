package com.chen.smartcity.ui.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.chen.smartcity.R;
import com.chen.smartcity.base.BaseFragment;
import com.chen.smartcity.model.bean.UserInfoResult;
import com.chen.smartcity.presenter.IUserInfoPresenter;
import com.chen.smartcity.ui.activity.LoginActivity;
import com.chen.smartcity.ui.activity.MainActivity;
import com.chen.smartcity.ui.activity.mine.DingdanActivity;
import com.chen.smartcity.ui.activity.mine.MeetActivity;
import com.chen.smartcity.ui.activity.mine.UpdatePasswordActivity;
import com.chen.smartcity.ui.activity.mine.UserInfoActivity;
import com.chen.smartcity.utils.Constants;
import com.chen.smartcity.utils.LogUtils;
import com.chen.smartcity.utils.PresenterManager;
import com.chen.smartcity.view.IUserInfoCallback;

public class UserInfoFragment extends BaseFragment implements IUserInfoCallback {

    private ImageView cover;
    private LinearLayout settingLL;
    private Button loginBtn;
    private TextView loginOutBtn, username, userinfoTv, dingdanTv, passwordTv, meetTv;
    private IUserInfoPresenter mMinePresenter;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void initView(View rootView) {
        setUpState(State.SUCCESS);
        settingLL = rootView.findViewById(R.id.mine_setting);
        loginBtn = rootView.findViewById(R.id.mine_login);
        loginOutBtn = rootView.findViewById(R.id.mine_logout);
        cover = rootView.findViewById(R.id.mine_cover);
        username = rootView.findViewById(R.id.mine_username);
        userinfoTv = rootView.findViewById(R.id.mine_user_info);
        dingdanTv = rootView.findViewById(R.id.mine_user_diangdan);
        passwordTv = rootView.findViewById(R.id.mine_user_password);
        meetTv = rootView.findViewById(R.id.mine_user_meet);

        if (findByKey("token") != null) {
            settingLL.setVisibility(View.VISIBLE);
            loginOutBtn.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initPresenter() {
        mMinePresenter = PresenterManager.getInstance().getMinePresenter();
        mMinePresenter.registerViewCallback(this);
    }

    @Override
    protected void initListener() {
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.d(this, "login");
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        loginOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeByKey("token");
                getActivity().finish();
                startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });

        userinfoTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), UserInfoActivity.class));
            }
        });

        dingdanTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), DingdanActivity.class));
            }
        });

        passwordTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), UpdatePasswordActivity.class));
            }
        });

        meetTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), MeetActivity.class));
            }
        });
    }

    @Override
    protected void loadData() {
        if (mMinePresenter != null && findByKey("token") != null) {
            mMinePresenter.getUserInfo(findByKey("token"));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        if (findByKey("token") != null) {
            settingLL.setVisibility(View.VISIBLE);
            loginOutBtn.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.GONE);

            if (mMinePresenter != null) {
                mMinePresenter.getUserInfo(findByKey("token"));
            }
        }
    }

    @Override
    public void onLoadedUserInfo(UserInfoResult result) {
        if (result.getUser().getAvatar() == "") {
            cover.setImageResource(R.mipmap.ic_launcher_round);
        } else {
            Glide.with(getContext()).load(Constants.BASE_URL + result.getUser().getAvatar()).into(cover);
        }
        username.setText(result.getUser().getUserName());
        insertByKey("userId", String.valueOf(result.getUser().getUserId()));

        setUpState(State.SUCCESS);
    }

    @Override
    public void onError() {

    }

    @Override
    public void onLoading() {
        setUpState(State.LOADING);
    }

    @Override
    public void onEmpty() {

    }

    @Override
    protected void release() {
        super.release();
        if (mMinePresenter != null) {
            mMinePresenter.unregisterViewCallback(this);
        }
    }
}
