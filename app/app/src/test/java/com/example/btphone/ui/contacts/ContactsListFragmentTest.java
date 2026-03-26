package com.example.btphone.ui.contacts;

import android.os.Bundle;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btphone.R;
import com.example.btphone.adapter.ContactsAdapter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * {@link ContactsListFragment} 的单元测试。
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class ContactsListFragmentTest {

    @Test
    public void newInstance_returnsNonNull() {
        // When
        ContactsListFragment fragment = ContactsListFragment.newInstance();

        // Then
        assertNotNull(fragment);
    }

    @Test
    public void onViewCreated_adapterIsSet() {
        // Given / When
        FragmentScenario<ContactsListFragment> scenario =
                FragmentScenario.launchInContainer(ContactsListFragment.class);

        scenario.onFragment(fragment -> {
            // Then
            assertNotNull(fragment.getAdapter());
        });
    }

    @Test
    public void onViewCreated_recyclerViewHasLayoutManager() {
        // Given / When
        FragmentScenario<ContactsListFragment> scenario =
                FragmentScenario.launchInContainer(ContactsListFragment.class);

        scenario.onFragment(fragment -> {
            // Then
            assertNotNull(fragment.getViewBinding());
            RecyclerView rv = fragment.getViewBinding().rvContacts;
            assertNotNull(rv.getLayoutManager());
        });
    }

    @Test
    public void onViewCreated_mockDataLoaded() {
        // Given / When
        FragmentScenario<ContactsListFragment> scenario =
                FragmentScenario.launchInContainer(ContactsListFragment.class);

        scenario.onFragment(fragment -> {
            // Then
            ContactsAdapter adapter = fragment.getAdapter();
            assertNotNull(adapter);
            assertTrue("Mock data should produce items", adapter.getItemCount() > 0);
        });
    }

    @Test
    public void onViewCreated_letterIndexViewExists() {
        // Given / When
        FragmentScenario<ContactsListFragment> scenario =
                FragmentScenario.launchInContainer(ContactsListFragment.class);

        scenario.onFragment(fragment -> {
            // Then
            assertNotNull(fragment.getViewBinding());
            assertNotNull(fragment.getViewBinding().letterIndexView);
        });
    }

    @Test
    public void scrollToLetter_existingLetter_noException() {
        // Given / When
        FragmentScenario<ContactsListFragment> scenario =
                FragmentScenario.launchInContainer(ContactsListFragment.class);

        scenario.onFragment(fragment -> {
            // Then — should not throw
            fragment.scrollToLetter("C");
        });
    }

    @Test
    public void scrollToLetter_nonExistingLetter_noException() {
        // Given / When
        FragmentScenario<ContactsListFragment> scenario =
                FragmentScenario.launchInContainer(ContactsListFragment.class);

        scenario.onFragment(fragment -> {
            // Then — should not throw for non-existing letter
            fragment.scrollToLetter("Q");
        });
    }

    @Test
    public void onDestroyView_bindingNulled() {
        // Given
        FragmentScenario<ContactsListFragment> scenario =
                FragmentScenario.launchInContainer(ContactsListFragment.class);

        // Verify binding exists before destroy
        scenario.onFragment(fragment -> {
            assertNotNull(fragment.getViewBinding());
        });

        // When — move to CREATED state triggers onDestroyView (view destroyed, fragment alive)
        scenario.moveToState(Lifecycle.State.CREATED);

        // Then — binding should be nulled after view is destroyed
        scenario.onFragment(fragment -> {
            assertNull(fragment.getViewBinding());
        });
    }

    @Test
    public void adapter_containsHeaders() {
        // Given / When
        FragmentScenario<ContactsListFragment> scenario =
                FragmentScenario.launchInContainer(ContactsListFragment.class);

        scenario.onFragment(fragment -> {
            // Then
            ContactsAdapter adapter = fragment.getAdapter();
            assertNotNull(adapter);
            boolean hasHeader = false;
            for (int i = 0; i < adapter.getItemCount(); i++) {
                if (adapter.getItemViewType(i) == ContactsAdapter.TYPE_HEADER) {
                    hasHeader = true;
                    break;
                }
            }
            assertTrue("Should have at least one header", hasHeader);
        });
    }

    @Test
    public void adapter_containsContacts() {
        // Given / When
        FragmentScenario<ContactsListFragment> scenario =
                FragmentScenario.launchInContainer(ContactsListFragment.class);

        scenario.onFragment(fragment -> {
            // Then
            ContactsAdapter adapter = fragment.getAdapter();
            assertNotNull(adapter);
            boolean hasContact = false;
            for (int i = 0; i < adapter.getItemCount(); i++) {
                if (adapter.getItemViewType(i) == ContactsAdapter.TYPE_CONTACT) {
                    hasContact = true;
                    break;
                }
            }
            assertTrue("Should have at least one contact", hasContact);
        });
    }

    @Test
    public void adapter_letterPositionMap_hasExpectedLetters() {
        // Given / When
        FragmentScenario<ContactsListFragment> scenario =
                FragmentScenario.launchInContainer(ContactsListFragment.class);

        scenario.onFragment(fragment -> {
            // Then
            ContactsAdapter adapter = fragment.getAdapter();
            assertNotNull(adapter);
            assertTrue(adapter.getLetterPositionMap().containsKey("C"));
            assertTrue(adapter.getLetterPositionMap().containsKey("D"));
            assertTrue(adapter.getLetterPositionMap().containsKey("F"));
        });
    }

    @Test
    public void recyclerView_hasItemDecoration() {
        // Given / When
        FragmentScenario<ContactsListFragment> scenario =
                FragmentScenario.launchInContainer(ContactsListFragment.class);

        scenario.onFragment(fragment -> {
            // Then
            RecyclerView rv = fragment.getViewBinding().rvContacts;
            assertTrue("RecyclerView should have item decoration",
                    rv.getItemDecorationCount() > 0);
        });
    }
}
