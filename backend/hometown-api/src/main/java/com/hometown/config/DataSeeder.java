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
        // Developer (full API audit visibility)
        users.save(user("Dev Console", "dev@hometown.local", "dev123", "DEVELOPER"));
        // Customers
        users.save(user("Demo Customer", "customer@hometown.local", "customer123", "CUSTOMER"));
        users.save(user("Rahul Sharma", "rahul@example.com", "rahul123", "CUSTOMER"));
        users.save(user("Anita Verma", "anita@example.com", "anita123", "CUSTOMER"));
        users.save(user("John Doe", "john@example.com", "john123", "CUSTOMER"));

        Long wallArt = categories.save(category("Wall Art", "wall-art")).getId();
        Long pottery = categories.save(category("Pottery", "pottery")).getId();
        Long textiles = categories.save(category("Textiles", "textiles")).getId();
        Long decor = categories.save(category("Home Decor", "home-decor")).getId();

        products.save(product("Terracotta Vase Set", "Set of 3 hand-thrown terracotta vases with natural glaze.", "1299.00", 0, pottery, 15, "p2.jpg"));
        products.save(product("Handloom Cotton Throw", "Handwoven cotton throw blanket with block-print border.", "1299.00", 15, textiles, 20, "p3.jpg"));
        products.save(product("Brass Diya Lamp", "Hand-cast brass oil lamp for festive home decor.", "1199.00", 5, decor, 30, "p4.jpg"));
        products.save(product("Warli Painted Wall Plate", "Decorative wall plate with hand-painted Warli tribal art.", "1199.00", 0, wallArt, 25, "p5.jpg"));
        products.save(product("Jute Macrame Wall Hanging", "Boho jute macrame wall hanging, handcrafted.", "1199.00", 20, decor, 12, "p6.jpg"));
        products.save(paintingProduct("Hand-painted Pichwai Art", "Intricate Pichwai painting on cloth, traditional Rajasthani art.", "2199.00", 5, wallArt, 6, "p7.jpg", "CANVAS", 45, 60));
        products.save(product("Blue Pottery Bowl", "Jaipur blue pottery decorative bowl, hand-glazed.", "1299.00", 0, pottery, 18, "p8.jpg"));
        products.save(product("Kantha Embroidered Cushion", "Hand-embroidered Kantha cushion cover, recycled cotton.", "1099.00", 10, textiles, 22, "p9.jpg"));
        products.save(product("Brass Urli Bowl", "Traditional brass urli for floating flowers and diyas.", "1099.00", 0, decor, 14, "p10.jpg"));
        products.save(product("Dhokra Tribal Figurine", "Handcrafted Dhokra metal art figurine, lost-wax casting.", "1399.00", 0, decor, 9, "p11.jpg"));
        products.save(paintingProduct("Gond Painting on Paper", "Vibrant Gond tribal painting, hand-painted on handmade paper.", "1499.00", 8, wallArt, 11, "p12.jpg", "PAINTING", 30, 42));
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

    private Product product(String name, String desc, String price, int discount, Long categoryId, int stock, String image) {
        Product p = new Product();
        p.setName(name);
        p.setDescription(desc);
        p.setPrice(new BigDecimal(price));
        p.setDiscountPercent(discount);
        p.setCategoryId(categoryId);
        p.setStock(stock);
        p.setActive(true);
        p.setSellerId(1L);
        p.setImageUrls(List.of("/api/catalog/" + image));
        return p;
    }

    private Product paintingProduct(String name, String desc, String price, int discount, Long categoryId,
                                    int stock, String image, String artType, int artW, int artH) {
        Product p = product(name, desc, price, discount, categoryId, stock, image);
        p.setArtType(artType);
        p.setArtWidthCm(artW);
        p.setArtHeightCm(artH);
        return p;
    }

    private String slug(String s) {
        return s.toLowerCase().replaceAll("[^a-z0-9]+", "-");
    }
}
