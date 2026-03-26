package com.example.btphone.model;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * {@link Contact} 模型类的单元测试。
 */
public class ContactTest {

    @Test
    public void constructor_validInput_fieldsSetCorrectly() {
        // Given
        String name = "程德华";
        String phone = "13800138001";
        String sortLetter = "C";
        int colorIndex = 2;

        // When
        Contact contact = new Contact(name, phone, sortLetter, colorIndex);

        // Then
        assertEquals(name, contact.getName());
        assertEquals(phone, contact.getPhoneNumber());
        assertEquals("华", contact.getAvatarText());
        assertEquals(sortLetter, contact.getSortLetter());
        assertEquals(colorIndex, contact.getAvatarColorIndex());
    }

    @Test
    public void getAvatarText_singleCharName_returnsThatChar() {
        // Given / When
        Contact contact = new Contact("峰", "123", "F", 0);

        // Then
        assertEquals("峰", contact.getAvatarText());
    }

    @Test
    public void getAvatarText_multiCharName_returnsLastChar() {
        // Given / When
        Contact contact = new Contact("韩梅梅", "123", "H", 1);

        // Then
        assertEquals("梅", contact.getAvatarText());
    }

    @Test
    public void getAvatarText_emptyName_returnsEmpty() {
        // Given / When
        Contact contact = new Contact("", "123", "#", 0);

        // Then
        assertEquals("", contact.getAvatarText());
    }

    @Test
    public void compareTo_sameGroup_returnsZero() {
        // Given
        Contact c1 = new Contact("程德华", "111", "C", 0);
        Contact c2 = new Contact("陈小明", "222", "C", 1);

        // When
        int result = c1.compareTo(c2);

        // Then
        assertEquals(0, result);
    }

    @Test
    public void compareTo_differentGroup_returnsNonZero() {
        // Given
        Contact c1 = new Contact("程德华", "111", "C", 0);
        Contact c2 = new Contact("大黄", "222", "D", 1);

        // When / Then
        assertTrue(c1.compareTo(c2) < 0);
        assertTrue(c2.compareTo(c1) > 0);
    }

    @Test
    public void equals_sameData_returnsTrue() {
        // Given
        Contact c1 = new Contact("程德华", "13800138001", "C", 0);
        Contact c2 = new Contact("程德华", "13800138001", "C", 0);

        // When / Then
        assertTrue(c1.equals(c2));
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    public void equals_differentName_returnsFalse() {
        // Given
        Contact c1 = new Contact("程德华", "13800138001", "C", 0);
        Contact c2 = new Contact("陈小明", "13800138001", "C", 0);

        // When / Then
        assertFalse(c1.equals(c2));
    }

    @Test
    public void equals_differentPhone_returnsFalse() {
        // Given
        Contact c1 = new Contact("程德华", "111", "C", 0);
        Contact c2 = new Contact("程德华", "222", "C", 0);

        // When / Then
        assertFalse(c1.equals(c2));
    }

    @Test
    public void equals_differentSortLetter_returnsFalse() {
        // Given
        Contact c1 = new Contact("程德华", "111", "C", 0);
        Contact c2 = new Contact("程德华", "111", "D", 0);

        // When / Then
        assertFalse(c1.equals(c2));
    }

    @Test
    public void equals_differentColorIndex_returnsFalse() {
        // Given
        Contact c1 = new Contact("程德华", "111", "C", 0);
        Contact c2 = new Contact("程德华", "111", "C", 1);

        // When / Then
        assertFalse(c1.equals(c2));
    }

    @Test
    public void equals_null_returnsFalse() {
        // Given
        Contact contact = new Contact("程德华", "111", "C", 0);

        // When / Then
        assertFalse(contact.equals(null));
    }

    @Test
    public void equals_sameInstance_returnsTrue() {
        // Given
        Contact contact = new Contact("程德华", "111", "C", 0);

        // When / Then
        assertTrue(contact.equals(contact));
    }

    @Test
    public void equals_differentType_returnsFalse() {
        // Given
        Contact contact = new Contact("程德华", "111", "C", 0);

        // When / Then
        assertFalse(contact.equals("not a contact"));
    }

    @Test
    public void hashCode_sameContacts_sameHash() {
        // Given
        Contact c1 = new Contact("程德华", "111", "C", 0);
        Contact c2 = new Contact("程德华", "111", "C", 0);

        // When / Then
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    public void hashCode_differentContacts_likelyDifferentHash() {
        // Given
        Contact c1 = new Contact("程德华", "111", "C", 0);
        Contact c2 = new Contact("大黄", "222", "D", 1);

        // When / Then
        assertNotEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    public void toString_containsNameAndPhone() {
        // Given
        Contact contact = new Contact("程德华", "13800138001", "C", 0);

        // When
        String str = contact.toString();

        // Then
        assertTrue(str.contains("程德华"));
        assertTrue(str.contains("13800138001"));
        assertTrue(str.contains("C"));
    }

    @Test
    public void createMockContacts_returnsNonEmptyList() {
        // When
        List<Contact> contacts = Contact.createMockContacts();

        // Then
        assertNotNull(contacts);
        assertFalse(contacts.isEmpty());
    }

    @Test
    public void createMockContacts_sortedBySortLetter() {
        // When
        List<Contact> contacts = Contact.createMockContacts();

        // Then
        for (int i = 1; i < contacts.size(); i++) {
            String prev = contacts.get(i - 1).getSortLetter();
            String curr = contacts.get(i).getSortLetter();
            assertTrue("List should be sorted, but " + prev + " > " + curr,
                    prev.compareTo(curr) <= 0);
        }
    }

    @Test
    public void createMockContacts_allFieldsNonEmpty() {
        // When
        List<Contact> contacts = Contact.createMockContacts();

        // Then
        for (Contact contact : contacts) {
            assertFalse(contact.getName().isEmpty());
            assertFalse(contact.getPhoneNumber().isEmpty());
            assertFalse(contact.getAvatarText().isEmpty());
            assertFalse(contact.getSortLetter().isEmpty());
        }
    }

    @Test
    public void createMockContacts_avatarColorIndexInRange() {
        // When
        List<Contact> contacts = Contact.createMockContacts();

        // Then
        for (Contact contact : contacts) {
            int idx = contact.getAvatarColorIndex();
            assertTrue(idx >= 0 && idx < 4);
        }
    }

    @Test
    public void createMockContacts_containsMultipleGroups() {
        // When
        List<Contact> contacts = Contact.createMockContacts();

        // Then
        long groupCount = contacts.stream()
                .map(Contact::getSortLetter)
                .distinct()
                .count();
        assertTrue("Should have multiple groups", groupCount >= 3);
    }
}
