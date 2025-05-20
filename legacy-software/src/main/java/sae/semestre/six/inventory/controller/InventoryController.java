package sae.semestre.six.inventory.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sae.semestre.six.inventory.dao.InventoryDao;
import sae.semestre.six.inventory.model.Inventory;
import sae.semestre.six.inventory.service.InventoryService;
import sae.semestre.six.invoice.model.SupplierInvoice;
import sae.semestre.six.invoice.model.SupplierInvoiceDetail;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private InventoryDao inventoryDao;

    @Autowired
    private InventoryService inventoryService;

    @GetMapping("/items")
    public ResponseEntity<?> getAllItems() {
        try {
            List<Inventory> items = inventoryDao.findAll();
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error retrieving inventory items: " + e.getMessage());
        }
    }

    @GetMapping("/item/{id}")
    public ResponseEntity<?> getItemById(@PathVariable Long id) {
        try {
            Inventory item = inventoryDao.findById(id);
            if (item != null) {
                return ResponseEntity.ok(item);
            } else {
                return ResponseEntity.status(404).body("Item not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error retrieving item: " + e.getMessage());
        }
    }

    @PostMapping("/supplier-invoice")
    public ResponseEntity<?> processSupplierInvoice(@RequestBody SupplierInvoice invoice) {
        try {
            for (SupplierInvoiceDetail detail : invoice.getDetails()) {
                Inventory inventory = detail.getInventory();

                inventory.setQuantity(inventory.getQuantity() + detail.getQuantity());
                inventory.setUnitPrice(detail.getUnitPrice());
                inventory.setLastRestocked(new Date());

                inventoryDao.update(inventory);
            }
            return ResponseEntity.ok("Supplier invoice processed successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error processing supplier invoice: " + e.getMessage());
        }
    }

    @GetMapping("/low-stock")
    public ResponseEntity<?> getLowStockItemsID() {
        try {
            List<Inventory> lowStockItems = inventoryDao.findAll().stream()
                    .filter(Inventory::needsRestock)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(lowStockItems);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error retrieving low stock items: " + e.getMessage());
        }
    }

    @PostMapping("/reorder")
    public ResponseEntity<?> reorderItems() {
        try {
            int reorderedCount = inventoryService.reorderItems().size();
            return ResponseEntity.ok("Reorder requests sent for " + reorderedCount + " items");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error during reorder process: " + e.getMessage());
        }
    }

    @PutMapping("/change-price/{itemCode}")
    public ResponseEntity<?> changePriceItems(@PathVariable String itemCode, @RequestParam Double price) {
        try {
            inventoryService.verifyAndUpdatePrice(itemCode, price);
            return ResponseEntity.ok("Price updated successfully for item: " + itemCode);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating price: " + e.getMessage());
        }
    }
}
