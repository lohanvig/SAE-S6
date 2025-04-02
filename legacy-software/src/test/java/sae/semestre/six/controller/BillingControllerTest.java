package sae.semestre.six.controller;

import org.junit.Test;
import sae.semestre.six.bill.controller.BillingController;

import static org.junit.Assert.*;
import java.io.File;

public class BillingControllerTest {
    
    
    private BillingController billingController = BillingController.getInstance();
    
    @Test
    public void testProcessBill() {
        
        File billingFile = new File("C:\\hospital\\billing.txt");
        long initialFileSize = billingFile.length();
        
        String result = billingController.processBill(
            "TEST001",
            "DOC001",
            new String[]{"CONSULTATION"}
        );
        
        
        assertTrue(result.contains("successfully"));
        assertTrue(billingFile.length() > initialFileSize);
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