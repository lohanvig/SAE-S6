package sae.semestre.six.price_history.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sae.semestre.six.base.dao.GenericDao;
import sae.semestre.six.price_history.model.PriceHistory;

import java.util.List;

@Repository
public interface PriceHistoryDao extends JpaRepository<PriceHistory, Long> {
}
