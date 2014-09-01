package com.bluemor.reddotface.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bluemor.reddotface.R;
import com.bluemor.reddotface.util.Util;
import com.bluemor.reddotface.view.DragLayout;
import com.bluemor.reddotface.view.DragLayout.DragListener;
import com.nineoldandroids.view.ViewHelper;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.Random;

public class MainActivity extends Activity {

    private DragLayout sl;
    private GridView gv_img;
    private GalleryAdapter adapter;
    private ListView lv;
    private TextView tv_noimg;
    private ImageView iv_icon, iv_bottom;

    private void initImageLoader() {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_face)
                .showImageForEmptyUri(R.drawable.default_face)
                .showImageOnFail(R.drawable.default_face).cacheInMemory(true)
                .cacheOnDisc(true).considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300, true, true, true))
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(
                this).defaultDisplayImageOptions(defaultOptions).memoryCache(
                new WeakMemoryCache());

        ImageLoaderConfiguration config = builder.build();
        ImageLoader.getInstance().init(config);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initImageLoader();
        initDragLayout();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.addAll(Util.getGalleryPhotos(this));
        if (adapter.isEmpty()) {
            tv_noimg.setVisibility(View.VISIBLE);
        } else {
            tv_noimg.setVisibility(View.GONE);
            String s = "file://" + adapter.getItem(0);
            ImageLoader.getInstance().displayImage(s, iv_icon);
            ImageLoader.getInstance().displayImage(s, iv_bottom);
        }
        iv_icon.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
    }

    private void initDragLayout() {
        sl = (DragLayout) findViewById(R.id.sl_main);
        sl.setDragListener(new DragListener() {
            @Override
            public void onOpen() {
                lv.smoothScrollToPosition(new Random().nextInt(15));
            }

            @Override
            public void onClose() {
            }

            @Override
            public void onDrag(float percent) {
                animate(percent);
            }
        });
    }

    private void animate(float percent) {
        ViewGroup vg_left = sl.getVg_left();
        ViewGroup vg_main = sl.getVg_main();

        float wid = vg_left.getWidth();

        ViewHelper.setTranslationX(vg_left, -wid / 2.2f + wid / 2.2f * percent);
        ViewHelper.setScaleX(vg_left, 0.5f + 0.5f * percent);
        ViewHelper.setScaleY(vg_left, 0.5f + 0.5f * percent);
        ViewHelper.setAlpha(vg_left, percent);

        ViewHelper.setTranslationX(vg_main, -wid / 2.5f * percent);
        ViewHelper.setScaleX(vg_main, 1f - percent * 0.25f);
        ViewHelper.setScaleY(vg_main, 1f - percent * 0.25f);
        ViewHelper.setAlpha(iv_icon, 1f - percent);

        int color = (Integer) Util.evaluate(percent,
                Color.parseColor("#000000"),
                Color.parseColor("#009990"));
        sl.setBackgroundColor(color);
    }

    private void initView() {
        iv_icon = (ImageView) findViewById(R.id.iv_icon);
        iv_bottom = (ImageView) findViewById(R.id.iv_bottom);
        gv_img = (GridView) findViewById(R.id.gv_img);
        tv_noimg = (TextView) findViewById(R.id.iv_noimg);
        gv_img.setFastScrollEnabled(true);
        adapter = new GalleryAdapter(this);
        gv_img.setAdapter(adapter);
        gv_img.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                Intent intent = new Intent(MainActivity.this,
                        ImageActivity.class);
                intent.putExtra("path", adapter.getItem(position));
                startActivity(intent);
            }
        });
        lv = (ListView) findViewById(R.id.lv);
        lv.setAdapter(new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_list_item_1, new String[] {
                        "NewBee",
                        "ViCi Gaming", "Evil Geniuses", "Team DK",
                        "Invictus Gaming", "LGD", "Natus Vincere",
                        "Team Empire", "Alliance", "Cloud9", "Titan",
                        "Mousesports", "Fnatic", "Team Liquid", "MVP Phoenix"
                }));
        iv_icon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sl.open();
            }
        });
    }

}