package be.music.library_api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "L'ISBN est obligatoire")
    @Column(unique = true)
    private String isbn;

    @NotBlank(message = "Le titre est obligatoire")
    private String title;

    @NotNull(message = "L'année de publication est obligatoire")
    @Min(value = 1000, message = "L'année doit être valide")
    private Integer publicationYear;

    @NotNull(message = "Le nombre total d'exemplaires est obligatoire")
    @Min(value = 1, message = "Au moins un exemplaire")
    private Integer totalCopies;

    @NotNull(message = "Le nombre d'exemplaires disponibles est obligatoire")
    @Min(value = 0, message = "Ne peut pas être négatif")
    private Integer availableCopies;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToMany
    @JoinTable(
        name = "book_authors",
        joinColumns = @JoinColumn(name = "book_id"),
        inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private List<Author> authors;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<Loan> loans;
}
