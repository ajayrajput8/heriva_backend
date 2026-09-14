package com.madebyher.controller;

import com.madebyher.model.Woman;
import com.madebyher.repository.WomanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/women")
@RequiredArgsConstructor
public class PublicWomanController {

    private final WomanRepository womanRepository;

    @GetMapping
    public List<PublicWomanResponse> getPublicWomen() {

        return womanRepository.findAll()
                .stream()
                .filter(Woman::isActive)
                .map(woman -> new PublicWomanResponse(
                        woman.getId(),
                        woman.getName(),
                        woman.getVillage(),
                        woman.getDistrict(),
                        woman.getState(),
                        woman.getPhotoUrl(),
                        woman.getStory(),
                        woman.getSkills()
                ))
                .toList();
    }

    public record PublicWomanResponse(
            Long id,
            String name,
            String village,
            String district,
            String state,
            String photoUrl,
            String story,
            String skills
    ) {}
}