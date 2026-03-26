package com.example.btphone.ui.call;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.example.btphone.R;
import com.example.btphone.databinding.ActivityIncomingCallBinding;
import com.example.btphone.databinding.LayoutIncomingCallMiniBinding;

/**
 * 来电界面 Activity。
 * <p>
 * 支持三种显示模式：
 * <ul>
 *   <li>全屏来电模式 — 显示来电者姓名、号码、接听/挂断大按钮和缩小按钮</li>
 *   <li>小窗模式 — 显示浮窗弹框，包含号码、接听/挂断小按钮</li>
 *   <li>隐私小窗模式 — 隐藏号码，仅显示"来电"提示</li>
 * </ul>
 * 当前使用 mock 数据演示来电场景。
 */
public class IncomingCallActivity extends AppCompatActivity {

    /** 全屏模式 */
    public static final int MODE_FULLSCREEN = 0;

    /** 小窗模式 */
    public static final int MODE_MINI = 1;

    private ActivityIncomingCallBinding mBinding;
    private LayoutIncomingCallMiniBinding mMiniBinding;

    private int mCurrentMode = MODE_FULLSCREEN;
    private boolean mIsPrivacyMode = false;
    private String mCallerName;
    private String mPhoneNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityIncomingCallBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mMiniBinding = mBinding.containerMini;

        initMockData();
        setupClickListeners();
        switchToFullscreen();
    }

    /**
     * 初始化 mock 来电数据并填充到界面。
     */
    private void initMockData() {
        mCallerName = getString(R.string.incoming_call_mock_name);
        mPhoneNumber = getString(R.string.incoming_call_mock_number);

        mBinding.tvCallerName.setText(mCallerName);
        mBinding.tvPhoneNumber.setText(mPhoneNumber);
        mBinding.tvCallStatusFull.setText(R.string.incoming_call_status);
        mBinding.tvAnswerLabel.setText(R.string.incoming_call_answer);
        mBinding.tvHangupLabel.setText(R.string.incoming_call_hangup);

        mMiniBinding.tvMiniPhoneNumber.setText(mPhoneNumber);
        mMiniBinding.tvMiniCallStatus.setText(R.string.incoming_call_status_mini);
    }

    /**
     * 注册所有按钮点击事件。
     * <p>
     * 全屏模式：缩小按钮切到小窗，接听/挂断执行对应操作。
     * 小窗模式：点击非按钮区域展开全屏，接听/挂断按钮独立处理。
     */
    private void setupClickListeners() {
        mBinding.btnMinimize.setOnClickListener(v -> switchToMini());
        mBinding.btnAnswerFull.setOnClickListener(v -> answerCall());
        mBinding.btnHangupFull.setOnClickListener(v -> hangupCall());

        mMiniBinding.getRoot().setOnClickListener(v -> switchToFullscreen());
        mMiniBinding.btnMiniAnswer.setOnClickListener(v -> answerCall());
        mMiniBinding.btnMiniHangup.setOnClickListener(v -> hangupCall());
    }

    /**
     * 切换到全屏来电模式。
     * <p>
     * 显示全屏视图组，隐藏小窗。
     */
    @VisibleForTesting
    void switchToFullscreen() {
        mCurrentMode = MODE_FULLSCREEN;
        mBinding.groupFullscreen.setVisibility(View.VISIBLE);
        mMiniBinding.getRoot().setVisibility(View.GONE);
    }

    /**
     * 切换到小窗模式。
     * <p>
     * 隐藏全屏视图组，显示小窗并根据隐私模式更新内容。
     */
    @VisibleForTesting
    void switchToMini() {
        mCurrentMode = MODE_MINI;
        mBinding.groupFullscreen.setVisibility(View.GONE);
        mMiniBinding.getRoot().setVisibility(View.VISIBLE);
        updateMiniPrivacyMode();
    }

    /**
     * 设置隐私模式开关。
     * <p>
     * 隐私模式下小窗隐藏来电号码，仅显示"来电"提示。
     *
     * @param enabled {@code true} 开启隐私模式，{@code false} 关闭
     */
    @VisibleForTesting
    void setPrivacyMode(boolean enabled) {
        mIsPrivacyMode = enabled;
        if (mCurrentMode == MODE_MINI) {
            updateMiniPrivacyMode();
        }
    }

    /**
     * 根据隐私模式状态更新小窗文字显示。
     * <p>
     * 隐私模式：隐藏号码，状态文字调整为居中位置、主文字颜色和大字号。
     * 正常模式：恢复号码显示和原始状态文字样式。
     */
    private void updateMiniPrivacyMode() {
        if (mIsPrivacyMode) {
            mMiniBinding.tvMiniPhoneNumber.setVisibility(View.GONE);
            mMiniBinding.tvMiniCallStatus.setText(R.string.incoming_call_status_mini);
            ConstraintLayout.LayoutParams params =
                    (ConstraintLayout.LayoutParams) mMiniBinding.tvMiniCallStatus.getLayoutParams();
            params.topMargin = getResources()
                    .getDimensionPixelSize(R.dimen.incoming_mini_privacy_text_top);
            mMiniBinding.tvMiniCallStatus.setLayoutParams(params);
            mMiniBinding.tvMiniCallStatus.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimensionPixelSize(R.dimen.incoming_mini_phone_text_size));
            mMiniBinding.tvMiniCallStatus.setTextColor(
                    ContextCompat.getColor(this, R.color.dark_primary_text));
        } else {
            mMiniBinding.tvMiniPhoneNumber.setVisibility(View.VISIBLE);
            mMiniBinding.tvMiniPhoneNumber.setText(mPhoneNumber);
            mMiniBinding.tvMiniCallStatus.setText(R.string.incoming_call_status_mini);
            ConstraintLayout.LayoutParams params =
                    (ConstraintLayout.LayoutParams) mMiniBinding.tvMiniCallStatus.getLayoutParams();
            params.topMargin = getResources()
                    .getDimensionPixelSize(R.dimen.incoming_mini_status_top);
            mMiniBinding.tvMiniCallStatus.setLayoutParams(params);
            mMiniBinding.tvMiniCallStatus.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimensionPixelSize(R.dimen.incoming_mini_status_text_size));
            mMiniBinding.tvMiniCallStatus.setTextColor(
                    ContextCompat.getColor(this, R.color.dark_secondary_text));
        }
    }

    /**
     * 处理接听来电操作。
     * <p>
     * Mock 实现：结束当前 Activity。
     */
    @VisibleForTesting
    void answerCall() {
        finish();
    }

    /**
     * 处理挂断来电操作。
     * <p>
     * Mock 实现：结束当前 Activity。
     */
    @VisibleForTesting
    void hangupCall() {
        finish();
    }

    /**
     * 获取当前显示模式。
     *
     * @return {@link #MODE_FULLSCREEN} 或 {@link #MODE_MINI}
     */
    @VisibleForTesting
    int getCurrentMode() {
        return mCurrentMode;
    }

    /**
     * 获取隐私模式状态。
     *
     * @return {@code true} 表示隐私模式已开启
     */
    @VisibleForTesting
    boolean isPrivacyMode() {
        return mIsPrivacyMode;
    }

    /**
     * 获取来电者姓名。
     *
     * @return 来电者姓名
     */
    @VisibleForTesting
    @NonNull
    String getCallerName() {
        return mCallerName;
    }

    /**
     * 获取来电号码。
     *
     * @return 来电号码
     */
    @VisibleForTesting
    @NonNull
    String getPhoneNumber() {
        return mPhoneNumber;
    }

    /**
     * 获取 ViewBinding 实例（仅用于测试）。
     *
     * @return ActivityIncomingCallBinding 实例
     */
    @VisibleForTesting
    ActivityIncomingCallBinding getBinding() {
        return mBinding;
    }

    /**
     * 获取小窗 ViewBinding 实例（仅用于测试）。
     *
     * @return LayoutIncomingCallMiniBinding 实例
     */
    @VisibleForTesting
    LayoutIncomingCallMiniBinding getMiniBinding() {
        return mMiniBinding;
    }
}
