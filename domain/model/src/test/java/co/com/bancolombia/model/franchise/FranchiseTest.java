package co.com.bancolombia.model.franchise;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FranchiseTest {

    @Test
    void builderAndGettersSettersAndToBuilderWork() {
        Franchise f1 = Franchise.builder().franchiseId(10L).name("ACME").build();
        assertEquals(10L, f1.getFranchiseId());
        assertEquals("ACME", f1.getName());

        Franchise f2 = new Franchise();
        f2.setFranchiseId(20L);
        f2.setName("BRAND");
        assertEquals(20L, f2.getFranchiseId());
        assertEquals("BRAND", f2.getName());

        Franchise f3 = f1.toBuilder().name("NEW").build();
        assertEquals(10L, f3.getFranchiseId());
        assertEquals("NEW", f3.getName());

        Franchise f4 = new Franchise(30L, "XYZ");
        assertEquals(30L, f4.getFranchiseId());
        assertEquals("XYZ", f4.getName());

        // basic equals/hashCode/consistency check via toString non-null
        assertNotNull(f1.toString());
    }
}
