package com.carddex.sims2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.carddex.sims2.model.*;

public interface NomenclatureGroupRepository extends JpaRepository<NomenclatureGroup, Long> {

	@Query("select n from NomenclatureGroup n where n.code = :code")
	NomenclatureGroup findByCode(String code);
	
	@Query("select n from NomenclatureGroup n where n.items = :item")
	NomenclatureGroup findByItem(NomenclatureItem item);
}
