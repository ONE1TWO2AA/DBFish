package com.miracle.sport;

import android.view.View;

import com.miracle.R;
import com.miracle.base.BaseActivity;
import com.miracle.base.GOTO;
import com.miracle.databinding.ActivitySportMainBinding;
import com.miracle.sport.community.fragment.CommunityFragment;
import com.miracle.sport.home.fragment.HomeFragment;
import com.miracle.sport.me.fragment.MeFragment;
import com.miracle.sport.onetwo.frag.FragmentLotteryMain;
import com.miracle.sport.schedule.fragment.FragClubeTypeChannelVP;

/**
 * Created by Michael on 2018/10/27 13:32 (星期六) <->w<->
 */
public class SportMainActivity extends BaseActivity<ActivitySportMainBinding> {
    @Override
    public int getLayout() {
        return R.layout.activity_sport_main;
    }

    @Override
    public void initView() {
        hideTitle();
        showContent();
        //HomeFragment   FragClubeTypeChannelVP
        binding.zRadiogroup.setUp(getSupportFragmentManager(), R.id.container, new HomeFragment(), new FragClubeTypeChannelVP(), new CommunityFragment(), new MeFragment());
    }

    @Override
    public void overridePendingTransition(int enterAnim, int exitAnim) {
//        super.overridePendingTransition(enterAnim, exitAnim);
    }

    @Override
    public void initListener() {
        binding.tvContactCustomerService.setOnClickListener(this);
        binding.rlGroupChat.setOnClickListener(this);
    }

    @Override
    public void loadData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvContactCustomerService:
                GOTO.CustomerServiceActivity(mContext);
                break;
            case R.id.rlGroupChat:
                GOTO.ChatActivity(mContext);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
    }
}
