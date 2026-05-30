package com.hometown.product.service;

import com.hometown.common.web.ApiException;
import com.hometown.product.domain.Category;
import com.hometown.product.dto.CategoryRequest;
import com.hometown.product.dto.CategoryResponse;
import com.hometown.product.repo.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository repo;

    public CategoryService(CategoryRepository repo) {
        this.repo = repo;
    }

    public List<CategoryResponse> findAll() {
        return repo.findAll().stream()
                .map(c -> new CategoryResponse(c.getId(), c.getName(), c.getSlug()))
                .toList();
    }

    public CategoryResponse findById(Long id) {
        Category c = repo.findById(id)
                .orElseThrow(() -> ApiException.notFound("Category not found: " + id));
        return new CategoryResponse(c.getId(), c.getName(), c.getSlug());
    }

    @Transactional
    public CategoryResponse create(CategoryRequest req) {
        Category c = new Category();
        c.setName(req.name());
        c.setSlug(req.slug());
        c = repo.save(c);
        return new CategoryResponse(c.getId(), c.getName(), c.getSlug());
    }

    @Transactional
    public CategoryResponse update(Long id, CategoryRequest req) {
        Category c = repo.findById(id)
                .orElseThrow(() -> ApiException.notFound("Category not found: " + id));
        c.setName(req.name());
        c.setSlug(req.slug());
        return new CategoryResponse(c.getId(), c.getName(), c.getSlug());
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw ApiException.notFound("Category not found: " + id);
        }
        repo.deleteById(id);
    }
}
