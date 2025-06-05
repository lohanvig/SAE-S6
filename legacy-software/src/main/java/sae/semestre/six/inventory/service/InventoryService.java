package sae.semestre.six.inventory.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import sae.semestre.six.inventory.dao.InventoryDao;
import sae.semestre.six.inventory.model.Inventory;
import sae.semestre.six.invoice.model.SupplierInvoice;
import sae.semestre.six.invoice.model.SupplierInvoiceDetail;
import sae.semestre.six.service.EmailService;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service de gestion des stocks.
 *
 * Cette classe fournit des méthodes permettant de :
 * - Réapprovisionner les articles en rupture,
 * - Vérifier et mettre à jour les prix des articles,
 * - Mettre à jour le stock à partir de factures fournisseurs,
 * - Identifier les articles à faible stock,
 * - Diminuer le stock suite à une consommation.
 */
@Service
public class InventoryService {

    @Autowired
    private InventoryDao inventoryDao;

    private final EmailService emailService = EmailService.getInstance();


    /**
     * Réapprovisionne les articles nécessitant un restockage.
     *
     * Cette méthode identifie les articles dont le stock est inférieur au seuil de commande,
     * enregistre une commande dans un fichier texte, et envoie un email de demande de réapprovisionnement.
     *
     *
     * @return la liste des articles nécessitant un réapprovisionnement
     */

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


    /**
     * Vérifie et met à jour le prix d’un article.
     *
     * La méthode lève une exception si le prix est invalide, si l'article n’existe pas,
     * ou si le nouveau prix est identique à l'ancien.
     *
     * @param itemCode le code de l’article à mettre à jour
     * @param newPrice le nouveau prix à appliquer
     * @throws IllegalArgumentException si les données sont invalides
     */
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

    /**
     * Met à jour les stocks en fonction d'une facture fournisseur.
     *
     * Pour chaque article contenu dans la facture, cette méthode met à jour la quantité,
     * le prix unitaire et la date de dernier approvisionnement.
     *
     *
     * @param invoice la facture fournisseur contenant les détails de livraison
     * @throws IllegalArgumentException si la facture est invalide
     */
    public void updateInventory(SupplierInvoice invoice) {
        if (invoice == null || invoice.getDetails() == null || invoice.getDetails().isEmpty()) {
            throw new IllegalArgumentException("Invalid invoice data.");
        }
        for (SupplierInvoiceDetail detail : invoice.getDetails()) {
            Inventory inventory = detail.getInventory();

            inventory.setQuantity(inventory.getQuantity() + detail.getQuantity());
            inventory.setUnitPrice(detail.getUnitPrice());
            inventory.setLastRestocked(new Date());

            inventoryDao.update(inventory);
        }
    }

    /**
     * Retourne la liste des articles dont le stock est inférieur au seuil défini.
     *
     * @return une liste d'articles en rupture ou proche de l'être
     */
    public List<Inventory> getLowStockItems() {
        return inventoryDao.findAll().stream()
                .filter(Inventory::needsRestock)
                .collect(Collectors.toList());
    }

    /**
     * Diminue le stock d’un article.
     *
     * Cette méthode réduit le stock disponible d’un article donné, en fonction d’une quantité
     * consommée. Elle lève une exception si l’article n’existe pas ou si le stock est insuffisant.
     *
     * @param itemCode le code de l’article concerné
     * @param quantity la quantité à soustraire
     * @return le prix unitaire de l’article
     * @throws IllegalArgumentException si les données sont invalides
     */
    public Double decreaseStock(String itemCode, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }

        Inventory item = inventoryDao.findByItemCode(itemCode);
        if (item == null) {
            throw new IllegalArgumentException("Item not found: " + itemCode);
        }

        if (item.getQuantity() < quantity) {
            throw new IllegalArgumentException("Insufficient stock for item: " + itemCode);
        }

        inventoryDao.updateStock(itemCode, item.getQuantity() - quantity);
        return item.getUnitPrice();
    }

}