package com.guet.flexbox.preview;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Layout;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.litho.Column;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.ComponentTree;
import com.facebook.litho.LithoView;
import com.facebook.litho.widget.Text;
import com.facebook.litho.widget.VerticalScroll;
import com.facebook.yoga.YogaAlign;
import com.guet.flexbox.DynamicBox;
import com.guet.flexbox.EventListener;
import com.guet.flexbox.EventType;
import com.luke.flexbox.preview.R;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import java.io.ByteArrayInputStream;
import java.util.Map;

import ch.ielse.view.SwitchView;
import es.dmoral.toasty.Toasty;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PreviewActivity
        extends AppCompatActivity
        implements View.OnClickListener,
        Runnable,
        EventListener,
        NestedScrollView.OnScrollChangeListener,
        SwipeRefreshLayout.OnRefreshListener {

    private class DataCallback implements Callback<Map<String,Object>> {

        @Override
        public void onResponse(
                @NonNull Call<Map<String, Object>> call,
                @NonNull Response<Map<String, Object>> response) {
            mCacheData = response.body();
        }

        @Override
        public void onFailure(
                @NonNull Call<Map<String, Object>> call,
                @NonNull Throwable t) {
            t.printStackTrace();
            runOnUiThread(() -> Toasty.error(PreviewActivity.this,
                    "网络连接失败！").show());
        }
    }

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LithoView mLithoView;
    private Handler mMain = new Handler();
    private SwitchView mIsOpenEdge;
    private SwitchView mIsLiveReload;
    private SwitchView mIsOpenConsole;
    private ListView mConsole;


    private SAXReader mSAXReader = new SAXReader();
    private DataCallback mDataCallback = new DataCallback();
    private Callback<byte[]> mLayoutCallback = new Callback<byte[]>() {
        @Override
        public void onResponse(
                @NonNull Call<byte[]> call,
                @NonNull Response<byte[]> response) {
            byte[] bytes = response.body();
            if (bytes != null) {
                try {
                    mDocument = mSAXReader.read(new ByteArrayInputStream(bytes));
                } catch (DocumentException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toasty.error(PreviewActivity.this,
                            "解析Xml失败！").show());
                }
            }
        }

        @Override
        public void onFailure(
                @NonNull Call<byte[]> call,
                @NonNull Throwable t) {
            t.printStackTrace();
            runOnUiThread(() -> Toasty.error(PreviewActivity.this,
                    "网络连接失败！").show());
        }
    };
    private MockService mMockService;
    private Call<Map<String, Object>> mDataCall;
    private Call<byte[]> mLayoutCall;
    private ArrayAdapter<String> mAdapter;
    private volatile Document mDocument;
    private volatile Map<String, Object> mCacheData;

    private void reload() {
        Map<String, Object> node = mCacheData;
        if (node != null) {
            ComponentContext c = mLithoView.getComponentContext();
            ComponentTree componentTree = mLithoView.getComponentTree();
            if (componentTree != null) {
                componentTree.setRootAsync(
                        VerticalScroll.create(c)
                                .onScrollChangeListener(this)
                                .clipChildren(false)
                                .childComponent(
                                        Column.create(c)
//                                                .widthPx(UtilsKt.pt2Px(360))
                                                .alignItems(YogaAlign.CENTER)
                                                .child(DynamicBox.create(c)
//                                                        .marginPx(YogaEdge.TOP, UtilsKt.pt2Px(20))
                                                        .eventListener(this))
                                                .child(Text.create(c)
//                                                        .widthPx(UtilsKt.pt2Px(360))
//                                                        .heightPx(UtilsKt.pt2Px(40))
                                                        .backgroundColor(getResources()
                                                                .getColor(R.color.colorPrimary))
                                                        .textAlignment(Layout.Alignment.ALIGN_CENTER)
                                                        .text("这里是布局的下边界")
                                                        .textColor(Color.WHITE)
//                                                        .textSizePx(UtilsKt.pt2Px(25))
                                                        .typeface(Typeface.defaultFromStyle(Typeface.BOLD)))
                                ).build()
                );
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMain.removeCallbacks(this);
        mMain.post(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMain.removeCallbacks(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SimpleLithoHandler mWorker = new SimpleLithoHandler("layout");
        mLithoView = findViewById(R.id.host);
        mIsOpenEdge = findViewById(R.id.is_open_edge);
        mSwipeRefreshLayout = findViewById(R.id.pull);
        mIsLiveReload = findViewById(R.id.is_live_reload);
        mIsOpenConsole = findViewById(R.id.is_open_console);
        mConsole = findViewById(R.id.console);
        findViewById(R.id.transition).setBackground(new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{
                        getResources().getColor(R.color.background),
                        Color.TRANSPARENT
                }));
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mIsOpenConsole.setOnClickListener(this);
        mIsOpenEdge.setOnClickListener(this);
        mLithoView.setComponentTree(
                ComponentTree.create(mLithoView.getComponentContext())
                        .layoutThreadHandler(mWorker)
                        .build()
        );
        mAdapter = new ArrayAdapter<>(this, R.layout.console_item, R.id.text);
        mConsole.setAdapter(mAdapter);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            String url = bundle.getString("url");
            if (url != null) {
                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setTitle(url);
                }
                mMockService = new Retrofit.Builder()
                        .baseUrl(url)
                        .client(new OkHttpClient())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                        .create(MockService.class);
                onRefresh();
                return;
            }
        }
        finish();
    }

    @Override
    public void run() {
        if (mDataCall != null) {
            mDataCall.cancel();
        }
        if (mLayoutCall != null) {
            mLayoutCall.cancel();
        }
        if (mIsLiveReload.isOpened()) {
            reload();
            mLayoutCall = mMockService.layout();
            mDataCall = mMockService.data();
            mLayoutCall.enqueue(mLayoutCallback);
            mDataCall.enqueue(mDataCallback);
        }
        mMain.postDelayed(this, 1000);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.is_open_edge: {
                reload();
            }
            break;
            case R.id.is_open_console: {
                mConsole.setVisibility(mIsOpenConsole.isOpened() ? View.VISIBLE : View.GONE);
            }
            break;
        }
    }


    @Override
    public void onEvent(EventType type,
                        @Nullable String action) {
        mAdapter.add("event type=" + type.name() + " : event action=" + action);
    }

    @Override
    public void onScrollChange(NestedScrollView v,
                               int scrollX,
                               int scrollY,
                               int oldScrollX,
                               int oldScrollY
    ) {
        if (!mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setEnabled(scrollY <= 0);
        } else {
            mSwipeRefreshLayout.setEnabled(true);
        }
    }

    @Override
    public void onRefresh() {
//        mMockService.data().enqueue(new Callback<Map<String, Object>>() {
//            @Override
//            public void onResponse(
//                    @NonNull Call<Map<String, Object>> call,
//                    @NonNull Response<Map<String, Object>> response) {
//                PreviewActivity.this.onResponse(call, response);
//                runOnUiThread(() -> {
//                    reload();
//                    if (mSwipeRefreshLayout.isRefreshing()) {
//                        mSwipeRefreshLayout.setRefreshing(false);
//                    }
//                });
//            }
//
//            @Override
//            public void onFailure(
//                    @NonNull Call<Map<String, Object>> call,
//                    @NonNull Throwable t) {
//                PreviewActivity.this.onFailure(call, t);
//                runOnUiThread(() -> {
//                    if (mSwipeRefreshLayout.isRefreshing()) {
//                        mSwipeRefreshLayout.setRefreshing(false);
//                    }
//                });
//            }
//        });
    }
}
