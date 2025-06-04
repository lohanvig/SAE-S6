package sae.semestre.six.bill.controller;

import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import sae.semestre.six.bill.dao.BillDaoImpl;
import sae.semestre.six.bill.model.Bill;
import sae.semestre.six.bill.service.BillingService;
import java.util.*;

@RestController
@RequestMapping("/billing")
public class BillingController {

    private Map<String, Double> priceList = new HashMap<>();

    @Autowired
    private BillingService billService; // Injection automatique du service

    // Initialisation de la liste des prix des traitements
    public BillingController() {
        priceList.put("CONSULTATION", 50.0);
        priceList.put("XRAY", 150.0);
        priceList.put("CHIRURGIE", 1000.0);
    }

    @PostMapping("/process")
    public ResponseEntity<?> processBill(
            @RequestParam String patientId,
            @RequestParam String doctorId,
            @RequestParam String[] treatments) {
        try {
            String number = billService.processBill(patientId, doctorId, treatments);
            return ResponseEntity.status(201).body(Map.of("bill_number", number));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @GetMapping("/integrity")
    public ResponseEntity<?> verifyAllBillIntegrity() {
        List<Map<String, Object>> integrityResults = billService.verifyAllBillsIntegrity();
        return ResponseEntity.ok(integrityResults);
    }

    @GetMapping("/prices")
    public Map<String, Double> getPrices() {
        return priceList;
    }

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

    @GetMapping("/revenue")
    public ResponseEntity<?> getTotalRevenue() {
        return ResponseEntity.ok(Map.of("info", "Total Revenue: $" + billService.getTotalRevenue()));
    }

    @GetMapping("/pending")
    public ResponseEntity<?> getPendingBills() {
        try {
            return ResponseEntity.status(200).body(Map.of("bills", billService.getPendingBills()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}
