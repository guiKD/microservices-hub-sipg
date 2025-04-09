package com.github.cidarosa.ms_pagamento.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cidarosa.ms_pagamento.dto.PagamentoDTO;
import com.github.cidarosa.ms_pagamento.service.PagamentoService;
import com.github.cidarosa.ms_pagamento.service.exceptions.ResourceNotFoundException;
import com.github.cidarosa.ms_pagamento.tests.Factory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;


@WebMvcTest
public class PagamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PagamentoService service;

    private PagamentoDTO dto;
    private Long existingId;
    private Long nonExistingId;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() throws Exception {

        existingId = 1L;
        nonExistingId = 100L;

        dto = Factory.createPagamentoDTO();

        List<PagamentoDTO> list = List.of(dto);

        // simulando o comportamento do getALL
        Mockito.when(service.getAll()).thenReturn(list);

        // simulando o comportamento do getByID - service
        // id existe
        Mockito.when(service.getById(existingId)).thenReturn(dto);
        // id não existe - lança exception
        Mockito.when(service.getById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

        // simulando o comportamento do service - createPagamento
        Mockito.when(service.createPagamento(any())).thenReturn(dto);

        //simulando o comportamento do service - updatePagamento
        //id existe
        Mockito.when(service.updatePagamento(eq(existingId), any()))
                .thenReturn(dto);
        //id não existe
        Mockito.when(service.updatePagamento(eq(nonExistingId), any()))
                .thenThrow(ResourceNotFoundException.class);

        //simulando o comportamento do service - deletePagamento
        //id existe
        Mockito.doNothing().when(service).deletePagamento(existingId);
        //id não existe
        Mockito.doThrow(ResourceNotFoundException.class).when(service)
                .deletePagamento(nonExistingId);
    }

    @Test
    public void getAllShouldReturnListPagamentoDTO() throws Exception {

        //chamando a requisição com o método GET - endpoint /pagamentos
        ResultActions result = mockMvc.perform(get("/pagamentos")
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
    }

    @Test
    public void getByIdShouldReturnPagamentoDTOWhenIdExists() throws Exception {

        ResultActions result = mockMvc.perform(get("/pagamentos/{id}", existingId)
                .accept(MediaType.APPLICATION_JSON));

        //Assertions
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.valor").exists());
        result.andExpect(jsonPath("$.status").exists());

    }

    @Test
    public void getByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() throws Exception {

        ResultActions result = mockMvc.perform(get("/pagamentos/{id}", nonExistingId)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }

    @Test
    public void createPagamentoShouldReturnPagamentoDTOCreated() throws Exception{

        PagamentoDTO newPagamentoDTO = Factory.createNewPagamentoDTO();

        String jsonRequestBody = objectMapper.writeValueAsString(newPagamentoDTO);

        mockMvc.perform(post("/pagamentos")
                .content(jsonRequestBody)
                .contentType(MediaType.APPLICATION_JSON) // request
                .accept(MediaType.APPLICATION_JSON)) // response

                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.valor").exists())
                .andExpect(jsonPath("$.valor").value(32.25))
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.pedidoId").exists())
                .andExpect(jsonPath("$.formaDePagamentoId").exists());
    }

    @Test
    public void updatePagamentoShouldReturnPagamentoDTOWhenIdExists() throws Exception{

        String jsonRequestBody = objectMapper.writeValueAsString(dto);

        mockMvc.perform(put("/pagamentos/{id}", existingId)
                .content(jsonRequestBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").value(existingId))
                .andExpect(jsonPath("$.valor").exists())
                .andExpect(jsonPath("$.valor").value(32.25))
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.pedidoId").exists())
                .andExpect(jsonPath("$.formaDePagamentoId").exists());
    }

    @Test
    public void updatePagamentoShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() throws Exception {

        String jsonRequestBody = objectMapper.writeValueAsString(dto);

        mockMvc.perform(put("/pagamentos/{id}", nonExistingId)
                        .content(jsonRequestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

      }

      @Test
    @DisplayName("deletePagamento deverá não fazer nada quando Id existe")
    public void deletePagamentoShouldDoNothingWhenIdExists() throws Exception {

        mockMvc.perform(delete("/pagamentos/{id}", existingId)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

      }

      @Test
    public void deletePagamentoShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() throws Exception {

        mockMvc.perform(delete("/pagamentos/{id}", nonExistingId)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
      }

}
