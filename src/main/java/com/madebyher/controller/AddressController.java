package com.madebyher.controller;

import com.madebyher.model.Address;
import com.madebyher.model.User;
import com.madebyher.repository.AddressRepository;
import com.madebyher.service.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {
    private final AddressRepository repository;
    private final CurrentUserService currentUser;

    @GetMapping
    public List<Address> list() { return repository.findByUserId(currentUser.get().getId()); }

    @PostMapping
    public Address create(@RequestBody Address address) {
        User u = currentUser.get();
        address.setId(null);
        address.setUser(u);
        return repository.save(address);
    }

    @PutMapping("/{id}")
    public Address update(@PathVariable Long id, @RequestBody Address incoming) {
        Address a = repository.findById(id).orElseThrow();
        if (!a.getUser().getId().equals(currentUser.get().getId()))
            throw new org.springframework.security.access.AccessDeniedException("Forbidden");
        a.setFullName(incoming.getFullName());
        a.setPhone(incoming.getPhone());
        a.setAddressLine1(incoming.getAddressLine1());
        a.setAddressLine2(incoming.getAddressLine2());
        a.setCity(incoming.getCity());
        a.setState(incoming.getState());
        a.setPincode(incoming.getPincode());
        a.setDefaultAddress(incoming.isDefaultAddress());
        return repository.save(a);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        Address a = repository.findById(id).orElseThrow();
        if (!a.getUser().getId().equals(currentUser.get().getId()))
            throw new org.springframework.security.access.AccessDeniedException("Forbidden");
        repository.delete(a);
    }
}
