package com.issuerMs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.issuerMs.Entity.IssuerMsEntity;

public interface IssuerMsRepo extends JpaRepository<IssuerMsEntity, Integer>{

	@Query(value = "SELECT * FROM issuer_ms_entity WHERE cust_id = :custId", nativeQuery = true)
	List<IssuerMsEntity> findByCustId(@Param("custId") Long custId);
	
	@Query(value = "DELETE FROM issuer_ms_entity WHERE cust_id = :custId", nativeQuery = true)
	List<IssuerMsEntity> deleteByIssuerId(@Param("custId") Long custId);
	
}
