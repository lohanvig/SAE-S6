package sae.semestre.six.price_history.dao;

import sae.semestre.six.base.dao.GenericDao;
import sae.semestre.six.price_history.model.PriceHistory;

import java.util.List;


public interface Price_historyDao extends GenericDao<PriceHistory, Long> {
    PriceHistory findById(Long itemCode);

    List<PriceHistory> findAll();
}
