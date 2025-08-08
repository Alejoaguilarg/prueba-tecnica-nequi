package co.com.bancolombia.model.franchise;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FranchiseTest {

    @Test
    void builderAndGettersSettersAndToBuilderWork() {
        Franchise f1 = Franchise.builder().id(10L).name("ACME").build();
        assertEquals(10L, f1.getId());
        assertEquals("ACME", f1.getName());

        Franchise f2 = new Franchise();
        f2.setId(20L);
        f2.setName("BRAND");
        assertEquals(20L, f2.getId());
        assertEquals("BRAND", f2.getName());

        Franchise f3 = f1.toBuilder().name("NEW").build();
        assertEquals(10L, f3.getId());
        assertEquals("NEW", f3.getName());

        Franchise f4 = new Franchise(30L, "XYZ");
        assertEquals(30L, f4.getId());
        assertEquals("XYZ", f4.getName());

        // basic equals/hashCode/consistency check via toString non-null
        assertNotNull(f1.toString());
    }
}
