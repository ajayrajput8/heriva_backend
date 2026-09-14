package com.madebyher.controller;

import com.madebyher.model.Category;
import com.madebyher.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryRepository repository;

    @GetMapping
    public List<Category> all() { return repository.findByActiveTrue(); }
}
