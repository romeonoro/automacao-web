package com.automacao.automacaoweb.serial;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException; // Importar esta exceção também
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration; // Certifique-se de que esta importação está presente para getPortIdentifiers()

@Component
public class SerialCommunication {

    private static final Logger logger = LoggerFactory.getLogger(SerialCommunication.class);

    private SerialPort serialPort; // Referência ao objeto SerialPort para gerenciamento
    private OutputStream outputStream; // Stream de saída para enviar dados

    @Value("${arduino.serial.baudrate}")
    private int baudRate; // Taxa de baud configurada no application.properties

    @Value("${arduino.serial.port}")
    private String portName; // Nome da porta COM configurada no application.properties

    /**
     * Método que inicializa a comunicação com a porta serial.
     * Este método é anotado com @PostConstruct, garantindo que será executado
     * automaticamente pelo Spring após a construção e injeção de dependências do bean.
     */
    @PostConstruct
    public void initialize() {
        logger.info("Tentando inicializar comunicação serial na porta {} com taxa de {} baud.", portName, baudRate);

        CommPortIdentifier portId = null;
        // Lista todas as portas disponíveis no sistema
        Enumeration<?> portList = CommPortIdentifier.getPortIdentifiers();

        boolean portFound = false;
        // Itera sobre as portas encontradas para localizar a porta configurada
        while (portList.hasMoreElements()) {
            CommPortIdentifier currentPortId = (CommPortIdentifier) portList.nextElement();
            // Verifica se é uma porta serial e se o nome corresponde ao configurado
            if (currentPortId.getPortType() == CommPortIdentifier.PORT_SERIAL && currentPortId.getName().equals(portName)) {
                portId = currentPortId;
                portFound = true;
                break;
            }
        }

        // Se a porta não foi encontrada, lança uma exceção para impedir a inicialização da aplicação
        if (!portFound) {
            logger.error("Porta COM '{}' não encontrada. Verifique a configuração e se o Arduino está conectado.", portName);
            throw new RuntimeException("Porta COM não encontrada: " + portName + ". Verifique se o Arduino está conectado e a porta está correta.");
        }

        try {
            // Abre a porta serial. "SerialCommunicationApp" é um nome de identificação para a aplicação,
            // e 2000 é o timeout em milissegundos para abrir a porta.
            serialPort = (SerialPort) portId.open("SerialCommunicationApp", 2000);
            outputStream = serialPort.getOutputStream(); // Obtém o stream de saída

            // Configura os parâmetros da porta serial (taxa de baud, bits de dados, bits de parada, paridade)
            serialPort.setSerialPortParams(baudRate,
                    SerialPort.DATABITS_8,    // 8 bits de dados
                    SerialPort.STOPBITS_1,    // 1 bit de parada
                    SerialPort.PARITY_NONE);  // Sem paridade

            logger.info("Comunicação serial com a porta {} aberta com sucesso na taxa {} baud.", portName, baudRate);

        } catch (PortInUseException pie) {
            logger.error("A porta serial '{}' já está em uso por outro aplicativo: {}", portName, pie.getMessage(), pie);
            throw new RuntimeException("Porta serial " + portName + " em uso. Certifique-se de que nenhum outro programa (como o Monitor Serial do Arduino IDE) está usando-a.", pie);
        } catch (UnsupportedCommOperationException ucoe) {
            logger.error("Operação de comunicação não suportada na porta {}: {}", portName, ucoe.getMessage(), ucoe);
            throw new RuntimeException("Configuração da porta serial não suportada. Verifique a taxa de baud ou outros parâmetros.", ucoe);
        } catch (IOException ioe) {
            logger.error("Erro de I/O ao abrir a porta serial {}: {}", portName, ioe.getMessage(), ioe);
            throw new RuntimeException("Erro de I/O ao tentar abrir a porta serial.", ioe);
        } catch (Exception e) { // Captura quaisquer outras exceções inesperadas
            logger.error("Erro inesperado ao inicializar a comunicação serial na porta {}: {}", portName, e.getMessage(), e);
            throw new RuntimeException("Falha inesperada ao inicializar a comunicação serial.", e);
        }
    }

    /**
     * Método que fecha a comunicação com a porta serial.
     * Este método é anotado com @PreDestroy, garantindo que será executado
     * automaticamente pelo Spring antes do bean ser destruído.
     * Isso é crucial para liberar a porta COM.
     */
    @PreDestroy
    public void close() {
        if (serialPort != null) {
            try {
                if (outputStream != null) {
                    outputStream.close(); // Fecha o stream de saída
                    logger.debug("OutputStream da porta serial {} fechado.", portName);
                }
                serialPort.close(); // Fecha a porta serial
                logger.info("Porta serial {} fechada com sucesso.", portName);
            } catch (IOException e) {
                logger.error("Erro de I/O ao fechar o stream da porta serial {}: {}", portName, e.getMessage(), e);
            } catch (Exception e) {
                logger.error("Erro inesperado ao fechar a porta serial {}: {}", portName, e.getMessage(), e);
            }
        } else {
            logger.warn("Tentativa de fechar a porta serial, mas ela não estava aberta ou serialPort é nulo.");
        }
    }

    /**
     * Envia um único byte (ou caractere, que é um byte em ASCII) pela porta serial.
     * O 'int data' é automaticamente convertido para byte ao ser escrito.
     *
     * @param data O inteiro (representando um caractere ASCII) a ser enviado.
     * @throws IOException Se ocorrer um erro de I/O durante a escrita.
     */
    public void enviaDados(int data) throws IOException {
        // Verifica se a porta serial e o stream de saída estão inicializados
        if (outputStream == null) {
            logger.warn("Tentativa de enviar dados, mas a porta serial não está aberta ou outputStream é nulo. Verifique a inicialização.");
            throw new IOException("Porta serial não está aberta para escrita.");
        }
        try {
            outputStream.write(data); // Envia o byte pela porta serial
            // Log de depuração para confirmar o envio e o caractere (se for ASCII)
            logger.debug("Dado '{}' (ASCII: {}) enviado para a porta serial {}.", (char) data, data, portName);
        } catch (IOException ex) {
            logger.error("Não foi possível enviar o dado '{}' (ASCII: {}) para a porta COM {}: {}", (char) data, data, portName, ex.getMessage(), ex);
            throw ex; // Relança a exceção para que o serviço chamador possa tratá-la
        }
    }
}