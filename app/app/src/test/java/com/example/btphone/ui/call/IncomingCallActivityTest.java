package com.example.btphone.ui.call;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.view.View;

import androidx.core.content.ContextCompat;

import com.example.btphone.R;
import com.example.btphone.databinding.ActivityIncomingCallBinding;
import com.example.btphone.databinding.LayoutIncomingCallMiniBinding;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

/**
 * {@link IncomingCallActivity} 单元测试。
 * <p>
 * 覆盖全屏/小窗切换、隐私模式、接听/挂断操作和 mock 数据初始化。
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class IncomingCallActivityTest {

    private IncomingCallActivity mActivity;
    private ActivityIncomingCallBinding mBinding;
    private LayoutIncomingCallMiniBinding mMiniBinding;

    @Before
    public void setUp() {
        ActivityController<IncomingCallActivity> controller =
                Robolectric.buildActivity(IncomingCallActivity.class);
        controller.create();
        mActivity = controller.get();
        mBinding = mActivity.getBinding();
        mMiniBinding = mActivity.getMiniBinding();
    }

    // ==================== Initialization ====================

    @Test
    public void onCreate_default_startsInFullscreenMode() {
        // Then
        assertEquals(IncomingCallActivity.MODE_FULLSCREEN, mActivity.getCurrentMode());
    }

    @Test
    public void onCreate_default_fullscreenGroupVisible() {
        // Then
        assertEquals(View.VISIBLE, mBinding.groupFullscreen.getVisibility());
    }

    @Test
    public void onCreate_default_miniContainerGone() {
        // Then
        assertEquals(View.GONE, mMiniBinding.getRoot().getVisibility());
    }

    @Test
    public void onCreate_default_privacyModeOff() {
        // Then
        assertFalse(mActivity.isPrivacyMode());
    }

    @Test
    public void onCreate_default_bindingsNotNull() {
        // Then
        assertNotNull(mBinding);
        assertNotNull(mMiniBinding);
    }

    // ==================== Mock data ====================

    @Test
    public void getCallerName_default_returnsMockName() {
        // Then
        assertEquals("火龙果", mActivity.getCallerName());
    }

    @Test
    public void getPhoneNumber_default_returnsMockNumber() {
        // Then
        assertEquals("136 3890 0900", mActivity.getPhoneNumber());
    }

    @Test
    public void onCreate_default_callerNameDisplayed() {
        // Then
        assertEquals("火龙果", mBinding.tvCallerName.getText().toString());
    }

    @Test
    public void onCreate_default_phoneNumberDisplayed() {
        // Then
        assertEquals("136 3890 0900", mBinding.tvPhoneNumber.getText().toString());
    }

    @Test
    public void onCreate_default_fullscreenStatusDisplayed() {
        // Then
        String expected = mActivity.getString(R.string.incoming_call_status);
        assertEquals(expected, mBinding.tvCallStatusFull.getText().toString());
    }

    @Test
    public void onCreate_default_answerLabelDisplayed() {
        // Then
        String expected = mActivity.getString(R.string.incoming_call_answer);
        assertEquals(expected, mBinding.tvAnswerLabel.getText().toString());
    }

    @Test
    public void onCreate_default_hangupLabelDisplayed() {
        // Then
        String expected = mActivity.getString(R.string.incoming_call_hangup);
        assertEquals(expected, mBinding.tvHangupLabel.getText().toString());
    }

    @Test
    public void onCreate_default_miniPhoneNumberDisplayed() {
        // Then
        assertEquals("136 3890 0900", mMiniBinding.tvMiniPhoneNumber.getText().toString());
    }

    @Test
    public void onCreate_default_miniStatusDisplayed() {
        // Then
        String expected = mActivity.getString(R.string.incoming_call_status_mini);
        assertEquals(expected, mMiniBinding.tvMiniCallStatus.getText().toString());
    }

    // ==================== Mode switching ====================

    @Test
    public void switchToMini_fromFullscreen_modeChangesToMini() {
        // When
        mActivity.switchToMini();

        // Then
        assertEquals(IncomingCallActivity.MODE_MINI, mActivity.getCurrentMode());
    }

    @Test
    public void switchToMini_fromFullscreen_fullscreenGroupGone() {
        // When
        mActivity.switchToMini();

        // Then
        assertEquals(View.GONE, mBinding.groupFullscreen.getVisibility());
    }

    @Test
    public void switchToMini_fromFullscreen_miniContainerVisible() {
        // When
        mActivity.switchToMini();

        // Then
        assertEquals(View.VISIBLE, mMiniBinding.getRoot().getVisibility());
    }

    @Test
    public void switchToFullscreen_fromMini_modeChangesToFullscreen() {
        // Given
        mActivity.switchToMini();

        // When
        mActivity.switchToFullscreen();

        // Then
        assertEquals(IncomingCallActivity.MODE_FULLSCREEN, mActivity.getCurrentMode());
    }

    @Test
    public void switchToFullscreen_fromMini_fullscreenGroupVisible() {
        // Given
        mActivity.switchToMini();

        // When
        mActivity.switchToFullscreen();

        // Then
        assertEquals(View.VISIBLE, mBinding.groupFullscreen.getVisibility());
    }

    @Test
    public void switchToFullscreen_fromMini_miniContainerGone() {
        // Given
        mActivity.switchToMini();

        // When
        mActivity.switchToFullscreen();

        // Then
        assertEquals(View.GONE, mMiniBinding.getRoot().getVisibility());
    }

    // ==================== Privacy mode ====================

    @Test
    public void setPrivacyMode_enableInMini_phoneNumberHidden() {
        // Given
        mActivity.switchToMini();

        // When
        mActivity.setPrivacyMode(true);

        // Then
        assertTrue(mActivity.isPrivacyMode());
        assertEquals(View.GONE, mMiniBinding.tvMiniPhoneNumber.getVisibility());
    }

    @Test
    public void setPrivacyMode_enableInMini_statusTextUsesPrivacyStyle() {
        // Given
        mActivity.switchToMini();

        // When
        mActivity.setPrivacyMode(true);

        // Then
        String expected = mActivity.getString(R.string.incoming_call_status_mini);
        assertEquals(expected, mMiniBinding.tvMiniCallStatus.getText().toString());
        int expectedColor = ContextCompat.getColor(mActivity, R.color.dark_primary_text);
        assertEquals(expectedColor, mMiniBinding.tvMiniCallStatus.getCurrentTextColor());
    }

    @Test
    public void setPrivacyMode_disableInMini_phoneNumberVisible() {
        // Given
        mActivity.switchToMini();
        mActivity.setPrivacyMode(true);

        // When
        mActivity.setPrivacyMode(false);

        // Then
        assertFalse(mActivity.isPrivacyMode());
        assertEquals(View.VISIBLE, mMiniBinding.tvMiniPhoneNumber.getVisibility());
        assertEquals("136 3890 0900", mMiniBinding.tvMiniPhoneNumber.getText().toString());
    }

    @Test
    public void setPrivacyMode_disableInMini_statusTextUsesNormalStyle() {
        // Given
        mActivity.switchToMini();
        mActivity.setPrivacyMode(true);

        // When
        mActivity.setPrivacyMode(false);

        // Then
        int expectedColor = ContextCompat.getColor(mActivity, R.color.dark_secondary_text);
        assertEquals(expectedColor, mMiniBinding.tvMiniCallStatus.getCurrentTextColor());
    }

    @Test
    public void setPrivacyMode_enableInFullscreen_stateChangesButNoMiniUpdate() {
        // Given — still in fullscreen

        // When
        mActivity.setPrivacyMode(true);

        // Then — privacy flag is set
        assertTrue(mActivity.isPrivacyMode());
        // Mini phone number visibility unchanged (mini not visible anyway)
    }

    @Test
    public void setPrivacyMode_enableThenSwitchToMini_privacyApplied() {
        // Given
        mActivity.setPrivacyMode(true);

        // When
        mActivity.switchToMini();

        // Then
        assertEquals(View.GONE, mMiniBinding.tvMiniPhoneNumber.getVisibility());
    }

    // ==================== Click handlers ====================

    @Test
    public void btnMinimize_click_switchesToMini() {
        // When
        mBinding.btnMinimize.performClick();

        // Then
        assertEquals(IncomingCallActivity.MODE_MINI, mActivity.getCurrentMode());
    }

    @Test
    public void btnAnswerFull_click_finishesActivity() {
        // When
        mBinding.btnAnswerFull.performClick();

        // Then
        assertTrue(mActivity.isFinishing());
    }

    @Test
    public void btnHangupFull_click_finishesActivity() {
        // When
        mBinding.btnHangupFull.performClick();

        // Then
        assertTrue(mActivity.isFinishing());
    }

    @Test
    public void miniRoot_click_switchesToFullscreen() {
        // Given
        mActivity.switchToMini();

        // When
        mMiniBinding.getRoot().performClick();

        // Then
        assertEquals(IncomingCallActivity.MODE_FULLSCREEN, mActivity.getCurrentMode());
    }

    @Test
    public void btnMiniAnswer_click_finishesActivity() {
        // Given
        mActivity.switchToMini();

        // When
        mMiniBinding.btnMiniAnswer.performClick();

        // Then
        assertTrue(mActivity.isFinishing());
    }

    @Test
    public void btnMiniHangup_click_finishesActivity() {
        // Given
        mActivity.switchToMini();

        // When
        mMiniBinding.btnMiniHangup.performClick();

        // Then
        assertTrue(mActivity.isFinishing());
    }

    // ==================== answerCall / hangupCall direct ====================

    @Test
    public void answerCall_always_finishesActivity() {
        // When
        mActivity.answerCall();

        // Then
        assertTrue(mActivity.isFinishing());
    }

    @Test
    public void hangupCall_always_finishesActivity() {
        // When
        mActivity.hangupCall();

        // Then
        assertTrue(mActivity.isFinishing());
    }

    // ==================== Edge cases ====================

    @Test
    public void switchToFullscreen_calledTwice_staysFullscreen() {
        // When
        mActivity.switchToFullscreen();
        mActivity.switchToFullscreen();

        // Then
        assertEquals(IncomingCallActivity.MODE_FULLSCREEN, mActivity.getCurrentMode());
        assertEquals(View.VISIBLE, mBinding.groupFullscreen.getVisibility());
    }

    @Test
    public void switchToMini_calledTwice_staysMini() {
        // When
        mActivity.switchToMini();
        mActivity.switchToMini();

        // Then
        assertEquals(IncomingCallActivity.MODE_MINI, mActivity.getCurrentMode());
        assertEquals(View.VISIBLE, mMiniBinding.getRoot().getVisibility());
    }

    @Test
    public void setPrivacyMode_toggleMultipleTimes_correctState() {
        // Given
        mActivity.switchToMini();

        // When
        mActivity.setPrivacyMode(true);
        mActivity.setPrivacyMode(false);
        mActivity.setPrivacyMode(true);

        // Then
        assertTrue(mActivity.isPrivacyMode());
        assertEquals(View.GONE, mMiniBinding.tvMiniPhoneNumber.getVisibility());
    }

    @Test
    public void getCurrentMode_afterMiniThenFullscreen_returnsFullscreen() {
        // Given
        mActivity.switchToMini();

        // When
        mActivity.switchToFullscreen();

        // Then
        assertEquals(IncomingCallActivity.MODE_FULLSCREEN, mActivity.getCurrentMode());
    }
}
