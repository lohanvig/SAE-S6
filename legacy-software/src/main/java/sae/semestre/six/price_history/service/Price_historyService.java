package sae.semestre.six.price_history.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sae.semestre.six.price_history.dao.Price_historyDao;
import sae.semestre.six.price_history.model.PriceHistory;

import java.util.List;

@Service
public class Price_historyService {

    @Autowired
    private Price_historyDao priceHistoryDao;

    public List<PriceHistory> getItemsByInventoryId(Long inventoryId) {
        return priceHistoryDao.findAll().stream()
                .filter(item -> item.getInventoryId().equals(inventoryId))
                .toList();
    }

}
