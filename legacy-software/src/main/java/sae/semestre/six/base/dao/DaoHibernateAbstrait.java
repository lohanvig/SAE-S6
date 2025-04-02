package sae.semestre.six.base.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

public abstract class DaoHibernateAbstrait<T, ID extends Serializable> implements GenericDao<T, ID> {
    
    private final Class<T> classeEntite;
    
    @PersistenceContext
    private EntityManager gestionnaireEntite;
    
    @SuppressWarnings("unchecked")
    public DaoHibernateAbstrait() {
        this.classeEntite = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }
    
    protected EntityManager getGestionnaireEntite() {
        return gestionnaireEntite;
    }
    
    @Override
    public T findById(ID id) {
        try {
            return gestionnaireEntite.find(classeEntite, id);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<T> findAll() {
        try {
            return gestionnaireEntite.createQuery("from " + classeEntite.getName())
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    @Override
    public void save(T entite) {
        try {
            gestionnaireEntite.persist(entite);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    @Override
    public void update(T entite) {
        try {
            gestionnaireEntite.merge(entite);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    @Override
    public void delete(T entite) {
        try {
            gestionnaireEntite.remove(entite);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    @Override
    public void deleteById(ID id) {
        T entite = findById(id);
        if (entite != null) {
            delete(entite);
        }
    }
} 