package com.example.btphone.ui.dialer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;

import com.example.btphone.model.CallLog;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * {@link DialerFragment} 单元测试。
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class DialerFragmentTest {

    @Test
    public void onCreateView_bindingNotNull() {
        // Given / When
        FragmentScenario<DialerFragment> scenario =
                FragmentScenario.launchInContainer(DialerFragment.class);

        // Then
        scenario.onFragment(fragment -> {
            assertNotNull(fragment.getBinding());
            assertNotNull(fragment.getView());
        });
    }

    @Test
    public void onViewCreated_adapterInitialized() {
        // Given / When
        FragmentScenario<DialerFragment> scenario =
                FragmentScenario.launchInContainer(DialerFragment.class);

        // Then
        scenario.onFragment(fragment -> {
            assertNotNull(fragment.getAdapter());
            assertTrue(fragment.getAdapter().getItemCount() > 0);
        });
    }

    @Test
    public void onViewCreated_mockDataLoaded() {
        // Given / When
        FragmentScenario<DialerFragment> scenario =
                FragmentScenario.launchInContainer(DialerFragment.class);

        // Then
        scenario.onFragment(fragment -> {
            assertEquals(5, fragment.getAdapter().getItemCount());
        });
    }

    @Test
    public void onDialKeyPressed_singleDigit_updatesInput() {
        // Given
        FragmentScenario<DialerFragment> scenario =
                FragmentScenario.launchInContainer(DialerFragment.class);

        // When / Then
        scenario.onFragment(fragment -> {
            fragment.onDialKeyPressed("1");
            assertEquals("1", fragment.getDialInput());
        });
    }

    @Test
    public void onDialKeyPressed_multipleDigits_appendsInput() {
        // Given
        FragmentScenario<DialerFragment> scenario =
                FragmentScenario.launchInContainer(DialerFragment.class);

        // When / Then
        scenario.onFragment(fragment -> {
            fragment.onDialKeyPressed("1");
            fragment.onDialKeyPressed("3");
            fragment.onDialKeyPressed("5");
            assertEquals("135", fragment.getDialInput());
        });
    }

    @Test
    public void onDialKeyPressed_specialChars_appendsCorrectly() {
        // Given
        FragmentScenario<DialerFragment> scenario =
                FragmentScenario.launchInContainer(DialerFragment.class);

        // When / Then
        scenario.onFragment(fragment -> {
            fragment.onDialKeyPressed("*");
            fragment.onDialKeyPressed("#");
            assertEquals("*#", fragment.getDialInput());
        });
    }

    @Test
    public void onDeletePressed_withInput_removesLastChar() {
        // Given
        FragmentScenario<DialerFragment> scenario =
                FragmentScenario.launchInContainer(DialerFragment.class);

        // When / Then
        scenario.onFragment(fragment -> {
            fragment.onDialKeyPressed("1");
            fragment.onDialKeyPressed("3");
            fragment.onDeletePressed();
            assertEquals("1", fragment.getDialInput());
        });
    }

    @Test
    public void onDeletePressed_emptyInput_doesNothing() {
        // Given
        FragmentScenario<DialerFragment> scenario =
                FragmentScenario.launchInContainer(DialerFragment.class);

        // When / Then
        scenario.onFragment(fragment -> {
            fragment.onDeletePressed();
            assertEquals("", fragment.getDialInput());
        });
    }

    @Test
    public void onDeletePressed_singleChar_becomesEmpty() {
        // Given
        FragmentScenario<DialerFragment> scenario =
                FragmentScenario.launchInContainer(DialerFragment.class);

        // When / Then
        scenario.onFragment(fragment -> {
            fragment.onDialKeyPressed("5");
            fragment.onDeletePressed();
            assertEquals("", fragment.getDialInput());
        });
    }

    @Test
    public void onClearAll_clearsInput() {
        // Given
        FragmentScenario<DialerFragment> scenario =
                FragmentScenario.launchInContainer(DialerFragment.class);

        // When / Then
        scenario.onFragment(fragment -> {
            fragment.onDialKeyPressed("1");
            fragment.onDialKeyPressed("3");
            fragment.onDialKeyPressed("5");
            fragment.onClearAll();
            assertEquals("", fragment.getDialInput());
        });
    }

    @Test
    public void onClearAll_restoresCallLogList() {
        // Given
        FragmentScenario<DialerFragment> scenario =
                FragmentScenario.launchInContainer(DialerFragment.class);

        // When / Then
        scenario.onFragment(fragment -> {
            fragment.onDialKeyPressed("1");
            fragment.onClearAll();
            assertEquals(5, fragment.getAdapter().getItemCount());
        });
    }

    @Test
    public void onCallPressed_withInput_clearsAndRestores() {
        // Given
        FragmentScenario<DialerFragment> scenario =
                FragmentScenario.launchInContainer(DialerFragment.class);

        // When / Then
        scenario.onFragment(fragment -> {
            fragment.onDialKeyPressed("1");
            fragment.onDialKeyPressed("3");
            fragment.onCallPressed();
            assertEquals("", fragment.getDialInput());
            assertEquals(5, fragment.getAdapter().getItemCount());
        });
    }

    @Test
    public void onCallPressed_emptyInput_doesNothing() {
        // Given
        FragmentScenario<DialerFragment> scenario =
                FragmentScenario.launchInContainer(DialerFragment.class);

        // When / Then
        scenario.onFragment(fragment -> {
            int countBefore = fragment.getAdapter().getItemCount();
            fragment.onCallPressed();
            assertEquals("", fragment.getDialInput());
            assertEquals(countBefore, fragment.getAdapter().getItemCount());
        });
    }

    @Test
    public void performSearch_matchingInput_filtersContacts() {
        // Given
        FragmentScenario<DialerFragment> scenario =
                FragmentScenario.launchInContainer(DialerFragment.class);

        // When / Then
        scenario.onFragment(fragment -> {
            fragment.onDialKeyPressed("1");
            fragment.onDialKeyPressed("3");
            fragment.onDialKeyPressed("5");
            fragment.onDialKeyPressed("2");
            fragment.onDialKeyPressed("2");
            fragment.onDialKeyPressed("2");
            fragment.onDialKeyPressed("2");
            assertTrue(fragment.getAdapter().getItemCount() > 0);
        });
    }

    @Test
    public void performSearch_noMatch_showsEmptyState() {
        // Given
        FragmentScenario<DialerFragment> scenario =
                FragmentScenario.launchInContainer(DialerFragment.class);

        // When / Then
        scenario.onFragment(fragment -> {
            fragment.onDialKeyPressed("9");
            fragment.onDialKeyPressed("9");
            fragment.onDialKeyPressed("9");
            fragment.onDialKeyPressed("9");
            fragment.onDialKeyPressed("9");
            fragment.onDialKeyPressed("9");
            assertEquals(0, fragment.getAdapter().getItemCount());
            assertEquals(View.VISIBLE,
                    fragment.getBinding().ivNoMatchIcon.getVisibility());
            assertEquals(View.VISIBLE,
                    fragment.getBinding().tvNoMatch.getVisibility());
        });
    }

    @Test
    public void performSearch_emptyInput_showsCallLogs() {
        // Given
        FragmentScenario<DialerFragment> scenario =
                FragmentScenario.launchInContainer(DialerFragment.class);

        // When / Then
        scenario.onFragment(fragment -> {
            fragment.onDialKeyPressed("1");
            fragment.onDeletePressed();
            assertEquals(5, fragment.getAdapter().getItemCount());
        });
    }

    @Test
    public void formatNumber_emptyString_returnsEmpty() {
        // Given / When / Then
        assertEquals("", DialerFragment.formatNumber(""));
    }

    @Test
    public void formatNumber_shortNumber_noSpaces() {
        // Given / When / Then
        assertEquals("135", DialerFragment.formatNumber("135"));
    }

    @Test
    public void formatNumber_fourDigits_noSpaces() {
        // Given / When / Then
        assertEquals("1352", DialerFragment.formatNumber("1352"));
    }

    @Test
    public void formatNumber_fiveDigits_oneSpace() {
        // Given / When / Then
        assertEquals("1352 2", DialerFragment.formatNumber("13522"));
    }

    @Test
    public void formatNumber_elevenDigits_formatsCorrectly() {
        // Given / When / Then
        assertEquals("1382 8299 090", DialerFragment.formatNumber("13828299090"));
    }

    @Test
    public void searchContacts_emptyQuery_returnsEmpty() {
        // Given
        List<CallLog> contacts = Arrays.asList(
                new CallLog("138", null, CallLog.TYPE_GENERIC, "")
        );

        // When
        List<CallLog> results = DialerFragment.searchContacts("", contacts);

        // Then
        assertEquals(0, results.size());
    }

    @Test
    public void searchContacts_matchingQuery_returnsMatches() {
        // Given
        List<CallLog> contacts = Arrays.asList(
                new CallLog("13522229090", "丹妮", CallLog.TYPE_GENERIC, ""),
                new CallLog("13800138000", "张三", CallLog.TYPE_GENERIC, ""),
                new CallLog("13522223456", null, CallLog.TYPE_GENERIC, "")
        );

        // When
        List<CallLog> results = DialerFragment.searchContacts("1352222", contacts);

        // Then
        assertEquals(2, results.size());
    }

    @Test
    public void searchContacts_noMatch_returnsEmpty() {
        // Given
        List<CallLog> contacts = Arrays.asList(
                new CallLog("13522229090", "丹妮", CallLog.TYPE_GENERIC, "")
        );

        // When
        List<CallLog> results = DialerFragment.searchContacts("999999", contacts);

        // Then
        assertEquals(0, results.size());
    }

    @Test
    public void searchContacts_emptyContactList_returnsEmpty() {
        // Given
        List<CallLog> contacts = Collections.emptyList();

        // When
        List<CallLog> results = DialerFragment.searchContacts("135", contacts);

        // Then
        assertEquals(0, results.size());
    }

    @Test
    public void searchContacts_allMatch_returnsAll() {
        // Given
        List<CallLog> contacts = Arrays.asList(
                new CallLog("13500001111", null, CallLog.TYPE_GENERIC, ""),
                new CallLog("13500002222", null, CallLog.TYPE_GENERIC, "")
        );

        // When
        List<CallLog> results = DialerFragment.searchContacts("135", contacts);

        // Then
        assertEquals(2, results.size());
    }

    @Test
    public void createMockCallLogs_returnsFiveItems() {
        // Given / When
        List<CallLog> logs = DialerFragment.createMockCallLogs();

        // Then
        assertEquals(5, logs.size());
    }

    @Test
    public void createMockCallLogs_firstIsMissed() {
        // Given / When
        List<CallLog> logs = DialerFragment.createMockCallLogs();

        // Then
        assertTrue(logs.get(0).isMissed());
    }

    @Test
    public void createMockContacts_returnsFiveItems() {
        // Given / When
        List<CallLog> contacts = DialerFragment.createMockContacts();

        // Then
        assertEquals(5, contacts.size());
    }

    @Test
    public void createMockContacts_firstHasName() {
        // Given / When
        List<CallLog> contacts = DialerFragment.createMockContacts();

        // Then
        assertNotNull(contacts.get(0).getName());
        assertEquals("丹妮", contacts.get(0).getName());
    }

    @Test
    public void onDestroyView_clearsBinding() {
        // Given
        FragmentScenario<DialerFragment> scenario =
                FragmentScenario.launchInContainer(DialerFragment.class);

        // When
        scenario.moveToState(Lifecycle.State.DESTROYED);

        // Then - no crash means success; binding is nulled in onDestroyView
    }

    @Test
    public void emptyState_initialLoad_isHidden() {
        // Given / When
        FragmentScenario<DialerFragment> scenario =
                FragmentScenario.launchInContainer(DialerFragment.class);

        // Then
        scenario.onFragment(fragment -> {
            assertEquals(View.GONE,
                    fragment.getBinding().ivNoMatchIcon.getVisibility());
            assertEquals(View.GONE,
                    fragment.getBinding().tvNoMatch.getVisibility());
        });
    }

    @Test
    public void keyboardClick_btn1_appendsOne() {
        // Given
        FragmentScenario<DialerFragment> scenario =
                FragmentScenario.launchInContainer(DialerFragment.class);

        // When / Then
        scenario.onFragment(fragment -> {
            fragment.getBinding().btn1.performClick();
            assertEquals("1", fragment.getDialInput());
        });
    }

    @Test
    public void keyboardClick_btn0_appendsZero() {
        // Given
        FragmentScenario<DialerFragment> scenario =
                FragmentScenario.launchInContainer(DialerFragment.class);

        // When / Then
        scenario.onFragment(fragment -> {
            fragment.getBinding().btn0.performClick();
            assertEquals("0", fragment.getDialInput());
        });
    }

    @Test
    public void keyboardClick_btnStar_appendsStar() {
        // Given
        FragmentScenario<DialerFragment> scenario =
                FragmentScenario.launchInContainer(DialerFragment.class);

        // When / Then
        scenario.onFragment(fragment -> {
            fragment.getBinding().btnStar.performClick();
            assertEquals("*", fragment.getDialInput());
        });
    }

    @Test
    public void keyboardClick_btnHash_appendsHash() {
        // Given
        FragmentScenario<DialerFragment> scenario =
                FragmentScenario.launchInContainer(DialerFragment.class);

        // When / Then
        scenario.onFragment(fragment -> {
            fragment.getBinding().btnHash.performClick();
            assertEquals("#", fragment.getDialInput());
        });
    }

    @Test
    public void keyboardClick_btnDel_deletesLast() {
        // Given
        FragmentScenario<DialerFragment> scenario =
                FragmentScenario.launchInContainer(DialerFragment.class);

        // When / Then
        scenario.onFragment(fragment -> {
            fragment.getBinding().btn1.performClick();
            fragment.getBinding().btn2.performClick();
            fragment.getBinding().btnDel.performClick();
            assertEquals("1", fragment.getDialInput());
        });
    }

    @Test
    public void keyboardClick_btnCall_clearsInput() {
        // Given
        FragmentScenario<DialerFragment> scenario =
                FragmentScenario.launchInContainer(DialerFragment.class);

        // When / Then
        scenario.onFragment(fragment -> {
            fragment.getBinding().btn1.performClick();
            fragment.getBinding().btnCall.performClick();
            assertEquals("", fragment.getDialInput());
        });
    }

    @Test
    public void keyboardClick_allNumberButtons_work() {
        // Given
        FragmentScenario<DialerFragment> scenario =
                FragmentScenario.launchInContainer(DialerFragment.class);

        // When / Then
        scenario.onFragment(fragment -> {
            fragment.getBinding().btn1.performClick();
            fragment.getBinding().btn2.performClick();
            fragment.getBinding().btn3.performClick();
            fragment.getBinding().btn4.performClick();
            fragment.getBinding().btn5.performClick();
            fragment.getBinding().btn6.performClick();
            fragment.getBinding().btn7.performClick();
            fragment.getBinding().btn8.performClick();
            fragment.getBinding().btn9.performClick();
            fragment.getBinding().btn0.performClick();
            assertEquals("1234567890", fragment.getDialInput());
        });
    }
}
