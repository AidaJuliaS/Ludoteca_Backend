package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.client.model.ClientDto;
import com.ccsw.tutorial.common.pagination.PageableRequest;
import com.ccsw.tutorial.config.ResponsePage;
import com.ccsw.tutorial.game.model.GameDto;
import com.ccsw.tutorial.loan.model.LoanDto;
import com.ccsw.tutorial.loan.model.LoanSearchDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class LoanIT {

    public static final String LOCALHOST = "http://localhost:";
    public static final String SERVICE_PATH = "/loan";

    public static final Long MODIFY_LOAN_ID = 3L;

    public static final Long DELETE_LOAN_ID = 5L;

    private static final int TOTAL_LOANS = 5;
    private static final int PAGE_SIZE = 5;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    ParameterizedTypeReference<ResponsePage<LoanDto>> responseTypePage = new ParameterizedTypeReference<ResponsePage<LoanDto>>() {
    };

    @Test
    public void findFirstPageWithFiveSizeShouldReturnFirstFiveResults() {

        LoanSearchDto searchDto = new LoanSearchDto();
        searchDto.setPageable(new PageableRequest(0, PAGE_SIZE));

        ResponseEntity<ResponsePage<LoanDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage);

        assertNotNull(response);
        assertEquals(TOTAL_LOANS, response.getBody().getTotalElements());
        assertEquals(PAGE_SIZE, response.getBody().getContent().size());
    }

    @Test
    public void saveWithoutIdShouldCreateNewLoan() {

        long newLoanSize = TOTAL_LOANS + 1;

        GameDto game = new GameDto();
        game.setId(1L);

        ClientDto client = new ClientDto();
        client.setId(1L);

        LoanDto dto = new LoanDto();
        dto.setGame(game);
        dto.setClient(client);

        dto.setDateStart(LocalDate.of(2027, 2, 1));
        dto.setDateEnd(LocalDate.of(2027, 2, 5));

        restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.PUT, new HttpEntity<>(dto), Void.class);

        LoanSearchDto searchDto = new LoanSearchDto();
        searchDto.setPageable(new PageableRequest(0, (int) newLoanSize));

        ResponseEntity<ResponsePage<LoanDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage);

        assertNotNull(response);
        assertEquals(newLoanSize, response.getBody().getTotalElements());
    }


    @Test
    public void modifyWithExistIdShouldModifyLoan() {

        GameDto game = new GameDto();
        game.setId(2L);

        ClientDto client = new ClientDto();
        client.setId(2L);

        LoanDto dto = new LoanDto();
        dto.setGame(game);
        dto.setClient(client);

        dto.setDateStart(LocalDate.of(2027, 3, 1));
        dto.setDateEnd(LocalDate.of(2027, 3, 10));

        restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + MODIFY_LOAN_ID, HttpMethod.PUT, new HttpEntity<>(dto), Void.class);

        LoanSearchDto searchDto = new LoanSearchDto();
        searchDto.setPageable(new PageableRequest(0, PAGE_SIZE));

        ResponseEntity<ResponsePage<LoanDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage);

        LoanDto loan = response.getBody().getContent().stream().filter(item -> item.getId().equals(MODIFY_LOAN_ID)).findFirst().orElse(null);

        assertNotNull(loan);
        assertEquals(2L, loan.getClient().getId());
    }

    @Test
    public void modifyWithNotExistIdShouldThrowException() {

        long loanId = TOTAL_LOANS + 1;

        GameDto game = new GameDto();
        game.setId(1L);

        ClientDto client = new ClientDto();
        client.setId(1L);

        LoanDto dto = new LoanDto();
        dto.setGame(game);
        dto.setClient(client);

        dto.setDateStart(LocalDate.of(2027, 2, 1));
        dto.setDateEnd(LocalDate.of(2027, 2, 5));

        ResponseEntity<?> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + loanId, HttpMethod.PUT, new HttpEntity<>(dto), Void.class);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void findAllShouldReturnAllLoans() {

        ResponseEntity<List<LoanDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.GET, null, responseTypeList);

        assertNotNull(response);
        assertEquals(TOTAL_LOANS, response.getBody().size());
    }

    @Test
    public void deleteWithExistsIdShouldDeleteLoan() {

        long newLoansSize = TOTAL_LOANS - 1;

        restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + DELETE_LOAN_ID, HttpMethod.DELETE, null, Void.class);

        LoanSearchDto searchDto = new LoanSearchDto();
        searchDto.setPageable(new PageableRequest(0, TOTAL_LOANS));

        ResponseEntity<ResponsePage<LoanDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage);

        assertNotNull(response);
        assertEquals(newLoansSize, response.getBody().getTotalElements());
    }

    @Test
    public void deleteWithNotExistsIdShouldThrowException() {

        long deleteLoanId = TOTAL_LOANS + 1;

        ResponseEntity<?> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + deleteLoanId, HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    ParameterizedTypeReference<List<LoanDto>> responseTypeList = new ParameterizedTypeReference<List<LoanDto>>() {
    };

    @Test
    public void findByGameShouldReturnFilteredLoans() {

        LoanSearchDto searchDto = new LoanSearchDto();
        searchDto.setGameId(1L);
        searchDto.setPageable(new PageableRequest(0, PAGE_SIZE));

        ResponseEntity<ResponsePage<LoanDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage);

        assertNotNull(response);
        assertEquals(2, response.getBody().getTotalElements());
    }

    @Test
    public void findByClientShouldReturnFilteredLoans() {

        LoanSearchDto searchDto = new LoanSearchDto();
        searchDto.setClientId(2L);
        searchDto.setPageable(new PageableRequest(0, PAGE_SIZE));

        ResponseEntity<ResponsePage<LoanDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage);

        assertNotNull(response);
        assertEquals(1, response.getBody().getTotalElements());
    }

    @Test
    public void findByDateShouldReturnLoansWithinRange() {

        LoanSearchDto searchDto = new LoanSearchDto();
        searchDto.setDate(LocalDate.of(2027, 1, 5));
        searchDto.setPageable(new PageableRequest(0, PAGE_SIZE));

        ResponseEntity<ResponsePage<LoanDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage);
        assertNotNull(response);
        assertEquals(4, response.getBody().getTotalElements());
    }

    @Test
    public void saveLoanWithEndDateBeforeStartDateShouldFail() {

        GameDto game = new GameDto();
        game.setId(1L);

        ClientDto client = new ClientDto();
        client.setId(1L);

        LoanDto dto = new LoanDto();
        dto.setGame(game);
        dto.setClient(client);

        dto.setDateStart(LocalDate.of(2027, 1, 10));
        dto.setDateEnd(LocalDate.of(2027, 1, 1));

        ResponseEntity<?> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.PUT, new HttpEntity<>(dto), Void.class);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void saveLoanWith14DaysShouldCreateLoan() {

        long newLoanSize = TOTAL_LOANS + 1;

        GameDto game = new GameDto();
        game.setId(2L);

        ClientDto client = new ClientDto();
        client.setId(2L);

        LoanDto dto = new LoanDto();
        dto.setGame(game);
        dto.setClient(client);

        dto.setDateStart(LocalDate.of(2027, 2, 1));
        dto.setDateEnd(LocalDate.of(2027, 2, 15));

        restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.PUT, new HttpEntity<>(dto), Void.class);

        LoanSearchDto searchDto = new LoanSearchDto();
        searchDto.setPageable(new PageableRequest(0, TOTAL_LOANS + 1));

        ResponseEntity<ResponsePage<LoanDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage);

        assertNotNull(response);
        assertEquals(newLoanSize, response.getBody().getTotalElements());
    }

    @Test
    public void saveLoanWithMoreThan14DaysShouldFail() {

        GameDto game = new GameDto();
        game.setId(1L);

        ClientDto client = new ClientDto();
        client.setId(1L);

        LoanDto dto = new LoanDto();
        dto.setGame(game);
        dto.setClient(client);

        dto.setDateStart(LocalDate.of(2027, 1, 1));
        dto.setDateEnd(LocalDate.of(2027, 1, 20));

        ResponseEntity<?> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.PUT, new HttpEntity<>(dto), Void.class);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void saveLoanWithOverlappedGameShouldFail() {

        GameDto game = new GameDto();
        game.setId(1L);

        ClientDto client = new ClientDto();
        client.setId(1L);

        LoanDto dto = new LoanDto();
        dto.setGame(game);
        dto.setClient(client);

        dto.setDateStart(LocalDate.of(2027, 1, 3));
        dto.setDateEnd(LocalDate.of(2027, 1, 5));

        ResponseEntity<?> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.PUT, new HttpEntity<>(dto), Void.class);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void saveLoanWithTwoSimultaneousLoansShouldBeAllowed() {

        long newLoanSize = TOTAL_LOANS + 1;

        GameDto game = new GameDto();
        game.setId(2L);

        ClientDto client = new ClientDto();
        client.setId(2L);

        LoanDto dto = new LoanDto();
        dto.setGame(game);
        dto.setClient(client);

        dto.setDateStart(LocalDate.of(2027, 2, 1));
        dto.setDateEnd(LocalDate.of(2027, 2, 5));

        ResponseEntity<?> saveResponse = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.PUT, new HttpEntity<>(dto), Void.class);

        assertEquals(HttpStatus.OK, saveResponse.getStatusCode());

        LoanSearchDto searchDto = new LoanSearchDto();
        searchDto.setPageable(new PageableRequest(0, TOTAL_LOANS + 1));

        ResponseEntity<ResponsePage<LoanDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST, new HttpEntity<>(searchDto), responseTypePage);

        assertNotNull(response);
        assertEquals(newLoanSize, response.getBody().getTotalElements());
    }

    @Test
    public void saveLoanWithMoreThanTwoSimultaneousLoansShouldFail() {

        GameDto game = new GameDto();
        game.setId(3L);

        ClientDto client = new ClientDto();
        client.setId(1L);

        LoanDto dto = new LoanDto();
        dto.setGame(game);
        dto.setClient(client);

        dto.setDateStart(LocalDate.of(2027, 1, 5));
        dto.setDateEnd(LocalDate.of(2027, 1, 8));

        ResponseEntity<?> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.PUT, new HttpEntity<>(dto), Void.class);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

}