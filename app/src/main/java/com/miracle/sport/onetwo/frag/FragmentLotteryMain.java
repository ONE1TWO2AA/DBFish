package com.miracle.sport.onetwo.frag;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Message;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gongwen.marqueen.SimpleMF;
import com.miracle.R;
import com.miracle.base.network.GlideApp;
import com.miracle.base.network.ZCallback;
import com.miracle.base.network.ZClient;
import com.miracle.base.network.ZResponse;
import com.miracle.base.util.ContextHolder;
import com.miracle.base.view.CircleImageView;
import com.miracle.databinding.FragmentCpMainTopBinding;
import com.miracle.sport.onetwo.act.OneFragActivity;
import com.miracle.sport.onetwo.inter.CallBackListener;
import com.miracle.sport.onetwo.netbean.CPServer;
import com.miracle.sport.onetwo.netbean.CpListItem;
import com.miracle.sport.onetwo.netbean.LotteryCatListItem;
import com.miracle.sport.onetwo.netbean.LotteryCatTitleItem;
import com.miracle.sport.onetwo.operation.BussnisUtil;
import com.miracle.sport.onetwo.util.RandUtils;
import com.youth.banner.Banner;
import com.youth.banner.loader.ImageLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;

//FragmentCpMainBinding
public class FragmentLotteryMain extends HandleFragment<FragmentCpMainTopBinding>{
    public static int WHAT_GET_MARQUEE = 1;
    Banner banner;
    List<Spanned> mardatas = new ArrayList<Spanned>();
    FragmentCpMainTopBinding topBinding;

    LotteryListFragment newNumFrag;
    FragCpItemList subFrag;

    @Override
    public int getLayout() {
        return R.layout.fragment_cp_main;
    }

    @Override
    public void initView() {
        Log.i("TAG", "initView: xxxxxxxxxxx 1");
        setShowTitle(true);
        setTitle(getString(R.string.tab_name_home));
        getTitleBar().showLeft(false);
//        topBinding = DataBindingUtil.inflate(getActivity().getLayoutInflater(), R.layout.fragment_cp_main_top, null, false);
        topBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.fragment_cp_main_top, null, false);
        initTopHeader();

        subFrag = (FragCpItemList) getActivity().getSupportFragmentManager().findFragmentById(R.id.list_frag);
        subFrag.setShowBanner(false);
        subFrag.setCallBackListener(new CallBackListener<List<CpListItem>>() {
            @Override
            public void onStart() {
                if(subFrag.callBack.getPage() == 1) {
                    newNumFrag.loadData();
                    //最新公告
//                    BussnisUtil.getAllNewNumber(uiHandler ,WHAT_GET_MARQUEE);
                }
            }

            @Override
            public void onFinish(List<CpListItem> data) {
                if(data != null && data.size() > 0)
                {
                    topBinding.getRoot().setVisibility(View.VISIBLE);
                }else{
                    topBinding.getRoot().setVisibility(View.GONE);
                }
            }
        });

        subFrag.mAdapter.addHeaderView(topBinding.getRoot());
        subFrag.mAdapter.notifyDataSetChanged();
        subFrag.binding.getRoot().requestLayout();
    }

    private void initTopHeader() {
        initBanner();
        initMard(new ArrayList());
//        initHSuserBar();
        initHSuserBarType();
        initTicket();
        initButtons();

        newNumFrag = (LotteryListFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.main_farg_newnum_frag);
        newNumFrag.setShowSingle(true);
        LotteryCatTitleItem lotteryCatTitleItem = new LotteryCatTitleItem();
        lotteryCatTitleItem.setId(1);
        newNumFrag.setLotteryCatData(lotteryCatTitleItem);
        newNumFrag.loadData();

        //公告数据
        BussnisUtil.getAllNewNumber(uiHandler ,WHAT_GET_MARQUEE);
    }

    private void initButtons() {
//        View.OnClickListener subTitleClick = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                if(getActivity() instanceof MainActivity)
////                {
////                    MainActivity mainActivity = (MainActivity) getActivity();
//////                    mainActivity.switchTabIndex(2);
////                }
//            }
//        };
//        topBinding.mainFragMore1.setOnClickListener(subTitleClick);
//        topBinding.mainLl2.setOnClickListener(subTitleClick);

        //
        topBinding.getRoot().findViewById(R.id.main_farg_tryrand).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),OneFragActivity.class);
                intent.putExtra(OneFragActivity.EXTRA_KEY_FRAG_CLASS, RandomNumFragment.class);
                startActivity(intent);
            }
        });

        topBinding.getRoot().findViewById(R.id.main_frag_more3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OneFragActivity.class);
                intent.putExtra(OneFragActivity.EXTRA_KEY_FRAG_CLASS, Fragment1cp.class);
                startActivity(intent);
            }
        });

        topBinding.getRoot().findViewById(R.id.main_frag_more2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OneFragActivity.class);
                intent.putExtra(OneFragActivity.EXTRA_KEY_FRAG_CLASS, Fragment2cpChannelPager.class);
                startActivity(intent);
            }
        });
    }

    private void initTicket() {
        topBinding.mainLl1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OneFragActivity.class);
                intent.putExtra(OneFragActivity.EXTRA_KEY_FRAG_CLASS, CpHallFragment.class);
                startActivity(intent);
            }
        });
        topBinding.mainLl2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OneFragActivity.class);
                intent.putExtra(OneFragActivity.EXTRA_KEY_FRAG_CLASS, LotteryChartFragment.class);
                startActivity(intent);
            }
        });
    }
    private void initHSuserBarType(){
        ZClient.getService(CPServer.class).lotteryCategory().enqueue(new ZCallback<ZResponse<List<LotteryCatTitleItem>>>(){
            @Override
            protected void onSuccess(ZResponse<List<LotteryCatTitleItem>> zResponse) {
                for(LotteryCatTitleItem item : zResponse.getData()){
                    addToHS(item.getName(),item.getId(),item.getPic());
                }
            }
        });

//        addToHS("双色球",1,R.drawable.ssq);
//        addToHS("大乐透",2,R.drawable.dlt);
//        addToHS("福彩3d",3,R.drawable.fc3d);
//        addToHS("排列3",5,R.drawable.pl3);
//        addToHS("排列5",6,R.drawable.pl5);
//        addToHS("七星彩",7,R.drawable.qlc);
//        addToHS("七合彩",8,R.drawable.qxc);
    }

    private void addToHS(final String str, final int key, String picUrl){
//        View view = getLayoutInflater().inflate(R.layout.main_frag_hs1_item, null);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.main_frag_hs1_item,null);
        CircleImageView iv = (CircleImageView)view.findViewById(R.id.main_farg_hs1_iv);
        ((TextView)view.findViewById(R.id.main_farg_hs1_tv1)).setText(str);
        GlideApp.with(mContext).load(picUrl).placeholder(R.mipmap.defaule_img).into(iv);
        ((TextView)view.findViewById(R.id.main_farg_hs1_tv2)).setText((40+RandUtils.random.nextInt(50))+"%");
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                key
                int tmpkey = key;
                String tmpString = str.toString();
                Message msg = new Message();
                msg.what = LotteryListFragment.WHAT_KEY_SETLOTTERYCATDATA;
                msg.arg1 = tmpkey;
//                msg.obj = tmpString;

                Intent i = new Intent(getActivity(), OneFragActivity.class);
                i.putExtra(OneFragActivity.EXTRA_KEY_FRAG_CLASS, LotteryListFragment.class);
                i.putExtra(OneFragActivity.EXTRA_KEY_MSG, msg);
                startActivity(i);
            }
        });
        LinearLayout main_frag_hs_ll = topBinding.getRoot().findViewById(R.id.main_frag_hs_ll);
        main_frag_hs_ll.addView(view);
    }

    private void initHSuserBar() {
        HorizontalScrollView main_frag_hs2 = topBinding.getRoot().findViewById(R.id.main_frag_hs2);
        LinearLayout main_frag_hs_ll = topBinding.getRoot().findViewById(R.id.main_frag_hs_ll);
        List<Bitmap> bitmaps = null;
        List<String> userName = null;
        try {
            bitmaps = RandUtils.randImgs(mContext,7);
            userName = RandUtils.randUserName(mContext, 7);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        View.OnClickListener onClickListener = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(getActivity() != null && getActivity() instanceof MainActivity)
//                {
//                    MainActivity mainActivity = (MainActivity) getActivity();
//                    mainActivity.showChatAct(getActivity());
//                }
//            }
//        };
        for(int i = 0; i < bitmaps.size(); i++){
//            View view = getLayoutInflater().inflate(R.layout.main_frag_hs1_item, null);
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.main_frag_hs1_item,null);
            CircleImageView iv = (CircleImageView)view.findViewById(R.id.main_farg_hs1_iv);
            ((TextView)view.findViewById(R.id.main_farg_hs1_tv1)).setText(userName.get(i));
            iv.setImageBitmap(bitmaps.get(i));
//            view.setOnClickListener(onClickListener);
            main_frag_hs_ll.addView(view);
        }
    }

    private void initMard(List<Spanned> list) {
        mardatas.add(Html.fromHtml("新中奖号码已更新"));
        mardatas.addAll(list);
        SimpleMF<Spanned> marqueeFactory2 = new SimpleMF(mContext);
        marqueeFactory2.setData(mardatas);
        topBinding.marqueeView3.setMarqueeFactory(marqueeFactory2);
        topBinding.marqueeView3.stopFlipping();
        topBinding.marqueeView3.startFlipping();
    }

    private void initBanner() {
        banner = topBinding.mainFarg1Banner;
        ArrayList images = new ArrayList<>();
        images.add(R.mipmap.b3);
        images.add(R.mipmap.b5);
        banner.setImages(images).setImageLoader(new ImageLoader() {
            @Override
            public void displayImage(Context context, Object path, ImageView imageView) {
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                Glide.with(ContextHolder.getContext()).load(path).into(imageView);
            }
        });
        banner.setImages(images);
        banner.setDelayTime(1000 * 3);
        banner.start();
    }

    @Override
    public void initListener() {

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void loadData() {

    }

    @Override
    public void onHandleMessage(Message msg) {
        if(msg.what == WHAT_GET_MARQUEE){
            if(msg.obj != null && msg.obj instanceof List)
            {
                List<LotteryCatListItem> list = (List<LotteryCatListItem>) msg.obj;
                List<Spanned> data = new ArrayList<>();
                for(LotteryCatListItem item : list){
                    String str = item.getName() + ": <font color=\"#ff0000\">" + item.getHost_num() + "</font> <font color=\"#0000ff\">" + item.getFirst_num()+"</font>";
                    data.add(Html.fromHtml(str));
                }
                mardatas.clear();
                initMard(data);
            }
        }
    }
}
