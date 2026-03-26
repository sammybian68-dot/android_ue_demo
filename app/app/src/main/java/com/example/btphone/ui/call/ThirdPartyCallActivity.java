package com.example.btphone.ui.call;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.btphone.R;
import com.example.btphone.databinding.ActivityThirdPartyCallBinding;
import com.example.btphone.databinding.LayoutThirdPartyCallMiniBinding;
import com.example.btphone.databinding.LayoutThirdPartyIncomingMiniBinding;

/**
 * 第三方通话界面，支持四种显示模式：
 * <ul>
 *     <li>INCOMING_FULL — 第三方来电全屏（当前通话方 + 新来电方 + 接听/挂断）</li>
 *     <li>IN_CALL_FULL — 第三方通话全屏（两路通话 + 切换/键盘/静音/挂断按钮）</li>
 *     <li>INCOMING_MINI — 第三方来电小窗（双行 + 接听/挂断）</li>
 *     <li>IN_CALL_MINI — 第三方通话小窗（双行 + 切换/静音/挂断）</li>
 * </ul>
 */
public class ThirdPartyCallActivity extends AppCompatActivity {

    /** 第三方通话显示模式。 */
    @VisibleForTesting
    enum DisplayMode {
        INCOMING_FULL,
        IN_CALL_FULL,
        INCOMING_MINI,
        IN_CALL_MINI
    }

    /** Mock 当前通话方名称。 */
    @VisibleForTesting
    static final String MOCK_CURRENT_NAME = "火龙果";

    /** Mock 当前通话方号码。 */
    @VisibleForTesting
    static final String MOCK_CURRENT_NUMBER = "10086";

    /** Mock 当前通话时间。 */
    @VisibleForTesting
    static final String MOCK_CURRENT_TIME = "00:30";

    /** Mock 新来电方名称。 */
    @VisibleForTesting
    static final String MOCK_INCOMING_NAME = "中国移动";

    /** Mock 新来电方号码。 */
    @VisibleForTesting
    static final String MOCK_INCOMING_NUMBER = "10086";

    /** Mock 活跃通话名称（接听后）。 */
    @VisibleForTesting
    static final String MOCK_ACTIVE_NAME = "火龙果";

    /** Mock 活跃通话号码。 */
    @VisibleForTesting
    static final String MOCK_ACTIVE_NUMBER = "138 2221 2212";

    /** Mock 活跃通话时间。 */
    @VisibleForTesting
    static final String MOCK_ACTIVE_TIME = "00:02";

    private ActivityThirdPartyCallBinding mBinding;
    private LayoutThirdPartyIncomingMiniBinding mMiniIncomingBinding;
    private LayoutThirdPartyCallMiniBinding mMiniCallBinding;

    private DisplayMode mDisplayMode = DisplayMode.INCOMING_FULL;
    private boolean mIsMuted = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityThirdPartyCallBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mMiniIncomingBinding = mBinding.layoutMiniIncoming;
        mMiniCallBinding = mBinding.layoutMiniCall;

        initMockData();
        setupIncomingFullButtons();
        setupInCallFullButtons();
        setupMiniIncomingButtons();
        setupMiniCallButtons();
        showIncomingFull();
    }

    /**
     * 填充 Mock 数据到各视图形态。
     */
    private void initMockData() {
        mBinding.tvCurrentName.setText(MOCK_CURRENT_NAME);
        mBinding.tvCurrentStatus.setText(MOCK_CURRENT_TIME);
        mBinding.tvCallerName.setText(MOCK_INCOMING_NAME);
        mBinding.tvCallerNumber.setText(MOCK_INCOMING_NUMBER);
        mBinding.tvCallerStatus.setText(getString(R.string.tp_incoming_status));

        mBinding.tvHeldName.setText(MOCK_CURRENT_NUMBER);
        mBinding.tvHeldStatus.setText(getString(R.string.tp_call_hold));
        mBinding.tvActiveName.setText(MOCK_ACTIVE_NAME);
        mBinding.tvActiveTime.setText(MOCK_ACTIVE_TIME);

        mMiniIncomingBinding.tvMiniIncomingRow1Name.setText(MOCK_CURRENT_NUMBER);
        mMiniIncomingBinding.tvMiniIncomingRow1Status.setText(MOCK_CURRENT_TIME);
        mMiniIncomingBinding.tvMiniIncomingRow2Name.setText(MOCK_ACTIVE_NUMBER);
        mMiniIncomingBinding.tvMiniIncomingRow2Status.setText(
                getString(R.string.tp_incoming_status_mini));

        mMiniCallBinding.tvMiniCallRow1Name.setText(MOCK_CURRENT_NUMBER);
        mMiniCallBinding.tvMiniCallRow1Status.setText(getString(R.string.tp_call_hold));
        mMiniCallBinding.tvMiniCallRow2Name.setText(MOCK_ACTIVE_NUMBER);
        mMiniCallBinding.tvMiniCallRow2Status.setText(MOCK_ACTIVE_TIME);
    }

    /**
     * 注册来电全屏模式下的按钮点击事件。
     */
    private void setupIncomingFullButtons() {
        mBinding.btnMinimizeIncoming.setOnClickListener(v -> showIncomingMini());
        mBinding.btnAnswer.setOnClickListener(v -> onAnswerIncoming());
        mBinding.btnHangupIncoming.setOnClickListener(v -> hangUpIncoming());
        mBinding.btnHangUpCurrent.setOnClickListener(v -> hangUpCurrent());
    }

    /**
     * 注册通话全屏模式下的按钮点击事件。
     */
    private void setupInCallFullButtons() {
        mBinding.btnMinimizeCall.setOnClickListener(v -> showInCallMini());
        mBinding.btnHangupCall.setOnClickListener(v -> hangUpActiveCall());
        mBinding.btnMuteCall.setOnClickListener(v -> toggleMute());
        mBinding.btnSwitchCall.setOnClickListener(v -> switchCall());
        mBinding.btnSwitchPhone.setOnClickListener(v -> { /* 切换至手机功能预留 */ });
        mBinding.btnKeyboardCall.setOnClickListener(v -> { /* 数字键盘功能预留 */ });
        mBinding.btnHangUpHeld.setOnClickListener(v -> hangUpHeld());
    }

    /**
     * 注册来电小窗模式下的按钮点击事件。
     */
    private void setupMiniIncomingButtons() {
        mMiniIncomingBinding.getRoot().setOnClickListener(v -> showIncomingFull());
        mMiniIncomingBinding.btnMiniIncomingAnswer.setOnClickListener(v -> onAnswerIncoming());
        mMiniIncomingBinding.btnMiniIncomingHangup.setOnClickListener(v -> hangUpIncoming());
    }

    /**
     * 注册通话小窗模式下的按钮点击事件。
     */
    private void setupMiniCallButtons() {
        mMiniCallBinding.getRoot().setOnClickListener(v -> showInCallFull());
        mMiniCallBinding.btnMiniCallHangup.setOnClickListener(v -> hangUpActiveCall());
        mMiniCallBinding.btnMiniCallMute.setOnClickListener(v -> toggleMute());
        mMiniCallBinding.btnMiniCallCut.setOnClickListener(v -> switchCall());
    }

    // ==================== Display mode switching ====================

    /**
     * 切换到来电全屏模式。
     */
    @VisibleForTesting
    void showIncomingFull() {
        mDisplayMode = DisplayMode.INCOMING_FULL;
        mBinding.getRoot().setBackground(
                ContextCompat.getDrawable(this, R.drawable.android_bg));
        mBinding.groupIncoming.setVisibility(View.VISIBLE);
        mBinding.groupInCall.setVisibility(View.GONE);
        mMiniIncomingBinding.getRoot().setVisibility(View.GONE);
        mMiniCallBinding.getRoot().setVisibility(View.GONE);
    }

    /**
     * 切换到通话全屏模式。
     */
    @VisibleForTesting
    void showInCallFull() {
        mDisplayMode = DisplayMode.IN_CALL_FULL;
        mBinding.getRoot().setBackground(
                ContextCompat.getDrawable(this, R.drawable.android_bg));
        mBinding.groupIncoming.setVisibility(View.GONE);
        mBinding.groupInCall.setVisibility(View.VISIBLE);
        mMiniIncomingBinding.getRoot().setVisibility(View.GONE);
        mMiniCallBinding.getRoot().setVisibility(View.GONE);
    }

    /**
     * 切换到来电小窗模式。
     */
    @VisibleForTesting
    void showIncomingMini() {
        mDisplayMode = DisplayMode.INCOMING_MINI;
        mBinding.getRoot().setBackground(null);
        mBinding.groupIncoming.setVisibility(View.GONE);
        mBinding.groupInCall.setVisibility(View.GONE);
        mMiniIncomingBinding.getRoot().setVisibility(View.VISIBLE);
        mMiniCallBinding.getRoot().setVisibility(View.GONE);
    }

    /**
     * 切换到通话小窗模式。
     */
    @VisibleForTesting
    void showInCallMini() {
        mDisplayMode = DisplayMode.IN_CALL_MINI;
        mBinding.getRoot().setBackground(null);
        mBinding.groupIncoming.setVisibility(View.GONE);
        mBinding.groupInCall.setVisibility(View.GONE);
        mMiniIncomingBinding.getRoot().setVisibility(View.GONE);
        mMiniCallBinding.getRoot().setVisibility(View.VISIBLE);
    }

    // ==================== Call actions ====================

    /**
     * 接听第三方来电，当前通话保持，切换到通话全屏。
     */
    @VisibleForTesting
    void onAnswerIncoming() {
        showInCallFull();
    }

    /**
     * 挂断新来电（不影响当前通话），结束当前 Activity。
     */
    @VisibleForTesting
    void hangUpIncoming() {
        finish();
    }

    /**
     * 挂断当前正在通话的一方（来电界面下），结束当前 Activity。
     */
    @VisibleForTesting
    void hangUpCurrent() {
        finish();
    }

    /**
     * 挂断当前活跃通话，结束当前 Activity。
     */
    @VisibleForTesting
    void hangUpActiveCall() {
        finish();
    }

    /**
     * 挂断保持中的通话，结束当前 Activity。
     */
    @VisibleForTesting
    void hangUpHeld() {
        finish();
    }

    /**
     * 切换两路通话（交换保持/活跃状态）。
     */
    @VisibleForTesting
    void switchCall() {
        String heldName = mBinding.tvHeldName.getText().toString();
        String heldStatus = mBinding.tvHeldStatus.getText().toString();
        String activeName = mBinding.tvActiveName.getText().toString();
        String activeTime = mBinding.tvActiveTime.getText().toString();

        mBinding.tvHeldName.setText(activeName);
        mBinding.tvHeldStatus.setText(getString(R.string.tp_call_hold));
        mBinding.tvActiveName.setText(heldName);
        mBinding.tvActiveTime.setText(activeTime);

        String miniRow1 = mMiniCallBinding.tvMiniCallRow1Name.getText().toString();
        String miniRow2 = mMiniCallBinding.tvMiniCallRow2Name.getText().toString();
        String miniRow2Status = mMiniCallBinding.tvMiniCallRow2Status.getText().toString();
        mMiniCallBinding.tvMiniCallRow1Name.setText(miniRow2);
        mMiniCallBinding.tvMiniCallRow1Status.setText(getString(R.string.tp_call_hold));
        mMiniCallBinding.tvMiniCallRow2Name.setText(miniRow1);
        mMiniCallBinding.tvMiniCallRow2Status.setText(miniRow2Status);
    }

    /**
     * 切换静音/取消静音状态，更新标签文字。
     */
    @VisibleForTesting
    void toggleMute() {
        mIsMuted = !mIsMuted;
        mBinding.tvLabelMute.setText(
                mIsMuted ? getString(R.string.tp_mute) : getString(R.string.tp_unmute));
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
     * 判断是否处于静音状态。
     *
     * @return {@code true} 表示已静音
     */
    @VisibleForTesting
    boolean isMuted() {
        return mIsMuted;
    }

    /**
     * 获取 ViewBinding 实例（仅用于测试）。
     *
     * @return {@link ActivityThirdPartyCallBinding} 实例
     */
    @VisibleForTesting
    ActivityThirdPartyCallBinding getBinding() {
        return mBinding;
    }

    /**
     * 获取来电小窗 ViewBinding 实例（仅用于测试）。
     *
     * @return {@link LayoutThirdPartyIncomingMiniBinding} 实例
     */
    @VisibleForTesting
    LayoutThirdPartyIncomingMiniBinding getMiniIncomingBinding() {
        return mMiniIncomingBinding;
    }

    /**
     * 获取通话小窗 ViewBinding 实例（仅用于测试）。
     *
     * @return {@link LayoutThirdPartyCallMiniBinding} 实例
     */
    @VisibleForTesting
    LayoutThirdPartyCallMiniBinding getMiniCallBinding() {
        return mMiniCallBinding;
    }
}
