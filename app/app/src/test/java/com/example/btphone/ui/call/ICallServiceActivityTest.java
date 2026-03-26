package com.example.btphone.ui.call;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.view.View;

import com.example.btphone.R;
import com.example.btphone.databinding.ActivityIcallServiceBinding;
import com.example.btphone.databinding.LayoutIcallMiniBinding;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

/**
 * {@link ICallServiceActivity} 单元测试。
 * <p>
 * 覆盖入口模式 / 拨号中 / 通话中 × 全屏 / 小窗 的各种状态组合，
 * 以及按钮点击事件和计时器逻辑。
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class ICallServiceActivityTest {

    private ICallServiceActivity mActivity;
    private ActivityIcallServiceBinding mBinding;
    private LayoutIcallMiniBinding mMiniBinding;

    @Before
    public void setUp() {
        ActivityController<ICallServiceActivity> controller =
                Robolectric.buildActivity(ICallServiceActivity.class);
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
    public void onCreate_default_startsInEntryMode() {
        // Then
        assertTrue(mActivity.isEntryMode());
    }

    @Test
    public void onCreate_default_entryGroupVisible() {
        // Then
        assertEquals(View.VISIBLE, mBinding.groupEntry.getVisibility());
    }

    @Test
    public void onCreate_default_callingGroupGone() {
        // Then
        assertEquals(View.GONE, mBinding.groupCalling.getVisibility());
    }

    @Test
    public void onCreate_default_miniGone() {
        // Then
        assertEquals(View.GONE, mMiniBinding.getRoot().getVisibility());
    }

    @Test
    public void onCreate_default_fullscreenGroupVisible() {
        // Then
        assertEquals(View.VISIBLE, mBinding.groupFullscreen.getVisibility());
    }

    @Test
    public void onCreate_default_titleDisplayed() {
        // Then
        String expected = mActivity.getString(R.string.icall_service_title);
        assertEquals(expected, mBinding.tvTitle.getText().toString());
    }

    @Test
    public void onCreate_default_phoneDisplayed() {
        // Then
        String expected = mActivity.getString(R.string.icall_service_phone);
        assertEquals(expected, mBinding.tvPhone.getText().toString());
    }

    @Test
    public void onCreate_default_descDisplayed() {
        // Then
        String expected = mActivity.getString(R.string.icall_service_desc);
        assertEquals(expected, mBinding.tvDesc.getText().toString());
    }

    // ==================== Start dialing ====================

    @Test
    public void startDialing_fromEntry_noLongerEntry() {
        // When
        mActivity.startDialing();

        // Then
        assertFalse(mActivity.isEntryMode());
    }

    @Test
    public void startDialing_fromEntry_entryGroupGone() {
        // When
        mActivity.startDialing();

        // Then
        assertEquals(View.GONE, mBinding.groupEntry.getVisibility());
    }

    @Test
    public void startDialing_fromEntry_callingGroupVisible() {
        // When
        mActivity.startDialing();

        // Then
        assertEquals(View.VISIBLE, mBinding.groupCalling.getVisibility());
    }

    @Test
    public void startDialing_fromEntry_statusShowsDialing() {
        // When
        mActivity.startDialing();

        // Then
        String expected = mActivity.getString(R.string.service_call_dialing);
        assertEquals(expected, mBinding.tvStatus.getText().toString());
    }

    @Test
    public void startDialing_fromEntry_miniStatusShowsDialing() {
        // When
        mActivity.startDialing();

        // Then
        String expected = mActivity.getString(R.string.service_call_dialing);
        assertEquals(expected, mMiniBinding.tvMiniStatus.getText().toString());
    }

    @Test
    public void startDialing_fromEntry_displayModeFullscreen() {
        // When
        mActivity.startDialing();

        // Then
        assertEquals(BaseServiceCallActivity.MODE_FULLSCREEN, mActivity.getDisplayMode());
    }

    // ==================== Full screen / mini switching ====================

    @Test
    public void showMiniWindow_afterDialing_miniVisible() {
        // Given
        mActivity.startDialing();

        // When
        mActivity.showMiniWindow();

        // Then
        assertEquals(View.VISIBLE, mMiniBinding.getRoot().getVisibility());
        assertEquals(BaseServiceCallActivity.MODE_MINI, mActivity.getDisplayMode());
    }

    @Test
    public void showMiniWindow_afterDialing_fullscreenGone() {
        // Given
        mActivity.startDialing();

        // When
        mActivity.showMiniWindow();

        // Then
        assertEquals(View.GONE, mBinding.groupFullscreen.getVisibility());
    }

    @Test
    public void showFullScreen_afterMini_fullscreenVisible() {
        // Given
        mActivity.startDialing();
        mActivity.showMiniWindow();

        // When
        mActivity.showFullScreen();

        // Then
        assertEquals(View.VISIBLE, mBinding.groupFullscreen.getVisibility());
        assertEquals(View.GONE, mMiniBinding.getRoot().getVisibility());
    }

    @Test
    public void showFullScreen_afterMini_callingGroupVisible() {
        // Given
        mActivity.startDialing();
        mActivity.showMiniWindow();

        // When
        mActivity.showFullScreen();

        // Then
        assertEquals(View.VISIBLE, mBinding.groupCalling.getVisibility());
    }

    // ==================== In-call state ====================

    @Test
    public void switchToInCall_fromDialing_stateChanges() {
        // Given
        mActivity.startDialing();

        // When
        mActivity.switchToInCall();

        // Then
        assertEquals(BaseServiceCallActivity.STATE_IN_CALL, mActivity.getCallState());
    }

    @Test
    public void switchToInCall_fromDialing_statusShowsTimer() {
        // Given
        mActivity.startDialing();

        // When
        mActivity.switchToInCall();

        // Then
        String expected = mActivity.getString(R.string.service_call_default_timer);
        assertEquals(expected, mBinding.tvStatus.getText().toString());
    }

    @Test
    public void switchToInCall_fromDialing_miniStatusShowsTimer() {
        // Given
        mActivity.startDialing();

        // When
        mActivity.switchToInCall();

        // Then
        String expected = mActivity.getString(R.string.service_call_default_timer);
        assertEquals(expected, mMiniBinding.tvMiniStatus.getText().toString());
    }

    @Test
    public void switchToInCall_fromDialing_timerStarts() {
        // Given
        mActivity.startDialing();

        // When
        mActivity.switchToInCall();

        // Then
        assertTrue(mActivity.isTimerRunning());
    }

    // ==================== Click handlers ====================

    @Test
    public void btnCall_click_startsDialing() {
        // When
        mBinding.btnCall.performClick();

        // Then
        assertFalse(mActivity.isEntryMode());
    }

    @Test
    public void btnMinimize_click_switchesToMini() {
        // Given
        mActivity.startDialing();

        // When
        mBinding.btnMinimize.performClick();

        // Then
        assertEquals(BaseServiceCallActivity.MODE_MINI, mActivity.getDisplayMode());
    }

    @Test
    public void btnHangup_click_finishesActivity() {
        // Given
        mActivity.startDialing();

        // When
        mBinding.btnHangup.performClick();

        // Then
        assertTrue(mActivity.isFinishing());
    }

    @Test
    public void miniRoot_click_switchesToFullscreen() {
        // Given
        mActivity.startDialing();
        mActivity.showMiniWindow();

        // When
        mMiniBinding.getRoot().performClick();

        // Then
        assertEquals(BaseServiceCallActivity.MODE_FULLSCREEN, mActivity.getDisplayMode());
    }

    @Test
    public void btnMiniHangup_click_finishesActivity() {
        // Given
        mActivity.startDialing();
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
        mActivity.startDialing();
        mActivity.switchToInCall();

        // When
        mActivity.hangUp();

        // Then
        assertFalse(mActivity.isTimerRunning());
    }

    // ==================== Edge cases ====================

    @Test
    public void showEntryMode_calledTwice_staysEntry() {
        // When
        mActivity.showEntryMode();
        mActivity.showEntryMode();

        // Then
        assertTrue(mActivity.isEntryMode());
    }

    @Test
    public void startDialing_calledTwice_staysDialing() {
        // When
        mActivity.startDialing();
        mActivity.startDialing();

        // Then
        assertFalse(mActivity.isEntryMode());
        assertEquals(BaseServiceCallActivity.STATE_DIALING, mActivity.getCallState());
    }

    @Test
    public void miniTitle_showsServiceTitle() {
        // Then
        String expected = mActivity.getString(R.string.icall_service_title);
        assertEquals(expected, mMiniBinding.tvMiniTitle.getText().toString());
    }
}
