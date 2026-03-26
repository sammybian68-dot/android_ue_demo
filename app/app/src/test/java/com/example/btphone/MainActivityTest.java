package com.example.btphone;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * {@link MainActivity} 的单元测试。
 * <p>
 * 覆盖 Tab 切换逻辑、文字颜色更新、指示线位移和设备名设置。
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 30)
public class MainActivityTest {

    private MainActivity mActivity;
    private ActivityController<MainActivity> mController;

    @Before
    public void setUp() {
        mController = Robolectric.buildActivity(MainActivity.class)
                .create()
                .start()
                .resume();
        mActivity = mController.get();
    }

    // ==================== onCreate ====================

    @Test
    public void onCreate_activityCreated_isNotNull() {
        // Given / When — activity created in setUp

        // Then
        assertNotNull(mActivity);
    }

    @Test
    public void onCreate_bindingInflated_viewsNotNull() {
        // Given / When — activity created in setUp

        // Then
        assertNotNull(mActivity.getBinding());
        assertNotNull(mActivity.getBinding().ivBtIcon);
        assertNotNull(mActivity.getBinding().tvDeviceName);
        assertNotNull(mActivity.getBinding().tvTabCall);
        assertNotNull(mActivity.getBinding().tvTabContacts);
        assertNotNull(mActivity.getBinding().ivTabIndicator);
        assertNotNull(mActivity.getBinding().fragmentContainer);
    }

    @Test
    public void onCreate_defaultTab_isContacts() {
        // Given / When — activity created in setUp, default tab = CONTACTS

        // Then
        assertEquals(MainActivity.TAB_CONTACTS, mActivity.getCurrentTab());
    }

    // ==================== selectTab ====================

    @Test
    public void selectTab_call_updatesCurrentTab() {
        // Given
        int expectedTab = MainActivity.TAB_CALL;

        // When
        mActivity.selectTab(expectedTab);

        // Then
        assertEquals(expectedTab, mActivity.getCurrentTab());
    }

    @Test
    public void selectTab_contacts_updatesCurrentTab() {
        // Given
        mActivity.selectTab(MainActivity.TAB_CALL);

        // When
        mActivity.selectTab(MainActivity.TAB_CONTACTS);

        // Then
        assertEquals(MainActivity.TAB_CONTACTS, mActivity.getCurrentTab());
    }

    @Test
    public void selectTab_invalidTab_noChange() {
        // Given
        int initialTab = mActivity.getCurrentTab();

        // When
        mActivity.selectTab(-1);

        // Then
        assertEquals(initialTab, mActivity.getCurrentTab());
    }

    @Test
    public void selectTab_invalidTabHigh_noChange() {
        // Given
        int initialTab = mActivity.getCurrentTab();

        // When
        mActivity.selectTab(99);

        // Then
        assertEquals(initialTab, mActivity.getCurrentTab());
    }

    @Test
    public void selectTab_sameTab_noRedundantUpdate() {
        // Given — default is CONTACTS
        assertEquals(MainActivity.TAB_CONTACTS, mActivity.getCurrentTab());

        // When — select same tab again
        mActivity.selectTab(MainActivity.TAB_CONTACTS);

        // Then — tab remains the same
        assertEquals(MainActivity.TAB_CONTACTS, mActivity.getCurrentTab());
    }

    // ==================== Tab text color ====================

    @Test
    public void selectTab_call_callTabIsActive() {
        // Given
        int activeColor = mActivity.getColor(R.color.tab_active);

        // When
        mActivity.selectTab(MainActivity.TAB_CALL);

        // Then
        TextView tvCall = mActivity.getBinding().tvTabCall;
        assertEquals(activeColor, tvCall.getCurrentTextColor());
    }

    @Test
    public void selectTab_call_contactsTabIsInactive() {
        // Given
        int inactiveColor = mActivity.getColor(R.color.tab_inactive);

        // When
        mActivity.selectTab(MainActivity.TAB_CALL);

        // Then
        TextView tvContacts = mActivity.getBinding().tvTabContacts;
        assertEquals(inactiveColor, tvContacts.getCurrentTextColor());
    }

    @Test
    public void selectTab_contacts_contactsTabIsActive() {
        // Given
        mActivity.selectTab(MainActivity.TAB_CALL);
        int activeColor = mActivity.getColor(R.color.tab_active);

        // When
        mActivity.selectTab(MainActivity.TAB_CONTACTS);

        // Then
        TextView tvContacts = mActivity.getBinding().tvTabContacts;
        assertEquals(activeColor, tvContacts.getCurrentTextColor());
    }

    @Test
    public void selectTab_contacts_callTabIsInactive() {
        // Given
        mActivity.selectTab(MainActivity.TAB_CALL);
        int inactiveColor = mActivity.getColor(R.color.tab_inactive);

        // When
        mActivity.selectTab(MainActivity.TAB_CONTACTS);

        // Then
        TextView tvCall = mActivity.getBinding().tvTabCall;
        assertEquals(inactiveColor, tvCall.getCurrentTextColor());
    }

    // ==================== Tab indicator position ====================

    @Test
    public void selectTab_call_indicatorMovesToCallPosition() {
        // Given
        int expectedMargin = mActivity.getResources()
                .getDimensionPixelSize(R.dimen.tab_indicator_call_margin_start);

        // When
        mActivity.selectTab(MainActivity.TAB_CALL);

        // Then
        ImageView indicator = mActivity.getBinding().ivTabIndicator;
        ConstraintLayout.LayoutParams params =
                (ConstraintLayout.LayoutParams) indicator.getLayoutParams();
        assertEquals(expectedMargin, params.leftMargin);
    }

    @Test
    public void selectTab_contacts_indicatorMovesToContactsPosition() {
        // Given
        mActivity.selectTab(MainActivity.TAB_CALL);
        int expectedMargin = mActivity.getResources()
                .getDimensionPixelSize(R.dimen.tab_indicator_contacts_margin_start);

        // When
        mActivity.selectTab(MainActivity.TAB_CONTACTS);

        // Then
        ImageView indicator = mActivity.getBinding().ivTabIndicator;
        ConstraintLayout.LayoutParams params =
                (ConstraintLayout.LayoutParams) indicator.getLayoutParams();
        assertEquals(expectedMargin, params.leftMargin);
    }

    // ==================== Tab click listeners ====================

    @Test
    public void clickTabCall_switchesToCallTab() {
        // Given — default is contacts

        // When
        mActivity.getBinding().tvTabCall.performClick();

        // Then
        assertEquals(MainActivity.TAB_CALL, mActivity.getCurrentTab());
    }

    @Test
    public void clickTabContacts_switchesToContactsTab() {
        // Given
        mActivity.selectTab(MainActivity.TAB_CALL);

        // When
        mActivity.getBinding().tvTabContacts.performClick();

        // Then
        assertEquals(MainActivity.TAB_CONTACTS, mActivity.getCurrentTab());
    }

    // ==================== setDeviceName ====================

    @Test
    public void setDeviceName_validName_updatesTextView() {
        // Given
        String newName = "Galaxy S24";

        // When
        mActivity.setDeviceName(newName);

        // Then
        TextView tvDeviceName = mActivity.getBinding().tvDeviceName;
        assertEquals(newName, tvDeviceName.getText().toString());
    }

    @Test
    public void setDeviceName_emptyName_updatesTextView() {
        // Given
        String emptyName = "";

        // When
        mActivity.setDeviceName(emptyName);

        // Then
        TextView tvDeviceName = mActivity.getBinding().tvDeviceName;
        assertEquals(emptyName, tvDeviceName.getText().toString());
    }

    @Test
    public void setDeviceName_longName_updatesTextView() {
        // Given
        String longName = "ABCD1234ABCD1234ABCD1234";

        // When
        mActivity.setDeviceName(longName);

        // Then
        TextView tvDeviceName = mActivity.getBinding().tvDeviceName;
        assertEquals(longName, tvDeviceName.getText().toString());
    }

    // ==================== Default device name ====================

    @Test
    public void onCreate_defaultDeviceName_isIPhoneX() {
        // Given / When — created in setUp

        // Then
        String expected = mActivity.getString(R.string.default_device_name);
        TextView tvDeviceName = mActivity.getBinding().tvDeviceName;
        assertEquals(expected, tvDeviceName.getText().toString());
    }

    // ==================== Lifecycle ====================

    @Test
    public void onDestroy_noException() {
        // Given / When
        mController.pause().stop().destroy();

        // Then — no exception thrown
        assertNotNull(mActivity);
    }
}
