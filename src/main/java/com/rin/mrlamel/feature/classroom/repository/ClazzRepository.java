package com.rin.mrlamel.feature.classroom.repository;

import com.rin.mrlamel.feature.classroom.model.Clazz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClazzRepository extends JpaRepository<Clazz, Long> {

}
