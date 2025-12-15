package be.music.library_api.config;

import be.music.library_api.model.*;
import be.music.library_api.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CategoryService categoryService;
    private final AuthorService authorService;
    private final BookService bookService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Créer des catégories
        Category fiction = new Category();
        fiction.setName("Fiction");
        fiction.setDescription("Romans et nouvelles");

        Category science = new Category();
        science.setName("Science");
        science.setDescription("Ouvrages scientifiques");

        Category history = new Category();
        history.setName("Histoire");
        history.setDescription("Livres d'histoire");

        fiction = categoryService.save(fiction);
        science = categoryService.save(science);
        history = categoryService.save(history);

        // Créer des auteurs
        Author author1 = new Author();
        author1.setFirstName("Victor");
        author1.setLastName("Hugo");
        author1.setNationality("Français");

        Author author2 = new Author();
        author2.setFirstName("Albert");
        author2.setLastName("Einstein");
        author2.setNationality("Allemand");

        Author author3 = new Author();
        author3.setFirstName("Marie");
        author3.setLastName("Curie");
        author3.setNationality("Polonaise");

        Author author4 = new Author();
        author4.setFirstName("Isaac");
        author4.setLastName("Asimov");
        author4.setNationality("Américain");

        author1 = authorService.save(author1);
        author2 = authorService.save(author2);
        author3 = authorService.save(author3);
        author4 = authorService.save(author4);

        // Créer des livres
        Book book1 = new Book();
        book1.setIsbn("978-2-07-040850-4");
        book1.setTitle("Les Misérables");
        book1.setPublicationYear(1862);
        book1.setTotalCopies(5);
        book1.setAvailableCopies(5);
        book1.setCategory(fiction);
        book1.setAuthors(Arrays.asList(author1));

        Book book2 = new Book();
        book2.setIsbn("978-3-16-148410-0");
        book2.setTitle("La Relativité");
        book2.setPublicationYear(1916);
        book2.setTotalCopies(3);
        book2.setAvailableCopies(3);
        book2.setCategory(science);
        book2.setAuthors(Arrays.asList(author2));

        Book book3 = new Book();
        book3.setIsbn("978-0-12-345678-9");
        book3.setTitle("Radioactivité");
        book3.setPublicationYear(1898);
        book3.setTotalCopies(2);
        book3.setAvailableCopies(2);
        book3.setCategory(science);
        book3.setAuthors(Arrays.asList(author3));

        Book book4 = new Book();
        book4.setIsbn("978-0-553-29335-5");
        book4.setTitle("Fondation");
        book4.setPublicationYear(1951);
        book4.setTotalCopies(4);
        book4.setAvailableCopies(4);
        book4.setCategory(fiction);
        book4.setAuthors(Arrays.asList(author4));

        bookService.save(book1);
        bookService.save(book2);
        bookService.save(book3);
        bookService.save(book4);

        // Créer des utilisateurs
        User user = new User();
        user.setUsername("user");
        user.setPassword(passwordEncoder.encode("user123"));
        user.setEmail("user@example.com");
        user.setRole(User.Role.USER);

        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setEmail("admin@example.com");
        admin.setRole(User.Role.ADMIN);

        userService.save(user);
        userService.save(admin);
    }
}
