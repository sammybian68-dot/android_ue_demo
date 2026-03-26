package com.example.btphone.ui.call;

import android.view.View;

import com.example.btphone.R;
import com.example.btphone.databinding.ActivityInCallBinding;

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
 * {@link InCallActivity} 的单元测试。
 * <p>
 * 覆盖全屏/小窗/顶栏切换、DTMF 键盘、静音、切换对话框、按钮点击等全部场景。
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class InCallActivityTest {

    private InCallActivity mActivity;
    private ActivityInCallBinding mBinding;
    private ActivityController<InCallActivity> mController;

    @Before
    public void setUp() {
        mController = Robolectric.buildActivity(InCallActivity.class)
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
        assertEquals(InCallActivity.DisplayMode.FULL_SCREEN, mActivity.getDisplayMode());
    }

    @Test
    public void onCreate_initialState_fullScreenGroupVisible() {
        // Given - activity created

        // When - initial state

        // Then
        assertEquals(View.VISIBLE, mBinding.groupFullScreen.getVisibility());
    }

    @Test
    public void onCreate_initialState_miniWindowGone() {
        // Given - activity created

        // When - initial state

        // Then
        assertEquals(View.GONE, mBinding.layoutMini.getRoot().getVisibility());
    }

    @Test
    public void onCreate_initialState_statusBarGone() {
        // Given - activity created

        // When - initial state

        // Then
        assertEquals(View.GONE, mBinding.layoutStatusBar.getRoot().getVisibility());
    }

    @Test
    public void onCreate_initialState_dtmfHidden() {
        // Given - activity created

        // When - initial state

        // Then
        assertFalse(mActivity.isDtmfVisible());
        assertEquals(View.GONE, mBinding.layoutDtmf.getRoot().getVisibility());
    }

    @Test
    public void onCreate_initialState_notMuted() {
        // Given - activity created

        // When - initial state

        // Then
        assertFalse(mActivity.isMuted());
    }

    @Test
    public void onCreate_initialState_switchDialogHidden() {
        // Given - activity created

        // When - initial state

        // Then
        assertFalse(mActivity.isSwitchDialogVisible());
    }

    @Test
    public void onCreate_initialState_dtmfInputEmpty() {
        // Given - activity created

        // When - initial state

        // Then
        assertEquals("", mActivity.getDtmfInput());
    }

    // ==================== Mock data ====================

    @Test
    public void onCreate_mockData_displaysPhoneNumber() {
        // Given - activity created

        // When - initial state

        // Then
        assertEquals(InCallActivity.MOCK_PHONE_NUMBER,
                mBinding.tvPhoneNumber.getText().toString());
    }

    @Test
    public void onCreate_mockData_displaysCallTime() {
        // Given - activity created

        // When - initial state

        // Then
        assertEquals(InCallActivity.MOCK_CALL_TIME,
                mBinding.tvCallTime.getText().toString());
    }

    @Test
    public void onCreate_mockData_displaysMiniPhoneNumber() {
        // Given - activity created

        // When - initial state

        // Then
        assertEquals(InCallActivity.MOCK_PHONE_NUMBER,
                mBinding.layoutMini.tvMiniPhoneNumber.getText().toString());
    }

    @Test
    public void onCreate_mockData_displaysMiniCallTime() {
        // Given - activity created

        // When - initial state

        // Then
        assertEquals(InCallActivity.MOCK_CALL_TIME,
                mBinding.layoutMini.tvMiniCallTime.getText().toString());
    }

    @Test
    public void onCreate_mockData_displaysStatusBarTime() {
        // Given - activity created

        // When - initial state

        // Then
        assertEquals(InCallActivity.MOCK_CALL_TIME,
                mBinding.layoutStatusBar.tvStatusBarTime.getText().toString());
    }

    // ==================== showFullScreen ====================

    @Test
    public void showFullScreen_fromMini_switchesToFull() {
        // Given
        mActivity.showMiniWindow();
        assertEquals(InCallActivity.DisplayMode.MINI_WINDOW, mActivity.getDisplayMode());

        // When
        mActivity.showFullScreen();

        // Then
        assertEquals(InCallActivity.DisplayMode.FULL_SCREEN, mActivity.getDisplayMode());
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
    public void showFullScreen_fromStatusBar_switchesToFull() {
        // Given
        mActivity.showStatusBar();
        assertEquals(InCallActivity.DisplayMode.STATUS_BAR, mActivity.getDisplayMode());

        // When
        mActivity.showFullScreen();

        // Then
        assertEquals(InCallActivity.DisplayMode.FULL_SCREEN, mActivity.getDisplayMode());
    }

    @Test
    public void showFullScreen_fromStatusBar_hidesStatusBar() {
        // Given
        mActivity.showStatusBar();

        // When
        mActivity.showFullScreen();

        // Then
        assertEquals(View.GONE, mBinding.layoutStatusBar.getRoot().getVisibility());
    }

    @Test
    public void showFullScreen_calledTwice_staysFullScreen() {
        // Given - already full screen

        // When
        mActivity.showFullScreen();
        mActivity.showFullScreen();

        // Then
        assertEquals(InCallActivity.DisplayMode.FULL_SCREEN, mActivity.getDisplayMode());
        assertEquals(View.VISIBLE, mBinding.groupFullScreen.getVisibility());
    }

    @Test
    public void showFullScreen_hidesDtmfKeyboard() {
        // Given
        mActivity.showDtmfKeyboard();
        assertTrue(mActivity.isDtmfVisible());

        // When
        mActivity.showMiniWindow();
        mActivity.showFullScreen();

        // Then
        assertFalse(mActivity.isDtmfVisible());
    }

    // ==================== showMiniWindow ====================

    @Test
    public void showMiniWindow_fromFullScreen_switchesToMini() {
        // Given
        assertEquals(InCallActivity.DisplayMode.FULL_SCREEN, mActivity.getDisplayMode());

        // When
        mActivity.showMiniWindow();

        // Then
        assertEquals(InCallActivity.DisplayMode.MINI_WINDOW, mActivity.getDisplayMode());
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
    public void showMiniWindow_fromFullScreen_hidesStatusBar() {
        // Given - initial full screen

        // When
        mActivity.showMiniWindow();

        // Then
        assertEquals(View.GONE, mBinding.layoutStatusBar.getRoot().getVisibility());
    }

    @Test
    public void showMiniWindow_calledTwice_staysMini() {
        // Given
        mActivity.showMiniWindow();

        // When
        mActivity.showMiniWindow();

        // Then
        assertEquals(InCallActivity.DisplayMode.MINI_WINDOW, mActivity.getDisplayMode());
        assertEquals(View.GONE, mBinding.groupFullScreen.getVisibility());
    }

    // ==================== showStatusBar ====================

    @Test
    public void showStatusBar_fromMini_switchesToStatusBar() {
        // Given
        mActivity.showMiniWindow();

        // When
        mActivity.showStatusBar();

        // Then
        assertEquals(InCallActivity.DisplayMode.STATUS_BAR, mActivity.getDisplayMode());
    }

    @Test
    public void showStatusBar_hidesFullScreen() {
        // Given - initial full screen

        // When
        mActivity.showStatusBar();

        // Then
        assertEquals(View.GONE, mBinding.groupFullScreen.getVisibility());
    }

    @Test
    public void showStatusBar_hidesMini() {
        // Given
        mActivity.showMiniWindow();

        // When
        mActivity.showStatusBar();

        // Then
        assertEquals(View.GONE, mBinding.layoutMini.getRoot().getVisibility());
    }

    @Test
    public void showStatusBar_showsStatusBarView() {
        // Given - initial full screen

        // When
        mActivity.showStatusBar();

        // Then
        assertEquals(View.VISIBLE, mBinding.layoutStatusBar.getRoot().getVisibility());
    }

    @Test
    public void showStatusBar_removesBackground() {
        // Given - initial full screen

        // When
        mActivity.showStatusBar();

        // Then
        assertNull(mBinding.getRoot().getBackground());
    }

    // ==================== DTMF keyboard ====================

    @Test
    public void toggleDtmfKeyboard_fromHidden_showsDtmf() {
        // Given
        assertFalse(mActivity.isDtmfVisible());

        // When
        mActivity.toggleDtmfKeyboard();

        // Then
        assertTrue(mActivity.isDtmfVisible());
        assertEquals(View.VISIBLE, mBinding.layoutDtmf.getRoot().getVisibility());
    }

    @Test
    public void toggleDtmfKeyboard_fromVisible_hidesDtmf() {
        // Given
        mActivity.showDtmfKeyboard();
        assertTrue(mActivity.isDtmfVisible());

        // When
        mActivity.toggleDtmfKeyboard();

        // Then
        assertFalse(mActivity.isDtmfVisible());
        assertEquals(View.GONE, mBinding.layoutDtmf.getRoot().getVisibility());
    }

    @Test
    public void showDtmfKeyboard_setsVisible() {
        // Given
        assertFalse(mActivity.isDtmfVisible());

        // When
        mActivity.showDtmfKeyboard();

        // Then
        assertTrue(mActivity.isDtmfVisible());
    }

    @Test
    public void hideDtmfKeyboard_setsHidden() {
        // Given
        mActivity.showDtmfKeyboard();

        // When
        mActivity.hideDtmfKeyboard();

        // Then
        assertFalse(mActivity.isDtmfVisible());
    }

    @Test
    public void onDtmfKeyPressed_singleDigit_updatesDisplay() {
        // Given
        mActivity.showDtmfKeyboard();

        // When
        mActivity.onDtmfKeyPressed('5');

        // Then
        assertEquals("5", mActivity.getDtmfInput());
        assertEquals("5", mBinding.layoutDtmf.tvDtmfDisplay.getText().toString());
    }

    @Test
    public void onDtmfKeyPressed_multipleDigits_appendsAll() {
        // Given
        mActivity.showDtmfKeyboard();

        // When
        mActivity.onDtmfKeyPressed('1');
        mActivity.onDtmfKeyPressed('2');
        mActivity.onDtmfKeyPressed('3');

        // Then
        assertEquals("123", mActivity.getDtmfInput());
    }

    @Test
    public void onDtmfKeyPressed_starAndHash_appendsCorrectly() {
        // Given
        mActivity.showDtmfKeyboard();

        // When
        mActivity.onDtmfKeyPressed('*');
        mActivity.onDtmfKeyPressed('#');

        // Then
        assertEquals("*#", mActivity.getDtmfInput());
    }

    // ==================== Mute ====================

    @Test
    public void toggleMute_firstCall_mutesCall() {
        // Given
        assertFalse(mActivity.isMuted());

        // When
        mActivity.toggleMute();

        // Then
        assertTrue(mActivity.isMuted());
    }

    @Test
    public void toggleMute_secondCall_unmutesCall() {
        // Given
        mActivity.toggleMute();
        assertTrue(mActivity.isMuted());

        // When
        mActivity.toggleMute();

        // Then
        assertFalse(mActivity.isMuted());
    }

    @Test
    public void toggleMute_updatesLabelToUnmute() {
        // Given
        assertFalse(mActivity.isMuted());

        // When
        mActivity.toggleMute();

        // Then
        assertEquals(mActivity.getString(R.string.in_call_unmute),
                mBinding.tvLabelMute.getText().toString());
    }

    @Test
    public void toggleMute_toggleBack_updatesLabelToMute() {
        // Given
        mActivity.toggleMute();

        // When
        mActivity.toggleMute();

        // Then
        assertEquals(mActivity.getString(R.string.in_call_mute),
                mBinding.tvLabelMute.getText().toString());
    }

    // ==================== Switch dialog ====================

    @Test
    public void showSwitchDialog_showsMaskAndDialog() {
        // Given
        assertFalse(mActivity.isSwitchDialogVisible());

        // When
        mActivity.showSwitchDialog();

        // Then
        assertTrue(mActivity.isSwitchDialogVisible());
        assertEquals(View.VISIBLE, mBinding.viewDialogMask.getVisibility());
        assertEquals(View.VISIBLE, mBinding.layoutSwitchDialog.getVisibility());
    }

    @Test
    public void dismissSwitchDialog_hidesMaskAndDialog() {
        // Given
        mActivity.showSwitchDialog();

        // When
        mActivity.dismissSwitchDialog();

        // Then
        assertFalse(mActivity.isSwitchDialogVisible());
        assertEquals(View.GONE, mBinding.viewDialogMask.getVisibility());
        assertEquals(View.GONE, mBinding.layoutSwitchDialog.getVisibility());
    }

    @Test
    public void onSwitchConfirmed_dismissesDialog() {
        // Given
        mActivity.showSwitchDialog();

        // When
        mActivity.onSwitchConfirmed();

        // Then
        assertFalse(mActivity.isSwitchDialogVisible());
    }

    // ==================== Button clicks: full screen ====================

    @Test
    public void btnMinimize_click_switchesToMiniWindow() {
        // Given
        assertEquals(InCallActivity.DisplayMode.FULL_SCREEN, mActivity.getDisplayMode());

        // When
        mBinding.btnMinimize.performClick();

        // Then
        assertEquals(InCallActivity.DisplayMode.MINI_WINDOW, mActivity.getDisplayMode());
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
    public void btnMuteFull_click_togglesMute() {
        // Given
        assertFalse(mActivity.isMuted());

        // When
        mBinding.btnMuteFull.performClick();

        // Then
        assertTrue(mActivity.isMuted());
    }

    @Test
    public void btnSwitchPhone_click_showsDialog() {
        // Given
        assertFalse(mActivity.isSwitchDialogVisible());

        // When
        mBinding.btnSwitchPhone.performClick();

        // Then
        assertTrue(mActivity.isSwitchDialogVisible());
    }

    @Test
    public void btnKeyboard_click_togglesDtmf() {
        // Given
        assertFalse(mActivity.isDtmfVisible());

        // When
        mBinding.btnKeyboard.performClick();

        // Then
        assertTrue(mActivity.isDtmfVisible());
    }

    @Test
    public void btnKeyboard_clickTwice_hidesDtmf() {
        // Given
        assertFalse(mActivity.isDtmfVisible());

        // When
        mBinding.btnKeyboard.performClick();
        mBinding.btnKeyboard.performClick();

        // Then
        assertFalse(mActivity.isDtmfVisible());
    }

    // ==================== Button clicks: mini window ====================

    @Test
    public void miniWindowRoot_click_switchesToFullScreen() {
        // Given
        mActivity.showMiniWindow();

        // When
        mBinding.layoutMini.getRoot().performClick();

        // Then
        assertEquals(InCallActivity.DisplayMode.FULL_SCREEN, mActivity.getDisplayMode());
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
    public void btnMiniMute_click_togglesMute() {
        // Given
        mActivity.showMiniWindow();
        assertFalse(mActivity.isMuted());

        // When
        mBinding.layoutMini.btnMiniMute.performClick();

        // Then
        assertTrue(mActivity.isMuted());
    }

    // ==================== Button clicks: status bar ====================

    @Test
    public void statusBar_click_switchesToFullScreen() {
        // Given
        mActivity.showStatusBar();

        // When
        mBinding.layoutStatusBar.getRoot().performClick();

        // Then
        assertEquals(InCallActivity.DisplayMode.FULL_SCREEN, mActivity.getDisplayMode());
    }

    // ==================== Button clicks: switch dialog ====================

    @Test
    public void btnDialogCancel_click_dismissesDialog() {
        // Given
        mActivity.showSwitchDialog();

        // When
        mBinding.btnDialogCancel.performClick();

        // Then
        assertFalse(mActivity.isSwitchDialogVisible());
    }

    @Test
    public void btnDialogConfirm_click_dismissesDialog() {
        // Given
        mActivity.showSwitchDialog();

        // When
        mBinding.btnDialogConfirm.performClick();

        // Then
        assertFalse(mActivity.isSwitchDialogVisible());
    }

    @Test
    public void viewDialogMask_click_dismissesDialog() {
        // Given
        mActivity.showSwitchDialog();

        // When
        mBinding.viewDialogMask.performClick();

        // Then
        assertFalse(mActivity.isSwitchDialogVisible());
    }

    // ==================== Button clicks: DTMF keys ====================

    @Test
    public void dtmfKey1_click_appends1() {
        // Given
        mActivity.showDtmfKeyboard();

        // When
        mBinding.layoutDtmf.btnDtmf1.performClick();

        // Then
        assertEquals("1", mActivity.getDtmfInput());
    }

    @Test
    public void dtmfKey2_click_appends2() {
        // Given
        mActivity.showDtmfKeyboard();

        // When
        mBinding.layoutDtmf.btnDtmf2.performClick();

        // Then
        assertEquals("2", mActivity.getDtmfInput());
    }

    @Test
    public void dtmfKey3_click_appends3() {
        // Given
        mActivity.showDtmfKeyboard();

        // When
        mBinding.layoutDtmf.btnDtmf3.performClick();

        // Then
        assertEquals("3", mActivity.getDtmfInput());
    }

    @Test
    public void dtmfKey4_click_appends4() {
        // Given
        mActivity.showDtmfKeyboard();

        // When
        mBinding.layoutDtmf.btnDtmf4.performClick();

        // Then
        assertEquals("4", mActivity.getDtmfInput());
    }

    @Test
    public void dtmfKey5_click_appends5() {
        // Given
        mActivity.showDtmfKeyboard();

        // When
        mBinding.layoutDtmf.btnDtmf5.performClick();

        // Then
        assertEquals("5", mActivity.getDtmfInput());
    }

    @Test
    public void dtmfKey6_click_appends6() {
        // Given
        mActivity.showDtmfKeyboard();

        // When
        mBinding.layoutDtmf.btnDtmf6.performClick();

        // Then
        assertEquals("6", mActivity.getDtmfInput());
    }

    @Test
    public void dtmfKey7_click_appends7() {
        // Given
        mActivity.showDtmfKeyboard();

        // When
        mBinding.layoutDtmf.btnDtmf7.performClick();

        // Then
        assertEquals("7", mActivity.getDtmfInput());
    }

    @Test
    public void dtmfKey8_click_appends8() {
        // Given
        mActivity.showDtmfKeyboard();

        // When
        mBinding.layoutDtmf.btnDtmf8.performClick();

        // Then
        assertEquals("8", mActivity.getDtmfInput());
    }

    @Test
    public void dtmfKey9_click_appends9() {
        // Given
        mActivity.showDtmfKeyboard();

        // When
        mBinding.layoutDtmf.btnDtmf9.performClick();

        // Then
        assertEquals("9", mActivity.getDtmfInput());
    }

    @Test
    public void dtmfKey0_click_appends0() {
        // Given
        mActivity.showDtmfKeyboard();

        // When
        mBinding.layoutDtmf.btnDtmf0.performClick();

        // Then
        assertEquals("0", mActivity.getDtmfInput());
    }

    @Test
    public void dtmfKeyStar_click_appendsStar() {
        // Given
        mActivity.showDtmfKeyboard();

        // When
        mBinding.layoutDtmf.btnDtmfStar.performClick();

        // Then
        assertEquals("*", mActivity.getDtmfInput());
    }

    @Test
    public void dtmfKeyHash_click_appendsHash() {
        // Given
        mActivity.showDtmfKeyboard();

        // When
        mBinding.layoutDtmf.btnDtmfHash.performClick();

        // Then
        assertEquals("#", mActivity.getDtmfInput());
    }

    @Test
    public void dtmfKeys_multipleClicks_appendsSequence() {
        // Given
        mActivity.showDtmfKeyboard();

        // When
        mBinding.layoutDtmf.btnDtmf1.performClick();
        mBinding.layoutDtmf.btnDtmf0.performClick();
        mBinding.layoutDtmf.btnDtmf8.performClick();
        mBinding.layoutDtmf.btnDtmfStar.performClick();

        // Then
        assertEquals("108*", mActivity.getDtmfInput());
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

    // ==================== Getters ====================

    @Test
    public void getDisplayMode_initial_returnsFull() {
        // Given - activity created

        // When
        InCallActivity.DisplayMode mode = mActivity.getDisplayMode();

        // Then
        assertEquals(InCallActivity.DisplayMode.FULL_SCREEN, mode);
    }

    @Test
    public void getBinding_called_returnsNonNull() {
        // Given - activity created

        // When
        ActivityInCallBinding binding = mActivity.getBinding();

        // Then
        assertNotNull(binding);
    }

    @Test
    public void isDtmfVisible_afterShow_returnsTrue() {
        // Given
        mActivity.showDtmfKeyboard();

        // When
        boolean result = mActivity.isDtmfVisible();

        // Then
        assertTrue(result);
    }

    @Test
    public void isDtmfVisible_afterHide_returnsFalse() {
        // Given
        mActivity.showDtmfKeyboard();
        mActivity.hideDtmfKeyboard();

        // When
        boolean result = mActivity.isDtmfVisible();

        // Then
        assertFalse(result);
    }

    @Test
    public void isMuted_afterToggle_returnsTrue() {
        // Given
        mActivity.toggleMute();

        // When
        boolean result = mActivity.isMuted();

        // Then
        assertTrue(result);
    }

    @Test
    public void isSwitchDialogVisible_afterShow_returnsTrue() {
        // Given
        mActivity.showSwitchDialog();

        // When
        boolean result = mActivity.isSwitchDialogVisible();

        // Then
        assertTrue(result);
    }

    @Test
    public void getDtmfInput_afterKeyPress_returnsInput() {
        // Given
        mActivity.onDtmfKeyPressed('9');

        // When
        String result = mActivity.getDtmfInput();

        // Then
        assertEquals("9", result);
    }

    // ==================== Edge cases ====================

    @Test
    public void showFullScreen_withDtmfOpen_closesDtmf() {
        // Given
        mActivity.showDtmfKeyboard();
        assertTrue(mActivity.isDtmfVisible());

        // When (switch to mini then back to full)
        mActivity.showMiniWindow();

        // Then
        assertFalse(mActivity.isDtmfVisible());
    }

    @Test
    public void showMiniWindow_withDtmfOpen_closesDtmf() {
        // Given
        mActivity.showDtmfKeyboard();
        assertTrue(mActivity.isDtmfVisible());

        // When
        mActivity.showMiniWindow();

        // Then
        assertFalse(mActivity.isDtmfVisible());
    }

    @Test
    public void showStatusBar_withDtmfOpen_closesDtmf() {
        // Given
        mActivity.showDtmfKeyboard();
        assertTrue(mActivity.isDtmfVisible());

        // When
        mActivity.showStatusBar();

        // Then
        assertFalse(mActivity.isDtmfVisible());
    }

    @Test
    public void multipleTransitions_fullMiniStatusFull_correctState() {
        // Given - starts full

        // When
        mActivity.showMiniWindow();
        mActivity.showStatusBar();
        mActivity.showFullScreen();

        // Then
        assertEquals(InCallActivity.DisplayMode.FULL_SCREEN, mActivity.getDisplayMode());
        assertEquals(View.VISIBLE, mBinding.groupFullScreen.getVisibility());
        assertEquals(View.GONE, mBinding.layoutMini.getRoot().getVisibility());
        assertEquals(View.GONE, mBinding.layoutStatusBar.getRoot().getVisibility());
    }

    @Test
    public void multipleTransitions_fullStatusMini_correctState() {
        // Given - starts full

        // When
        mActivity.showStatusBar();
        mActivity.showMiniWindow();

        // Then
        assertEquals(InCallActivity.DisplayMode.MINI_WINDOW, mActivity.getDisplayMode());
        assertEquals(View.GONE, mBinding.groupFullScreen.getVisibility());
        assertEquals(View.VISIBLE, mBinding.layoutMini.getRoot().getVisibility());
        assertEquals(View.GONE, mBinding.layoutStatusBar.getRoot().getVisibility());
    }
}
