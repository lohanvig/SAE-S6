package sae.semestre.six.tests.inventory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sae.semestre.six.invoice.model.SupplierInvoice;
import sae.semestre.six.inventory.service.InventoryService;


import static org.junit.jupiter.api.Assertions.*;

class InventoryTest {

    private InventoryService inventoryService;

    @BeforeEach
    void setUp() {
        inventoryService = new InventoryService();
    }

    @Test
    void testVerifyAndUpdatePrice_shouldThrowIfNewPriceIsNullOrInvalid() {
        assertThrows(IllegalArgumentException.class,
                () -> inventoryService.verifyAndUpdatePrice("item1", null));
        assertThrows(IllegalArgumentException.class,
                () -> inventoryService.verifyAndUpdatePrice("item1", -5.0));
        assertThrows(IllegalArgumentException.class,
                () -> inventoryService.verifyAndUpdatePrice("item1", 0.0));
    }

    @Test
    void testUpdateInventory_shouldThrowOnInvalidInvoice() {
        assertThrows(IllegalArgumentException.class, () -> inventoryService.updateInventory(null));
        assertThrows(IllegalArgumentException.class, () -> inventoryService.updateInventory(new SupplierInvoice()));
        SupplierInvoice invoiceWithNullDetails = new SupplierInvoice();
        invoiceWithNullDetails.setDetails(null);
        assertThrows(IllegalArgumentException.class, () -> inventoryService.updateInventory(invoiceWithNullDetails));
    }
}
