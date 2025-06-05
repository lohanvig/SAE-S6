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
import java.util.Optional;

/**
 * Contrôleur permettant de consulter l’historique des prix des articles en stock.
 */
@RestController
@RequestMapping("/price_history")
public class PriceHistoryController {

    @Autowired
    private PriceHistoryDao priceHistoryDao;

    @Autowired
    private PriceHistoryService priceHistoryService;

    /**
     * Récupère tous les enregistrements de prix disponibles.
     *
     * @return {@link ResponseEntity} contenant une {@link List} de {@link PriceHistory}
     * ou un message d'erreur en cas d'échec
     */
    @GetMapping("/prices")
    public ResponseEntity<?> getAllItems() {
        try {
            return ResponseEntity.ok(priceHistoryDao.findAll());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error retrieving inventory items: " + e.getMessage());
        }
    }

    /**
     * Récupère un enregistrement de prix en fonction de son identifiant unique.
     *
     * @param id identifiant de l'historique de prix à rechercher
     * @return {@link ResponseEntity} contenant un objet {@link PriceHistory} si trouvé,
     * ou un message d'erreur 404 si l'élément n’existe pas
     */
    @GetMapping("/price/{id}")
    public ResponseEntity<?> getItemById(@PathVariable Long id) {
        try {
            Optional<PriceHistory> item = priceHistoryDao.findById(id);
            if (item.isPresent()) {
                return ResponseEntity.ok(item);
            } else {
                return ResponseEntity.status(404).body("Item not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error retrieving item: " + e.getMessage());
        }
    }

    /**
     * Récupère tous les enregistrements de prix associés à un article d'inventaire donné.
     *
     * @param inventoryId identifiant de l'article d'inventaire
     * @return {@link ResponseEntity} contenant une {@link List} de {@link PriceHistory}
     * ou un message d'erreur en cas d'échec
     */
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
