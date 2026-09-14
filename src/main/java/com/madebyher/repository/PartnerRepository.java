package com.madebyher.repository;
import com.madebyher.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface PartnerRepository extends JpaRepository<VillagePartner, Long> {
    Optional<VillagePartner> findByUserId(Long userId);
    List<VillagePartner> findByStatus(PartnerStatus status);
}
