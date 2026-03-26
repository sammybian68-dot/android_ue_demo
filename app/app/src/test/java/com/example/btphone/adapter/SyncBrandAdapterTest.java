package com.example.btphone.adapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.view.View;
import android.widget.FrameLayout;

import androidx.test.core.app.ApplicationProvider;

import com.example.btphone.R;
import com.example.btphone.model.SyncBrand;

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
 * {@link SyncBrandAdapter} 单元测试。
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class SyncBrandAdapterTest {

    private SyncBrandAdapter mAdapter;
    private List<SyncBrand> mBrands;

    @Before
    public void setUp() {
        mBrands = Arrays.asList(
                new SyncBrand("华为", "华为详情"),
                new SyncBrand("苹果", "苹果详情"),
                new SyncBrand("其他", "其他详情")
        );
        mAdapter = new SyncBrandAdapter(mBrands);
    }

    @Test
    public void getItemCount_threeBrands_returnsThree() {
        // When / Then
        assertEquals(3, mAdapter.getItemCount());
    }

    @Test
    public void getItemCount_emptyList_returnsZero() {
        // Given
        SyncBrandAdapter emptyAdapter = new SyncBrandAdapter(Collections.emptyList());

        // When / Then
        assertEquals(0, emptyAdapter.getItemCount());
    }

    @Test
    public void getItem_validPosition_returnsCorrectBrand() {
        // When / Then
        assertEquals("华为", mAdapter.getItem(0).getBrandName());
        assertEquals("苹果", mAdapter.getItem(1).getBrandName());
        assertEquals("其他", mAdapter.getItem(2).getBrandName());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getItem_invalidPosition_throwsException() {
        // When
        mAdapter.getItem(5);
    }

    @Test
    public void getItem_detailText_returnsCorrectValue() {
        // When / Then
        assertEquals("华为详情", mAdapter.getItem(0).getDetailText());
        assertEquals("苹果详情", mAdapter.getItem(1).getDetailText());
        assertEquals("其他详情", mAdapter.getItem(2).getDetailText());
    }

    @Test
    public void setBrands_newList_updatesData() {
        // Given
        List<SyncBrand> newBrands = Arrays.asList(
                new SyncBrand("品牌A", "详情A")
        );

        // When
        mAdapter.setBrands(newBrands);

        // Then
        assertEquals(1, mAdapter.getItemCount());
        assertEquals("品牌A", mAdapter.getItem(0).getBrandName());
    }

    @Test
    public void setBrands_emptyList_clearsData() {
        // When
        mAdapter.setBrands(Collections.emptyList());

        // Then
        assertEquals(0, mAdapter.getItemCount());
    }

    @Test
    public void constructor_createsDefensiveCopy() {
        // Given
        List<SyncBrand> original = new ArrayList<>();
        original.add(new SyncBrand("品牌", "详情"));
        SyncBrandAdapter adapter = new SyncBrandAdapter(original);

        // When
        original.add(new SyncBrand("新品牌", "新详情"));

        // Then
        assertEquals(1, adapter.getItemCount());
    }

    @Test
    public void onCreateViewHolder_returnsNonNull() {
        // Given
        FrameLayout parent = new FrameLayout(ApplicationProvider.getApplicationContext());

        // When
        SyncBrandAdapter.ViewHolder vh = mAdapter.onCreateViewHolder(parent, 0);

        // Then
        assertNotNull(vh);
        assertNotNull(vh.itemView);
    }

    @Test
    public void onBindViewHolder_collapsed_detailHidden() {
        // Given
        FrameLayout parent = new FrameLayout(ApplicationProvider.getApplicationContext());
        SyncBrandAdapter.ViewHolder vh = mAdapter.onCreateViewHolder(parent, 0);

        // When
        mAdapter.onBindViewHolder(vh, 0);

        // Then
        View detailLayout = vh.itemView.findViewById(R.id.layout_detail);
        assertEquals(View.GONE, detailLayout.getVisibility());
    }

    @Test
    public void onBindViewHolder_expanded_detailVisible() {
        // Given
        mAdapter.getItem(0).setExpanded(true);
        FrameLayout parent = new FrameLayout(ApplicationProvider.getApplicationContext());
        SyncBrandAdapter.ViewHolder vh = mAdapter.onCreateViewHolder(parent, 0);

        // When
        mAdapter.onBindViewHolder(vh, 0);

        // Then
        View detailLayout = vh.itemView.findViewById(R.id.layout_detail);
        assertEquals(View.VISIBLE, detailLayout.getVisibility());
    }

    @Test
    public void onBindViewHolder_clickItem_togglesExpanded() {
        // Given
        FrameLayout parent = new FrameLayout(ApplicationProvider.getApplicationContext());
        SyncBrandAdapter.ViewHolder vh = mAdapter.onCreateViewHolder(parent, 0);
        mAdapter.onBindViewHolder(vh, 0);
        assertFalse(mAdapter.getItem(0).isExpanded());

        // When
        vh.itemView.performClick();

        // Then
        assertTrue(mAdapter.getItem(0).isExpanded());
    }

    @Test
    public void onBindViewHolder_clickExpandedItem_collapses() {
        // Given
        mAdapter.getItem(1).setExpanded(true);
        FrameLayout parent = new FrameLayout(ApplicationProvider.getApplicationContext());
        SyncBrandAdapter.ViewHolder vh = mAdapter.onCreateViewHolder(parent, 0);
        mAdapter.onBindViewHolder(vh, 1);

        // When
        vh.itemView.performClick();

        // Then
        assertFalse(mAdapter.getItem(1).isExpanded());
    }

    @Test
    public void onBindViewHolder_multipleItems_bindsAll() {
        // Given
        FrameLayout parent = new FrameLayout(ApplicationProvider.getApplicationContext());

        // When / Then
        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            SyncBrandAdapter.ViewHolder vh = mAdapter.onCreateViewHolder(parent, 0);
            mAdapter.onBindViewHolder(vh, i);
            assertNotNull(vh.itemView);
        }
    }

    @Test
    public void setBrands_defensiveCopy_originalUnaffected() {
        // Given
        List<SyncBrand> newBrands = new ArrayList<>();
        newBrands.add(new SyncBrand("品牌X", "详情X"));
        mAdapter.setBrands(newBrands);

        // When
        newBrands.clear();

        // Then
        assertEquals(1, mAdapter.getItemCount());
    }
}
