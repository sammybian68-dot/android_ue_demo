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
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * {@link BluetoothDisconnectedView} 蓝牙未连接小卡控件的单元测试。
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class BluetoothDisconnectedViewTest {

    private BluetoothDisconnectedView mView;
    private Activity mActivity;

    @Before
    public void setUp() {
        mActivity = Robolectric.buildActivity(Activity.class).create().get();
        mView = new BluetoothDisconnectedView(mActivity);
    }

    @Test
    public void constructor_oneArg_createsSuccessfully() {
        // Then
        assertNotNull(mView);
    }

    @Test
    public void constructor_twoArgs_createsSuccessfully() {
        // When
        BluetoothDisconnectedView view = new BluetoothDisconnectedView(mActivity, null);

        // Then
        assertNotNull(view);
    }

    @Test
    public void constructor_threeArgs_createsSuccessfully() {
        // When
        BluetoothDisconnectedView view = new BluetoothDisconnectedView(mActivity, null, 0);

        // Then
        assertNotNull(view);
    }

    @Test
    public void getAddIconView_returnsNonNull() {
        // Then
        assertNotNull(mView.getAddIconView());
    }

    @Test
    public void getAddDeviceTextView_returnsNonNull() {
        // Then
        assertNotNull(mView.getAddDeviceTextView());
    }

    @Test
    public void getDeviceIconView_returnsNonNull() {
        // Then
        assertNotNull(mView.getDeviceIconView());
    }

    @Test
    public void addDeviceText_showsCorrectString() {
        // Then
        String expected = mActivity.getString(R.string.bt_disconnected_add_device);
        assertEquals(expected, mView.getAddDeviceTextView().getText().toString());
    }

    @Test
    public void addIcon_hasDrawable() {
        // Then
        ImageView icon = mView.getAddIconView();
        assertNotNull(icon.getDrawable());
    }

    @Test
    public void deviceIcon_hasDrawable() {
        // Then
        ImageView icon = mView.getDeviceIconView();
        assertNotNull(icon.getDrawable());
    }

    @Test
    public void cardClick_callsListener() {
        // Given
        BluetoothDisconnectedView.OnAddDeviceClickListener listener =
                mock(BluetoothDisconnectedView.OnAddDeviceClickListener.class);
        mView.setOnAddDeviceClickListener(listener);

        // When
        mView.performClick();

        // Then
        verify(listener).onAddDeviceClick();
    }

    @Test
    public void cardClick_noListener_noException() {
        // Given
        mView.setOnAddDeviceClickListener(null);

        // When / Then — no exception
        mView.performClick();
    }

    @Test
    public void setOnAddDeviceClickListener_replacesPrevious() {
        // Given
        BluetoothDisconnectedView.OnAddDeviceClickListener first =
                mock(BluetoothDisconnectedView.OnAddDeviceClickListener.class);
        BluetoothDisconnectedView.OnAddDeviceClickListener second =
                mock(BluetoothDisconnectedView.OnAddDeviceClickListener.class);
        mView.setOnAddDeviceClickListener(first);

        // When
        mView.setOnAddDeviceClickListener(second);
        mView.performClick();

        // Then
        verify(first, never()).onAddDeviceClick();
        verify(second).onAddDeviceClick();
    }

    @Test
    public void addIconView_isInstanceOfImageView() {
        // Then
        assertTrue(mView.getAddIconView() instanceof ImageView);
    }

    @Test
    public void addDeviceTextView_isInstanceOfTextView() {
        // Then
        assertTrue(mView.getAddDeviceTextView() instanceof TextView);
    }

    @Test
    public void deviceIconView_isInstanceOfImageView() {
        // Then
        assertTrue(mView.getDeviceIconView() instanceof ImageView);
    }
}
