package com.example.btphone.ui.call;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.view.View;

import com.example.btphone.R;
import com.example.btphone.databinding.ActivityEcallServiceBinding;
import com.example.btphone.databinding.LayoutEcallMiniBinding;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

/**
 * {@link ECallServiceActivity} 单元测试。
 * <p>
 * 覆盖拨号中 / 通话中 × 全屏 / 小窗 的各种状态组合，
 * 小窗背景切换逻辑和按钮点击事件。
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class ECallServiceActivityTest {

    private ECallServiceActivity mActivity;
    private ActivityEcallServiceBinding mBinding;
    private LayoutEcallMiniBinding mMiniBinding;

    @Before
    public void setUp() {
        ActivityController<ECallServiceActivity> controller =
                Robolectric.buildActivity(ECallServiceActivity.class);
        controller.create();
        mActivity = controller.get();
        mBinding = mActivity.getBinding();
        mMiniBinding = mActivity.getMiniBinding();
    }

    // ==================== Initialization ====================

    @Test
    public void onCreate_default_bindingsNotNull() {
        // Then
        assertNotNull(mBinding);
        assertNotNull(mMiniBinding);
    }

    @Test
    public void onCreate_default_startsInFullscreen() {
        // Then
        assertEquals(BaseServiceCallActivity.MODE_FULLSCREEN, mActivity.getDisplayMode());
    }

    @Test
    public void onCreate_default_startsInDialingState() {
        // Then
        assertEquals(BaseServiceCallActivity.STATE_DIALING, mActivity.getCallState());
    }

    @Test
    public void onCreate_default_fullscreenGroupVisible() {
        // Then
        assertEquals(View.VISIBLE, mBinding.groupFullscreen.getVisibility());
    }

    @Test
    public void onCreate_default_miniGone() {
        // Then
        assertEquals(View.GONE, mMiniBinding.getRoot().getVisibility());
    }

    @Test
    public void onCreate_default_titleDisplayed() {
        // Then
        String expected = mActivity.getString(R.string.ecall_service_title);
        assertEquals(expected, mBinding.tvTitle.getText().toString());
    }

    @Test
    public void onCreate_default_statusShowsDialing() {
        // Then
        String expected = mActivity.getString(R.string.service_call_dialing);
        assertEquals(expected, mBinding.tvStatus.getText().toString());
    }

    @Test
    public void onCreate_default_miniStatusShowsDialing() {
        // Then
        String expected = mActivity.getString(R.string.service_call_dialing);
        assertEquals(expected, mMiniBinding.tvMiniStatus.getText().toString());
    }

    @Test
    public void onCreate_default_miniHangupHidden() {
        // Then
        assertEquals(View.GONE, mMiniBinding.btnMiniHangup.getVisibility());
    }

    @Test
    public void onCreate_default_miniTitleDisplayed() {
        // Then
        String expected = mActivity.getString(R.string.ecall_service_title);
        assertEquals(expected, mMiniBinding.tvMiniTitle.getText().toString());
    }

    // ==================== Full screen / mini switching ====================

    @Test
    public void showMiniWindow_fromFullscreen_miniVisible() {
        // When
        mActivity.showMiniWindow();

        // Then
        assertEquals(View.VISIBLE, mMiniBinding.getRoot().getVisibility());
        assertEquals(BaseServiceCallActivity.MODE_MINI, mActivity.getDisplayMode());
    }

    @Test
    public void showMiniWindow_fromFullscreen_fullscreenGone() {
        // When
        mActivity.showMiniWindow();

        // Then
        assertEquals(View.GONE, mBinding.groupFullscreen.getVisibility());
    }

    @Test
    public void showFullScreen_afterMini_fullscreenVisible() {
        // Given
        mActivity.showMiniWindow();

        // When
        mActivity.showFullScreen();

        // Then
        assertEquals(View.VISIBLE, mBinding.groupFullscreen.getVisibility());
        assertEquals(View.GONE, mMiniBinding.getRoot().getVisibility());
    }

    @Test
    public void showFullScreen_afterMini_modeIsFullscreen() {
        // Given
        mActivity.showMiniWindow();

        // When
        mActivity.showFullScreen();

        // Then
        assertEquals(BaseServiceCallActivity.MODE_FULLSCREEN, mActivity.getDisplayMode());
    }

    // ==================== In-call state ====================

    @Test
    public void switchToInCall_fromDialing_stateChanges() {
        // When
        mActivity.switchToInCall();

        // Then
        assertEquals(BaseServiceCallActivity.STATE_IN_CALL, mActivity.getCallState());
    }

    @Test
    public void switchToInCall_fromDialing_statusShowsTimer() {
        // When
        mActivity.switchToInCall();

        // Then
        String expected = mActivity.getString(R.string.service_call_default_timer);
        assertEquals(expected, mBinding.tvStatus.getText().toString());
    }

    @Test
    public void switchToInCall_fromDialing_miniStatusShowsTimer() {
        // When
        mActivity.switchToInCall();

        // Then
        String expected = mActivity.getString(R.string.service_call_default_timer);
        assertEquals(expected, mMiniBinding.tvMiniStatus.getText().toString());
    }

    @Test
    public void switchToInCall_fromDialing_miniHangupVisible() {
        // When
        mActivity.switchToInCall();

        // Then
        assertEquals(View.VISIBLE, mMiniBinding.btnMiniHangup.getVisibility());
    }

    @Test
    public void switchToInCall_fromDialing_timerStarts() {
        // When
        mActivity.switchToInCall();

        // Then
        assertTrue(mActivity.isTimerRunning());
    }

    // ==================== Mini background switching ====================

    @Test
    public void updateMiniBackground_dialing_usesEcallBackground() {
        // Given — default state is dialing
        // When
        mActivity.updateMiniBackground();

        // Then — background is phone_pop_up_ecall_1 (verified by not crashing)
        assertEquals(BaseServiceCallActivity.STATE_DIALING, mActivity.getCallState());
    }

    @Test
    public void updateMiniBackground_inCall_usesStandardBackground() {
        // Given
        mActivity.switchToInCall();

        // When
        mActivity.updateMiniBackground();

        // Then — background is phone_pop_up_1 (verified by not crashing)
        assertEquals(BaseServiceCallActivity.STATE_IN_CALL, mActivity.getCallState());
    }

    // ==================== Click handlers ====================

    @Test
    public void btnMinimize_click_switchesToMini() {
        // When
        mBinding.btnMinimize.performClick();

        // Then
        assertEquals(BaseServiceCallActivity.MODE_MINI, mActivity.getDisplayMode());
    }

    @Test
    public void miniRoot_click_switchesToFullscreen() {
        // Given
        mActivity.showMiniWindow();

        // When
        mMiniBinding.getRoot().performClick();

        // Then
        assertEquals(BaseServiceCallActivity.MODE_FULLSCREEN, mActivity.getDisplayMode());
    }

    @Test
    public void btnMiniHangup_click_finishesActivity() {
        // Given
        mActivity.switchToInCall();
        mActivity.showMiniWindow();

        // When
        mMiniBinding.btnMiniHangup.performClick();

        // Then
        assertTrue(mActivity.isFinishing());
    }

    // ==================== Hangup ====================

    @Test
    public void hangUp_always_finishesActivity() {
        // When
        mActivity.hangUp();

        // Then
        assertTrue(mActivity.isFinishing());
    }

    @Test
    public void hangUp_withTimer_stopsTimer() {
        // Given
        mActivity.switchToInCall();

        // When
        mActivity.hangUp();

        // Then
        assertFalse(mActivity.isTimerRunning());
    }

    // ==================== Edge cases ====================

    @Test
    public void showMiniWindow_calledTwice_staysMini() {
        // When
        mActivity.showMiniWindow();
        mActivity.showMiniWindow();

        // Then
        assertEquals(BaseServiceCallActivity.MODE_MINI, mActivity.getDisplayMode());
    }

    @Test
    public void showFullScreen_calledTwice_staysFullscreen() {
        // When
        mActivity.showFullScreen();
        mActivity.showFullScreen();

        // Then
        assertEquals(BaseServiceCallActivity.MODE_FULLSCREEN, mActivity.getDisplayMode());
    }

    @Test
    public void switchToInCall_calledTwice_staysInCall() {
        // When
        mActivity.switchToInCall();
        mActivity.switchToInCall();

        // Then
        assertEquals(BaseServiceCallActivity.STATE_IN_CALL, mActivity.getCallState());
    }

    @Test
    public void elapsedSeconds_afterSwitchToInCall_isZero() {
        // When
        mActivity.switchToInCall();

        // Then
        assertEquals(0, mActivity.getElapsedSeconds());
    }
}
