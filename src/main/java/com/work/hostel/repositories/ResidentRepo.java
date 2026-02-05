package com.work.hostel.repositories;

import com.work.hostel.models.Resident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ResidentRepo extends JpaRepository<Resident, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE Resident r SET r.personCount = :personCount WHERE r.personCount IS NULL")
    int updateNullPersonCount(@Param("personCount") Integer personCount);
}
