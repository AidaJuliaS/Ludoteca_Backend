package com.ccsw.tutorial.loan.model;

import com.ccsw.tutorial.common.pagination.PageableRequest;
import java.time.LocalDate;

/**
 * @author ccsw
 *
 */
public class LoanSearchDto {

    private Long gameId;
    private Long clientId;
    private LocalDate date;
    private PageableRequest pageable;

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId){
        this.gameId = gameId;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId){
        this.clientId = clientId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date){
        this.date = date;
    }

    public PageableRequest getPageable() {
        return pageable;
    }

    public void setPageable(PageableRequest pageable) {
        this.pageable = pageable;
    }


}
