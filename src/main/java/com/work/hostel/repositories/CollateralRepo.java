package com.work.hostel.repositories;

import com.work.hostel.models.Collateral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollateralRepo extends JpaRepository<Collateral, Long> {
}
