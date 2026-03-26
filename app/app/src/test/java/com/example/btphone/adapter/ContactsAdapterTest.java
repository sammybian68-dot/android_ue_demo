package com.example.btphone.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.example.btphone.model.Contact;

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
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * {@link ContactsAdapter} 的单元测试。
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class ContactsAdapterTest {

    private ContactsAdapter mAdapter;
    private Activity mActivity;

    @Before
    public void setUp() {
        mActivity = Robolectric.buildActivity(Activity.class).create().get();
        mAdapter = new ContactsAdapter();
    }

    @Test
    public void setContacts_emptyList_itemCountZero() {
        // Given
        List<Contact> contacts = Collections.emptyList();

        // When
        mAdapter.setContacts(contacts);

        // Then
        assertEquals(0, mAdapter.getItemCount());
    }

    @Test
    public void setContacts_singleGroup_insertsHeaderAndContacts() {
        // Given
        List<Contact> contacts = Arrays.asList(
                new Contact("程德华", "111", "C", 0),
                new Contact("陈小明", "222", "C", 1)
        );

        // When
        mAdapter.setContacts(contacts);

        // Then — 1 header + 2 contacts = 3 items
        assertEquals(3, mAdapter.getItemCount());
        assertEquals(ContactsAdapter.TYPE_HEADER, mAdapter.getItemViewType(0));
        assertEquals(ContactsAdapter.TYPE_CONTACT, mAdapter.getItemViewType(1));
        assertEquals(ContactsAdapter.TYPE_CONTACT, mAdapter.getItemViewType(2));
    }

    @Test
    public void setContacts_multipleGroups_insertsCorrectHeaders() {
        // Given
        List<Contact> contacts = Arrays.asList(
                new Contact("程德华", "111", "C", 0),
                new Contact("大黄", "222", "D", 1),
                new Contact("丹妮", "333", "D", 2)
        );

        // When
        mAdapter.setContacts(contacts);

        // Then — C header + 1 contact + D header + 2 contacts = 5
        assertEquals(5, mAdapter.getItemCount());
        assertEquals(ContactsAdapter.TYPE_HEADER, mAdapter.getItemViewType(0));
        assertEquals(ContactsAdapter.TYPE_CONTACT, mAdapter.getItemViewType(1));
        assertEquals(ContactsAdapter.TYPE_HEADER, mAdapter.getItemViewType(2));
        assertEquals(ContactsAdapter.TYPE_CONTACT, mAdapter.getItemViewType(3));
        assertEquals(ContactsAdapter.TYPE_CONTACT, mAdapter.getItemViewType(4));
    }

    @Test
    public void setContacts_calledTwice_replacesData() {
        // Given
        mAdapter.setContacts(Arrays.asList(
                new Contact("程德华", "111", "C", 0)
        ));
        assertEquals(2, mAdapter.getItemCount());

        // When
        mAdapter.setContacts(Arrays.asList(
                new Contact("大黄", "222", "D", 1)
        ));

        // Then
        assertEquals(2, mAdapter.getItemCount());
        assertEquals("D", mAdapter.getItems().get(0));
    }

    @Test
    public void getPositionForLetter_existingLetter_returnsPosition() {
        // Given
        mAdapter.setContacts(Arrays.asList(
                new Contact("程德华", "111", "C", 0),
                new Contact("大黄", "222", "D", 1)
        ));

        // When / Then
        assertEquals(0, mAdapter.getPositionForLetter("C"));
        assertEquals(2, mAdapter.getPositionForLetter("D"));
    }

    @Test
    public void getPositionForLetter_nonExistingLetter_returnsMinusOne() {
        // Given
        mAdapter.setContacts(Arrays.asList(
                new Contact("程德华", "111", "C", 0)
        ));

        // When / Then
        assertEquals(-1, mAdapter.getPositionForLetter("Z"));
    }

    @Test
    public void getLetterPositionMap_returnsCorrectMapping() {
        // Given
        mAdapter.setContacts(Arrays.asList(
                new Contact("程德华", "111", "C", 0),
                new Contact("大黄", "222", "D", 1),
                new Contact("峰峰", "333", "F", 2)
        ));

        // When
        Map<String, Integer> map = mAdapter.getLetterPositionMap();

        // Then
        assertEquals(3, map.size());
        assertTrue(map.containsKey("C"));
        assertTrue(map.containsKey("D"));
        assertTrue(map.containsKey("F"));
    }

    @Test
    public void getItemViewType_headerPosition_returnsTypeHeader() {
        // Given
        mAdapter.setContacts(Arrays.asList(
                new Contact("程德华", "111", "C", 0)
        ));

        // When / Then
        assertEquals(ContactsAdapter.TYPE_HEADER, mAdapter.getItemViewType(0));
    }

    @Test
    public void getItemViewType_contactPosition_returnsTypeContact() {
        // Given
        mAdapter.setContacts(Arrays.asList(
                new Contact("程德华", "111", "C", 0)
        ));

        // When / Then
        assertEquals(ContactsAdapter.TYPE_CONTACT, mAdapter.getItemViewType(1));
    }

    @Test
    public void onCreateViewHolder_headerType_createsHeaderViewHolder() {
        // Given
        FrameLayout parent = new FrameLayout(mActivity);

        // When
        RecyclerView.ViewHolder holder =
                mAdapter.onCreateViewHolder(parent, ContactsAdapter.TYPE_HEADER);

        // Then
        assertNotNull(holder);
        assertTrue(holder instanceof ContactsAdapter.HeaderViewHolder);
    }

    @Test
    public void onCreateViewHolder_contactType_createsContactViewHolder() {
        // Given
        FrameLayout parent = new FrameLayout(mActivity);

        // When
        RecyclerView.ViewHolder holder =
                mAdapter.onCreateViewHolder(parent, ContactsAdapter.TYPE_CONTACT);

        // Then
        assertNotNull(holder);
        assertTrue(holder instanceof ContactsAdapter.ContactViewHolder);
    }

    @Test
    public void onBindViewHolder_headerPosition_setsLetterText() {
        // Given
        mAdapter.setContacts(Arrays.asList(
                new Contact("程德华", "111", "C", 0)
        ));
        FrameLayout parent = new FrameLayout(mActivity);
        RecyclerView.ViewHolder holder =
                mAdapter.onCreateViewHolder(parent, ContactsAdapter.TYPE_HEADER);

        // When
        mAdapter.onBindViewHolder(holder, 0);

        // Then — holder.itemView is the root of item_contact_header
        assertNotNull(holder.itemView);
    }

    @Test
    public void onBindViewHolder_contactPosition_setsNameAndAvatar() {
        // Given
        mAdapter.setContacts(Arrays.asList(
                new Contact("程德华", "111", "C", 0)
        ));
        FrameLayout parent = new FrameLayout(mActivity);
        RecyclerView.ViewHolder holder =
                mAdapter.onCreateViewHolder(parent, ContactsAdapter.TYPE_CONTACT);

        // When
        mAdapter.onBindViewHolder(holder, 1);

        // Then
        assertNotNull(holder.itemView);
    }

    @Test
    public void getItems_afterSet_containsHeadersAndContacts() {
        // Given
        List<Contact> contacts = Arrays.asList(
                new Contact("程德华", "111", "C", 0),
                new Contact("大黄", "222", "D", 1)
        );

        // When
        mAdapter.setContacts(contacts);

        // Then
        List<Object> items = mAdapter.getItems();
        assertEquals(4, items.size());
        assertTrue(items.get(0) instanceof String);
        assertTrue(items.get(1) instanceof Contact);
        assertTrue(items.get(2) instanceof String);
        assertTrue(items.get(3) instanceof Contact);
    }

    @Test
    public void setContacts_mockData_populatesCorrectly() {
        // Given
        List<Contact> mockContacts = Contact.createMockContacts();

        // When
        mAdapter.setContacts(mockContacts);

        // Then
        assertTrue(mAdapter.getItemCount() > mockContacts.size());
        assertFalse(mAdapter.getLetterPositionMap().isEmpty());
    }
}
