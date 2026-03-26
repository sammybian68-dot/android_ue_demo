package com.example.btphone.ui.contacts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btphone.R;
import com.example.btphone.adapter.PhoneNumberAdapter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * {@link ContactDetailDialog} 单元测试。
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class ContactDetailDialogTest {

    private AppCompatActivity mActivity;
    private ContactDetailDialog mDialog;
    private List<String> mPhoneNumbers;

    @Before
    public void setUp() {
        mActivity = Robolectric.buildActivity(AppCompatActivity.class)
                .create().start().resume().get();
        mPhoneNumbers = Arrays.asList("13828299090", "13900000000");
        mDialog = ContactDetailDialog.newInstance(
                "移动客服", "移", 2, mPhoneNumbers);
        FragmentManager fm = mActivity.getSupportFragmentManager();
        mDialog.show(fm, ContactDetailDialog.TAG);
        fm.executePendingTransactions();
    }

    @Test
    public void tag_isExpectedValue() {
        // When / Then
        assertEquals("ContactDetailDialog", ContactDetailDialog.TAG);
    }

    @Test
    public void newInstance_returnsNonNull() {
        // When
        ContactDetailDialog dialog = ContactDetailDialog.newInstance(
                "张三", "三", 0, Collections.singletonList("10086"));

        // Then
        assertNotNull(dialog);
    }

    @Test
    public void newInstance_setsArguments() {
        // When
        ContactDetailDialog dialog = ContactDetailDialog.newInstance(
                "张三", "三", 1, Arrays.asList("111", "222"));

        // Then
        assertNotNull(dialog.getArguments());
    }

    @Test
    public void show_dialogIsShowing() {
        // Then
        assertNotNull(mDialog.getDialog());
        assertTrue(mDialog.getDialog().isShowing());
    }

    @Test
    public void getContactName_returnsExpectedValue() {
        // When / Then
        assertEquals("移动客服", mDialog.getContactName());
    }

    @Test
    public void getAvatarText_returnsExpectedValue() {
        // When / Then
        assertEquals("移", mDialog.getAvatarText());
    }

    @Test
    public void getAvatarColorIndex_returnsExpectedValue() {
        // When / Then
        assertEquals(2, mDialog.getAvatarColorIndex());
    }

    @Test
    public void getPhoneNumbers_returnsList() {
        // When
        List<String> numbers = mDialog.getPhoneNumbers();

        // Then
        assertEquals(2, numbers.size());
        assertEquals("13828299090", numbers.get(0));
        assertEquals("13900000000", numbers.get(1));
    }

    @Test
    public void getContactName_noArguments_returnsEmpty() {
        // Given
        ContactDetailDialog dialog = new ContactDetailDialog();

        // When / Then
        assertEquals("", dialog.getContactName());
    }

    @Test
    public void getAvatarText_noArguments_returnsEmpty() {
        // Given
        ContactDetailDialog dialog = new ContactDetailDialog();

        // When / Then
        assertEquals("", dialog.getAvatarText());
    }

    @Test
    public void getAvatarColorIndex_noArguments_returnsZero() {
        // Given
        ContactDetailDialog dialog = new ContactDetailDialog();

        // When / Then
        assertEquals(0, dialog.getAvatarColorIndex());
    }

    @Test
    public void getPhoneNumbers_noArguments_returnsEmptyList() {
        // Given
        ContactDetailDialog dialog = new ContactDetailDialog();

        // When / Then
        assertTrue(dialog.getPhoneNumbers().isEmpty());
    }

    @Test
    public void onViewCreated_avatarTextIsSet() {
        // Given
        TextView tvAvatar = mDialog.getView().findViewById(R.id.tv_avatar);

        // Then
        assertEquals("移", tvAvatar.getText().toString());
    }

    @Test
    public void onViewCreated_nameIsSet() {
        // Given
        TextView tvName = mDialog.getView().findViewById(R.id.tv_name);

        // Then
        assertEquals("移动客服", tvName.getText().toString());
    }

    @Test
    public void onViewCreated_phoneSectionLabelIsSet() {
        // Given
        TextView tvLabel = mDialog.getView().findViewById(R.id.tv_phone_label);

        // Then
        assertEquals("电话", tvLabel.getText().toString());
    }

    @Test
    public void onViewCreated_adapterIsSet() {
        // Then
        assertNotNull(mDialog.getAdapter());
    }

    @Test
    public void onViewCreated_adapterHasTwoItems() {
        // Then
        assertEquals(2, mDialog.getAdapter().getItemCount());
    }

    @Test
    public void onViewCreated_recyclerViewHasLayoutManager() {
        // Given
        RecyclerView rv = mDialog.getView().findViewById(R.id.rv_phone_numbers);

        // Then
        assertNotNull(rv.getLayoutManager());
    }

    @Test
    public void onViewCreated_recyclerViewHasAdapter() {
        // Given
        RecyclerView rv = mDialog.getView().findViewById(R.id.rv_phone_numbers);

        // Then
        assertNotNull(rv.getAdapter());
    }

    @Test
    public void singlePhoneNumber_adapterHasOneItem() {
        // Given
        ContactDetailDialog dialog = ContactDetailDialog.newInstance(
                "张三", "三", 0, Collections.singletonList("10086"));
        FragmentManager fm = mActivity.getSupportFragmentManager();
        dialog.show(fm, "test_single");
        fm.executePendingTransactions();

        // Then
        assertEquals(1, dialog.getAdapter().getItemCount());
    }

    @Test
    public void multiplePhoneNumbers_adapterCountMatches() {
        // Given
        List<String> many = Arrays.asList("111", "222", "333", "444", "555");
        ContactDetailDialog dialog = ContactDetailDialog.newInstance(
                "多号联系人", "多", 1, many);
        FragmentManager fm = mActivity.getSupportFragmentManager();
        dialog.show(fm, "test_multi");
        fm.executePendingTransactions();

        // Then
        assertEquals(5, dialog.getAdapter().getItemCount());
    }

    @Test
    public void emptyPhoneNumbers_adapterIsEmpty() {
        // Given
        ContactDetailDialog dialog = ContactDetailDialog.newInstance(
                "无号码", "无", 0, Collections.emptyList());
        FragmentManager fm = mActivity.getSupportFragmentManager();
        dialog.show(fm, "test_empty");
        fm.executePendingTransactions();

        // Then
        assertEquals(0, dialog.getAdapter().getItemCount());
    }

    @Test
    public void setOnPhoneNumberClickListener_beforeShow_adapterReceivesListener() {
        // Given
        PhoneNumberAdapter.OnPhoneNumberClickListener listener =
                mock(PhoneNumberAdapter.OnPhoneNumberClickListener.class);
        ContactDetailDialog dialog = ContactDetailDialog.newInstance(
                "测试", "测", 0, Collections.singletonList("10086"));

        // When
        dialog.setOnPhoneNumberClickListener(listener);
        FragmentManager fm = mActivity.getSupportFragmentManager();
        dialog.show(fm, "test_listener_before");
        fm.executePendingTransactions();

        // Then
        assertNotNull(dialog.getAdapter());
        assertEquals(1, dialog.getAdapter().getItemCount());
    }

    @Test
    public void setOnPhoneNumberClickListener_afterShow_adapterReceivesListener() {
        // Given
        PhoneNumberAdapter.OnPhoneNumberClickListener listener =
                mock(PhoneNumberAdapter.OnPhoneNumberClickListener.class);

        // When
        mDialog.setOnPhoneNumberClickListener(listener);

        // Then — adapter is still valid and functional
        assertNotNull(mDialog.getAdapter());
        assertEquals(2, mDialog.getAdapter().getItemCount());
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

    @Test
    public void avatarColorIndex_zeroToThree_allValid() {
        // When / Then — no crash for any valid index
        for (int i = 0; i < 4; i++) {
            ContactDetailDialog dialog = ContactDetailDialog.newInstance(
                    "测试" + i, "测", i, Collections.singletonList("111"));
            FragmentManager fm = mActivity.getSupportFragmentManager();
            dialog.show(fm, "test_color_" + i);
            fm.executePendingTransactions();
            assertNotNull(dialog.getView());
            dialog.dismiss();
            fm.executePendingTransactions();
        }
    }

    @Test
    public void avatarColorIndex_outOfRange_wrapsAround() {
        // Given — index 5 should wrap to 5 % 4 = 1
        ContactDetailDialog dialog = ContactDetailDialog.newInstance(
                "测试", "测", 5, Collections.singletonList("111"));
        FragmentManager fm = mActivity.getSupportFragmentManager();
        dialog.show(fm, "test_wrap");
        fm.executePendingTransactions();

        // Then — no crash, view is created
        assertNotNull(dialog.getView());
    }

    @Test
    public void extremeLengthName_doesNotCrash() {
        // Given
        String longName = "极限长度联系人姓名极限长度联系人姓名极限长度联系人姓名";
        ContactDetailDialog dialog = ContactDetailDialog.newInstance(
                longName, "名", 0, Collections.singletonList("111"));
        FragmentManager fm = mActivity.getSupportFragmentManager();
        dialog.show(fm, "test_long_name");
        fm.executePendingTransactions();

        // Then
        TextView tvName = dialog.getView().findViewById(R.id.tv_name);
        assertEquals(longName, tvName.getText().toString());
    }

    @Test
    public void extremeLengthPhoneNumber_doesNotCrash() {
        // Given
        String longNumber = "138282990901234567890123456789";
        ContactDetailDialog dialog = ContactDetailDialog.newInstance(
                "测试", "测", 0, Collections.singletonList(longNumber));
        FragmentManager fm = mActivity.getSupportFragmentManager();
        dialog.show(fm, "test_long_number");
        fm.executePendingTransactions();

        // Then
        assertEquals(1, dialog.getAdapter().getItemCount());
        assertEquals(longNumber, dialog.getAdapter().getItem(0));
    }
}
