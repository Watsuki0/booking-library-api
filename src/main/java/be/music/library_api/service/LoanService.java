package be.music.library_api.service;

import be.music.library_api.model.Loan;
import be.music.library_api.model.LoanStatus;
import be.music.library_api.model.User;
import be.music.library_api.model.Book;
import be.music.library_api.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final UserService userService;
    private final BookService bookService;

    private static final int MAX_ACTIVE_LOANS = 3;
    private static final int LOAN_DURATION_DAYS = 14;
    private static final double PENALTY_PER_DAY = 0.50;

    public List<Loan> findAll() {
        return loanRepository.findAll();
    }

    public Loan findById(Long id) {
        return loanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Emprunt non trouvé"));
    }

    public List<Loan> findByUserId(Long userId) {
        return loanRepository.findByUserId(userId);
    }

    public List<Loan> findActiveLoans() {
        return loanRepository.findByStatus(LoanStatus.ACTIVE);
    }

    public List<Loan> findOverdueLoans() {
        return loanRepository.findOverdueLoans(LocalDate.now(), LoanStatus.ACTIVE);
    }

    @Transactional
    public Loan createLoan(Long userId, Long bookId) {
        User user = userService.findById(userId);
        Book book = bookService.findById(bookId);

        // Vérifier les règles métier
        validateLoanCreation(user, book);

        // Créer l'emprunt
        Loan loan = new Loan();
        loan.setUser(user);
        loan.setBook(book);
        loan.setLoanDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(LOAN_DURATION_DAYS));
        loan.setStatus(LoanStatus.ACTIVE);

        // Décrémenter les exemplaires disponibles
        bookService.decrementAvailableCopies(book);

        return loanRepository.save(loan);
    }

    @Transactional
    public Loan returnBook(Long loanId) {
        Loan loan = findById(loanId);

        if (loan.getStatus() != LoanStatus.ACTIVE) {
            throw new RuntimeException("Cet emprunt n'est pas actif");
        }

        loan.setReturnDate(LocalDate.now());

        // Vérifier si en retard
        if (LocalDate.now().isAfter(loan.getDueDate())) {
            loan.setStatus(LoanStatus.OVERDUE);
        } else {
            loan.setStatus(LoanStatus.RETURNED);
        }

        // Incrémenter les exemplaires disponibles
        bookService.incrementAvailableCopies(loan.getBook());

        return loanRepository.save(loan);
    }

    public double calculatePenalty(Long loanId) {
        Loan loan = findById(loanId);

        if (loan.getReturnDate() == null) {
            // Emprunt pas encore retourné, calculer pénalité potentielle
            LocalDate today = LocalDate.now();
            if (today.isAfter(loan.getDueDate())) {
                long daysLate = ChronoUnit.DAYS.between(loan.getDueDate(), today);
                return daysLate * PENALTY_PER_DAY;
            }
            return 0.0;
        } else {
            // Emprunt retourné, calculer pénalité réelle
            if (loan.getReturnDate().isAfter(loan.getDueDate())) {
                long daysLate = ChronoUnit.DAYS.between(loan.getDueDate(), loan.getReturnDate());
                return daysLate * PENALTY_PER_DAY;
            }
            return 0.0;
        }
    }

    private void validateLoanCreation(User user, Book book) {
        // 1. Limite d'emprunts simultanés
        long activeLoansCount = loanRepository.countActiveLoansByUser(user.getId(), LoanStatus.ACTIVE);
        if (activeLoansCount >= MAX_ACTIVE_LOANS) {
            throw new RuntimeException("L'utilisateur a atteint la limite maximale d'emprunts actifs (" + MAX_ACTIVE_LOANS + ")");
        }

        // 2. Vérification de disponibilité
        if (book.getAvailableCopies() <= 0) {
            throw new RuntimeException("Aucun exemplaire disponible pour ce livre");
        }

        // 3. Pas de double emprunt
        List<Loan> existingLoans = loanRepository.findActiveLoanByUserAndBook(user.getId(), book.getId(), LoanStatus.ACTIVE);
        if (!existingLoans.isEmpty()) {
            throw new RuntimeException("L'utilisateur a déjà emprunté ce livre et ne l'a pas encore retourné");
        }
    }
}
