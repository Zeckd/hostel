package com.work.hostel.repositories;

import com.work.hostel.models.Resident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResidentRepo extends JpaRepository<Resident, Long> {
}
