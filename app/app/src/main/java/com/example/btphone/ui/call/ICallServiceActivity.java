package com.example.btphone.ui.call;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import com.example.btphone.R;
import com.example.btphone.databinding.ActivityIcallServiceBinding;
import com.example.btphone.databinding.LayoutIcallMiniBinding;

/**
 * I-Call 客服中心 Activity。
 * <p>
 * 支持以下模式：
 * <ul>
 *     <li>入口模式 — 显示客服中心名称、号码、说明文字、拨打按钮</li>
 *     <li>拨号中全屏 — 显示名称、拨号状态、挂断按钮</li>
 *     <li>拨号中小窗 — 显示小窗弹框含 I-Call 图标、名称、状态、挂断</li>
 *     <li>通话中全屏 — 显示名称、通话计时、挂断按钮</li>
 *     <li>通话中小窗 — 显示小窗含计时和挂断</li>
 * </ul>
 */
public class ICallServiceActivity extends BaseServiceCallActivity {

    /** 入口模式（仅 I-Call 有此状态）。 */
    public static final int STATE_ENTRY = -1;

    private ActivityIcallServiceBinding mBinding;
    private LayoutIcallMiniBinding mMiniBinding;
    private boolean mIsEntry = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityIcallServiceBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mMiniBinding = mBinding.containerMini;

        setupClickListeners();
        showEntryMode();
    }

    /**
     * 注册所有交互按钮的点击事件。
     */
    private void setupClickListeners() {
        mBinding.btnMinimize.setOnClickListener(v -> showMiniWindow());
        mBinding.btnCall.setOnClickListener(v -> startDialing());
        mBinding.btnHangup.setOnClickListener(v -> hangUp());
        mMiniBinding.getRoot().setOnClickListener(v -> showFullScreen());
        mMiniBinding.btnMiniHangup.setOnClickListener(v -> hangUp());
    }

    /**
     * 显示入口模式：名称、号码、说明、拨打按钮。
     */
    @VisibleForTesting
    void showEntryMode() {
        mIsEntry = true;
        mBinding.groupEntry.setVisibility(View.VISIBLE);
        mBinding.groupCalling.setVisibility(View.GONE);
        mMiniBinding.getRoot().setVisibility(View.GONE);
        mBinding.groupFullscreen.setVisibility(View.VISIBLE);
    }

    /**
     * 发起拨号，切换到拨号中全屏模式。
     */
    @VisibleForTesting
    void startDialing() {
        mIsEntry = false;
        mBinding.groupEntry.setVisibility(View.GONE);
        mBinding.groupCalling.setVisibility(View.VISIBLE);
        mBinding.tvStatus.setText(R.string.service_call_dialing);
        mMiniBinding.tvMiniStatus.setText(R.string.service_call_dialing);
        showFullScreen();
    }

    @Override
    @VisibleForTesting
    void showFullScreen() {
        super.showFullScreen();
        if (!mIsEntry) {
            mBinding.groupCalling.setVisibility(View.VISIBLE);
        }
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
        if (mode == MODE_MINI) {
            mBinding.groupEntry.setVisibility(View.GONE);
            mBinding.groupCalling.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCallStateChanged(int state) {
        if (state == STATE_IN_CALL) {
            mBinding.tvStatus.setText(R.string.service_call_default_timer);
            mMiniBinding.tvMiniStatus.setText(R.string.service_call_default_timer);
        }
    }

    @Override
    protected void onTimerTick(@NonNull String formattedTime) {
        mBinding.tvStatus.setText(formattedTime);
        mMiniBinding.tvMiniStatus.setText(formattedTime);
    }

    /**
     * 判断当前是否处于入口模式。
     *
     * @return {@code true} 表示入口模式
     */
    @VisibleForTesting
    boolean isEntryMode() {
        return mIsEntry;
    }

    /**
     * 获取 ViewBinding 实例（仅用于测试）。
     *
     * @return {@link ActivityIcallServiceBinding} 实例
     */
    @VisibleForTesting
    ActivityIcallServiceBinding getBinding() {
        return mBinding;
    }

    /**
     * 获取小窗 ViewBinding 实例（仅用于测试）。
     *
     * @return {@link LayoutIcallMiniBinding} 实例
     */
    @VisibleForTesting
    LayoutIcallMiniBinding getMiniBinding() {
        return mMiniBinding;
    }
}
