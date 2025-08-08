package co.com.bancolombia.model.product;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    void builderGettersSettersToBuilderAndToString() {
        Product p1 = Product.builder().name("Pencil").stock(10).branchId(5L).build();
        assertEquals("Pencil", p1.getName());
        assertEquals(10, p1.getStock());
        assertEquals(5L, p1.getBranchId());

        Product p2 = new Product();
        p2.setName("Pen");
        p2.setStock(20);
        p2.setBranchId(6L);
        assertEquals("Pen", p2.getName());
        assertEquals(20, p2.getStock());
        assertEquals(6L, p2.getBranchId());

        Product p3 = p1.toBuilder().name("Eraser").build();
        assertEquals("Eraser", p3.getName());
        assertEquals(10, p3.getStock());
        assertEquals(5L, p3.getBranchId());

        // toString should be non-null and contain the class name typically
        assertNotNull(p1.toString());
    }
}
