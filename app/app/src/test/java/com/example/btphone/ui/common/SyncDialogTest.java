package com.example.btphone.ui.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btphone.R;
import com.example.btphone.model.SyncBrand;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

/**
 * {@link SyncDialog} 单元测试。
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class SyncDialogTest {

    private AppCompatActivity mActivity;
    private SyncDialog mDialog;

    @Before
    public void setUp() {
        mActivity = Robolectric.buildActivity(AppCompatActivity.class)
                .create().start().resume().get();
        mDialog = SyncDialog.newInstance();
        FragmentManager fm = mActivity.getSupportFragmentManager();
        mDialog.show(fm, SyncDialog.TAG);
        fm.executePendingTransactions();
    }

    @Test
    public void newInstance_returnsNonNull() {
        // When
        SyncDialog dialog = SyncDialog.newInstance();

        // Then
        assertNotNull(dialog);
    }

    @Test
    public void tag_isExpectedValue() {
        // When / Then
        assertEquals("SyncDialog", SyncDialog.TAG);
    }

    @Test
    public void show_dialogIsShowing() {
        // Then
        assertNotNull(mDialog.getDialog());
        assertTrue(mDialog.getDialog().isShowing());
    }

    @Test
    public void buildDefaultBrands_returnsThreeBrands() {
        // When
        List<SyncBrand> brands = mDialog.buildDefaultBrands();

        // Then
        assertEquals(3, brands.size());
    }

    @Test
    public void buildDefaultBrands_firstIsHuawei() {
        // When
        List<SyncBrand> brands = mDialog.buildDefaultBrands();

        // Then
        assertEquals("华为", brands.get(0).getBrandName());
    }

    @Test
    public void buildDefaultBrands_secondIsApple() {
        // When
        List<SyncBrand> brands = mDialog.buildDefaultBrands();

        // Then
        assertEquals("苹果", brands.get(1).getBrandName());
    }

    @Test
    public void buildDefaultBrands_thirdIsOther() {
        // When
        List<SyncBrand> brands = mDialog.buildDefaultBrands();

        // Then
        assertEquals("其他", brands.get(2).getBrandName());
    }

    @Test
    public void buildDefaultBrands_allCollapsedByDefault() {
        // When
        List<SyncBrand> brands = mDialog.buildDefaultBrands();

        // Then
        for (SyncBrand brand : brands) {
            assertFalse(brand.isExpanded());
        }
    }

    @Test
    public void buildDefaultBrands_allHaveDetailText() {
        // When
        List<SyncBrand> brands = mDialog.buildDefaultBrands();

        // Then
        for (SyncBrand brand : brands) {
            assertFalse(brand.getDetailText().isEmpty());
        }
    }

    @Test
    public void onViewCreated_adapterIsSet() {
        // Then
        assertNotNull(mDialog.getAdapter());
    }

    @Test
    public void onViewCreated_adapterHasThreeItems() {
        // Then
        assertEquals(3, mDialog.getAdapter().getItemCount());
    }

    @Test
    public void onViewCreated_recyclerViewHasLayoutManager() {
        // Given
        RecyclerView rv = mDialog.getView().findViewById(R.id.rv_brands);

        // Then
        assertNotNull(rv.getLayoutManager());
    }

    @Test
    public void onViewCreated_recyclerViewHasAdapter() {
        // Given
        RecyclerView rv = mDialog.getView().findViewById(R.id.rv_brands);

        // Then
        assertNotNull(rv.getAdapter());
    }

    @Test
    public void onViewCreated_disclaimerContainsExpectedText() {
        // Given
        TextView tvDisclaimer = mDialog.getView().findViewById(R.id.tv_disclaimer);

        // Then
        String text = tvDisclaimer.getText().toString();
        assertTrue(text.contains("若按上述路径"));
        assertTrue(text.contains("配对蓝牙设备"));
    }

    @Test
    public void onViewCreated_titleIsSet() {
        // Given
        TextView tvTitle = mDialog.getView().findViewById(R.id.tv_title);

        // Then
        assertEquals("同步操作", tvTitle.getText().toString());
    }

    @Test
    public void dismiss_dialogDisappears() {
        // Given
        assertTrue(mDialog.getDialog().isShowing());

        // When
        mDialog.dismiss();
        mActivity.getSupportFragmentManager().executePendingTransactions();

        // Then
        assertNull(mDialog.getDialog());
    }
}
