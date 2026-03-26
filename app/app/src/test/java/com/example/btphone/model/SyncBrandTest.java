package com.example.btphone.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * {@link SyncBrand} 模型类的单元测试。
 */
public class SyncBrandTest {

    private SyncBrand mBrand;

    @Before
    public void setUp() {
        mBrand = new SyncBrand("华为", "华为详细说明");
    }

    @Test
    public void constructor_validInput_fieldsSetCorrectly() {
        // Given / When
        SyncBrand brand = new SyncBrand("苹果", "苹果详细说明");

        // Then
        assertEquals("苹果", brand.getBrandName());
        assertEquals("苹果详细说明", brand.getDetailText());
        assertFalse(brand.isExpanded());
    }

    @Test
    public void getBrandName_returnsCorrectValue() {
        // When / Then
        assertEquals("华为", mBrand.getBrandName());
    }

    @Test
    public void getDetailText_returnsCorrectValue() {
        // When / Then
        assertEquals("华为详细说明", mBrand.getDetailText());
    }

    @Test
    public void isExpanded_defaultFalse() {
        // When / Then
        assertFalse(mBrand.isExpanded());
    }

    @Test
    public void setExpanded_true_becomesExpanded() {
        // When
        mBrand.setExpanded(true);

        // Then
        assertTrue(mBrand.isExpanded());
    }

    @Test
    public void setExpanded_false_becomesCollapsed() {
        // Given
        mBrand.setExpanded(true);

        // When
        mBrand.setExpanded(false);

        // Then
        assertFalse(mBrand.isExpanded());
    }

    @Test
    public void toggleExpanded_fromCollapsed_becomesExpanded() {
        // Given
        assertFalse(mBrand.isExpanded());

        // When
        mBrand.toggleExpanded();

        // Then
        assertTrue(mBrand.isExpanded());
    }

    @Test
    public void toggleExpanded_fromExpanded_becomesCollapsed() {
        // Given
        mBrand.setExpanded(true);

        // When
        mBrand.toggleExpanded();

        // Then
        assertFalse(mBrand.isExpanded());
    }

    @Test
    public void toggleExpanded_twice_returnsToOriginal() {
        // Given
        boolean original = mBrand.isExpanded();

        // When
        mBrand.toggleExpanded();
        mBrand.toggleExpanded();

        // Then
        assertEquals(original, mBrand.isExpanded());
    }

    @Test
    public void constructor_emptyStrings_acceptedGracefully() {
        // Given / When
        SyncBrand brand = new SyncBrand("", "");

        // Then
        assertEquals("", brand.getBrandName());
        assertEquals("", brand.getDetailText());
    }
}
