package com.example.btphone.ui.common;

import android.app.Activity;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * {@link LoadingStateView} 可复用加载态控件的单元测试。
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class LoadingStateViewTest {

    private LoadingStateView mView;
    private Activity mActivity;

    @Before
    public void setUp() {
        mActivity = Robolectric.buildActivity(Activity.class).create().get();
        mView = new LoadingStateView(mActivity);
    }

    @Test
    public void constructor_oneArg_createsSuccessfully() {
        // Then
        assertNotNull(mView);
    }

    @Test
    public void constructor_twoArgs_createsSuccessfully() {
        // When
        LoadingStateView view = new LoadingStateView(mActivity, null);

        // Then
        assertNotNull(view);
    }

    @Test
    public void constructor_threeArgs_createsSuccessfully() {
        // When
        LoadingStateView view = new LoadingStateView(mActivity, null, 0);

        // Then
        assertNotNull(view);
    }

    @Test
    public void getLoadingIconView_returnsNonNull() {
        // Then
        assertNotNull(mView.getLoadingIconView());
    }

    @Test
    public void getLoadingTextView_returnsNonNull() {
        // Then
        assertNotNull(mView.getLoadingTextView());
    }

    @Test
    public void loadingText_showsCorrectString() {
        // Then
        String expected = mActivity.getString(R.string.loading_syncing);
        assertEquals(expected, mView.getLoadingTextView().getText().toString());
    }

    @Test
    public void loadingIcon_hasDrawable() {
        // Then
        ImageView icon = mView.getLoadingIconView();
        assertNotNull(icon.getDrawable());
    }

    @Test
    public void createRotateAnimation_returnsNonNull() {
        // When
        RotateAnimation anim = mView.createRotateAnimation();

        // Then
        assertNotNull(anim);
    }

    @Test
    public void createRotateAnimation_infiniteRepeat() {
        // When
        RotateAnimation anim = mView.createRotateAnimation();

        // Then
        assertEquals(Animation.INFINITE, anim.getRepeatCount());
    }

    @Test
    public void createRotateAnimation_duration1000() {
        // When
        RotateAnimation anim = mView.createRotateAnimation();

        // Then
        assertEquals(1000L, anim.getDuration());
    }

    @Test
    public void startLoading_setsAnimationOnIcon() {
        // When
        mView.startLoading();

        // Then
        Animation anim = mView.getLoadingIconView().getAnimation();
        assertNotNull(anim);
    }

    @Test
    public void stopLoading_clearsAnimation() {
        // Given
        mView.startLoading();
        assertNotNull(mView.getLoadingIconView().getAnimation());

        // When
        mView.stopLoading();

        // Then
        assertNull(mView.getLoadingIconView().getAnimation());
    }

    @Test
    public void startLoading_afterStop_restoresAnimation() {
        // Given
        mView.startLoading();
        mView.stopLoading();
        assertNull(mView.getLoadingIconView().getAnimation());

        // When
        mView.startLoading();

        // Then
        assertNotNull(mView.getLoadingIconView().getAnimation());
    }

    @Test
    public void loadingTextView_isInstanceOfTextView() {
        // Then
        assertTrue(mView.getLoadingTextView() instanceof TextView);
    }

    @Test
    public void loadingIconView_isInstanceOfImageView() {
        // Then
        assertTrue(mView.getLoadingIconView() instanceof ImageView);
    }
}
