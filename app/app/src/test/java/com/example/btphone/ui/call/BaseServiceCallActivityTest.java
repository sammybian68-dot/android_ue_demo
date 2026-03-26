package com.example.btphone.ui.call;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * {@link BaseServiceCallActivity} 单元测试。
 * <p>
 * 覆盖静态方法 formatTime 的各种边界情况。
 * 状态机逻辑通过具体子类（ICall/ECall）的测试间接覆盖。
 */
@RunWith(JUnit4.class)
public class BaseServiceCallActivityTest {

    @Test
    public void formatTime_zero_returnsZero() {
        // Given
        int seconds = 0;

        // When
        String result = BaseServiceCallActivity.formatTime(seconds);

        // Then
        assertEquals("00:00", result);
    }

    @Test
    public void formatTime_oneSecond_returnsOneSecond() {
        // Given
        int seconds = 1;

        // When
        String result = BaseServiceCallActivity.formatTime(seconds);

        // Then
        assertEquals("00:01", result);
    }

    @Test
    public void formatTime_59Seconds_returnsCorrectly() {
        // Given
        int seconds = 59;

        // When
        String result = BaseServiceCallActivity.formatTime(seconds);

        // Then
        assertEquals("00:59", result);
    }

    @Test
    public void formatTime_60Seconds_returnsOneMinute() {
        // Given
        int seconds = 60;

        // When
        String result = BaseServiceCallActivity.formatTime(seconds);

        // Then
        assertEquals("01:00", result);
    }

    @Test
    public void formatTime_90Seconds_returnsOneMinuteThirty() {
        // Given
        int seconds = 90;

        // When
        String result = BaseServiceCallActivity.formatTime(seconds);

        // Then
        assertEquals("01:30", result);
    }

    @Test
    public void formatTime_3661Seconds_returnsLargeValue() {
        // Given
        int seconds = 3661;

        // When
        String result = BaseServiceCallActivity.formatTime(seconds);

        // Then
        assertEquals("61:01", result);
    }

    @Test
    public void stateConstants_correctValues() {
        // Then
        assertEquals(0, BaseServiceCallActivity.STATE_DIALING);
        assertEquals(1, BaseServiceCallActivity.STATE_IN_CALL);
        assertEquals(0, BaseServiceCallActivity.MODE_FULLSCREEN);
        assertEquals(1, BaseServiceCallActivity.MODE_MINI);
    }
}
