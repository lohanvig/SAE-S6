package sae.semestre.six.bill.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import sae.semestre.six.bill.dto.BillRequestDto;
import sae.semestre.six.bill.service.BillingService;
import java.util.*;

/**
 * Contrôleur REST exposant les points d'accès pour gérer les opérations de facturation.
 * Il permet de créer des factures, vérifier leur intégrité, consulter les prix,
 * obtenir le montant d'une facture, le chiffre d'affaires total et les factures en attente.
 */
@RestController
@RequestMapping("/billing")
public class BillingController {

    /**
     * Liste des prix associés aux traitements disponibles.
     */
    private Map<String, Double> priceList = new HashMap<>();

    @Autowired
    private BillingService billService;

    /**
     * Constructeur qui initialise la liste des traitements et leurs prix.
     */
    public BillingController() {
        priceList.put("CONSULTATION", 50.0);
        priceList.put("XRAY", 150.0);
        priceList.put("CHIRURGIE", 1000.0);
    }

    /**
     * Traite une demande de facturation à partir d'un patient, d'un médecin et d'une liste de traitements.
     *
     * @return une réponse HTTP avec le numéro de la facture créée
     */
    @PostMapping("/process")
    public ResponseEntity<?> processBill(@RequestBody BillRequestDto request) {
        try {
            String number = billService.processBill(request.getPatientId(), request.getDoctorNumber(),
                                                    request.getTreatments());
            return ResponseEntity.status(201).body(Map.of("bill_number", number));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    /**
     * Vérifie l'intégrité de toutes les factures enregistrées en comparant leurs empreintes numériques.
     *
     * @return une liste d'objets JSON contenant le numéro de facture et le statut d'intégrité
     */
    @GetMapping("/integrity")
    public ResponseEntity<?> verifyAllBillIntegrity() {
        List<Map<String, Object>> integrityResults = billService.verifyAllBillsIntegrity();
        return ResponseEntity.ok(integrityResults);
    }

    /**
     * Renvoie la liste des traitements disponibles avec leurs prix respectifs.
     *
     * @return une map contenant les traitements et leurs prix
     */
    @GetMapping("/prices")
    public Map<String, Double> getPrices() {
        return priceList;
    }

    /**
     * Retourne le montant total d'une facture spécifique.
     *
     * @param billNumber identifiant unique de la facture
     * @return un objet JSON contenant le numéro et le montant de la facture
     */
    @GetMapping("/prices/{billNumber}")
    public ResponseEntity<?> getBillTotal(@PathVariable String billNumber) {
        try {
            double total = billService.getBillTotal(billNumber);
            Map<String, Object> response = new HashMap<>();
            response.put("billNumber", billNumber);
            response.put("totalAmount", total);
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Calcule et retourne le chiffre d'affaires total généré par les factures enregistrées.
     *
     * @return une réponse HTTP contenant le montant total
     */
    @GetMapping("/revenue")
    public ResponseEntity<?> getTotalRevenue() {
        return ResponseEntity.ok(Map.of("info", "Total Revenue: $" + billService.getTotalRevenue()));
    }

    /**
     * Récupère la liste des factures dont le statut est "PENDING".
     *
     * @return une réponse contenant la liste des factures en attente
     */
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingBills() {
        try {
            return ResponseEntity.status(200).body(Map.of("bills", billService.getPendingBills()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}