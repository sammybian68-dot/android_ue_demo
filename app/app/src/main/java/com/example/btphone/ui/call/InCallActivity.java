package com.example.btphone.ui.call;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.btphone.R;
import com.example.btphone.databinding.ActivityInCallBinding;
import com.example.btphone.databinding.LayoutCallStatusBarBinding;
import com.example.btphone.databinding.LayoutInCallDtmfBinding;
import com.example.btphone.databinding.LayoutInCallMiniBinding;

/**
 * 通话中界面，支持全屏、小窗、DTMF 键盘和顶栏状态条四种形态。
 * <p>
 * 形态切换逻辑：
 * <ul>
 *     <li>全屏 → 点击缩小按钮 → 小窗</li>
 *     <li>小窗 → 点击小窗 → 全屏</li>
 *     <li>小窗 → 隐藏 → 顶栏状态条</li>
 *     <li>顶栏状态条 → 点击 → 全屏</li>
 *     <li>全屏 → 点击数字键盘 → DTMF 弹出</li>
 *     <li>DTMF → 再点键盘按钮 → 关闭 DTMF</li>
 * </ul>
 */
public class InCallActivity extends AppCompatActivity {

    /** 通话中显示模式。 */
    @VisibleForTesting
    enum DisplayMode {
        FULL_SCREEN,
        MINI_WINDOW,
        STATUS_BAR
    }

    /** Mock 通话号码。 */
    @VisibleForTesting
    static final String MOCK_PHONE_NUMBER = "138 2221 2212";

    /** Mock 通话时间。 */
    @VisibleForTesting
    static final String MOCK_CALL_TIME = "00:30";

    /** DTMF 按键字符表，与布局中按钮顺序一致。 */
    private static final char[] DTMF_KEYS = {
            '1', '2', '3', '4', '5', '6', '7', '8', '9', '*', '0', '#'
    };

    private ActivityInCallBinding mBinding;
    private LayoutInCallMiniBinding mMiniBinding;
    private LayoutInCallDtmfBinding mDtmfBinding;
    private LayoutCallStatusBarBinding mStatusBarBinding;

    private DisplayMode mDisplayMode = DisplayMode.FULL_SCREEN;
    private boolean mIsDtmfVisible = false;
    private boolean mIsMuted = false;
    private boolean mIsSwitchDialogVisible = false;
    private final StringBuilder mDtmfInput = new StringBuilder();

    @VisibleForTesting
    Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityInCallBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mMiniBinding = mBinding.layoutMini;
        mDtmfBinding = mBinding.layoutDtmf;
        mStatusBarBinding = mBinding.layoutStatusBar;

        initMockData();
        setupFullScreenButtons();
        setupMiniWindowButtons();
        setupStatusBarButtons();
        setupDtmfButtons();
        setupSwitchDialog();
        showFullScreen();
    }

    /**
     * 填充 Mock 数据到各视图形态。
     */
    private void initMockData() {
        mBinding.tvPhoneNumber.setText(MOCK_PHONE_NUMBER);
        mBinding.tvCallTime.setText(MOCK_CALL_TIME);
        mMiniBinding.tvMiniPhoneNumber.setText(MOCK_PHONE_NUMBER);
        mMiniBinding.tvMiniCallTime.setText(MOCK_CALL_TIME);
        mStatusBarBinding.tvStatusBarTime.setText(MOCK_CALL_TIME);
    }

    /**
     * 注册全屏模式下的按钮点击事件。
     */
    private void setupFullScreenButtons() {
        mBinding.btnMinimize.setOnClickListener(v -> showMiniWindow());
        mBinding.btnHangUpFull.setOnClickListener(v -> hangUp());
        mBinding.btnMuteFull.setOnClickListener(v -> toggleMute());
        mBinding.btnSwitchPhone.setOnClickListener(v -> showSwitchDialog());
        mBinding.btnKeyboard.setOnClickListener(v -> toggleDtmfKeyboard());
    }

    /**
     * 注册小窗模式下的按钮和容器点击事件。
     */
    private void setupMiniWindowButtons() {
        mMiniBinding.getRoot().setOnClickListener(v -> showFullScreen());
        mMiniBinding.btnMiniHangUp.setOnClickListener(v -> hangUp());
        mMiniBinding.btnMiniMute.setOnClickListener(v -> toggleMute());
    }

    /**
     * 注册顶栏状态条的点击事件。
     */
    private void setupStatusBarButtons() {
        mStatusBarBinding.getRoot().setOnClickListener(v -> showFullScreen());
    }

    /**
     * 注册 DTMF 键盘的按钮点击事件。
     */
    private void setupDtmfButtons() {
        TextView[] keys = {
                mDtmfBinding.btnDtmf1, mDtmfBinding.btnDtmf2, mDtmfBinding.btnDtmf3,
                mDtmfBinding.btnDtmf4, mDtmfBinding.btnDtmf5, mDtmfBinding.btnDtmf6,
                mDtmfBinding.btnDtmf7, mDtmfBinding.btnDtmf8, mDtmfBinding.btnDtmf9,
                mDtmfBinding.btnDtmfStar, mDtmfBinding.btnDtmf0, mDtmfBinding.btnDtmfHash
        };
        for (int i = 0; i < keys.length; i++) {
            final char digit = DTMF_KEYS[i];
            keys[i].setOnClickListener(v -> onDtmfKeyPressed(digit));
        }
    }

    /**
     * 注册切换手机确认对话框的按钮事件。
     */
    private void setupSwitchDialog() {
        mBinding.viewDialogMask.setOnClickListener(v -> dismissSwitchDialog());
        mBinding.btnDialogCancel.setOnClickListener(v -> dismissSwitchDialog());
        mBinding.btnDialogConfirm.setOnClickListener(v -> onSwitchConfirmed());
    }

    // ==================== Display mode switching ====================

    /**
     * 切换到全屏模式，隐藏小窗和顶栏。
     */
    @VisibleForTesting
    void showFullScreen() {
        mDisplayMode = DisplayMode.FULL_SCREEN;
        mBinding.getRoot().setBackground(
                ContextCompat.getDrawable(this, R.drawable.android_bg));
        mBinding.groupFullScreen.setVisibility(View.VISIBLE);
        mMiniBinding.getRoot().setVisibility(View.GONE);
        mStatusBarBinding.getRoot().setVisibility(View.GONE);
        hideDtmfKeyboard();
    }

    /**
     * 切换到小窗模式，隐藏全屏和顶栏。
     */
    @VisibleForTesting
    void showMiniWindow() {
        mDisplayMode = DisplayMode.MINI_WINDOW;
        mBinding.getRoot().setBackground(null);
        mBinding.groupFullScreen.setVisibility(View.GONE);
        mMiniBinding.getRoot().setVisibility(View.VISIBLE);
        mStatusBarBinding.getRoot().setVisibility(View.GONE);
        hideDtmfKeyboard();
    }

    /**
     * 切换到顶栏状态条模式，隐藏全屏和小窗。
     */
    @VisibleForTesting
    void showStatusBar() {
        mDisplayMode = DisplayMode.STATUS_BAR;
        mBinding.getRoot().setBackground(null);
        mBinding.groupFullScreen.setVisibility(View.GONE);
        mMiniBinding.getRoot().setVisibility(View.GONE);
        mStatusBarBinding.getRoot().setVisibility(View.VISIBLE);
        hideDtmfKeyboard();
    }

    // ==================== DTMF keyboard ====================

    /**
     * 切换 DTMF 键盘的显示/隐藏状态。
     */
    @VisibleForTesting
    void toggleDtmfKeyboard() {
        if (mIsDtmfVisible) {
            hideDtmfKeyboard();
        } else {
            showDtmfKeyboard();
        }
    }

    /**
     * 显示 DTMF 键盘并切换键盘按钮图标为按下态。
     */
    @VisibleForTesting
    void showDtmfKeyboard() {
        mIsDtmfVisible = true;
        mDtmfBinding.getRoot().setVisibility(View.VISIBLE);
        mBinding.btnKeyboard.setImageResource(R.drawable.keyboard_btn_number_keyboard_f);
    }

    /**
     * 隐藏 DTMF 键盘并恢复键盘按钮图标。
     */
    @VisibleForTesting
    void hideDtmfKeyboard() {
        mIsDtmfVisible = false;
        mDtmfBinding.getRoot().setVisibility(View.GONE);
        mBinding.btnKeyboard.setImageResource(R.drawable.keyboard_btn_number_keyboard_n);
    }

    /**
     * 处理 DTMF 按键按下，追加数字到显示区。
     *
     * @param digit 按下的 DTMF 字符
     */
    @VisibleForTesting
    void onDtmfKeyPressed(char digit) {
        mDtmfInput.append(digit);
        mDtmfBinding.tvDtmfDisplay.setText(mDtmfInput.toString());
    }

    // ==================== Call actions ====================

    /**
     * 执行挂断操作，结束当前 Activity。
     */
    @VisibleForTesting
    void hangUp() {
        finish();
    }

    /**
     * 切换静音/取消静音状态，更新标签文字。
     */
    @VisibleForTesting
    void toggleMute() {
        mIsMuted = !mIsMuted;
        mBinding.tvLabelMute.setText(
                mIsMuted ? getString(R.string.in_call_unmute) : getString(R.string.in_call_mute));
    }

    // ==================== Switch phone dialog ====================

    /**
     * 显示"切换至手机"确认对话框。
     */
    @VisibleForTesting
    void showSwitchDialog() {
        mIsSwitchDialogVisible = true;
        mBinding.viewDialogMask.setVisibility(View.VISIBLE);
        mBinding.layoutSwitchDialog.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏"切换至手机"确认对话框。
     */
    @VisibleForTesting
    void dismissSwitchDialog() {
        mIsSwitchDialogVisible = false;
        mBinding.viewDialogMask.setVisibility(View.GONE);
        mBinding.layoutSwitchDialog.setVisibility(View.GONE);
    }

    /**
     * 确认切换至手机回调（功能预留）。
     */
    @VisibleForTesting
    void onSwitchConfirmed() {
        dismissSwitchDialog();
    }

    // ==================== Getters (for testing) ====================

    /**
     * 获取当前显示模式。
     *
     * @return 当前 {@link DisplayMode}
     */
    @VisibleForTesting
    @NonNull
    DisplayMode getDisplayMode() {
        return mDisplayMode;
    }

    /**
     * 判断 DTMF 键盘是否可见。
     *
     * @return {@code true} 表示可见
     */
    @VisibleForTesting
    boolean isDtmfVisible() {
        return mIsDtmfVisible;
    }

    /**
     * 判断是否处于静音状态。
     *
     * @return {@code true} 表示已静音
     */
    @VisibleForTesting
    boolean isMuted() {
        return mIsMuted;
    }

    /**
     * 判断切换对话框是否可见。
     *
     * @return {@code true} 表示可见
     */
    @VisibleForTesting
    boolean isSwitchDialogVisible() {
        return mIsSwitchDialogVisible;
    }

    /**
     * 获取当前 DTMF 输入内容。
     *
     * @return DTMF 输入字符串
     */
    @VisibleForTesting
    @NonNull
    String getDtmfInput() {
        return mDtmfInput.toString();
    }

    /**
     * 获取 ViewBinding 实例（仅用于测试）。
     *
     * @return {@link ActivityInCallBinding} 实例
     */
    @VisibleForTesting
    ActivityInCallBinding getBinding() {
        return mBinding;
    }
}
