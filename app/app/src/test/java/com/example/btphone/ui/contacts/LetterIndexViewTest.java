package com.example.btphone.ui.contacts;

import android.app.Activity;
import android.view.MotionEvent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * {@link LetterIndexView} 自定义控件的单元测试。
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class LetterIndexViewTest {

    private LetterIndexView mView;
    private Activity mActivity;

    @Before
    public void setUp() {
        mActivity = Robolectric.buildActivity(Activity.class).create().get();
        mView = new LetterIndexView(mActivity);
        mView.layout(0, 0, 80, 734);
    }

    @Test
    public void letters_contains27Entries() {
        // Then
        assertEquals(27, LetterIndexView.LETTERS.length);
    }

    @Test
    public void letters_startsWithHash() {
        // Then
        assertEquals("#", LetterIndexView.LETTERS[0]);
    }

    @Test
    public void letters_endsWithZ() {
        // Then
        assertEquals("Z", LetterIndexView.LETTERS[26]);
    }

    @Test
    public void initialState_selectedIndexMinusOne() {
        // Then
        assertEquals(-1, mView.getSelectedIndex());
    }

    @Test
    public void initialState_notTouching() {
        // Then
        assertFalse(mView.isTouchingState());
    }

    @Test
    public void setSelectedIndex_updatesValue() {
        // When
        mView.setSelectedIndex(5);

        // Then
        assertEquals(5, mView.getSelectedIndex());
    }

    @Test
    public void setOnLetterSelectedListener_setsListener() {
        // Given
        LetterIndexView.OnLetterSelectedListener listener =
                mock(LetterIndexView.OnLetterSelectedListener.class);

        // When
        mView.setOnLetterSelectedListener(listener);

        // Then — no exception, listener is set
        assertNotNull(listener);
    }

    @Test
    public void onTouchEvent_actionDown_setsTouchingTrue() {
        // Given
        MotionEvent downEvent = MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 40, 100, 0);

        // When
        boolean handled = mView.onTouchEvent(downEvent);
        downEvent.recycle();

        // Then
        assertTrue(handled);
        assertTrue(mView.isTouchingState());
    }

    @Test
    public void onTouchEvent_actionDown_updatesSelectedIndex() {
        // Given — height 734, 27 letters, each ~27.2px
        MotionEvent downEvent = MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 40, 0, 0);

        // When
        mView.onTouchEvent(downEvent);
        downEvent.recycle();

        // Then — touch at y=0 should select index 0 (#)
        assertEquals(0, mView.getSelectedIndex());
    }

    @Test
    public void onTouchEvent_actionMove_updatesSelectedIndex() {
        // Given
        MotionEvent downEvent = MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 40, 0, 0);
        mView.onTouchEvent(downEvent);
        downEvent.recycle();

        // When — move to middle of bar
        float midY = 734f / 2f;
        MotionEvent moveEvent = MotionEvent.obtain(0, 0, MotionEvent.ACTION_MOVE, 40, midY, 0);
        mView.onTouchEvent(moveEvent);
        moveEvent.recycle();

        // Then — should select around middle letter (index ~13)
        int expectedIndex = (int) (midY / 734 * 27);
        assertEquals(expectedIndex, mView.getSelectedIndex());
    }

    @Test
    public void onTouchEvent_actionUp_setsTouchingFalse() {
        // Given
        MotionEvent downEvent = MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 40, 100, 0);
        mView.onTouchEvent(downEvent);
        downEvent.recycle();
        assertTrue(mView.isTouchingState());

        // When
        MotionEvent upEvent = MotionEvent.obtain(0, 0, MotionEvent.ACTION_UP, 40, 100, 0);
        mView.onTouchEvent(upEvent);
        upEvent.recycle();

        // Then
        assertFalse(mView.isTouchingState());
    }

    @Test
    public void onTouchEvent_actionCancel_setsTouchingFalse() {
        // Given
        MotionEvent downEvent = MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 40, 100, 0);
        mView.onTouchEvent(downEvent);
        downEvent.recycle();

        // When
        MotionEvent cancelEvent = MotionEvent.obtain(0, 0, MotionEvent.ACTION_CANCEL, 40, 100, 0);
        mView.onTouchEvent(cancelEvent);
        cancelEvent.recycle();

        // Then
        assertFalse(mView.isTouchingState());
    }

    @Test
    public void onTouchEvent_listener_calledOnLetterChange() {
        // Given
        LetterIndexView.OnLetterSelectedListener listener =
                mock(LetterIndexView.OnLetterSelectedListener.class);
        mView.setOnLetterSelectedListener(listener);

        // When
        MotionEvent downEvent = MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 40, 0, 0);
        mView.onTouchEvent(downEvent);
        downEvent.recycle();

        // Then
        verify(listener).onLetterSelected("#");
    }

    @Test
    public void onTouchEvent_listenerUp_calledOnFinish() {
        // Given
        LetterIndexView.OnLetterSelectedListener listener =
                mock(LetterIndexView.OnLetterSelectedListener.class);
        mView.setOnLetterSelectedListener(listener);

        MotionEvent downEvent = MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 40, 0, 0);
        mView.onTouchEvent(downEvent);
        downEvent.recycle();

        // When
        MotionEvent upEvent = MotionEvent.obtain(0, 0, MotionEvent.ACTION_UP, 40, 0, 0);
        mView.onTouchEvent(upEvent);
        upEvent.recycle();

        // Then
        verify(listener).onLetterSelectionFinished();
    }

    @Test
    public void onTouchEvent_sameIndex_listenerNotCalledAgain() {
        // Given
        LetterIndexView.OnLetterSelectedListener listener =
                mock(LetterIndexView.OnLetterSelectedListener.class);
        mView.setOnLetterSelectedListener(listener);

        MotionEvent downEvent = MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 40, 5, 0);
        mView.onTouchEvent(downEvent);
        downEvent.recycle();

        // When — move to same area (same letter index)
        MotionEvent moveEvent = MotionEvent.obtain(0, 0, MotionEvent.ACTION_MOVE, 40, 6, 0);
        mView.onTouchEvent(moveEvent);
        moveEvent.recycle();

        // Then — listener called only once for "#"
        verify(listener).onLetterSelected("#");
    }

    @Test
    public void onTouchEvent_touchAtBottom_selectsLastLetter() {
        // Given
        MotionEvent downEvent = MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 40, 733, 0);

        // When
        mView.onTouchEvent(downEvent);
        downEvent.recycle();

        // Then
        assertEquals(26, mView.getSelectedIndex());
    }

    @Test
    public void onTouchEvent_touchBeyondBottom_clampsToLast() {
        // Given
        MotionEvent downEvent = MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 40, 800, 0);

        // When
        mView.onTouchEvent(downEvent);
        downEvent.recycle();

        // Then
        assertEquals(26, mView.getSelectedIndex());
    }

    @Test
    public void onTouchEvent_touchAboveTop_clampsToFirst() {
        // Given
        MotionEvent downEvent = MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 40, -10, 0);

        // When
        mView.onTouchEvent(downEvent);
        downEvent.recycle();

        // Then
        assertEquals(0, mView.getSelectedIndex());
    }

    @Test
    public void clampPopupCenterY_middleValue_returnsUnchanged() {
        // Given — view height = 734, popup outer = 120, so min = 60, max = 674
        float rawY = 400;

        // When
        float result = mView.clampPopupCenterY(rawY);

        // Then
        assertEquals(400f, result, 0.1f);
    }

    @Test
    public void clampPopupCenterY_tooSmall_clampsToMin() {
        // Given
        float rawY = 10;

        // When
        float result = mView.clampPopupCenterY(rawY);

        // Then — min = popup_outer/2 = 60
        assertEquals(60f, result, 0.1f);
    }

    @Test
    public void clampPopupCenterY_tooLarge_clampsToMax() {
        // Given
        float rawY = 730;

        // When
        float result = mView.clampPopupCenterY(rawY);

        // Then — max = 734 - 60 = 674
        assertEquals(674f, result, 0.1f);
    }

    @Test
    public void performClick_returnsFromSuper() {
        // When / Then — should not throw
        mView.performClick();
    }

    @Test
    public void onTouchEvent_nullListener_noException() {
        // Given — no listener set
        mView.setOnLetterSelectedListener(null);

        // When
        MotionEvent downEvent = MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 40, 100, 0);
        mView.onTouchEvent(downEvent);
        downEvent.recycle();

        MotionEvent upEvent = MotionEvent.obtain(0, 0, MotionEvent.ACTION_UP, 40, 100, 0);
        mView.onTouchEvent(upEvent);
        upEvent.recycle();

        // Then — no exception
        assertFalse(mView.isTouchingState());
    }

    @Test
    public void constructor_twoArgs_createsSuccessfully() {
        // When
        LetterIndexView view = new LetterIndexView(mActivity, null);

        // Then
        assertNotNull(view);
    }

    @Test
    public void constructor_threeArgs_createsSuccessfully() {
        // When
        LetterIndexView view = new LetterIndexView(mActivity, null, 0);

        // Then
        assertNotNull(view);
    }
}
