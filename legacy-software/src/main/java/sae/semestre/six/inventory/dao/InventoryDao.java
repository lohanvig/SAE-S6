package sae.semestre.six.inventory.dao;

import sae.semestre.six.base.dao.GenericDao;
import sae.semestre.six.inventory.model.Inventory;
import java.util.List;

public interface InventoryDao extends GenericDao<Inventory, Long> {
    Inventory findByItemCode(String itemCode);
    List<Inventory> findByQuantityLessThan(Integer quantity);
    List<Inventory> findNeedingRestock();
    void update(Inventory inventory);
    void updateStock(String itemCode, Integer quantity);
    void updatePrice(String itemCode, Double price);
    List<Inventory> findAll();


} 