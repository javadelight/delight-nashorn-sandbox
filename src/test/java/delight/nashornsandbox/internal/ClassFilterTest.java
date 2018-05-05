package delight.nashornsandbox.internal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class ClassFilterTest {

    SandboxClassFilter classFilter;
    Set<Class<?>> allowed;
    Set<String> stringCache;

    @BeforeEach
    void beforeEach() {
        allowed = spy(new HashSet<Class<?>>());
        stringCache = spy(new HashSet<String>());
        classFilter = new SandboxClassFilter(allowed, stringCache);
        classFilter.add(String.class);

    }

    @Nested
    @DisplayName("Can add classes")
    class addClasses {
        @Test
        @DisplayName("Contains the given class")
        void containsClass() {
            assertTrue(allowed.contains(String.class));
            verify(allowed, times(1)).add(String.class);
        }

        @Test
        @DisplayName("Contains the given class's name")
        void containsName() {
            assertTrue(stringCache.contains(String.class.getName()));
            verify(stringCache, times(1)).add(String.class.getName());
        }
    }

   @Nested
    @DisplayName("Can remove classes")
    class removeClasses {
        @BeforeEach
        void beforeEach()
        {
            classFilter.remove(String.class);
        }
        @Test
        @DisplayName("No longer contains the given class")
        void containsClass() {
            assertFalse(allowed.contains(String.class));
            verify(allowed, times(1)).remove(String.class);
        }

        @Test
        @DisplayName("No longer contains the given class's name")
        void containsName() {
            assertFalse(stringCache.contains(String.class.getName()));
            verify(stringCache, times(1)).remove(String.class.getName());
        }
    }

    @Nested
    @DisplayName("Can clear classes")
    class clearClasses {
        @BeforeEach
        void beforeEach()
        {
            classFilter.clear();
        }
        @Test
        @DisplayName("No longer contains the given class")
        void containsClass() {
            assertEquals(0,allowed.size());
            verify(allowed, times(1)).clear();
        }

        @Test
        @DisplayName("No longer contains the given class's name")
        void containsName() {
            assertEquals(0,stringCache.size());
            verify(stringCache, times(1)).clear();
        }
    }



    @Nested
    @DisplayName("Exposes to scripts properly")
    class expose {

        @Test
        @DisplayName("Exposes registered Class")
        void containsClass()
        {
            assertTrue(classFilter.exposeToScripts(String.class.getName()));
        }

        @Test
        @DisplayName("Does not exposes unknown class")
        void notContainsClass()
        {
            assertFalse(classFilter.exposeToScripts(ArrayList.class.getName()));
        }
    }

}
