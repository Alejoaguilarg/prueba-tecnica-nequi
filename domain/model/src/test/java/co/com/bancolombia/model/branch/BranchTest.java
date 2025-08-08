package co.com.bancolombia.model.branch;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BranchTest {

    @Test
    void builderGettersSettersToBuilderAndToString() {
        Branch b1 = Branch.builder().branchId(1L).name("B1").franchiseId(2L).build();
        assertEquals(1L, b1.getBranchId());
        assertEquals("B1", b1.getName());
        assertEquals(2L, b1.getFranchiseId());

        Branch b2 = new Branch();
        b2.setBranchId(3L);
        b2.setName("B2");
        b2.setFranchiseId(4L);
        assertEquals(3L, b2.getBranchId());
        assertEquals("B2", b2.getName());
        assertEquals(4L, b2.getFranchiseId());

        Branch b3 = b1.toBuilder().name("B1-new").build();
        assertEquals(1L, b3.getBranchId());
        assertEquals("B1-new", b3.getName());
        assertEquals(2L, b3.getFranchiseId());

        Branch b4 = new Branch(5L, "B5", 6L);
        assertEquals(5L, b4.getBranchId());
        assertEquals("B5", b4.getName());
        assertEquals(6L, b4.getFranchiseId());

        assertNotNull(b1.toString());
    }
}
