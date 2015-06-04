package com.wallet.entity;
import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

@Configurable
@Entity
public class Wallet {

    /**
     */
    private BigDecimal availableMoney;

	@PersistenceContext
    public transient EntityManager entityManager;

	public static final List<String> fieldNames4OrderClauseFilter = java.util.Arrays.asList("availableMoney");

	public static final EntityManager entityManager() {
        EntityManager em = new Wallet().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

	public static long countWallets() {
        return entityManager().createQuery("SELECT COUNT(o) FROM Wallet o", Long.class).getSingleResult();
    }

	public static List<Wallet> findAllWallets() {
        return entityManager().createQuery("SELECT o FROM Wallet o", Wallet.class).getResultList();
    }

	public static List<Wallet> findAllWallets(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM Wallet o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, Wallet.class).getResultList();
    }

	public static Wallet findWallet(Long id) {
        if (id == null) return null;
        return entityManager().find(Wallet.class, id);
    }

	public static List<Wallet> findWalletEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM Wallet o", Wallet.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

	public static List<Wallet> findWalletEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM Wallet o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, Wallet.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

	@Transactional
    public void persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }

	@Transactional
    public void remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            Wallet attached = Wallet.findWallet(this.id);
            this.entityManager.remove(attached);
        }
    }

	@Transactional
    public void flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }

	@Transactional
    public void clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }

    public Wallet merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        
    	entityManager.lock(this, LockModeType.OPTIMISTIC);
        Wallet merged = this.entityManager.merge(this);
        this.entityManager.flush();
        
        return merged;
    }

    @Transactional
    public static Wallet update(Long id,BigDecimal val, EntityManager entityManager2){
    	
    	Wallet w = entityManager2.find(Wallet.class,id , LockModeType.PESSIMISTIC_WRITE);
    	w.setAvailableMoney(w.getAvailableMoney().add(val));
    	w.merge();
    	
    	return w;
    }
    
	public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

	@Version
    @Column(name = "version")
    private Integer version;

	public Long getId() {
        return this.id;
    }

	public void setId(Long id) {
        this.id = id;
    }

	public Integer getVersion() {
        return this.version;
    }

	public void setVersion(Integer version) {
        this.version = version;
    }

	public BigDecimal getAvailableMoney() {
        return this.availableMoney;
    }

	public void setAvailableMoney(BigDecimal availableMoney) {
        this.availableMoney = availableMoney;
    }
}
