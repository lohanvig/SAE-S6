package sae.semestre.six.price_history.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sae.semestre.six.price_history.dao.PriceHistoryDao;
import sae.semestre.six.price_history.model.PriceHistory;
import sae.semestre.six.price_history.service.PriceHistoryService;

import java.util.List;

@RestController
@RequestMapping("/price_history")
public class PriceHistoryController {

    private PriceHistoryDao priceHistoryDao;

    @Autowired
    private PriceHistoryService priceHistoryService;

    @GetMapping("/prices")
    public ResponseEntity<?> getAllItems() {
        try {
            return ResponseEntity.ok(priceHistoryDao.findAll());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error retrieving inventory items: " + e.getMessage());
        }
    }

    @GetMapping("/price/{id}")
    public ResponseEntity<?> getItemById(@PathVariable Long id) {
        try {
            PriceHistory item = priceHistoryDao.findById(id);
            if (item != null) {
                return ResponseEntity.ok(item);
            } else {
                return ResponseEntity.status(404).body("Item not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error retrieving item: " + e.getMessage());
        }
    }

    @GetMapping("/price/inventory/{inventoryId}")
    public ResponseEntity<?> getItemsByInventoryId(@PathVariable Long inventoryId) {
        try {
            List<PriceHistory> items = priceHistoryService.getItemsByInventoryId(inventoryId);
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error retrieving items: " + e.getMessage());
        }
    }
}
