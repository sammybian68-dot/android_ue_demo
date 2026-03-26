package com.example.btphone.ui.call;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;

import com.example.btphone.R;
import com.example.btphone.databinding.ActivityCallCenterBinding;

/**
 * 呼叫中心入口 Activity。
 * <p>
 * 包含两种模式：
 * <ul>
 *     <li>入口模式 — 显示 I-call 标题、号码、说明文字和拨打按钮</li>
 *     <li>呼叫中模式 — 显示"呼叫中心"标题、拨号状态、静音和挂断按钮</li>
 * </ul>
 */
public class CallCenterActivity extends AppCompatActivity {

    /** 入口模式。 */
    public static final int MODE_ENTRY = 0;

    /** 呼叫中模式。 */
    public static final int MODE_CALLING = 1;

    private ActivityCallCenterBinding mBinding;
    private int mCurrentMode = MODE_ENTRY;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityCallCenterBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        setupClickListeners();
        showEntry();
    }

    /**
     * 注册所有按钮点击事件。
     */
    private void setupClickListeners() {
        mBinding.btnMinimize.setOnClickListener(v -> onMinimizeClicked());
        mBinding.btnCall.setOnClickListener(v -> startCalling());
        mBinding.btnMute.setOnClickListener(v -> onMuteClicked());
        mBinding.btnHangup.setOnClickListener(v -> hangUp());
    }

    /**
     * 切换到入口模式。
     */
    @VisibleForTesting
    void showEntry() {
        mCurrentMode = MODE_ENTRY;
        mBinding.groupEntry.setVisibility(View.VISIBLE);
        mBinding.groupCalling.setVisibility(View.GONE);
    }

    /**
     * 发起呼叫，切换到呼叫中模式。
     */
    @VisibleForTesting
    void startCalling() {
        mCurrentMode = MODE_CALLING;
        mBinding.groupEntry.setVisibility(View.GONE);
        mBinding.groupCalling.setVisibility(View.VISIBLE);
    }

    /**
     * 挂断呼叫并结束 Activity。
     */
    @VisibleForTesting
    void hangUp() {
        finish();
    }

    /**
     * 缩小按钮点击回调（功能预留）。
     */
    @VisibleForTesting
    void onMinimizeClicked() {
        finish();
    }

    /**
     * 静音按钮点击回调（功能预留）。
     */
    @VisibleForTesting
    void onMuteClicked() {
        // 静音功能预留
    }

    /**
     * 获取当前显示模式。
     *
     * @return {@link #MODE_ENTRY} 或 {@link #MODE_CALLING}
     */
    @VisibleForTesting
    int getCurrentMode() {
        return mCurrentMode;
    }

    /**
     * 获取 ViewBinding 实例（仅用于测试）。
     *
     * @return {@link ActivityCallCenterBinding} 实例
     */
    @VisibleForTesting
    ActivityCallCenterBinding getBinding() {
        return mBinding;
    }
}
