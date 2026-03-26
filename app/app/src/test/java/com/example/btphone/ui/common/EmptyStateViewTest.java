package com.example.btphone.ui.common;

import android.app.Activity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.btphone.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * {@link EmptyStateView} 可复用空状态控件的单元测试。
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class EmptyStateViewTest {

    private EmptyStateView mView;
    private Activity mActivity;

    @Before
    public void setUp() {
        mActivity = Robolectric.buildActivity(Activity.class).create().get();
        mView = new EmptyStateView(mActivity);
    }

    @Test
    public void constructor_oneArg_createsSuccessfully() {
        // Then
        assertNotNull(mView);
    }

    @Test
    public void constructor_twoArgs_createsSuccessfully() {
        // When
        EmptyStateView view = new EmptyStateView(mActivity, null);

        // Then
        assertNotNull(view);
    }

    @Test
    public void constructor_threeArgs_createsSuccessfully() {
        // When
        EmptyStateView view = new EmptyStateView(mActivity, null, 0);

        // Then
        assertNotNull(view);
    }

    @Test
    public void getIconView_returnsNonNull() {
        // Then
        assertNotNull(mView.getIconView());
    }

    @Test
    public void getTitleView_returnsNonNull() {
        // Then
        assertNotNull(mView.getTitleView());
    }

    @Test
    public void getHint1View_returnsNonNull() {
        // Then
        assertNotNull(mView.getHint1View());
    }

    @Test
    public void getHint2View_returnsNonNull() {
        // Then
        assertNotNull(mView.getHint2View());
    }

    @Test
    public void getLearnMoreView_returnsNonNull() {
        // Then
        assertNotNull(mView.getLearnMoreView());
    }

    @Test
    public void getSyncButton_returnsNonNull() {
        // Then
        assertNotNull(mView.getSyncButton());
    }

    @Test
    public void getSyncLabelView_returnsNonNull() {
        // Then
        assertNotNull(mView.getSyncLabelView());
    }

    @Test
    public void setTitle_withStringRes_updatesText() {
        // When
        mView.setTitle(R.string.empty_no_contacts);

        // Then
        TextView titleView = mView.getTitleView();
        assertEquals(mActivity.getString(R.string.empty_no_contacts),
                titleView.getText().toString());
    }

    @Test
    public void setTitle_withCharSequence_updatesText() {
        // Given
        String title = "自定义标题";

        // When
        mView.setTitle(title);

        // Then
        assertEquals(title, mView.getTitleView().getText().toString());
    }

    @Test
    public void setIcon_updatesIconDrawable() {
        // When
        mView.setIcon(R.drawable.icon_no_linkman);

        // Then
        ImageView iconView = mView.getIconView();
        assertNotNull(iconView.getDrawable());
    }

    @Test
    public void defaultTitle_showsNoCallRecords() {
        // Then
        String expected = mActivity.getString(R.string.empty_no_call_records);
        assertEquals(expected, mView.getTitleView().getText().toString());
    }

    @Test
    public void hint1_showsCorrectText() {
        // Then
        String expected = mActivity.getString(R.string.empty_hint_line1);
        assertEquals(expected, mView.getHint1View().getText().toString());
    }

    @Test
    public void hint2_showsCorrectText() {
        // Then
        String expected = mActivity.getString(R.string.empty_hint_line2);
        assertEquals(expected, mView.getHint2View().getText().toString());
    }

    @Test
    public void learnMore_showsCorrectText() {
        // Then
        String expected = mActivity.getString(R.string.empty_learn_more);
        assertEquals(expected, mView.getLearnMoreView().getText().toString());
    }

    @Test
    public void syncLabel_showsCorrectText() {
        // Then
        String expected = mActivity.getString(R.string.empty_sync_button);
        assertEquals(expected, mView.getSyncLabelView().getText().toString());
    }

    @Test
    public void syncButton_click_callsListener() {
        // Given
        EmptyStateView.OnSyncClickListener listener =
                mock(EmptyStateView.OnSyncClickListener.class);
        mView.setOnSyncClickListener(listener);

        // When
        mView.getSyncButton().performClick();

        // Then
        verify(listener).onSyncClick();
    }

    @Test
    public void syncButton_click_noListener_noException() {
        // Given
        mView.setOnSyncClickListener(null);

        // When / Then — no exception
        mView.getSyncButton().performClick();
    }

    @Test
    public void learnMore_click_callsListener() {
        // Given
        EmptyStateView.OnLearnMoreClickListener listener =
                mock(EmptyStateView.OnLearnMoreClickListener.class);
        mView.setOnLearnMoreClickListener(listener);

        // When
        mView.getLearnMoreView().performClick();

        // Then
        verify(listener).onLearnMoreClick();
    }

    @Test
    public void learnMore_click_noListener_noException() {
        // Given
        mView.setOnLearnMoreClickListener(null);

        // When / Then — no exception
        mView.getLearnMoreView().performClick();
    }

    @Test
    public void setOnSyncClickListener_replacesPrevious() {
        // Given
        EmptyStateView.OnSyncClickListener first =
                mock(EmptyStateView.OnSyncClickListener.class);
        EmptyStateView.OnSyncClickListener second =
                mock(EmptyStateView.OnSyncClickListener.class);
        mView.setOnSyncClickListener(first);

        // When
        mView.setOnSyncClickListener(second);
        mView.getSyncButton().performClick();

        // Then
        verify(first, never()).onSyncClick();
        verify(second).onSyncClick();
    }

    @Test
    public void setOnLearnMoreClickListener_replacesPrevious() {
        // Given
        EmptyStateView.OnLearnMoreClickListener first =
                mock(EmptyStateView.OnLearnMoreClickListener.class);
        EmptyStateView.OnLearnMoreClickListener second =
                mock(EmptyStateView.OnLearnMoreClickListener.class);
        mView.setOnLearnMoreClickListener(first);

        // When
        mView.setOnLearnMoreClickListener(second);
        mView.getLearnMoreView().performClick();

        // Then
        verify(first, never()).onLearnMoreClick();
        verify(second).onLearnMoreClick();
    }

    @Test
    public void defaultIcon_hasDrawable() {
        // Then
        assertNotNull(mView.getIconView().getDrawable());
    }
}
