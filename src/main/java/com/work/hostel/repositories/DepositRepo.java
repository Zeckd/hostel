package com.work.hostel.repositories;

import com.work.hostel.models.Deposit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepositRepo extends JpaRepository<Deposit, Long> {
}
