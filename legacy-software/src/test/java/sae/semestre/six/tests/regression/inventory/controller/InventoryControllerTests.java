package sae.semestre.six.tests.regression.inventory.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import sae.semestre.six.inventory.controller.InventoryController;
import sae.semestre.six.inventory.dao.InventoryDao;
import sae.semestre.six.inventory.model.Inventory;
import sae.semestre.six.invoice.model.SupplierInvoice;
import sae.semestre.six.invoice.model.SupplierInvoiceDetail;
import sae.semestre.six.service.EmailService;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Classe de tests de non-régression pour InventoryController.
 * Ne teste pas l'envoi de mails.
 */
class InventoryControllerTests {

    @InjectMocks
    private InventoryController controller;

    @Mock
    private InventoryDao inventoryDao;

    @Mock
    private EmailService emailService; // Non testé ici

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        //controller.inventoryDao = inventoryDao;
    }

    /**
     * Vérifie qu'une facture fournisseur met à jour correctement l'inventaire.
     */
    @Test
    void traiteFactureFournisseurAvecSucces() {
        Inventory inventaire = new Inventory();
        inventaire.setQuantity(10);

        SupplierInvoiceDetail detail = new SupplierInvoiceDetail();
        detail.setQuantity(5);
        detail.setUnitPrice(2.5);
        detail.setInventory(inventaire);

        SupplierInvoice facture = new SupplierInvoice();
        //facture.setDetails(List.of(detail));

        String resultat = controller.processSupplierInvoice(facture);

        assertEquals("Supplier invoice processed successfully", resultat);
        assertEquals(15, inventaire.getQuantity());
        assertEquals(2.5, inventaire.getUnitPrice());
        verify(inventoryDao).update(inventaire);
    }

    /**
     * Vérifie qu'une exception pendant le traitement renvoie un message d'erreur.
     */
    @Test
    void retourneErreurLorsDeFactureInvalide() {
        SupplierInvoiceDetail detail = mock(SupplierInvoiceDetail.class);
        when(detail.getInventory()).thenThrow(new RuntimeException("Erreur simulée"));

        SupplierInvoice facture = new SupplierInvoice();
        //facture.setDetails(List.of(detail));

        String resultat = controller.processSupplierInvoice(facture);

        assertTrue(resultat.startsWith("Error:"));
    }

    /**
     * Vérifie que seuls les articles à faible stock sont retournés.
     */
    @Test
    void retourneArticlesFaibleStock() {
        Inventory stock1 = mock(Inventory.class);
        Inventory stock2 = mock(Inventory.class);

        when(stock1.needsRestock()).thenReturn(true);
        when(stock2.needsRestock()).thenReturn(false);
        when(inventoryDao.findAll()).thenReturn(List.of(stock1, stock2));

        List<Inventory> resultat = controller.getLowStockItems();

        assertEquals(1, resultat.size());
        assertTrue(resultat.contains(stock1));
    }

    /**
     * Vérifie qu'un fichier de commande est généré lors du réapprovisionnement.
     * Ne vérifie pas l’envoi d’emails.
     */
    @Test
    void genereCommandesPourArticlesFaibleStock() throws IOException {
        // Préparation du fichier cible
        String chemin = "C:\\hospital\\orders.txt";
        Path path = Paths.get(chemin);
        Files.deleteIfExists(path);
        Files.createDirectories(path.getParent());

        Inventory article = new Inventory();
        article.setItemCode("ITEM001");
        article.setName("Gants");
        article.setReorderLevel(10);

        when(inventoryDao.findNeedingRestock()).thenReturn(List.of(article));

        String resultat = controller.reorderItems();

        assertTrue(resultat.contains("1 items"));

        String contenu = Files.readString(path);
        assertTrue(contenu.contains("REORDER: ITEM001"));
        assertTrue(contenu.contains("Quantity: 20")); // 10 * 2
    }
}
