package com.example.btphone.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * {@link CallLog} 单元测试。
 */
public class CallLogTest {

    @Test
    public void constructor_allParams_storesCorrectly() {
        // Given
        String number = "13800138000";
        String name = "张三";
        int type = CallLog.TYPE_INCOMING;
        String date = "11/8";

        // When
        CallLog callLog = new CallLog(number, name, type, date);

        // Then
        assertEquals(number, callLog.getNumber());
        assertEquals(name, callLog.getName());
        assertEquals(type, callLog.getCallType());
        assertEquals(date, callLog.getDate());
    }

    @Test
    public void constructor_nullName_storesNull() {
        // Given / When
        CallLog callLog = new CallLog("10086", null, CallLog.TYPE_GENERIC, "12/1");

        // Then
        assertNull(callLog.getName());
    }

    @Test
    public void getDisplayText_withName_returnsName() {
        // Given
        CallLog callLog = new CallLog("13800138000", "张三", CallLog.TYPE_INCOMING, "11/8");

        // When
        String display = callLog.getDisplayText();

        // Then
        assertEquals("张三", display);
    }

    @Test
    public void getDisplayText_withEmptyName_returnsNumber() {
        // Given
        CallLog callLog = new CallLog("13800138000", "", CallLog.TYPE_INCOMING, "11/8");

        // When
        String display = callLog.getDisplayText();

        // Then
        assertEquals("13800138000", display);
    }

    @Test
    public void getDisplayText_withNullName_returnsNumber() {
        // Given
        CallLog callLog = new CallLog("10010", null, CallLog.TYPE_OUTGOING, "11/9");

        // When
        String display = callLog.getDisplayText();

        // Then
        assertEquals("10010", display);
    }

    @Test
    public void isMissed_typeMissed_returnsTrue() {
        // Given
        CallLog callLog = new CallLog("10086", null, CallLog.TYPE_MISSED, "11/8");

        // When / Then
        assertTrue(callLog.isMissed());
    }

    @Test
    public void isMissed_typeIncoming_returnsFalse() {
        // Given
        CallLog callLog = new CallLog("10086", null, CallLog.TYPE_INCOMING, "11/8");

        // When / Then
        assertFalse(callLog.isMissed());
    }

    @Test
    public void isMissed_typeOutgoing_returnsFalse() {
        // Given
        CallLog callLog = new CallLog("10086", null, CallLog.TYPE_OUTGOING, "11/8");

        // When / Then
        assertFalse(callLog.isMissed());
    }

    @Test
    public void isMissed_typeGeneric_returnsFalse() {
        // Given
        CallLog callLog = new CallLog("10086", null, CallLog.TYPE_GENERIC, "11/8");

        // When / Then
        assertFalse(callLog.isMissed());
    }

    @Test
    public void typeConstants_haveDistinctValues() {
        // Then
        assertTrue(CallLog.TYPE_MISSED != CallLog.TYPE_INCOMING);
        assertTrue(CallLog.TYPE_INCOMING != CallLog.TYPE_OUTGOING);
        assertTrue(CallLog.TYPE_OUTGOING != CallLog.TYPE_GENERIC);
        assertTrue(CallLog.TYPE_MISSED != CallLog.TYPE_GENERIC);
    }
}
