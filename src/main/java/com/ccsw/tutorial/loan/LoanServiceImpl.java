package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.client.ClientRepository;
import com.ccsw.tutorial.common.criteria.SearchCriteria;
import com.ccsw.tutorial.game.GameRepository;
import com.ccsw.tutorial.loan.model.Loan;
import com.ccsw.tutorial.loan.model.LoanDto;
import com.ccsw.tutorial.loan.model.LoanSearchDto;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * @author ccsw
 *
 */
@Service
@Transactional
public class LoanServiceImpl implements LoanService {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    LoanRepository loanRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public Loan get(Long id) {

        return this.loanRepository.findById(id).orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<Loan> findPage(LoanSearchDto dto) {

        LoanSpecification gameSpec = new LoanSpecification(new SearchCriteria("game.id", ":", dto.getGameId()));
        LoanSpecification clientSpec = new LoanSpecification(new SearchCriteria("client.id", ":", dto.getClientId()));

        LoanSpecification startDateSpec = new LoanSpecification(new SearchCriteria("dateStart", "<=", dto.getDate()));
        LoanSpecification endDateSpec = new LoanSpecification(new SearchCriteria("dateEnd", ">=", dto.getDate()));

        Specification<Loan> spec = gameSpec.and(clientSpec).and(startDateSpec).and(endDateSpec);

        return this.loanRepository.findAll(spec, dto.getPageable().getPageable());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Loan> findAll() {

        return (List<Loan>) this.loanRepository.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(Long id, LoanDto data) {

        Loan loan;

        if (id == null) {
            loan = new Loan();
        } else {
            loan = this.get(id);
        }

        // Validación fecha fin < fecha inicio
        if (data.getDateEnd().isBefore(data.getDateStart())) {
            throw new RuntimeException("End date cannot be before start date");
        }

        // Validación maximo 14 dias
        long days = ChronoUnit.DAYS.between(data.getDateStart(), data.getDateEnd());
        if (days > 14) {
            throw new RuntimeException("Maximum loan period is 14 days");
        }

        // Validacion que no exista juego prestado simultáneamente
        List<Loan> loans = this.loanRepository.findByGameId(data.getGame().getId());
        for (Loan existingLoan : loans) {

            // si modifico el mismo prestamo lo ignora
            if (id != null && existingLoan.getId().equals(id)) {
                continue;
            }

            boolean overlap = !data.getDateEnd().isBefore(existingLoan.getDateStart()) && !data.getDateStart().isAfter(existingLoan.getDateEnd());

            if (overlap) {
                throw new RuntimeException("Game already loaned");
            }
        }

        // Validación de que un cliente no tenga más de 2 prestamos de manera simultanea
        List<Loan> clientLoans = this.loanRepository.findByClientId(data.getClient().getId());

        int overlappingLoans = 0;

        for (Loan existingLoan : clientLoans) {

            // si modifico el mismo prestamo lo ignora
            if (id != null && existingLoan.getId().equals(id)) {
                continue;
            }

            boolean overlap = !data.getDateEnd().isBefore(existingLoan.getDateStart()) && !data.getDateStart().isAfter(existingLoan.getDateEnd());

            if (overlap) {
                overlappingLoans++;
            }
        }

        if (overlappingLoans >= 2) {
            throw new RuntimeException("Client cannot have more than 2 simultaneous loans");
        }

        loan.setGame(this.gameRepository.findById(data.getGame().getId()).orElse(null));
        loan.setClient(this.clientRepository.findById(data.getClient().getId()).orElse(null));
        loan.setDateStart(data.getDateStart());
        loan.setDateEnd(data.getDateEnd());

        this.loanRepository.save(loan);
    }

    @Override
    public void delete(Long id) throws Exception {

        if (this.loanRepository.findById(id).orElse(null) == null) {
            throw new Exception("Not exists");
        }

        this.loanRepository.deleteById(id);
    }
}
