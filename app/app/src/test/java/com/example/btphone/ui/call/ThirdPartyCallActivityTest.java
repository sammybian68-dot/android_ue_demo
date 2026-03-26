package com.example.btphone.ui.call;

import android.view.View;

import com.example.btphone.R;
import com.example.btphone.databinding.ActivityThirdPartyCallBinding;
import com.example.btphone.databinding.LayoutThirdPartyCallMiniBinding;
import com.example.btphone.databinding.LayoutThirdPartyIncomingMiniBinding;

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
 * {@link ThirdPartyCallActivity} 的单元测试。
 * <p>
 * 覆盖四种显示模式切换、来电接听、挂断、静音、通话交换等全部场景。
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class ThirdPartyCallActivityTest {

    private ThirdPartyCallActivity mActivity;
    private ActivityThirdPartyCallBinding mBinding;
    private LayoutThirdPartyIncomingMiniBinding mMiniIncomingBinding;
    private LayoutThirdPartyCallMiniBinding mMiniCallBinding;
    private ActivityController<ThirdPartyCallActivity> mController;

    @Before
    public void setUp() {
        mController = Robolectric.buildActivity(ThirdPartyCallActivity.class)
                .create()
                .start()
                .resume();
        mActivity = mController.get();
        mBinding = mActivity.getBinding();
        mMiniIncomingBinding = mActivity.getMiniIncomingBinding();
        mMiniCallBinding = mActivity.getMiniCallBinding();
    }

    // ==================== Initial state ====================

    @Test
    public void onCreate_initialState_isIncomingFull() {
        // Given - activity created in setUp

        // When - initial state after onCreate

        // Then
        assertEquals(ThirdPartyCallActivity.DisplayMode.INCOMING_FULL,
                mActivity.getDisplayMode());
    }

    @Test
    public void onCreate_initialState_incomingGroupVisible() {
        // Given - activity created

        // When - initial state

        // Then
        assertEquals(View.VISIBLE, mBinding.groupIncoming.getVisibility());
    }

    @Test
    public void onCreate_initialState_inCallGroupGone() {
        // Given - activity created

        // When - initial state

        // Then
        assertEquals(View.GONE, mBinding.groupInCall.getVisibility());
    }

    @Test
    public void onCreate_initialState_miniIncomingGone() {
        // Given - activity created

        // When - initial state

        // Then
        assertEquals(View.GONE, mMiniIncomingBinding.getRoot().getVisibility());
    }

    @Test
    public void onCreate_initialState_miniCallGone() {
        // Given - activity created

        // When - initial state

        // Then
        assertEquals(View.GONE, mMiniCallBinding.getRoot().getVisibility());
    }

    @Test
    public void onCreate_initialState_notMuted() {
        // Given - activity created

        // When - initial state

        // Then
        assertFalse(mActivity.isMuted());
    }

    // ==================== Mock data ====================

    @Test
    public void onCreate_mockData_displaysCurrentName() {
        // Given - activity created

        // When - initial state

        // Then
        assertEquals(ThirdPartyCallActivity.MOCK_CURRENT_NAME,
                mBinding.tvCurrentName.getText().toString());
    }

    @Test
    public void onCreate_mockData_displaysCurrentStatus() {
        // Given - activity created

        // When - initial state

        // Then
        assertEquals(ThirdPartyCallActivity.MOCK_CURRENT_TIME,
                mBinding.tvCurrentStatus.getText().toString());
    }

    @Test
    public void onCreate_mockData_displaysCallerName() {
        // Given - activity created

        // When - initial state

        // Then
        assertEquals(ThirdPartyCallActivity.MOCK_INCOMING_NAME,
                mBinding.tvCallerName.getText().toString());
    }

    @Test
    public void onCreate_mockData_displaysCallerNumber() {
        // Given - activity created

        // When - initial state

        // Then
        assertEquals(ThirdPartyCallActivity.MOCK_INCOMING_NUMBER,
                mBinding.tvCallerNumber.getText().toString());
    }

    @Test
    public void onCreate_mockData_displaysCallerStatus() {
        // Given - activity created

        // When - initial state

        // Then
        assertEquals(mActivity.getString(R.string.tp_incoming_status),
                mBinding.tvCallerStatus.getText().toString());
    }

    @Test
    public void onCreate_mockData_displaysHeldName() {
        // Given - activity created

        // When - initial state

        // Then
        assertEquals(ThirdPartyCallActivity.MOCK_CURRENT_NUMBER,
                mBinding.tvHeldName.getText().toString());
    }

    @Test
    public void onCreate_mockData_displaysHeldStatus() {
        // Given - activity created

        // When - initial state

        // Then
        assertEquals(mActivity.getString(R.string.tp_call_hold),
                mBinding.tvHeldStatus.getText().toString());
    }

    @Test
    public void onCreate_mockData_displaysActiveName() {
        // Given - activity created

        // When - initial state

        // Then
        assertEquals(ThirdPartyCallActivity.MOCK_ACTIVE_NAME,
                mBinding.tvActiveName.getText().toString());
    }

    @Test
    public void onCreate_mockData_displaysActiveTime() {
        // Given - activity created

        // When - initial state

        // Then
        assertEquals(ThirdPartyCallActivity.MOCK_ACTIVE_TIME,
                mBinding.tvActiveTime.getText().toString());
    }

    @Test
    public void onCreate_mockData_displaysMiniIncomingRow1Name() {
        // Given - activity created

        // When - initial state

        // Then
        assertEquals(ThirdPartyCallActivity.MOCK_CURRENT_NUMBER,
                mMiniIncomingBinding.tvMiniIncomingRow1Name.getText().toString());
    }

    @Test
    public void onCreate_mockData_displaysMiniIncomingRow1Status() {
        // Given - activity created

        // When - initial state

        // Then
        assertEquals(ThirdPartyCallActivity.MOCK_CURRENT_TIME,
                mMiniIncomingBinding.tvMiniIncomingRow1Status.getText().toString());
    }

    @Test
    public void onCreate_mockData_displaysMiniIncomingRow2Name() {
        // Given - activity created

        // When - initial state

        // Then
        assertEquals(ThirdPartyCallActivity.MOCK_ACTIVE_NUMBER,
                mMiniIncomingBinding.tvMiniIncomingRow2Name.getText().toString());
    }

    @Test
    public void onCreate_mockData_displaysMiniIncomingRow2Status() {
        // Given - activity created

        // When - initial state

        // Then
        assertEquals(mActivity.getString(R.string.tp_incoming_status_mini),
                mMiniIncomingBinding.tvMiniIncomingRow2Status.getText().toString());
    }

    @Test
    public void onCreate_mockData_displaysMiniCallRow1Name() {
        // Given - activity created

        // When - initial state

        // Then
        assertEquals(ThirdPartyCallActivity.MOCK_CURRENT_NUMBER,
                mMiniCallBinding.tvMiniCallRow1Name.getText().toString());
    }

    @Test
    public void onCreate_mockData_displaysMiniCallRow1Status() {
        // Given - activity created

        // When - initial state

        // Then
        assertEquals(mActivity.getString(R.string.tp_call_hold),
                mMiniCallBinding.tvMiniCallRow1Status.getText().toString());
    }

    @Test
    public void onCreate_mockData_displaysMiniCallRow2Name() {
        // Given - activity created

        // When - initial state

        // Then
        assertEquals(ThirdPartyCallActivity.MOCK_ACTIVE_NUMBER,
                mMiniCallBinding.tvMiniCallRow2Name.getText().toString());
    }

    @Test
    public void onCreate_mockData_displaysMiniCallRow2Status() {
        // Given - activity created

        // When - initial state

        // Then
        assertEquals(ThirdPartyCallActivity.MOCK_ACTIVE_TIME,
                mMiniCallBinding.tvMiniCallRow2Status.getText().toString());
    }

    // ==================== showIncomingFull ====================

    @Test
    public void showIncomingFull_fromInCallFull_switchesToIncomingFull() {
        // Given
        mActivity.showInCallFull();
        assertEquals(ThirdPartyCallActivity.DisplayMode.IN_CALL_FULL,
                mActivity.getDisplayMode());

        // When
        mActivity.showIncomingFull();

        // Then
        assertEquals(ThirdPartyCallActivity.DisplayMode.INCOMING_FULL,
                mActivity.getDisplayMode());
    }

    @Test
    public void showIncomingFull_showsIncomingGroup() {
        // Given
        mActivity.showInCallFull();

        // When
        mActivity.showIncomingFull();

        // Then
        assertEquals(View.VISIBLE, mBinding.groupIncoming.getVisibility());
    }

    @Test
    public void showIncomingFull_hidesInCallGroup() {
        // Given
        mActivity.showInCallFull();

        // When
        mActivity.showIncomingFull();

        // Then
        assertEquals(View.GONE, mBinding.groupInCall.getVisibility());
    }

    @Test
    public void showIncomingFull_hidesMiniWindows() {
        // Given
        mActivity.showIncomingMini();

        // When
        mActivity.showIncomingFull();

        // Then
        assertEquals(View.GONE, mMiniIncomingBinding.getRoot().getVisibility());
        assertEquals(View.GONE, mMiniCallBinding.getRoot().getVisibility());
    }

    @Test
    public void showIncomingFull_restoresBackground() {
        // Given
        mActivity.showIncomingMini();
        assertNull(mBinding.getRoot().getBackground());

        // When
        mActivity.showIncomingFull();

        // Then
        assertNotNull(mBinding.getRoot().getBackground());
    }

    // ==================== showInCallFull ====================

    @Test
    public void showInCallFull_fromIncomingFull_switchesToInCallFull() {
        // Given
        assertEquals(ThirdPartyCallActivity.DisplayMode.INCOMING_FULL,
                mActivity.getDisplayMode());

        // When
        mActivity.showInCallFull();

        // Then
        assertEquals(ThirdPartyCallActivity.DisplayMode.IN_CALL_FULL,
                mActivity.getDisplayMode());
    }

    @Test
    public void showInCallFull_showsInCallGroup() {
        // Given - initial incoming full

        // When
        mActivity.showInCallFull();

        // Then
        assertEquals(View.VISIBLE, mBinding.groupInCall.getVisibility());
    }

    @Test
    public void showInCallFull_hidesIncomingGroup() {
        // Given - initial incoming full

        // When
        mActivity.showInCallFull();

        // Then
        assertEquals(View.GONE, mBinding.groupIncoming.getVisibility());
    }

    @Test
    public void showInCallFull_hidesMiniWindows() {
        // Given
        mActivity.showInCallMini();

        // When
        mActivity.showInCallFull();

        // Then
        assertEquals(View.GONE, mMiniIncomingBinding.getRoot().getVisibility());
        assertEquals(View.GONE, mMiniCallBinding.getRoot().getVisibility());
    }

    @Test
    public void showInCallFull_restoresBackground() {
        // Given
        mActivity.showInCallMini();
        assertNull(mBinding.getRoot().getBackground());

        // When
        mActivity.showInCallFull();

        // Then
        assertNotNull(mBinding.getRoot().getBackground());
    }

    // ==================== showIncomingMini ====================

    @Test
    public void showIncomingMini_fromIncomingFull_switchesToIncomingMini() {
        // Given
        assertEquals(ThirdPartyCallActivity.DisplayMode.INCOMING_FULL,
                mActivity.getDisplayMode());

        // When
        mActivity.showIncomingMini();

        // Then
        assertEquals(ThirdPartyCallActivity.DisplayMode.INCOMING_MINI,
                mActivity.getDisplayMode());
    }

    @Test
    public void showIncomingMini_showsMiniIncoming() {
        // Given - initial incoming full

        // When
        mActivity.showIncomingMini();

        // Then
        assertEquals(View.VISIBLE, mMiniIncomingBinding.getRoot().getVisibility());
    }

    @Test
    public void showIncomingMini_hidesFullScreenGroups() {
        // Given - initial incoming full

        // When
        mActivity.showIncomingMini();

        // Then
        assertEquals(View.GONE, mBinding.groupIncoming.getVisibility());
        assertEquals(View.GONE, mBinding.groupInCall.getVisibility());
    }

    @Test
    public void showIncomingMini_hidesMiniCall() {
        // Given - initial incoming full

        // When
        mActivity.showIncomingMini();

        // Then
        assertEquals(View.GONE, mMiniCallBinding.getRoot().getVisibility());
    }

    @Test
    public void showIncomingMini_removesBackground() {
        // Given
        assertNotNull(mBinding.getRoot().getBackground());

        // When
        mActivity.showIncomingMini();

        // Then
        assertNull(mBinding.getRoot().getBackground());
    }

    // ==================== showInCallMini ====================

    @Test
    public void showInCallMini_fromInCallFull_switchesToInCallMini() {
        // Given
        mActivity.showInCallFull();

        // When
        mActivity.showInCallMini();

        // Then
        assertEquals(ThirdPartyCallActivity.DisplayMode.IN_CALL_MINI,
                mActivity.getDisplayMode());
    }

    @Test
    public void showInCallMini_showsMiniCall() {
        // Given
        mActivity.showInCallFull();

        // When
        mActivity.showInCallMini();

        // Then
        assertEquals(View.VISIBLE, mMiniCallBinding.getRoot().getVisibility());
    }

    @Test
    public void showInCallMini_hidesFullScreenGroups() {
        // Given
        mActivity.showInCallFull();

        // When
        mActivity.showInCallMini();

        // Then
        assertEquals(View.GONE, mBinding.groupIncoming.getVisibility());
        assertEquals(View.GONE, mBinding.groupInCall.getVisibility());
    }

    @Test
    public void showInCallMini_hidesMiniIncoming() {
        // Given
        mActivity.showInCallFull();

        // When
        mActivity.showInCallMini();

        // Then
        assertEquals(View.GONE, mMiniIncomingBinding.getRoot().getVisibility());
    }

    @Test
    public void showInCallMini_removesBackground() {
        // Given
        mActivity.showInCallFull();

        // When
        mActivity.showInCallMini();

        // Then
        assertNull(mBinding.getRoot().getBackground());
    }

    // ==================== onAnswerIncoming ====================

    @Test
    public void onAnswerIncoming_switchesToInCallFull() {
        // Given
        assertEquals(ThirdPartyCallActivity.DisplayMode.INCOMING_FULL,
                mActivity.getDisplayMode());

        // When
        mActivity.onAnswerIncoming();

        // Then
        assertEquals(ThirdPartyCallActivity.DisplayMode.IN_CALL_FULL,
                mActivity.getDisplayMode());
    }

    @Test
    public void onAnswerIncoming_hidesIncomingGroup() {
        // Given - incoming full visible

        // When
        mActivity.onAnswerIncoming();

        // Then
        assertEquals(View.GONE, mBinding.groupIncoming.getVisibility());
    }

    @Test
    public void onAnswerIncoming_showsInCallGroup() {
        // Given - incoming full visible

        // When
        mActivity.onAnswerIncoming();

        // Then
        assertEquals(View.VISIBLE, mBinding.groupInCall.getVisibility());
    }

    // ==================== hangUpIncoming ====================

    @Test
    public void hangUpIncoming_finishesActivity() {
        // Given - incoming full

        // When
        mActivity.hangUpIncoming();

        // Then
        assertTrue(mActivity.isFinishing());
    }

    // ==================== hangUpCurrent ====================

    @Test
    public void hangUpCurrent_finishesActivity() {
        // Given - incoming full

        // When
        mActivity.hangUpCurrent();

        // Then
        assertTrue(mActivity.isFinishing());
    }

    // ==================== hangUpActiveCall ====================

    @Test
    public void hangUpActiveCall_finishesActivity() {
        // Given
        mActivity.showInCallFull();

        // When
        mActivity.hangUpActiveCall();

        // Then
        assertTrue(mActivity.isFinishing());
    }

    // ==================== hangUpHeld ====================

    @Test
    public void hangUpHeld_finishesActivity() {
        // Given
        mActivity.showInCallFull();

        // When
        mActivity.hangUpHeld();

        // Then
        assertTrue(mActivity.isFinishing());
    }

    // ==================== toggleMute ====================

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
    public void toggleMute_updatesLabelToMute() {
        // Given
        mActivity.showInCallFull();
        assertFalse(mActivity.isMuted());

        // When
        mActivity.toggleMute();

        // Then
        assertEquals(mActivity.getString(R.string.tp_mute),
                mBinding.tvLabelMute.getText().toString());
    }

    @Test
    public void toggleMute_toggleBack_updatesLabelToUnmute() {
        // Given
        mActivity.showInCallFull();
        mActivity.toggleMute();

        // When
        mActivity.toggleMute();

        // Then
        assertEquals(mActivity.getString(R.string.tp_unmute),
                mBinding.tvLabelMute.getText().toString());
    }

    // ==================== switchCall ====================

    @Test
    public void switchCall_swapsHeldAndActiveNames() {
        // Given
        mActivity.showInCallFull();
        String originalHeldName = mBinding.tvHeldName.getText().toString();
        String originalActiveName = mBinding.tvActiveName.getText().toString();

        // When
        mActivity.switchCall();

        // Then
        assertEquals(originalActiveName, mBinding.tvHeldName.getText().toString());
        assertEquals(originalHeldName, mBinding.tvActiveName.getText().toString());
    }

    @Test
    public void switchCall_swapsMiniCallNames() {
        // Given
        mActivity.showInCallFull();
        String originalMiniRow1 = mMiniCallBinding.tvMiniCallRow1Name.getText().toString();
        String originalMiniRow2 = mMiniCallBinding.tvMiniCallRow2Name.getText().toString();

        // When
        mActivity.switchCall();

        // Then
        assertEquals(originalMiniRow2, mMiniCallBinding.tvMiniCallRow1Name.getText().toString());
        assertEquals(originalMiniRow1, mMiniCallBinding.tvMiniCallRow2Name.getText().toString());
    }

    @Test
    public void switchCall_setsHeldStatusToHold() {
        // Given
        mActivity.showInCallFull();

        // When
        mActivity.switchCall();

        // Then
        assertEquals(mActivity.getString(R.string.tp_call_hold),
                mBinding.tvHeldStatus.getText().toString());
    }

    @Test
    public void switchCall_calledTwice_restoresOriginal() {
        // Given
        mActivity.showInCallFull();
        String originalHeldName = mBinding.tvHeldName.getText().toString();
        String originalActiveName = mBinding.tvActiveName.getText().toString();

        // When
        mActivity.switchCall();
        mActivity.switchCall();

        // Then
        assertEquals(originalHeldName, mBinding.tvHeldName.getText().toString());
        assertEquals(originalActiveName, mBinding.tvActiveName.getText().toString());
    }

    // ==================== Button clicks: incoming full ====================

    @Test
    public void btnMinimizeIncoming_click_switchesToIncomingMini() {
        // Given
        assertEquals(ThirdPartyCallActivity.DisplayMode.INCOMING_FULL,
                mActivity.getDisplayMode());

        // When
        mBinding.btnMinimizeIncoming.performClick();

        // Then
        assertEquals(ThirdPartyCallActivity.DisplayMode.INCOMING_MINI,
                mActivity.getDisplayMode());
    }

    @Test
    public void btnAnswer_click_switchesToInCallFull() {
        // Given
        assertEquals(ThirdPartyCallActivity.DisplayMode.INCOMING_FULL,
                mActivity.getDisplayMode());

        // When
        mBinding.btnAnswer.performClick();

        // Then
        assertEquals(ThirdPartyCallActivity.DisplayMode.IN_CALL_FULL,
                mActivity.getDisplayMode());
    }

    @Test
    public void btnHangupIncoming_click_finishesActivity() {
        // Given - incoming full

        // When
        mBinding.btnHangupIncoming.performClick();

        // Then
        assertTrue(mActivity.isFinishing());
    }

    @Test
    public void btnHangUpCurrent_click_finishesActivity() {
        // Given - incoming full

        // When
        mBinding.btnHangUpCurrent.performClick();

        // Then
        assertTrue(mActivity.isFinishing());
    }

    // ==================== Button clicks: in-call full ====================

    @Test
    public void btnMinimizeCall_click_switchesToInCallMini() {
        // Given
        mActivity.showInCallFull();

        // When
        mBinding.btnMinimizeCall.performClick();

        // Then
        assertEquals(ThirdPartyCallActivity.DisplayMode.IN_CALL_MINI,
                mActivity.getDisplayMode());
    }

    @Test
    public void btnHangupCall_click_finishesActivity() {
        // Given
        mActivity.showInCallFull();

        // When
        mBinding.btnHangupCall.performClick();

        // Then
        assertTrue(mActivity.isFinishing());
    }

    @Test
    public void btnMuteCall_click_togglesMute() {
        // Given
        mActivity.showInCallFull();
        assertFalse(mActivity.isMuted());

        // When
        mBinding.btnMuteCall.performClick();

        // Then
        assertTrue(mActivity.isMuted());
    }

    @Test
    public void btnSwitchCall_click_swapsCalls() {
        // Given
        mActivity.showInCallFull();
        String originalHeld = mBinding.tvHeldName.getText().toString();
        String originalActive = mBinding.tvActiveName.getText().toString();

        // When
        mBinding.btnSwitchCall.performClick();

        // Then
        assertEquals(originalActive, mBinding.tvHeldName.getText().toString());
        assertEquals(originalHeld, mBinding.tvActiveName.getText().toString());
    }

    @Test
    public void btnHangUpHeld_click_finishesActivity() {
        // Given
        mActivity.showInCallFull();

        // When
        mBinding.btnHangUpHeld.performClick();

        // Then
        assertTrue(mActivity.isFinishing());
    }

    // ==================== Button clicks: mini incoming ====================

    @Test
    public void miniIncomingRoot_click_switchesToIncomingFull() {
        // Given
        mActivity.showIncomingMini();

        // When
        mMiniIncomingBinding.getRoot().performClick();

        // Then
        assertEquals(ThirdPartyCallActivity.DisplayMode.INCOMING_FULL,
                mActivity.getDisplayMode());
    }

    @Test
    public void btnMiniIncomingAnswer_click_switchesToInCallFull() {
        // Given
        mActivity.showIncomingMini();

        // When
        mMiniIncomingBinding.btnMiniIncomingAnswer.performClick();

        // Then
        assertEquals(ThirdPartyCallActivity.DisplayMode.IN_CALL_FULL,
                mActivity.getDisplayMode());
    }

    @Test
    public void btnMiniIncomingHangup_click_finishesActivity() {
        // Given
        mActivity.showIncomingMini();

        // When
        mMiniIncomingBinding.btnMiniIncomingHangup.performClick();

        // Then
        assertTrue(mActivity.isFinishing());
    }

    // ==================== Button clicks: mini call ====================

    @Test
    public void miniCallRoot_click_switchesToInCallFull() {
        // Given
        mActivity.showInCallMini();

        // When
        mMiniCallBinding.getRoot().performClick();

        // Then
        assertEquals(ThirdPartyCallActivity.DisplayMode.IN_CALL_FULL,
                mActivity.getDisplayMode());
    }

    @Test
    public void btnMiniCallHangup_click_finishesActivity() {
        // Given
        mActivity.showInCallMini();

        // When
        mMiniCallBinding.btnMiniCallHangup.performClick();

        // Then
        assertTrue(mActivity.isFinishing());
    }

    @Test
    public void btnMiniCallMute_click_togglesMute() {
        // Given
        mActivity.showInCallMini();
        assertFalse(mActivity.isMuted());

        // When
        mMiniCallBinding.btnMiniCallMute.performClick();

        // Then
        assertTrue(mActivity.isMuted());
    }

    @Test
    public void btnMiniCallCut_click_swapsCalls() {
        // Given
        mActivity.showInCallMini();
        String originalRow1 = mMiniCallBinding.tvMiniCallRow1Name.getText().toString();
        String originalRow2 = mMiniCallBinding.tvMiniCallRow2Name.getText().toString();

        // When
        mMiniCallBinding.btnMiniCallCut.performClick();

        // Then
        assertEquals(originalRow2, mMiniCallBinding.tvMiniCallRow1Name.getText().toString());
        assertEquals(originalRow1, mMiniCallBinding.tvMiniCallRow2Name.getText().toString());
    }

    // ==================== Getters ====================

    @Test
    public void getDisplayMode_initial_returnsIncomingFull() {
        // Given - activity created

        // When
        ThirdPartyCallActivity.DisplayMode mode = mActivity.getDisplayMode();

        // Then
        assertEquals(ThirdPartyCallActivity.DisplayMode.INCOMING_FULL, mode);
    }

    @Test
    public void getBinding_called_returnsNonNull() {
        // Given - activity created

        // When
        ActivityThirdPartyCallBinding binding = mActivity.getBinding();

        // Then
        assertNotNull(binding);
    }

    @Test
    public void getMiniIncomingBinding_called_returnsNonNull() {
        // Given - activity created

        // When
        LayoutThirdPartyIncomingMiniBinding binding = mActivity.getMiniIncomingBinding();

        // Then
        assertNotNull(binding);
    }

    @Test
    public void getMiniCallBinding_called_returnsNonNull() {
        // Given - activity created

        // When
        LayoutThirdPartyCallMiniBinding binding = mActivity.getMiniCallBinding();

        // Then
        assertNotNull(binding);
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

    // ==================== Multiple transitions ====================

    @Test
    public void multipleTransitions_incomingFullToMiniToFull_correctState() {
        // Given - starts incoming full

        // When
        mActivity.showIncomingMini();
        mActivity.showIncomingFull();

        // Then
        assertEquals(ThirdPartyCallActivity.DisplayMode.INCOMING_FULL,
                mActivity.getDisplayMode());
        assertEquals(View.VISIBLE, mBinding.groupIncoming.getVisibility());
        assertEquals(View.GONE, mMiniIncomingBinding.getRoot().getVisibility());
    }

    @Test
    public void multipleTransitions_answerThenMiniThenFull_correctState() {
        // Given - starts incoming full

        // When
        mActivity.onAnswerIncoming();
        mActivity.showInCallMini();
        mActivity.showInCallFull();

        // Then
        assertEquals(ThirdPartyCallActivity.DisplayMode.IN_CALL_FULL,
                mActivity.getDisplayMode());
        assertEquals(View.VISIBLE, mBinding.groupInCall.getVisibility());
        assertEquals(View.GONE, mMiniCallBinding.getRoot().getVisibility());
    }

    @Test
    public void multipleTransitions_allFourModes_correctFinalState() {
        // Given - starts incoming full

        // When
        mActivity.showIncomingMini();
        mActivity.showIncomingFull();
        mActivity.onAnswerIncoming();
        mActivity.showInCallMini();

        // Then
        assertEquals(ThirdPartyCallActivity.DisplayMode.IN_CALL_MINI,
                mActivity.getDisplayMode());
        assertEquals(View.GONE, mBinding.groupIncoming.getVisibility());
        assertEquals(View.GONE, mBinding.groupInCall.getVisibility());
        assertEquals(View.GONE, mMiniIncomingBinding.getRoot().getVisibility());
        assertEquals(View.VISIBLE, mMiniCallBinding.getRoot().getVisibility());
    }

    @Test
    public void showIncomingFull_calledTwice_staysIncomingFull() {
        // Given - already incoming full

        // When
        mActivity.showIncomingFull();
        mActivity.showIncomingFull();

        // Then
        assertEquals(ThirdPartyCallActivity.DisplayMode.INCOMING_FULL,
                mActivity.getDisplayMode());
        assertEquals(View.VISIBLE, mBinding.groupIncoming.getVisibility());
    }

    @Test
    public void showInCallFull_calledTwice_staysInCallFull() {
        // Given
        mActivity.showInCallFull();

        // When
        mActivity.showInCallFull();

        // Then
        assertEquals(ThirdPartyCallActivity.DisplayMode.IN_CALL_FULL,
                mActivity.getDisplayMode());
        assertEquals(View.VISIBLE, mBinding.groupInCall.getVisibility());
    }
}
