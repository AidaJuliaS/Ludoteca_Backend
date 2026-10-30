package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.client.ClientRepository;
import com.ccsw.tutorial.client.model.Client;
import com.ccsw.tutorial.client.model.ClientDto;
import com.ccsw.tutorial.game.GameRepository;
import com.ccsw.tutorial.game.model.Game;
import com.ccsw.tutorial.game.model.GameDto;
import com.ccsw.tutorial.loan.model.Loan;
import com.ccsw.tutorial.loan.model.LoanDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoanTest {

    public static final Long EXISTS_LOAN_ID = 1L;
    public static final Long NOT_EXISTS_LOAN_ID = 0L;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private LoanServiceImpl loanService;

    @Test
    public void getExistsLoanIdShouldReturnLoan() {

        Loan loan = mock(Loan.class);
        when(loan.getId()).thenReturn(EXISTS_LOAN_ID);
        when(loanRepository.findById(EXISTS_LOAN_ID)).thenReturn(Optional.of(loan));

        Loan loanResponse = loanService.get(EXISTS_LOAN_ID);

        assertNotNull(loanResponse);

        assertEquals(EXISTS_LOAN_ID, loanResponse.getId());
    }

    @Test
    public void getNotExistsLoanIdShouldReturnNull() {

        when(loanRepository.findById(NOT_EXISTS_LOAN_ID)).thenReturn(Optional.empty());

        Loan loan = loanService.get(NOT_EXISTS_LOAN_ID);

        assertNull(loan);
    }

    @Test
    public void deleteWithExistsIdShouldDeleteLoan() throws Exception {

        Loan loan = mock(Loan.class);

        when(loanRepository.findById(EXISTS_LOAN_ID)).thenReturn(Optional.of(loan));

        loanService.delete(EXISTS_LOAN_ID);

        verify(loanRepository, times(1)).deleteById(EXISTS_LOAN_ID);
    }

    @Test
    public void deleteWithNotExistsIdShouldThrowException() {

        when(loanRepository.findById(NOT_EXISTS_LOAN_ID)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> loanService.delete(NOT_EXISTS_LOAN_ID));
    }

    @Test
    public void saveLoanShouldSaveLoan() {

        GameDto gameDto = new GameDto();
        gameDto.setId(1L);

        ClientDto clientDto = new ClientDto();
        clientDto.setId(1L);

        LoanDto dto = new LoanDto();
        dto.setGame(gameDto);
        dto.setClient(clientDto);
        dto.setDateStart(LocalDate.of(2027, 1, 1));
        dto.setDateEnd(LocalDate.of(2027, 1, 10));

        Game game = new Game();
        game.setId(1L);

        Client client = new Client();
        client.setId(1L);

        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        loanService.save(null, dto);

        verify(loanRepository, times(1)).save(org.mockito.ArgumentMatchers.any(Loan.class));
    }

    @Test
    public void saveLoanWithEndDateBeforeStartDateShouldThrowException() {

        GameDto gameDto = new GameDto();
        gameDto.setId(1L);

        ClientDto clientDto = new ClientDto();
        clientDto.setId(1L);

        LoanDto dto = new LoanDto();
        dto.setGame(gameDto);
        dto.setClient(clientDto);

        dto.setDateStart(LocalDate.of(2027, 1, 10));
        dto.setDateEnd(LocalDate.of(2027, 1, 1));

        assertThrows(RuntimeException.class, () -> loanService.save(null, dto));
    }


}
