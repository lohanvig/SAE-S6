package sae.semestre.six.tests.bill;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import sae.semestre.six.bill.dao.BillDaoImpl;
import sae.semestre.six.bill.model.Bill;
import sae.semestre.six.bill.model.BillDetail;
import sae.semestre.six.bill.service.BillingService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

class BillingTest {

    @InjectMocks
    private BillingService billingService;

    @Mock
    private BillDaoImpl billDao;


    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(billingService, "adminEmail", "admin@clinic.com");
    }

    @Test
    void testGetTotalRevenueNonNull() {
        when(billDao.getTotalRevenue()).thenReturn(1000.0);
        assertEquals(1000.0, billingService.getTotalRevenue());
    }

    @Test
    void testGetTotalRevenueNull() {
        when(billDao.getTotalRevenue()).thenReturn(null);
        assertEquals(0.0, billingService.getTotalRevenue());
    }

    @Test
    void testGetPendingBills() {
        Bill bill1 = mock(Bill.class);
        when(billDao.findByStatus("PENDING")).thenReturn(List.of(bill1));

        List<Bill> result = billingService.getPendingBills();
        assertEquals(1, result.size());
    }

    @Test
    void testVerifyBillIntegrityValid() {
        Bill bill = mock(Bill.class);
        when(bill.getBillNumber()).thenReturn("B001");
        when(bill.getHash()).thenReturn("abc123");
        when(bill.recalculateHash()).thenReturn("abc123");
        when(billDao.findAll()).thenReturn(List.of(bill));

        List<Map<String, Object>> results = billingService.verifyAllBillsIntegrity();
        assertTrue((Boolean) results.get(0).get("isValid"));
    }

    @Test
    void testVerifyBillIntegrityInvalid() {
        Bill bill = mock(Bill.class);
        when(bill.getBillNumber()).thenReturn("B002");
        when(bill.getHash()).thenReturn("wrong");
        when(bill.recalculateHash()).thenReturn("correct");
        when(billDao.findAll()).thenReturn(List.of(bill));

        List<Map<String, Object>> results = billingService.verifyAllBillsIntegrity();
        assertFalse((Boolean) results.get(0).get("isValid"));
    }

    @Test
    void testGetBillTotalOK() {
        Bill bill = mock(Bill.class);
        when(billDao.findByBillNumber("B100")).thenReturn(bill);
        when(bill.getTotalAmount()).thenReturn(500.0);

        double total = billingService.getBillTotal("B100");
        assertEquals(500.0, total);
    }

    @Test
    void testGetBillTotalNotFound() {
        when(billDao.findByBillNumber("X404")).thenThrow(new NoSuchElementException());
        assertThrows(NoSuchElementException.class, () -> billingService.getBillTotal("X404"));
    }

    @Test
    void testAddBillDetailsWithNoDiscount() {
        Bill bill = new Bill();
        double price = 100.0;
        float discountAmount = 1000.0f; // seuil jamais atteint
        float discountRate = 0.9f; // 10% de remise

        bill.addBillDetails("Consultation", price, discountAmount, discountRate);

        assertEquals(100.0, bill.getTotalAmount());
        assertEquals(1, bill.getBillDetails().size());
        BillDetail detail = bill.getBillDetails().iterator().next();
        assertEquals("Consultation", detail.getTreatmentName());
        assertEquals(100.0, detail.getUnitPrice());
        assertEquals(1, detail.getQuantity());
        assertEquals(100.0, detail.getLineTotal());
    }

    @Test
    void testAddBillDetailsWithDiscountApplied() {
        Bill bill = new Bill();
        double price = 1500.0;
        float discountAmount = 1000.0f;
        float discountRate = 0.8f; // 20% de remise

        bill.addBillDetails("Surgery", price, discountAmount, discountRate);

        assertEquals(1200.0, bill.getTotalAmount(), 0.1);
        assertEquals(1, bill.getBillDetails().size());
    }

    @Test
    void testBillLockThrowsIfAlreadyLocked() {
        Bill bill = new Bill();
        bill.setHash("someHash");

        Exception exception = assertThrows(IllegalArgumentException.class, bill::lock);
        assertEquals("Cette facture est déjà vérouillée.", exception.getMessage());
    }

    @Test
    void testBillLockGeneratesHash() {
        Bill bill = new Bill();
        bill.setBillNumber("B500");
        bill.setTotalAmount(100.0);
        bill.setStatus("PENDING");

        // Supposons que patient et doctor soient null ici
        bill.lock();

        assertNotNull(bill.getHash());
    }
}
