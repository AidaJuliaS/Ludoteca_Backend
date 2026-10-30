package com.ccsw.tutorial.client;

import com.ccsw.tutorial.client.model.Client;
import com.ccsw.tutorial.client.model.ClientDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientTest {

    // Constantes
    public static final String CLIENT_NAME = "CLI1";
    public static final Long EXISTS_CLIENT_ID = 1L;

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientServiceImpl clientService;

    // Tests
    @Test
    public void findAllShouldReturnAllClients() {

        List<Client> list = new ArrayList<>();
        list.add(mock(Client.class));

        when(clientRepository.findAll()).thenReturn(list);

        List<Client> clients = clientService.findAll();

        assertNotNull(clients);
        assertEquals(1, clients.size());
    }

    @Test
    public void saveNotExistsClientIdShouldInsert() {

        ClientDto clientDto = new ClientDto();
        clientDto.setName(CLIENT_NAME);

        ArgumentCaptor<Client> client = ArgumentCaptor.forClass(Client.class);

        clientService.save(null, clientDto);

        verify(clientRepository).save(client.capture());

        assertEquals(CLIENT_NAME, client.getValue().getName());
    }

    @Test
    public void saveExistsClientIdShouldUpdate() {

        ClientDto clientDto = new ClientDto();
        clientDto.setName(CLIENT_NAME);

        Client client = mock(Client.class);
        when(clientRepository.findById(EXISTS_CLIENT_ID)).thenReturn(Optional.of(client));

        clientService.save(EXISTS_CLIENT_ID, clientDto);

        verify(clientRepository).save(client);
    }

    @Test
    public void saveExistingNameShouldThrowException() {

        ClientDto dto = new ClientDto();
        dto.setName("Cliente 1");

        Client existingClient = new Client();
        existingClient.setId(1L);
        existingClient.setName("Cliente 1");

        when(clientRepository.findByName("Cliente 1")).thenReturn(existingClient);

        assertThrows(RuntimeException.class, () -> clientService.save(null, dto));
    }

    @Test
    public void modifyWithExistingNameShouldThrowException() {

        ClientDto dto = new ClientDto();
        dto.setName("Cliente 1");

        Client existingClient = new Client();
        existingClient.setId(1L);
        existingClient.setName("Cliente 1");

        when(clientRepository.findByName("Cliente 1")).thenReturn(existingClient);

        assertThrows(RuntimeException.class, () -> clientService.save(2L, dto));
    }

    @Test
    public void deleteExistsClientIdShouldDelete() throws Exception {

        Client category = mock(Client.class);
        when(clientRepository.findById(EXISTS_CLIENT_ID)).thenReturn(Optional.of(category));

        clientService.delete(EXISTS_CLIENT_ID);

        verify(clientRepository).deleteById(EXISTS_CLIENT_ID);
    }

    // Constante
    public static final Long NOT_EXISTS_CLIENT_ID = 0L;

    @Test
    public void getExistsClientIdShouldReturnClient() {

        Client client = mock(Client.class);
        when(client.getId()).thenReturn(EXISTS_CLIENT_ID);
        when(clientRepository.findById(EXISTS_CLIENT_ID)).thenReturn(Optional.of(client));

        Client clientResponse = clientService.get(EXISTS_CLIENT_ID);

        assertNotNull(clientResponse);
        assertEquals(EXISTS_CLIENT_ID, client.getId());
    }

    @Test
    public void getNotExistsClientIdShouldReturnNull() {

        when(clientRepository.findById(NOT_EXISTS_CLIENT_ID)).thenReturn(Optional.empty());

        Client client = clientService.get(NOT_EXISTS_CLIENT_ID);

        assertNull(client);
    }
}
