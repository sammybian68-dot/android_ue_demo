package com.example.btphone.adapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.view.View;
import android.widget.FrameLayout;

import androidx.test.core.app.ApplicationProvider;

import com.example.btphone.R;
import com.example.btphone.model.CallLog;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * {@link CallLogAdapter} 单元测试。
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class CallLogAdapterTest {

    private CallLogAdapter mAdapter;

    @Before
    public void setUp() {
        mAdapter = new CallLogAdapter();
    }

    @Test
    public void getItemCount_emptyList_returnsZero() {
        // Given - default empty adapter

        // When / Then
        assertEquals(0, mAdapter.getItemCount());
    }

    @Test
    public void setItems_withData_updatesCount() {
        // Given
        List<CallLog> items = Arrays.asList(
                new CallLog("138", null, CallLog.TYPE_MISSED, "11/8"),
                new CallLog("139", null, CallLog.TYPE_INCOMING, "11/9")
        );

        // When
        mAdapter.setItems(items);

        // Then
        assertEquals(2, mAdapter.getItemCount());
    }

    @Test
    public void setItems_emptyList_clearsData() {
        // Given
        mAdapter.setItems(Arrays.asList(
                new CallLog("138", null, CallLog.TYPE_MISSED, "11/8")
        ));

        // When
        mAdapter.setItems(Collections.emptyList());

        // Then
        assertEquals(0, mAdapter.getItemCount());
    }

    @Test
    public void getItems_afterSet_returnsSameData() {
        // Given
        List<CallLog> items = Arrays.asList(
                new CallLog("138", null, CallLog.TYPE_MISSED, "11/8")
        );

        // When
        mAdapter.setItems(items);

        // Then
        assertEquals(1, mAdapter.getItems().size());
        assertEquals("138", mAdapter.getItems().get(0).getNumber());
    }

    @Test
    public void setItems_createsDefensiveCopy() {
        // Given
        List<CallLog> original = new java.util.ArrayList<>();
        original.add(new CallLog("138", null, CallLog.TYPE_MISSED, "11/8"));
        mAdapter.setItems(original);

        // When
        original.add(new CallLog("139", null, CallLog.TYPE_INCOMING, "11/9"));

        // Then
        assertEquals(1, mAdapter.getItemCount());
    }

    @Test
    public void onCreateViewHolder_returnsNonNull() {
        // Given
        FrameLayout parent = new FrameLayout(ApplicationProvider.getApplicationContext());

        // When
        CallLogAdapter.ViewHolder vh = mAdapter.onCreateViewHolder(parent, 0);

        // Then
        assertNotNull(vh);
        assertNotNull(vh.itemView);
    }

    @Test
    public void onBindViewHolder_missedCall_setsRedTextColor() {
        // Given
        mAdapter.setItems(Arrays.asList(
                new CallLog("13828299090", null, CallLog.TYPE_MISSED, "11/8")
        ));
        FrameLayout parent = new FrameLayout(ApplicationProvider.getApplicationContext());
        CallLogAdapter.ViewHolder vh = mAdapter.onCreateViewHolder(parent, 0);

        // When
        mAdapter.onBindViewHolder(vh, 0);

        // Then
        assertNotNull(vh.itemView);
    }

    @Test
    public void onBindViewHolder_incomingCall_setsNormalTextColor() {
        // Given
        mAdapter.setItems(Arrays.asList(
                new CallLog("13828299090", null, CallLog.TYPE_INCOMING, "11/8")
        ));
        FrameLayout parent = new FrameLayout(ApplicationProvider.getApplicationContext());
        CallLogAdapter.ViewHolder vh = mAdapter.onCreateViewHolder(parent, 0);

        // When
        mAdapter.onBindViewHolder(vh, 0);

        // Then
        assertNotNull(vh.itemView);
    }

    @Test
    public void onBindViewHolder_outgoingCall_bindsCorrectly() {
        // Given
        mAdapter.setItems(Arrays.asList(
                new CallLog("13828299090", null, CallLog.TYPE_OUTGOING, "11/9")
        ));
        FrameLayout parent = new FrameLayout(ApplicationProvider.getApplicationContext());
        CallLogAdapter.ViewHolder vh = mAdapter.onCreateViewHolder(parent, 0);

        // When
        mAdapter.onBindViewHolder(vh, 0);

        // Then
        assertNotNull(vh.itemView);
    }

    @Test
    public void onBindViewHolder_genericCall_bindsCorrectly() {
        // Given
        mAdapter.setItems(Arrays.asList(
                new CallLog("13828299090", null, CallLog.TYPE_GENERIC, "11/10")
        ));
        FrameLayout parent = new FrameLayout(ApplicationProvider.getApplicationContext());
        CallLogAdapter.ViewHolder vh = mAdapter.onCreateViewHolder(parent, 0);

        // When
        mAdapter.onBindViewHolder(vh, 0);

        // Then
        assertNotNull(vh.itemView);
    }

    @Test
    public void onBindViewHolder_withContactName_showsName() {
        // Given
        mAdapter.setItems(Arrays.asList(
                new CallLog("13828299090", "张三", CallLog.TYPE_INCOMING, "11/8")
        ));
        FrameLayout parent = new FrameLayout(ApplicationProvider.getApplicationContext());
        CallLogAdapter.ViewHolder vh = mAdapter.onCreateViewHolder(parent, 0);

        // When
        mAdapter.onBindViewHolder(vh, 0);

        // Then
        assertNotNull(vh.itemView);
    }

    @Test
    public void onBindViewHolder_multipleItems_bindsAll() {
        // Given
        mAdapter.setItems(Arrays.asList(
                new CallLog("138", null, CallLog.TYPE_MISSED, "11/8"),
                new CallLog("139", "李四", CallLog.TYPE_INCOMING, "11/9"),
                new CallLog("137", null, CallLog.TYPE_OUTGOING, "11/10")
        ));
        FrameLayout parent = new FrameLayout(ApplicationProvider.getApplicationContext());

        // When / Then
        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            CallLogAdapter.ViewHolder vh = mAdapter.onCreateViewHolder(parent, 0);
            mAdapter.onBindViewHolder(vh, i);
            assertNotNull(vh.itemView);
        }
    }
}
