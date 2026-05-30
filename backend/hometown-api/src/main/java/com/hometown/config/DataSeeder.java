package com.hometown.config;

import com.hometown.product.domain.Category;
import com.hometown.product.domain.Product;
import com.hometown.product.repo.CategoryRepository;
import com.hometown.product.repo.ProductRepository;
import com.hometown.user.domain.User;
import com.hometown.user.repo.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@Profile("h2 | postgres | disk")
public class DataSeeder implements CommandLineRunner {

    private final UserRepository users;
    private final CategoryRepository categories;
    private final ProductRepository products;
    private final PasswordEncoder encoder;

    public DataSeeder(UserRepository users, CategoryRepository categories,
                      ProductRepository products, PasswordEncoder encoder) {
        this.users = users;
        this.categories = categories;
        this.products = products;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        if (users.count() > 0) return;

        // Admins
        users.save(user("HomeTown Admin", "admin@hometown.local", "admin123", "ADMIN"));
        users.save(user("Priya (Admin)", "priya.admin@hometown.local", "admin123", "ADMIN"));
        users.save(user("Store Manager", "manager@hometown.local", "manager123", "ADMIN"));
        // Customers
        users.save(user("Demo Customer", "customer@hometown.local", "customer123", "CUSTOMER"));
        users.save(user("Rahul Sharma", "rahul@example.com", "rahul123", "CUSTOMER"));
        users.save(user("Anita Verma", "anita@example.com", "anita123", "CUSTOMER"));
        users.save(user("John Doe", "john@example.com", "john123", "CUSTOMER"));

        Long wallArt = categories.save(category("Wall Art", "wall-art")).getId();
        Long pottery = categories.save(category("Pottery", "pottery")).getId();
        Long textiles = categories.save(category("Textiles", "textiles")).getId();
        Long decor = categories.save(category("Home Decor", "home-decor")).getId();

        products.save(product("Madhubani Hand-painted Canvas", "Traditional Madhubani folk art, hand-painted on cotton canvas.", "1499.00", 10, wallArt, 8));
        products.save(product("Terracotta Vase Set", "Set of 3 hand-thrown terracotta vases with natural glaze.", "899.00", 0, pottery, 15));
        products.save(product("Handloom Cotton Throw", "Handwoven cotton throw blanket with block-print border.", "1299.00", 15, textiles, 20));
        products.save(product("Brass Diya Lamp", "Hand-cast brass oil lamp for festive home decor.", "649.00", 5, decor, 30));
        products.save(product("Warli Painted Wall Plate", "Decorative wall plate with hand-painted Warli tribal art.", "499.00", 0, wallArt, 25));
        products.save(product("Jute Macrame Wall Hanging", "Boho jute macrame wall hanging, handcrafted.", "799.00", 20, decor, 12));
    }

    private User user(String name, String email, String rawPassword, String role) {
        User u = new User();
        u.setName(name);
        u.setEmail(email);
        u.setPasswordHash(encoder.encode(rawPassword));
        u.setRole(role);
        return u;
    }

    private Category category(String name, String slug) {
        Category c = new Category();
        c.setName(name);
        c.setSlug(slug);
        return c;
    }

    private Product product(String name, String desc, String price, int discount, Long categoryId, int stock) {
        Product p = new Product();
        p.setName(name);
        p.setDescription(desc);
        p.setPrice(new BigDecimal(price));
        p.setDiscountPercent(discount);
        p.setCategoryId(categoryId);
        p.setStock(stock);
        p.setActive(true);
        p.setSellerId(1L);
        p.setImageUrls(List.of("https://picsum.photos/seed/" + slug(name) + "/400/300"));
        return p;
    }

    private String slug(String s) {
        return s.toLowerCase().replaceAll("[^a-z0-9]+", "-");
    }
}
