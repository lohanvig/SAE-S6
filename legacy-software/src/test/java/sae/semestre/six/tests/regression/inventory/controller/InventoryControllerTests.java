package sae.semestre.six.tests.regression.inventory.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import sae.semestre.six.inventory.controller.InventoryController;
import sae.semestre.six.inventory.dao.InventoryDao;
import sae.semestre.six.inventory.model.Inventory;
import sae.semestre.six.invoice.model.SupplierInvoice;
import sae.semestre.six.invoice.model.SupplierInvoiceDetail;
import sae.semestre.six.inventory.service.InventoryService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InventoryControllerTests {

    @InjectMocks
    private InventoryController controller;

    @Mock
    private InventoryDao inventoryDao;

    @Mock
    private InventoryService inventoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void traiteFactureFournisseurAvecSucces() {
        Inventory inventaire = new Inventory();
        inventaire.setQuantity(10);

        SupplierInvoiceDetail detail = new SupplierInvoiceDetail();
        detail.setQuantity(5);
        detail.setUnitPrice(2.5);
        detail.setInventory(inventaire);

        SupplierInvoice facture = new SupplierInvoice();
        facture.setDetails(new HashSet<>(List.of(detail)));

        ResponseEntity<?> resultat = controller.processSupplierInvoice(facture);

        assertEquals(200, resultat.getStatusCodeValue());
        assertEquals("Supplier invoice processed successfully", resultat.getBody());
        assertEquals(15, inventaire.getQuantity());
        assertEquals(2.5, inventaire.getUnitPrice());
        verify(inventoryDao).update(inventaire);
    }

    @Test
    void retourneErreurLorsDeFactureInvalide() {
        SupplierInvoiceDetail detail = mock(SupplierInvoiceDetail.class);
        when(detail.getInventory()).thenThrow(new RuntimeException("Erreur simulée"));

        SupplierInvoice facture = new SupplierInvoice();
        facture.setDetails(new HashSet<>(List.of(detail)));

        ResponseEntity<?> resultat = controller.processSupplierInvoice(facture);

        assertEquals(500, resultat.getStatusCodeValue());
        assertTrue(resultat.getBody().toString().startsWith("Error processing supplier invoice:"));
    }

    @Test
    void retourneArticlesFaibleStock() {
        Inventory stock1 = mock(Inventory.class);
        Inventory stock2 = mock(Inventory.class);

        when(stock1.needsRestock()).thenReturn(true);
        when(stock2.needsRestock()).thenReturn(false);
        when(inventoryDao.findAll()).thenReturn(List.of(stock1, stock2));

        ResponseEntity<?> resultat = controller.getLowStockItemsID();

        assertEquals(200, resultat.getStatusCodeValue());

        List<Inventory> resultBody = (List<Inventory>) resultat.getBody();
        assertEquals(1, resultBody.size());
        assertTrue(resultBody.contains(stock1));
    }

    @Test
    void genereCommandesPourArticlesFaibleStock() {
        Inventory article = new Inventory();
        article.setItemCode("ITEM001");
        article.setName("Gants");
        article.setReorderLevel(10);

        when(inventoryService.reorderItems()).thenReturn(List.of(article));

        ResponseEntity<?> resultat = controller.reorderItems();

        assertEquals(200, resultat.getStatusCodeValue());
        assertTrue(resultat.getBody().toString().contains("1 items"));
    }

    @Test
    void retourneItemParIdPresent() {
        Inventory article = new Inventory();
        article.setId(1L);
        when(inventoryDao.findById(1L)).thenReturn(article);

        ResponseEntity<?> response = controller.getItemById(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(article, response.getBody());
    }

    @Test
    void retourneItemParIdAbsent() {
        when(inventoryDao.findById(999L)).thenReturn(null);

        ResponseEntity<?> response = controller.getItemById(999L);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Item not found", response.getBody());
    }

    @Test
    void retourneErreurSiExceptionDansGetAllItems() {
        when(inventoryDao.findAll()).thenThrow(new RuntimeException("Erreur simulée"));

        ResponseEntity<?> response = controller.getAllItems();

        assertEquals(500, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().startsWith("Error retrieving inventory items:"));
    }

    @Test
    void retourneErreurSiExceptionDansGetItemById() {
        when(inventoryDao.findById(1L)).thenThrow(new RuntimeException("Erreur simulée"));

        ResponseEntity<?> response = controller.getItemById(1L);

        assertEquals(500, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().startsWith("Error retrieving item:"));
    }

    @Test
    void retourneErreurSiExceptionDansLowStock() {
        when(inventoryDao.findAll()).thenThrow(new RuntimeException("Erreur simulée"));

        ResponseEntity<?> response = controller.getLowStockItemsID();

        assertEquals(500, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().startsWith("Error retrieving low stock items:"));
    }

    @Test
    void retourneErreurSiExceptionDansReorder() {
        when(inventoryService.reorderItems()).thenThrow(new RuntimeException("Erreur simulée"));

        ResponseEntity<?> response = controller.reorderItems();

        assertEquals(500, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().startsWith("Error during reorder process:"));
    }
}
