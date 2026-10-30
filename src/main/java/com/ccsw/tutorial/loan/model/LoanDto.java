package com.ccsw.tutorial.loan.model;

import com.ccsw.tutorial.client.model.ClientDto;
import com.ccsw.tutorial.game.model.GameDto;

import java.time.LocalDate;

public class LoanDto {

    private Long id;

    private GameDto game;

    private ClientDto client;

    private LocalDate dateStart;

    private LocalDate dateEnd;

    /**
     * @return id
     */
    public Long getId() {

        return this.id;
    }

    /**
     * @param id new value of {@link #getId}.
     */
    public void setId(Long id) {

        this.id = id;
    }

    /**
     * @return game
     */
    public GameDto getGame() {

        return this.game;
    }

    /**
     * @param game new value of {@link #getGame}.
     */
    public void setGame(GameDto game) {

        this.game = game;
    }

    /**
     * @return client
     */
    public ClientDto getClient() {

        return this.client;
    }

    /**
     * @param client new value of {@link #getClient}.
     */
    public void setClient(ClientDto client) {

        this.client = client;
    }

    /**
     * @return dateStart
     */
    public LocalDate getDateStart() {

        return this.dateStart;
    }

    /**
     * @param dateStart new value of {@link #getDateStart}.
     */
    public void setDateStart(LocalDate dateStart) {

        this.dateStart = dateStart;
    }

    /**
     * @return dateEnd
     */
    public LocalDate getDateEnd() {

        return this.dateEnd;
    }

    /**
     * @param dateEnd new value of {@link #getDateEnd}.
     */
    public void setDateEnd(LocalDate dateEnd) {

        this.dateEnd = dateEnd;
    }
}
