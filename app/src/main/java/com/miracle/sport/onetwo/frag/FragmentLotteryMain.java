package com.miracle.sport.onetwo.frag;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Message;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
import com.miracle.databinding.FragmentCpMainTopBinding;
import com.miracle.sport.onetwo.act.OneFragActivity;
import com.miracle.sport.onetwo.netbean.FSServer;
import com.miracle.sport.onetwo.netbean.FishType;
import com.miracle.sport.onetwo.netbean.LotteryCatListItem;
import com.youth.banner.Banner;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;
import java.util.List;

//FragmentCpMainBinding
public class FragmentLotteryMain extends HandleFragment<FragmentCpMainTopBinding>{
    public static int WHAT_GET_MARQUEE = 1;
    Banner banner;
    List<Spanned> mardatas = new ArrayList<Spanned>();
    FragmentCpMainTopBinding topBinding;

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
        topBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.fragment_cp_main_top, null, false);
        initTopHeader();

        subFrag = addSubFragment(R.id.list_frag, FragCpItemList.class, new AddSubFragListener<FragCpItemList>() {
            @Override
            public void onFindFromTag(FragCpItemList fragCpItemList) {
                subFrag = fragCpItemList;
                setupSubFrag();
            }

            @Override
            public void onNewInstance(FragCpItemList fragCpItemList) {
                subFrag = fragCpItemList;
            }

            @Override
            public void onCommit() {
                setupSubFrag();
            }
        });

    }

    public void setupSubFrag(){
        subFrag.setFragLifeListner(new FragLifeListner() {
            @Override
            public void onViewCreated() {
                subFrag.mAdapter.addHeaderView(topBinding.getRoot());
                subFrag.mAdapter.notifyDataSetChanged();
                subFrag.binding.getRoot().requestLayout();
                initHSuserBarType();
                subFrag.loadData();
            }

            @Override
            public void onDestoryView() {

            }
        });
    }

    private void initTopHeader() {
        initBanner();
        initMard(new ArrayList());
        initTicket();
        initButtons();

        //公告数据
//        BussnisUtil.getAllNewNumber(uiHandler ,WHAT_GET_MARQUEE);
//        ZClient.getService(FSServer.class)

    }

    private void initButtons() {
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
        ZClient.getService(FSServer.class).fishType().enqueue(new ZCallback<ZResponse<List<FishType>>>(){
            @Override
            protected void onSuccess(ZResponse<List<FishType>> zResponse) {
                LinearLayout main_frag_hs_ll = topBinding.getRoot().findViewById(R.id.main_frag_hs_ll);
                main_frag_hs_ll.removeAllViews();
                for(FishType item : zResponse.getData()){
                    //排除 ‘推荐’
                    if(1 != item.getId())
                        addToHS(item.getName(),item.getId(),item.getPic());
                }
            }
        });
    }

    private void addToHS(final String str, final int key, String picUrl){
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.main_frag_hs1_item,null);
        ImageView iv = view.findViewById(R.id.main_farg_hs1_iv);
        ((TextView)view.findViewById(R.id.main_farg_hs1_tv1)).setText(str);
        GlideApp.with(mContext).load(picUrl).placeholder(R.mipmap.defaule_img).into(iv);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开分类文章列表
                Intent i = new Intent(getActivity(), OneFragActivity.class);
                i.putExtra(OneFragActivity.EXTRA_KEY_FRAG_CLASS, FragCpItemList.class);
                Message msg = new Message();
                msg.what = FragCpItemList.MSG_WHAT_KEY_REQKEY;
                msg.arg1 = key;
                i.putExtra(OneFragActivity.EXTRA_KEY_MSG, msg);
                i.putExtra(OneFragActivity.EXTRA_KEY_ACT_TITLE, ""+str);
                startActivity(i);
            }
        });
        LinearLayout main_frag_hs_ll = topBinding.getRoot().findViewById(R.id.main_frag_hs_ll);
        main_frag_hs_ll.addView(view);
    }

    private void initMard(List<Spanned> list) {
        mardatas.add(Html.fromHtml("游戏资讯已更新"));
        mardatas.add(Html.fromHtml("炮台大全已更新"));
        mardatas.add(Html.fromHtml("游戏技巧已更新"));
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
        images.add(R.mipmap.banner_f2);
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
