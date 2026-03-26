package com.example.btphone.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 通话记录数据模型。
 * <p>
 * 包含号码、联系人姓名、通话类型和日期信息。
 */
public class CallLog {

    /** 未接来电 */
    public static final int TYPE_MISSED = 0;

    /** 已接来电 */
    public static final int TYPE_INCOMING = 1;

    /** 呼出 */
    public static final int TYPE_OUTGOING = 2;

    /** 通用 */
    public static final int TYPE_GENERIC = 3;

    @NonNull
    private final String mNumber;

    @Nullable
    private final String mName;

    private final int mCallType;

    @NonNull
    private final String mDate;

    /**
     * 创建通话记录。
     *
     * @param number   电话号码，不能为 null
     * @param name     联系人姓名，可以为 null
     * @param callType 通话类型，{@link #TYPE_MISSED}、{@link #TYPE_INCOMING}、
     *                 {@link #TYPE_OUTGOING} 或 {@link #TYPE_GENERIC}
     * @param date     日期字符串，不能为 null
     */
    public CallLog(@NonNull String number, @Nullable String name, int callType,
                   @NonNull String date) {
        mNumber = number;
        mName = name;
        mCallType = callType;
        mDate = date;
    }

    /**
     * 获取电话号码。
     *
     * @return 电话号码
     */
    @NonNull
    public String getNumber() {
        return mNumber;
    }

    /**
     * 获取联系人姓名。
     *
     * @return 联系人姓名，可能为 null
     */
    @Nullable
    public String getName() {
        return mName;
    }

    /**
     * 获取通话类型。
     *
     * @return 通话类型常量
     */
    public int getCallType() {
        return mCallType;
    }

    /**
     * 获取日期字符串。
     *
     * @return 日期字符串
     */
    @NonNull
    public String getDate() {
        return mDate;
    }

    /**
     * 获取用于显示的文本：有姓名时显示姓名，否则显示号码。
     *
     * @return 显示文本
     */
    @NonNull
    public String getDisplayText() {
        if (mName != null && !mName.isEmpty()) {
            return mName;
        }
        return mNumber;
    }

    /**
     * 判断是否为未接来电。
     *
     * @return 是否未接
     */
    public boolean isMissed() {
        return mCallType == TYPE_MISSED;
    }
}
