package sae.semestre.six.tests.regression.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import sae.semestre.six.bill.controller.BillingController;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BillingControllerTest {
    
    @Autowired
    private BillingController billingController;

    @Test
    public void testProcessBill() {
        // Appel de la méthode processBill et récupération de la réponse
        ResponseEntity<?> responseEntity = billingController.processBill(
                "PT001",  // patientId
                "DR001",   // doctorId
                new String[]{"CONSULTATION"}  // traitements
        );

        assertEquals(201, responseEntity.getStatusCodeValue(), "Le statut de la réponse doit être 201.");

        String id = (String) responseEntity.getBody();
        assertNotNull(id, "La réponse ne doit pas être nulle.");
        assertTrue(id.startsWith("BILL"), "Le numéro de facture doit commencer par 'BILL'.");
    }

    @Test
    public void testCalculateInsurance() {
        
        double result = Double.parseDouble(
            billingController.calculateInsurance(1000.0)
                .replace("Insurance coverage: $", "")
        );
        
        
        assertEquals(700.0, result, 0.01);
    }
    
    
    @Test
    public void testUpdatePrice() {
        billingController.updatePrice("CONSULTATION", 75.0);
        assertEquals(75.0, billingController.getPrices().get("CONSULTATION"), 0.01);
    }
} 