package com.madebyher.controller;

import com.madebyher.model.*;
import com.madebyher.service.PartnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/partner")
@RequiredArgsConstructor
public class PartnerController {
    private final PartnerService service;

    @GetMapping("/me")
    public VillagePartner me() { return service.me(); }

    @GetMapping("/women")
    public List<Woman> women() { return service.women(); }

    @PostMapping("/women")
    public Woman addWoman(@RequestBody Woman woman) { return service.addWoman(woman); }

    @PutMapping("/women/{id}")
    public Woman updateWoman(@PathVariable Long id, @RequestBody Woman woman) {
        return service.updateWoman(id, woman);
    }

    @DeleteMapping("/women/{id}")
    public void removeWoman(@PathVariable Long id) { service.removeWoman(id); }

    @PutMapping("/profile")
    public Map<String,String> profile(
            @RequestParam String village,
            @RequestParam String district,
            @RequestParam String state,
            @RequestParam(required=false) String pincode,
            @RequestParam(required=false) String bio) {
        service.applyProfile(village, district, state, pincode, bio);
        return Map.of("message", "Profile updated");
    }
}
