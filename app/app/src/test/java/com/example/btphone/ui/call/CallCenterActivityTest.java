package com.example.btphone.ui.call;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.view.View;

import com.example.btphone.R;
import com.example.btphone.databinding.ActivityCallCenterBinding;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

/**
 * {@link CallCenterActivity} 单元测试。
 * <p>
 * 覆盖入口模式 / 呼叫中模式切换、按钮点击事件和 mock 数据显示。
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class CallCenterActivityTest {

    private CallCenterActivity mActivity;
    private ActivityCallCenterBinding mBinding;

    @Before
    public void setUp() {
        ActivityController<CallCenterActivity> controller =
                Robolectric.buildActivity(CallCenterActivity.class);
        controller.create();
        mActivity = controller.get();
        mBinding = mActivity.getBinding();
    }

    // ==================== Initialization ====================

    @Test
    public void onCreate_default_bindingNotNull() {
        // Then
        assertNotNull(mBinding);
    }

    @Test
    public void onCreate_default_startsInEntryMode() {
        // Then
        assertEquals(CallCenterActivity.MODE_ENTRY, mActivity.getCurrentMode());
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
    public void onCreate_default_entryTitleDisplayed() {
        // Then
        String expected = mActivity.getString(R.string.call_center_title);
        assertEquals(expected, mBinding.tvEntryTitle.getText().toString());
    }

    @Test
    public void onCreate_default_entryPhoneDisplayed() {
        // Then
        String expected = mActivity.getString(R.string.call_center_phone);
        assertEquals(expected, mBinding.tvEntryPhone.getText().toString());
    }

    @Test
    public void onCreate_default_descLine1Displayed() {
        // Then
        String expected = mActivity.getString(R.string.call_center_desc_line1);
        assertEquals(expected, mBinding.tvDescLine1.getText().toString());
    }

    @Test
    public void onCreate_default_descLine2Displayed() {
        // Then
        String expected = mActivity.getString(R.string.call_center_desc_line2);
        assertEquals(expected, mBinding.tvDescLine2.getText().toString());
    }

    @Test
    public void onCreate_default_callingTitleText() {
        // Then
        String expected = mActivity.getString(R.string.call_center_calling_title);
        assertEquals(expected, mBinding.tvCallingTitle.getText().toString());
    }

    // ==================== Mode switching ====================

    @Test
    public void startCalling_fromEntry_switchesToCallingMode() {
        // When
        mActivity.startCalling();

        // Then
        assertEquals(CallCenterActivity.MODE_CALLING, mActivity.getCurrentMode());
    }

    @Test
    public void startCalling_fromEntry_entryGroupGone() {
        // When
        mActivity.startCalling();

        // Then
        assertEquals(View.GONE, mBinding.groupEntry.getVisibility());
    }

    @Test
    public void startCalling_fromEntry_callingGroupVisible() {
        // When
        mActivity.startCalling();

        // Then
        assertEquals(View.VISIBLE, mBinding.groupCalling.getVisibility());
    }

    @Test
    public void showEntry_fromCalling_switchesToEntryMode() {
        // Given
        mActivity.startCalling();

        // When
        mActivity.showEntry();

        // Then
        assertEquals(CallCenterActivity.MODE_ENTRY, mActivity.getCurrentMode());
    }

    @Test
    public void showEntry_fromCalling_entryGroupVisible() {
        // Given
        mActivity.startCalling();

        // When
        mActivity.showEntry();

        // Then
        assertEquals(View.VISIBLE, mBinding.groupEntry.getVisibility());
    }

    @Test
    public void showEntry_fromCalling_callingGroupGone() {
        // Given
        mActivity.startCalling();

        // When
        mActivity.showEntry();

        // Then
        assertEquals(View.GONE, mBinding.groupCalling.getVisibility());
    }

    // ==================== Click handlers ====================

    @Test
    public void btnCall_click_switchesToCallingMode() {
        // When
        mBinding.btnCall.performClick();

        // Then
        assertEquals(CallCenterActivity.MODE_CALLING, mActivity.getCurrentMode());
    }

    @Test
    public void btnHangup_click_finishesActivity() {
        // Given
        mActivity.startCalling();

        // When
        mBinding.btnHangup.performClick();

        // Then
        assertTrue(mActivity.isFinishing());
    }

    @Test
    public void btnMinimize_click_finishesActivity() {
        // When
        mBinding.btnMinimize.performClick();

        // Then
        assertTrue(mActivity.isFinishing());
    }

    @Test
    public void btnMute_click_doesNotCrash() {
        // Given
        mActivity.startCalling();

        // When
        mBinding.btnMute.performClick();

        // Then — no crash
        assertEquals(CallCenterActivity.MODE_CALLING, mActivity.getCurrentMode());
    }

    // ==================== Edge cases ====================

    @Test
    public void startCalling_calledTwice_staysInCallingMode() {
        // When
        mActivity.startCalling();
        mActivity.startCalling();

        // Then
        assertEquals(CallCenterActivity.MODE_CALLING, mActivity.getCurrentMode());
    }

    @Test
    public void showEntry_calledTwice_staysInEntryMode() {
        // When
        mActivity.showEntry();
        mActivity.showEntry();

        // Then
        assertEquals(CallCenterActivity.MODE_ENTRY, mActivity.getCurrentMode());
    }

    @Test
    public void hangUp_always_finishesActivity() {
        // When
        mActivity.hangUp();

        // Then
        assertTrue(mActivity.isFinishing());
    }

    @Test
    public void onMuteClicked_always_doesNotCrash() {
        // When
        mActivity.onMuteClicked();

        // Then — no exception
        assertNotNull(mActivity);
    }
}
