package sae.semestre.six.bill.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import sae.semestre.six.bill.service.BillingService;
import java.util.*;

@RestController
@RequestMapping("/billing")
public class BillingController {

    private Map<String, Double> priceList = new HashMap<>();
    private double totalRevenue = 0.0;
    private List<String> pendingBills = new ArrayList<>();

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

    @PutMapping("/price")
    public String updatePrice(
            @RequestParam String treatment,
            @RequestParam double price) {
        priceList.put(treatment, price);
        recalculateAllPendingBills();
        return "Price updated";
    }

    private void recalculateAllPendingBills() {
        for (String billId : pendingBills) {
            processBill(billId, "RECALC", new String[]{"CONSULTATION"});
        }
    }

    @GetMapping("/prices")
    public Map<String, Double> getPrices() {
        return priceList;
    }

    @GetMapping("/insurance")
    public String calculateInsurance(@RequestParam double amount) {
        double coverage = amount;
        return "Insurance coverage: $" + coverage;
    }

    @GetMapping("/revenue")
    public String getTotalRevenue() {
        return "Total Revenue: $" + totalRevenue;
    }

    @GetMapping("/pending")
    public List<String> getPendingBills() {
        return pendingBills;
    }
}
