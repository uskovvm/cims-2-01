package com.carddex.sims2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.carddex.sims2.model.*;

public interface NomenclatureItemRepository extends JpaRepository<NomenclatureItem, Long> {

	@Query("select n from NomenclatureItem n where n.code = :code")
	NomenclatureItem findByCode(String code);
}
