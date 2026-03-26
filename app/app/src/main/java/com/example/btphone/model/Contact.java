package com.example.btphone.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 联系人数据模型。
 * <p>
 * 存储联系人的姓名、电话号码、头像文字（姓名末字）以及用于分组排序的拼音首字母。
 */
public class Contact implements Comparable<Contact> {

    private final String mName;
    private final String mPhoneNumber;
    private final String mAvatarText;
    private final String mSortLetter;
    private final int mAvatarColorIndex;

    /**
     * 构造联系人实例。
     *
     * @param name            联系人姓名，不能为 null
     * @param phoneNumber     电话号码，不能为 null
     * @param sortLetter      拼音首字母（大写 A-Z），不能为 null
     * @param avatarColorIndex 头像背景颜色索引（0-3 循环使用）
     */
    public Contact(@NonNull String name, @NonNull String phoneNumber,
                   @NonNull String sortLetter, int avatarColorIndex) {
        mName = name;
        mPhoneNumber = phoneNumber;
        mAvatarText = name.isEmpty() ? "" : String.valueOf(name.charAt(name.length() - 1));
        mSortLetter = sortLetter;
        mAvatarColorIndex = avatarColorIndex;
    }

    /**
     * 获取联系人姓名。
     *
     * @return 姓名
     */
    @NonNull
    public String getName() {
        return mName;
    }

    /**
     * 获取电话号码。
     *
     * @return 电话号码
     */
    @NonNull
    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    /**
     * 获取头像显示文字（姓名末字）。
     *
     * @return 头像文字
     */
    @NonNull
    public String getAvatarText() {
        return mAvatarText;
    }

    /**
     * 获取拼音排序首字母。
     *
     * @return 大写首字母 A-Z 或 "#"
     */
    @NonNull
    public String getSortLetter() {
        return mSortLetter;
    }

    /**
     * 获取头像背景颜色索引。
     *
     * @return 颜色索引 0-3
     */
    public int getAvatarColorIndex() {
        return mAvatarColorIndex;
    }

    @Override
    public int compareTo(@NonNull Contact other) {
        return mSortLetter.compareTo(other.mSortLetter);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Contact)) {
            return false;
        }
        Contact other = (Contact) obj;
        return mName.equals(other.mName)
                && mPhoneNumber.equals(other.mPhoneNumber)
                && mSortLetter.equals(other.mSortLetter)
                && mAvatarColorIndex == other.mAvatarColorIndex;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mName, mPhoneNumber, mSortLetter, mAvatarColorIndex);
    }

    @NonNull
    @Override
    public String toString() {
        return "Contact{name='" + mName + "', phone='" + mPhoneNumber
                + "', sort='" + mSortLetter + "'}";
    }

    /**
     * 生成 Mock 联系人列表，用于 UI 演示。
     * <p>
     * 按拼音首字母排序，包含多个分组以展示字母索引效果。
     *
     * @return 按拼音排序的联系人列表
     */
    @NonNull
    public static List<Contact> createMockContacts() {
        List<Contact> contacts = new ArrayList<>();
        int idx = 0;
        contacts.add(new Contact("程德华", "13800138001", "C", idx++ % 4));
        contacts.add(new Contact("陈小明", "13800138002", "C", idx++ % 4));
        contacts.add(new Contact("大黄", "13828299090", "D", idx++ % 4));
        contacts.add(new Contact("丹妮", "13800138004", "D", idx++ % 4));
        contacts.add(new Contact("杜鹃", "13800138005", "D", idx++ % 4));
        contacts.add(new Contact("峰峰", "13800138006", "F", idx++ % 4));
        contacts.add(new Contact("冯思", "13800138007", "F", idx++ % 4));
        contacts.add(new Contact("高山", "13800138008", "G", idx++ % 4));
        contacts.add(new Contact("韩梅梅", "13800138009", "H", idx++ % 4));
        contacts.add(new Contact("黄磊", "13800138010", "H", idx++ % 4));
        contacts.add(new Contact("李雷", "13800138011", "L", idx++ % 4));
        contacts.add(new Contact("林涛", "13800138012", "L", idx++ % 4));
        contacts.add(new Contact("马云", "13800138013", "M", idx++ % 4));
        contacts.add(new Contact("王刚", "13800138014", "W", idx++ % 4));
        contacts.add(new Contact("吴亮", "13800138015", "W", idx++ % 4));
        contacts.add(new Contact("徐峰", "13800138016", "X", idx++ % 4));
        contacts.add(new Contact("张伟", "13800138017", "Z", idx++ % 4));
        contacts.add(new Contact("赵敏", "13800138018", "Z", idx++ % 4));
        Collections.sort(contacts);
        return contacts;
    }
}
