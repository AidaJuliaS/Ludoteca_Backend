package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.loan.model.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * @author ccsw
 *
 */
public interface LoanRepository extends CrudRepository<Loan, Long>, JpaSpecificationExecutor<Loan> {

    /**
     * Método para recuperar un listado paginado de {@link Loan}
     *
     * @param pageable pageable
     * @return {@link Page} de {@link Loan}
     */
    Page<Loan> findAll(Pageable pageable);

    List<Loan> findByGameId(Long gameId);

    List<Loan> findByClientId(Long clientId);

    boolean existsByClientId(Long clientId);

    @Query("""
            SELECT COUNT(l)
            FROM Loan l
            WHERE l.game.id = :gameId
            AND l.dateStart <= :dateEnd
            AND l.dateEnd >= :dateStart
            AND (:loanId IS NULL OR l.id <> :loanId)
            """)
    long countOverlappingLoansForGame(
            @Param("gameId") Long gameId,
            @Param("dateStart") LocalDate dateStart,
            @Param("dateEnd") LocalDate dateEnd,
            @Param("loanId") Long loanId);

    @Query("""
            SELECT COUNT(l)
            FROM Loan l
            WHERE l.client.id = :clientId
            AND l.dateStart <= :dateEnd
            AND l.dateEnd >= :dateStart
            AND (:loanId IS NULL OR l.id <> :loanId)
            """)
    long countOverlappingLoansForClient(
            @Param("clientId") Long clientId,
            @Param("dateStart") LocalDate dateStart,
            @Param("dateEnd") LocalDate dateEnd,
            @Param("loanId") Long loanId);
}
