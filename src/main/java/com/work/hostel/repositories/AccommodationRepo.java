package com.work.hostel.repositories;

import com.work.hostel.models.Accommodation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccommodationRepo extends JpaRepository<Accommodation, Long> {
}
