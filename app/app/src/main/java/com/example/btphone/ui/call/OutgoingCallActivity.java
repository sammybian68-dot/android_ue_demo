package com.example.btphone.ui.call;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.btphone.R;
import com.example.btphone.databinding.ActivityOutgoingCallBinding;

/**
 * 拨号中界面（全屏 + 小窗模式）。
 * <p>
 * 显示正在拨号的号码、归属地，提供挂断、静音、切换至手机、数字键盘等操作按钮。
 * 支持全屏与小窗模式之间的切换：
 * <ul>
 *     <li>全屏下点击缩小按钮 → 切换到小窗</li>
 *     <li>小窗下点击窗口背景 → 切换到全屏</li>
 * </ul>
 */
public class OutgoingCallActivity extends AppCompatActivity {

    /** Mock 拨号号码。 */
    @VisibleForTesting
    static final String MOCK_PHONE_NUMBER = "138 2221 2212";

    /** Mock 归属地。 */
    @VisibleForTesting
    static final String MOCK_LOCATION = "广东省广州市";

    /** 拨号中状态下静音按钮的禁用透明度。 */
    private static final float MUTE_DISABLED_ALPHA = 0.35f;

    private ActivityOutgoingCallBinding mBinding;
    private boolean mIsFullScreen = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityOutgoingCallBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        initMockData();
        setupFullScreenButtons();
        setupMiniWindowButtons();
        applyDisabledState();
        showFullScreen();
    }

    /**
     * 填充 Mock 数据到全屏和小窗视图。
     */
    private void initMockData() {
        mBinding.tvPhoneNumber.setText(MOCK_PHONE_NUMBER);
        mBinding.tvLocation.setText(MOCK_LOCATION);
        mBinding.layoutMini.tvMiniPhoneNumber.setText(MOCK_PHONE_NUMBER);
    }

    /**
     * 注册全屏模式下的按钮点击事件。
     */
    private void setupFullScreenButtons() {
        mBinding.btnMinimize.setOnClickListener(v -> showMiniWindow());
        mBinding.btnHangUpFull.setOnClickListener(v -> hangUp());
        mBinding.btnSwitchPhone.setOnClickListener(v -> onSwitchPhoneClicked());
        mBinding.btnKeyboard.setOnClickListener(v -> onKeyboardClicked());
    }

    /**
     * 注册小窗模式下的按钮和容器点击事件。
     */
    private void setupMiniWindowButtons() {
        mBinding.layoutMini.getRoot().setOnClickListener(v -> showFullScreen());
        mBinding.layoutMini.btnMiniHangUp.setOnClickListener(v -> hangUp());
        mBinding.layoutMini.btnMiniMute.setOnClickListener(v -> onMiniMuteClicked());
    }

    /**
     * 设置拨号中状态下静音按钮为禁用态（降低透明度并禁用点击）。
     */
    private void applyDisabledState() {
        mBinding.btnMuteFull.setEnabled(false);
        mBinding.btnMuteFull.setAlpha(MUTE_DISABLED_ALPHA);
    }

    /**
     * 切换到全屏模式。
     * <p>
     * 显示全屏拨号界面的所有元素，隐藏小窗。
     */
    @VisibleForTesting
    void showFullScreen() {
        mIsFullScreen = true;
        mBinding.getRoot().setBackground(
                ContextCompat.getDrawable(this, R.drawable.android_bg));
        mBinding.groupFullScreen.setVisibility(View.VISIBLE);
        mBinding.layoutMini.getRoot().setVisibility(View.GONE);
    }

    /**
     * 切换到小窗模式。
     * <p>
     * 隐藏全屏拨号界面的所有元素，显示拨号小窗。
     */
    @VisibleForTesting
    void showMiniWindow() {
        mIsFullScreen = false;
        mBinding.getRoot().setBackground(null);
        mBinding.groupFullScreen.setVisibility(View.GONE);
        mBinding.layoutMini.getRoot().setVisibility(View.VISIBLE);
    }

    /**
     * 执行挂断操作，结束当前 Activity。
     */
    @VisibleForTesting
    void hangUp() {
        finish();
    }

    /**
     * 切换至手机按钮点击回调（功能预留）。
     */
    @VisibleForTesting
    void onSwitchPhoneClicked() {
        // 切换至手机功能预留
    }

    /**
     * 数字键盘按钮点击回调（功能预留）。
     */
    @VisibleForTesting
    void onKeyboardClicked() {
        // 数字键盘功能预留
    }

    /**
     * 小窗静音按钮点击回调（功能预留）。
     */
    @VisibleForTesting
    void onMiniMuteClicked() {
        // 小窗静音功能预留
    }

    /**
     * 判断当前是否为全屏模式。
     *
     * @return {@code true} 表示全屏模式，{@code false} 表示小窗模式
     */
    @VisibleForTesting
    boolean isFullScreen() {
        return mIsFullScreen;
    }

    /**
     * 获取 ViewBinding 实例（仅用于测试）。
     *
     * @return {@link ActivityOutgoingCallBinding} 实例
     */
    @VisibleForTesting
    ActivityOutgoingCallBinding getBinding() {
        return mBinding;
    }
}
