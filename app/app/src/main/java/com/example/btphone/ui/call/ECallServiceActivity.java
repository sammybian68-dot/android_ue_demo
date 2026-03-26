package com.example.btphone.ui.call;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import com.example.btphone.R;
import com.example.btphone.databinding.ActivityEcallServiceBinding;
import com.example.btphone.databinding.LayoutEcallMiniBinding;

/**
 * E-Call 服务中心 Activity。
 * <p>
 * E-Call 没有入口页，直接从拨号中状态开始。
 * 支持以下模式：
 * <ul>
 *     <li>拨号中全屏 — 显示"服务中心"名称、拨号状态、缩小按钮</li>
 *     <li>拨号中小窗 — E-Call 专属红色小窗背景，无挂断按钮</li>
 *     <li>通话中全屏 — 显示名称、通话计时、缩小按钮</li>
 *     <li>通话中小窗 — 标准小窗背景 + 挂断按钮</li>
 * </ul>
 */
public class ECallServiceActivity extends BaseServiceCallActivity {

    private ActivityEcallServiceBinding mBinding;
    private LayoutEcallMiniBinding mMiniBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityEcallServiceBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mMiniBinding = mBinding.containerMini;

        initDialingState();
        setupClickListeners();
        showFullScreen();
    }

    /**
     * 初始化为拨号中状态。
     */
    private void initDialingState() {
        mBinding.tvStatus.setText(R.string.service_call_dialing);
        mMiniBinding.tvMiniStatus.setText(R.string.service_call_dialing);
        mMiniBinding.btnMiniHangup.setVisibility(View.GONE);
        updateMiniBackground();
    }

    /**
     * 注册按钮点击事件。
     */
    private void setupClickListeners() {
        mBinding.btnMinimize.setOnClickListener(v -> showMiniWindow());
        mMiniBinding.getRoot().setOnClickListener(v -> showFullScreen());
        mMiniBinding.btnMiniHangup.setOnClickListener(v -> hangUp());
    }

    @NonNull
    @Override
    protected View getFullscreenGroup() {
        return mBinding.groupFullscreen;
    }

    @NonNull
    @Override
    protected View getMiniRoot() {
        return mMiniBinding.getRoot();
    }

    @Override
    protected void onDisplayModeChanged(int mode) {
        // E-Call 不需要额外的模式切换处理
    }

    @Override
    protected void onCallStateChanged(int state) {
        if (state == STATE_IN_CALL) {
            mBinding.tvStatus.setText(R.string.service_call_default_timer);
            mMiniBinding.tvMiniStatus.setText(R.string.service_call_default_timer);
            mMiniBinding.btnMiniHangup.setVisibility(View.VISIBLE);
            updateMiniBackground();
        }
    }

    @Override
    protected void onTimerTick(@NonNull String formattedTime) {
        mBinding.tvStatus.setText(formattedTime);
        mMiniBinding.tvMiniStatus.setText(formattedTime);
    }

    /**
     * 根据呼叫状态更新小窗背景。
     * <p>
     * 拨号中使用 E-Call 专属红色背景，通话中切换为标准背景。
     */
    @VisibleForTesting
    void updateMiniBackground() {
        if (getCallState() == STATE_DIALING) {
            mMiniBinding.getRoot().setBackgroundResource(R.drawable.phone_pop_up_ecall_1);
        } else {
            mMiniBinding.getRoot().setBackgroundResource(R.drawable.phone_pop_up_1);
        }
    }

    /**
     * 获取 ViewBinding 实例（仅用于测试）。
     *
     * @return {@link ActivityEcallServiceBinding} 实例
     */
    @VisibleForTesting
    ActivityEcallServiceBinding getBinding() {
        return mBinding;
    }

    /**
     * 获取小窗 ViewBinding 实例（仅用于测试）。
     *
     * @return {@link LayoutEcallMiniBinding} 实例
     */
    @VisibleForTesting
    LayoutEcallMiniBinding getMiniBinding() {
        return mMiniBinding;
    }
}
