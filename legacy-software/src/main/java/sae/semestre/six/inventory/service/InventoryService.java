package sae.semestre.six.inventory.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import sae.semestre.six.inventory.dao.InventoryDao;
import sae.semestre.six.inventory.model.Inventory;
import sae.semestre.six.service.EmailService;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryService {

    @Autowired
    private InventoryDao inventoryDao;

    private final EmailService emailService = EmailService.getInstance();


    public List<Inventory> reorderItems() {
        List<Inventory> lowStockItems = inventoryDao.findNeedingRestock();

        for (Inventory item : lowStockItems) {

            int reorderQuantity = item.getReorderLevel() * 2;


            try (FileWriter fw = new FileWriter("C:\\hospital\\orders.txt", true)) {
                fw.write("REORDER: " + item.getItemCode() + ", Quantity: " + reorderQuantity + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }


            emailService.sendEmail(
                    "supplier@example.com",
                    "Reorder Request",
                    "Please restock " + item.getName() + " (Quantity: " + reorderQuantity + ")"
            );
        }

        return lowStockItems;
    }


    @Transactional
    public void verifyAndUpdatePrice(String itemCode, Double newPrice) {
        if (newPrice == null || newPrice <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero.");
        }

        Inventory item = inventoryDao.findByItemCode(itemCode);
        if (item == null) {
            throw new IllegalArgumentException("Item not found: " + itemCode);
        }

        if (item.getUnitPrice().equals(newPrice)) {
            throw new IllegalArgumentException("New price is the same as the current price.");
        }

        inventoryDao.updatePrice(itemCode, newPrice);
    }

}