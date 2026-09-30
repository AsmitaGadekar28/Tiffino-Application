package com.tiffino.tiffino.repository;

import com.tiffino.tiffino.entity.OfferDate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface OfferDateRepo extends JpaRepository<OfferDate, Long> {
    List<OfferDate> findAllByOfferDate(LocalDate date);
}
