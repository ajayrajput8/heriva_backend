package com.madebyher.repository;
import com.madebyher.model.Woman;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface WomanRepository extends JpaRepository<Woman, Long> {
    List<Woman> findByPartnerId(Long partnerId);
    long countByPartnerIdAndActiveTrue(Long partnerId);
}
