// src/main/java/com/automacao/automacaoweb/model/LogEntry.java

package com.automacao.automacaoweb.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import java.time.LocalDateTime;

@Entity // Marca esta classe como uma entidade JPA (mapeia para uma tabela no DB)
public class LogEntry {

    @Id // Marca este campo como a chave primária
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Geração automática de ID
    private Long id;

    @Column(nullable = false) // Coluna não pode ser nula
    private String action; // Ex: "Ligar LED", "Desligar LED"

    @Column(nullable = false)
    private LocalDateTime timestamp; // Quando a ação ocorreu

    @Column
    private String status; // Ex: "SUCESSO", "FALHA"

    @Column(length = 500) // Limita o tamanho da string
    private String message; // Mensagem detalhada sobre o log

    // Construtor padrão (necessário para JPA)
    public LogEntry() {
    }

    // Construtor para facilitar a criação de logs
    public LogEntry(String action, String status, String message) {
        this.action = action;
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now(); // Define o timestamp automaticamente
    }

    // Getters e Setters (gerados automaticamente pela IDE ou escritos manualmente)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "LogEntry{" +
                "id=" + id +
                ", action='" + action + '\'' +
                ", timestamp=" + timestamp +
                ", status='" + status + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}