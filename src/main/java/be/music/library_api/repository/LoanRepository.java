package be.music.library_api.repository;

import be.music.library_api.model.Loan;
import be.music.library_api.model.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByUserId(Long userId);

    List<Loan> findByStatus(LoanStatus status);

    @Query("SELECT l FROM Loan l WHERE l.dueDate < :currentDate AND l.status = :status")
    List<Loan> findOverdueLoans(@Param("currentDate") LocalDate currentDate, @Param("status") LoanStatus status);

    @Query("SELECT COUNT(l) FROM Loan l WHERE l.user.id = :userId AND l.status = :status")
    long countActiveLoansByUser(@Param("userId") Long userId, @Param("status") LoanStatus status);

    @Query("SELECT l FROM Loan l WHERE l.user.id = :userId AND l.book.id = :bookId AND l.status = :status")
    List<Loan> findActiveLoanByUserAndBook(@Param("userId") Long userId, @Param("bookId") Long bookId, @Param("status") LoanStatus status);
}