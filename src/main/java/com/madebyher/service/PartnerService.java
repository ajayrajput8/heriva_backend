package com.madebyher.service;

import com.madebyher.model.*;
import com.madebyher.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PartnerService {
    private final PartnerRepository partnerRepository;
    private final WomanRepository womanRepository;
    private final CurrentUserService currentUser;

    @Value("${app.partner.max-women:5}")
    private int maxWomen;

    public VillagePartner me() {
        return partnerRepository.findByUserId(currentUser.get().getId()).orElseThrow();
    }

    public List<Woman> women() {
        return womanRepository.findByPartnerId(me().getId());
    }

    public Woman addWoman(Woman woman) {
        VillagePartner partner = me();
        if (partner.getStatus() != PartnerStatus.APPROVED)
            throw new IllegalArgumentException("Partner is not approved yet");

        long count = womanRepository.countByPartnerIdAndActiveTrue(partner.getId());
        if (count >= maxWomen)
            throw new IllegalArgumentException("You can manage only " + maxWomen + " women currently");

        woman.setId(null);
        woman.setPartner(partner);
        woman.setActive(true);
        return womanRepository.save(woman);
    }

    public Woman updateWoman(Long id, Woman incoming) {
        Woman woman = womanRepository.findById(id).orElseThrow();
        if (!woman.getPartner().getId().equals(me().getId()))
            throw new IllegalArgumentException("Not your assigned woman");

        woman.setName(incoming.getName());
        woman.setPhone(incoming.getPhone());
        woman.setVillage(incoming.getVillage());
        woman.setDistrict(incoming.getDistrict());
        woman.setState(incoming.getState());
        woman.setPhotoUrl(incoming.getPhotoUrl());
        woman.setStory(incoming.getStory());
        woman.setSkills(incoming.getSkills());
        return womanRepository.save(woman);
    }

    public void removeWoman(Long id) {
        Woman woman = womanRepository.findById(id).orElseThrow();
        if (!woman.getPartner().getId().equals(me().getId()))
            throw new IllegalArgumentException("Not your assigned woman");
        woman.setActive(false);
        womanRepository.save(woman);
    }

    public void applyProfile(String village, String district, String state, String pincode, String bio) {
        VillagePartner p = me();
        p.setVillage(village);
        p.setDistrict(district);
        p.setState(state);
        p.setPincode(pincode);
        p.setBio(bio);
        partnerRepository.save(p);
    }

    public void approve(Long id, boolean approve) {
        if (currentUser.get().getRole() != Role.ADMIN)
            throw new org.springframework.security.access.AccessDeniedException("Admin only");
        VillagePartner p = partnerRepository.findById(id).orElseThrow();
        p.setStatus(approve ? PartnerStatus.APPROVED : PartnerStatus.REJECTED);
        p.setApprovedAt(approve ? LocalDateTime.now() : null);
        partnerRepository.save(p);
    }
}
