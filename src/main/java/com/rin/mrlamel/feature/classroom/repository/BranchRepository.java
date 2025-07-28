package com.rin.mrlamel.feature.classroom.repository;

import com.rin.mrlamel.feature.classroom.dto.BranchDto;
import com.rin.mrlamel.feature.classroom.model.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BranchRepository extends JpaRepository<Branch, Long> {
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Long id);


    @Query("""
            SELECT new com.rin.mrlamel.feature.classroom.dto.BranchDto(b.id, b.name, b.address, b.phone, COUNT(r.id))
            FROM Branch b
            LEFT JOIN Room r ON r.branch.id = b.id
            GROUP BY b.id
            ORDER BY b.name ASC
            """)
    List<BranchDto> findAllByOrderByNameAsc();
}
