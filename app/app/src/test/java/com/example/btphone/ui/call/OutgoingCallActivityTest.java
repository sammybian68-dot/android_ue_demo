package com.example.btphone.ui.call;

import android.view.View;

import com.example.btphone.databinding.ActivityOutgoingCallBinding;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * {@link OutgoingCallActivity} 的单元测试。
 * <p>
 * 覆盖全屏/小窗切换、按钮点击、Mock 数据展示、禁用状态等场景。
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class OutgoingCallActivityTest {

    private OutgoingCallActivity mActivity;
    private ActivityOutgoingCallBinding mBinding;
    private ActivityController<OutgoingCallActivity> mController;

    @Before
    public void setUp() {
        mController = Robolectric.buildActivity(OutgoingCallActivity.class)
                .create()
                .start()
                .resume();
        mActivity = mController.get();
        mBinding = mActivity.getBinding();
    }

    // ==================== Initial state ====================

    @Test
    public void onCreate_initialState_isFullScreen() {
        // Given - activity created in setUp

        // When - initial state after onCreate

        // Then
        assertTrue(mActivity.isFullScreen());
    }

    @Test
    public void onCreate_initialState_fullScreenGroupVisible() {
        // Given - activity created in setUp

        // When - initial state

        // Then
        assertEquals(View.VISIBLE, mBinding.groupFullScreen.getVisibility());
    }

    @Test
    public void onCreate_initialState_miniWindowGone() {
        // Given - activity created in setUp

        // When - initial state

        // Then
        assertEquals(View.GONE, mBinding.layoutMini.getRoot().getVisibility());
    }

    // ==================== Mock data ====================

    @Test
    public void onCreate_mockData_displaysPhoneNumber() {
        // Given - activity created in setUp

        // When - initial state

        // Then
        assertEquals(OutgoingCallActivity.MOCK_PHONE_NUMBER,
                mBinding.tvPhoneNumber.getText().toString());
    }

    @Test
    public void onCreate_mockData_displaysLocation() {
        // Given - activity created in setUp

        // When - initial state

        // Then
        assertEquals(OutgoingCallActivity.MOCK_LOCATION,
                mBinding.tvLocation.getText().toString());
    }

    @Test
    public void onCreate_mockData_displaysMiniPhoneNumber() {
        // Given - activity created in setUp

        // When - initial state

        // Then
        assertEquals(OutgoingCallActivity.MOCK_PHONE_NUMBER,
                mBinding.layoutMini.tvMiniPhoneNumber.getText().toString());
    }

    // ==================== Disabled state ====================

    @Test
    public void onCreate_muteButton_isDisabled() {
        // Given - activity created in setUp

        // When - initial state

        // Then
        assertFalse(mBinding.btnMuteFull.isEnabled());
    }

    @Test
    public void onCreate_muteButton_alphaReduced() {
        // Given - activity created in setUp

        // When - initial state

        // Then
        assertEquals(0.35f, mBinding.btnMuteFull.getAlpha(), 0.01f);
    }

    // ==================== showFullScreen ====================

    @Test
    public void showFullScreen_fromMini_switchesToFull() {
        // Given
        mActivity.showMiniWindow();
        assertFalse(mActivity.isFullScreen());

        // When
        mActivity.showFullScreen();

        // Then
        assertTrue(mActivity.isFullScreen());
    }

    @Test
    public void showFullScreen_fromMini_restoresBackground() {
        // Given
        mActivity.showMiniWindow();
        assertNull(mBinding.getRoot().getBackground());

        // When
        mActivity.showFullScreen();

        // Then
        assertNotNull(mBinding.getRoot().getBackground());
    }

    @Test
    public void showFullScreen_fromMini_showsFullScreenGroup() {
        // Given
        mActivity.showMiniWindow();

        // When
        mActivity.showFullScreen();

        // Then
        assertEquals(View.VISIBLE, mBinding.groupFullScreen.getVisibility());
    }

    @Test
    public void showFullScreen_fromMini_hidesMiniWindow() {
        // Given
        mActivity.showMiniWindow();

        // When
        mActivity.showFullScreen();

        // Then
        assertEquals(View.GONE, mBinding.layoutMini.getRoot().getVisibility());
    }

    @Test
    public void showFullScreen_calledTwice_staysFullScreen() {
        // Given - already full screen

        // When
        mActivity.showFullScreen();
        mActivity.showFullScreen();

        // Then
        assertTrue(mActivity.isFullScreen());
        assertEquals(View.VISIBLE, mBinding.groupFullScreen.getVisibility());
    }

    // ==================== showMiniWindow ====================

    @Test
    public void showMiniWindow_fromFullScreen_switchesToMini() {
        // Given
        assertTrue(mActivity.isFullScreen());

        // When
        mActivity.showMiniWindow();

        // Then
        assertFalse(mActivity.isFullScreen());
    }

    @Test
    public void showMiniWindow_fromFullScreen_removesBackground() {
        // Given
        assertNotNull(mBinding.getRoot().getBackground());

        // When
        mActivity.showMiniWindow();

        // Then
        assertNull(mBinding.getRoot().getBackground());
    }

    @Test
    public void showMiniWindow_fromFullScreen_hidesFullScreenGroup() {
        // Given - initial full screen

        // When
        mActivity.showMiniWindow();

        // Then
        assertEquals(View.GONE, mBinding.groupFullScreen.getVisibility());
    }

    @Test
    public void showMiniWindow_fromFullScreen_showsMiniWindow() {
        // Given - initial full screen

        // When
        mActivity.showMiniWindow();

        // Then
        assertEquals(View.VISIBLE, mBinding.layoutMini.getRoot().getVisibility());
    }

    @Test
    public void showMiniWindow_calledTwice_staysMini() {
        // Given
        mActivity.showMiniWindow();

        // When
        mActivity.showMiniWindow();

        // Then
        assertFalse(mActivity.isFullScreen());
        assertEquals(View.GONE, mBinding.groupFullScreen.getVisibility());
    }

    // ==================== Button clicks: full screen ====================

    @Test
    public void btnMinimize_click_switchesToMiniWindow() {
        // Given
        assertTrue(mActivity.isFullScreen());

        // When
        mBinding.btnMinimize.performClick();

        // Then
        assertFalse(mActivity.isFullScreen());
    }

    @Test
    public void btnHangUpFull_click_finishesActivity() {
        // Given - activity in full screen

        // When
        mBinding.btnHangUpFull.performClick();

        // Then
        assertTrue(mActivity.isFinishing());
    }

    @Test
    public void btnSwitchPhone_click_doesNotFinish() {
        // Given - activity in full screen

        // When
        mBinding.btnSwitchPhone.performClick();

        // Then
        assertFalse(mActivity.isFinishing());
        assertTrue(mActivity.isFullScreen());
    }

    @Test
    public void btnKeyboard_click_doesNotFinish() {
        // Given - activity in full screen

        // When
        mBinding.btnKeyboard.performClick();

        // Then
        assertFalse(mActivity.isFinishing());
        assertTrue(mActivity.isFullScreen());
    }

    // ==================== Button clicks: mini window ====================

    @Test
    public void miniWindowRoot_click_switchesToFullScreen() {
        // Given
        mActivity.showMiniWindow();

        // When
        mBinding.layoutMini.getRoot().performClick();

        // Then
        assertTrue(mActivity.isFullScreen());
    }

    @Test
    public void btnMiniHangUp_click_finishesActivity() {
        // Given
        mActivity.showMiniWindow();

        // When
        mBinding.layoutMini.btnMiniHangUp.performClick();

        // Then
        assertTrue(mActivity.isFinishing());
    }

    @Test
    public void btnMiniMute_click_doesNotFinish() {
        // Given
        mActivity.showMiniWindow();

        // When
        mBinding.layoutMini.btnMiniMute.performClick();

        // Then
        assertFalse(mActivity.isFinishing());
    }

    // ==================== hangUp ====================

    @Test
    public void hangUp_called_finishesActivity() {
        // Given - activity running

        // When
        mActivity.hangUp();

        // Then
        assertTrue(mActivity.isFinishing());
    }

    // ==================== Placeholder method coverage ====================

    @Test
    public void onSwitchPhoneClicked_called_noException() {
        // Given - activity running

        // When
        mActivity.onSwitchPhoneClicked();

        // Then - no exception, activity still alive
        assertFalse(mActivity.isFinishing());
    }

    @Test
    public void onKeyboardClicked_called_noException() {
        // Given - activity running

        // When
        mActivity.onKeyboardClicked();

        // Then - no exception, activity still alive
        assertFalse(mActivity.isFinishing());
    }

    @Test
    public void onMiniMuteClicked_called_noException() {
        // Given - activity running

        // When
        mActivity.onMiniMuteClicked();

        // Then - no exception, activity still alive
        assertFalse(mActivity.isFinishing());
    }

    // ==================== Getters ====================

    @Test
    public void isFullScreen_initial_returnsTrue() {
        // Given - activity created

        // When
        boolean result = mActivity.isFullScreen();

        // Then
        assertTrue(result);
    }

    @Test
    public void isFullScreen_afterShowMini_returnsFalse() {
        // Given
        mActivity.showMiniWindow();

        // When
        boolean result = mActivity.isFullScreen();

        // Then
        assertFalse(result);
    }

    @Test
    public void getBinding_called_returnsNonNull() {
        // Given - activity created

        // When
        ActivityOutgoingCallBinding binding = mActivity.getBinding();

        // Then
        assertNotNull(binding);
    }
}
