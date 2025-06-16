package com.automacao.automacaoweb.service;

import com.automacao.automacaoweb.serial.SerialCommunication;
import com.automacao.automacaoweb.model.LogEntry;
import com.automacao.automacaoweb.repository.LogEntryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException; // Importar IOException

@Service
public class LedService {

    private static final Logger logger = LoggerFactory.getLogger(LedService.class);
    private final SerialCommunication serialCommunication;
    private final LogEntryRepository logEntryRepository;

    // Construtor com injeção de dependência
    public LedService(SerialCommunication serialCommunication, LogEntryRepository logEntryRepository) {
        this.serialCommunication = serialCommunication;
        this.logEntryRepository = logEntryRepository;
    }

    /**
     * Tenta ligar o LED enviando o caractere '1' para a porta serial.
     * Registra o resultado da operação no banco de dados.
     * @return Uma mensagem de sucesso ou erro.
     */
    public String turnOnLed() {
        char commandChar = '1'; // Comando para ligar o LED (caractere '1')
        String result;
        String status;
        String message;

        try {
            // Envia o caractere '1' (convertido para int implicitamente) para o Arduino
            serialCommunication.enviaDados(commandChar);
            result = "LED Ligado com sucesso!";
            status = "SUCESSO";
            message = "Comando '" + commandChar + "' enviado para o Arduino.";
            logger.info(result);
        } catch (IOException e) {
            result = "Erro de I/O ao ligar LED: " + e.getMessage();
            status = "FALHA";
            message = "Exceção de I/O ao enviar comando '" + commandChar + "': " + e.getMessage();
            logger.error(result, e);
        } catch (Exception e) {
            result = "Erro inesperado ao ligar LED: " + e.getMessage();
            status = "FALHA";
            message = "Exceção inesperada ao enviar comando '" + commandChar + "': " + e.getMessage();
            logger.error(result, e);
        }
        logEntryRepository.save(new LogEntry("Ligar LED", status, message));
        return result;
    }

    /**
     * Tenta desligar o LED enviando o caractere '2' para a porta serial.
     * Registra o resultado da operação no banco de dados.
     * @return Uma mensagem de sucesso ou erro.
     */
    public String turnOffLed() {
        char commandChar = '2'; // Comando para desligar o LED (caractere '2')
        String result;
        String status;
        String message;

        try {
            // Envia o caractere '2' (convertido para int implicitamente) para o Arduino
            serialCommunication.enviaDados(commandChar);
            result = "LED Desligado com sucesso!";
            status = "SUCESSO";
            message = "Comando '" + commandChar + "' enviado para o Arduino.";
            logger.info(result);
        } catch (IOException e) {
            result = "Erro de I/O ao desligar LED: " + e.getMessage();
            status = "FALHA";
            message = "Exceção de I/O ao enviar comando '" + commandChar + "': " + e.getMessage();
            logger.error(result, e);
        } catch (Exception e) {
            result = "Erro inesperado ao desligar LED: " + e.getMessage();
            status = "FALHA";
            message = "Exceção inesperada ao enviar comando '" + commandChar + "': " + e.getMessage();
            logger.error(result, e);
        }
        logEntryRepository.save(new LogEntry("Desligar LED", status, message));
        return result;
    }
}