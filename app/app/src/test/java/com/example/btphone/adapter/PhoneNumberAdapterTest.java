package com.example.btphone.adapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import android.widget.FrameLayout;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * {@link PhoneNumberAdapter} 单元测试。
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class PhoneNumberAdapterTest {

    private PhoneNumberAdapter mAdapter;
    private List<String> mPhoneNumbers;

    @Before
    public void setUp() {
        mPhoneNumbers = Arrays.asList("13800138000", "13900139000", "15000150000");
        mAdapter = new PhoneNumberAdapter(mPhoneNumbers);
    }

    @Test
    public void constructor_storesCopy_originalChangesDoNotAffect() {
        // Given
        List<String> original = new ArrayList<>();
        original.add("111");
        PhoneNumberAdapter adapter = new PhoneNumberAdapter(original);

        // When
        original.add("222");

        // Then
        assertEquals(1, adapter.getItemCount());
    }

    @Test
    public void getItemCount_threeNumbers_returnsThree() {
        // When / Then
        assertEquals(3, mAdapter.getItemCount());
    }

    @Test
    public void getItemCount_emptyList_returnsZero() {
        // Given
        PhoneNumberAdapter emptyAdapter = new PhoneNumberAdapter(Collections.emptyList());

        // When / Then
        assertEquals(0, emptyAdapter.getItemCount());
    }

    @Test
    public void getItem_validPositions_returnsCorrectNumber() {
        // When / Then
        assertEquals("13800138000", mAdapter.getItem(0));
        assertEquals("13900139000", mAdapter.getItem(1));
        assertEquals("15000150000", mAdapter.getItem(2));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getItem_invalidPosition_throwsException() {
        // When
        mAdapter.getItem(5);
    }

    @Test
    public void getPhoneNumbers_returnsUnmodifiableList() {
        // When
        List<String> result = mAdapter.getPhoneNumbers();

        // Then
        assertEquals(3, result.size());
        assertEquals("13800138000", result.get(0));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getPhoneNumbers_returnedListIsUnmodifiable() {
        // When
        mAdapter.getPhoneNumbers().add("newNumber");
    }

    @Test
    public void setPhoneNumbers_updatesData() {
        // Given
        List<String> newNumbers = Arrays.asList("10086", "10010");

        // When
        mAdapter.setPhoneNumbers(newNumbers);

        // Then
        assertEquals(2, mAdapter.getItemCount());
        assertEquals("10086", mAdapter.getItem(0));
        assertEquals("10010", mAdapter.getItem(1));
    }

    @Test
    public void setPhoneNumbers_emptyList_clearsData() {
        // When
        mAdapter.setPhoneNumbers(Collections.emptyList());

        // Then
        assertEquals(0, mAdapter.getItemCount());
    }

    @Test
    public void setPhoneNumbers_defensiveCopy() {
        // Given
        List<String> newNumbers = new ArrayList<>();
        newNumbers.add("10086");
        mAdapter.setPhoneNumbers(newNumbers);

        // When
        newNumbers.clear();

        // Then
        assertEquals(1, mAdapter.getItemCount());
    }

    @Test
    public void setOnPhoneNumberClickListener_setsListener() {
        // Given
        PhoneNumberAdapter.OnPhoneNumberClickListener listener =
                mock(PhoneNumberAdapter.OnPhoneNumberClickListener.class);

        // When
        mAdapter.setOnPhoneNumberClickListener(listener);

        // Then
        assertEquals(listener, mAdapter.getListener());
    }

    @Test
    public void setOnPhoneNumberClickListener_null_clearsListener() {
        // Given
        mAdapter.setOnPhoneNumberClickListener(
                mock(PhoneNumberAdapter.OnPhoneNumberClickListener.class));

        // When
        mAdapter.setOnPhoneNumberClickListener(null);

        // Then
        assertNull(mAdapter.getListener());
    }

    @Test
    public void getListener_default_returnsNull() {
        // When / Then
        assertNull(mAdapter.getListener());
    }

    @Test
    public void onCreateViewHolder_returnsNonNull() {
        // Given
        FrameLayout parent = new FrameLayout(ApplicationProvider.getApplicationContext());

        // When
        PhoneNumberAdapter.ViewHolder vh = mAdapter.onCreateViewHolder(parent, 0);

        // Then
        assertNotNull(vh);
        assertNotNull(vh.itemView);
    }

    @Test
    public void onBindViewHolder_setsPhoneNumber() {
        // Given
        FrameLayout parent = new FrameLayout(ApplicationProvider.getApplicationContext());
        PhoneNumberAdapter.ViewHolder vh = mAdapter.onCreateViewHolder(parent, 0);

        // When
        mAdapter.onBindViewHolder(vh, 0);

        // Then
        assertNotNull(vh.itemView);
    }

    @Test
    public void onBindViewHolder_clickItem_triggersListener() {
        // Given
        PhoneNumberAdapter.OnPhoneNumberClickListener listener =
                mock(PhoneNumberAdapter.OnPhoneNumberClickListener.class);
        mAdapter.setOnPhoneNumberClickListener(listener);
        FrameLayout parent = new FrameLayout(ApplicationProvider.getApplicationContext());
        PhoneNumberAdapter.ViewHolder vh = mAdapter.onCreateViewHolder(parent, 0);
        mAdapter.onBindViewHolder(vh, 1);

        // When
        vh.itemView.performClick();

        // Then
        verify(listener).onPhoneNumberClick("13900139000", 1);
    }

    @Test
    public void onBindViewHolder_clickItem_noListener_noException() {
        // Given
        FrameLayout parent = new FrameLayout(ApplicationProvider.getApplicationContext());
        PhoneNumberAdapter.ViewHolder vh = mAdapter.onCreateViewHolder(parent, 0);
        mAdapter.onBindViewHolder(vh, 0);

        // When / Then — no crash
        vh.itemView.performClick();
    }

    @Test
    public void onBindViewHolder_allItems_bindsWithoutError() {
        // Given
        FrameLayout parent = new FrameLayout(ApplicationProvider.getApplicationContext());

        // When / Then
        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            PhoneNumberAdapter.ViewHolder vh = mAdapter.onCreateViewHolder(parent, 0);
            mAdapter.onBindViewHolder(vh, i);
            assertNotNull(vh.itemView);
        }
    }

    @Test
    public void onBindViewHolder_singleNumber_bindsCorrectly() {
        // Given
        PhoneNumberAdapter singleAdapter = new PhoneNumberAdapter(
                Collections.singletonList("10086"));
        FrameLayout parent = new FrameLayout(ApplicationProvider.getApplicationContext());
        PhoneNumberAdapter.ViewHolder vh = singleAdapter.onCreateViewHolder(parent, 0);

        // When
        singleAdapter.onBindViewHolder(vh, 0);

        // Then
        assertEquals(1, singleAdapter.getItemCount());
        assertEquals("10086", singleAdapter.getItem(0));
    }

    @Test
    public void onBindViewHolder_extremeLengthNumber_doesNotCrash() {
        // Given
        String longNumber = "138282990901234567890123456789";
        PhoneNumberAdapter longAdapter = new PhoneNumberAdapter(
                Collections.singletonList(longNumber));
        FrameLayout parent = new FrameLayout(ApplicationProvider.getApplicationContext());
        PhoneNumberAdapter.ViewHolder vh = longAdapter.onCreateViewHolder(parent, 0);

        // When
        longAdapter.onBindViewHolder(vh, 0);

        // Then
        assertEquals(longNumber, longAdapter.getItem(0));
    }
}
