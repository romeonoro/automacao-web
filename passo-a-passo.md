### Configuração do RXTX Library (Crucial para Comunicação Serial)

1.  **Baixe a biblioteca RXTX:** Você precisa do `RXTXcomm.jar` e da biblioteca nativa correspondente (`rxtxSerial.dll` para Windows).
2.  **Verifique a compatibilidade de versão:** **Este é um ponto crítico.** Certifique-se de que a versão do JAR no seu `pom.xml` (`2.1.7` no seu caso) corresponde à versão da DLL nativa que você copiará.
3.  **Localize a DLL:** Copie o arquivo `rxtxSerial.dll` (versão 2.1.7) para o diretório `bin` da sua instalação do JDK em uso. Por exemplo: `C:\Program Files\Java\jdk-23\bin`.

## Configuração e Execução

### 1. Configuração do Arduino

1.  **Fiação:**
    * Conecte o pino longo (anodo) do LED ao pino digital **13** do Arduino (via um resistor de 220-330 Ohm).
    * Conecte o pino curto (catodo) do LED ao GND (terra) do Arduino.

2.  **Código do Arduino (Sketch):**
    Abra o Arduino IDE, copie e cole o seguinte código:

    ```arduino
    int ledPin = 13; // atribui o pino 13 à variável ledPin
    int dado;        // variável que receberá os dados da porta serial

    void setup(){
      Serial.begin(9600); // Frequência da porta serial (baud rate) - DEVE CORRESPONDER AO SPRING BOOT
      pinMode(ledPin, OUTPUT); // define o pino o ledPin como saída
      Serial.println("Arduino pronto para receber comandos."); // Feedback inicial
    }

    void loop(){
      if(Serial.available() > 0){ // verifica se existe comunicação com a porta serial
        dado = Serial.read(); // lê os dados da porta serial
        Serial.print("Recebido: "); // Para depuração: mostra o byte recebido
        Serial.println((char)dado); // Para depuração: mostra o caractere recebido

        switch(dado){
          case '1': // Se o caractere '1' for recebido
            digitalWrite(ledPin, HIGH); // Liga o pino ledPin (LED)
            Serial.println("LED Ligado"); // Feedback
            break;
          case '2': // Se o caractere '2' for recebido
            digitalWrite(ledPin, LOW); // Desliga o pino ledPin (LED)
            Serial.println("LED Desligado"); // Feedback
            break;
          default:
            Serial.print("Comando desconhecido (ASCII: ");
            Serial.print(dado);
            Serial.println(")");
            break;
        }
      }
    }
    ```
3.  **Upload:**
    * Selecione a placa Arduino correta (`Tools` -> `Board`).
    * Selecione a porta COM correta (`Tools` -> `Port`).
    * Faça o upload do sketch para o Arduino.
    * **IMPORTANTE:** Após o upload, **FECHE O MONITOR SERIAL DO ARDUINO IDE**. A porta serial só pode ser usada por uma aplicação por vez.

### 2. Configuração do Banco de Dados MySQL

1.  Crie um banco de dados no MySQL (ex: `automacao`).
    ```sql
    CREATE DATABASE automacao;
    ```
2.  Configure as credenciais do banco de dados no `src/main/resources/application.properties`:

    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/automacao?useTimezone=true&serverTimezone=UTC&createDatabaseIfNotExist=true
    spring.datasource.username=seu_usuario_mysql
    spring.datasource.password=sua_senha_mysql
    spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
    spring.jpa.hibernate.ddl-auto=update # Ou create, create-drop para testar, mas 'update' é bom para desenvolvimento
    spring.jpa.show-sql=true
    spring.jpa.properties.hibernate.format_sql=true
    ```

    A tabela `log_entry` será criada automaticamente pelo Hibernate na primeira execução (se `ddl-auto` for `update` ou `create`).

### 3. Configuração da Aplicação Spring Boot

1.  **`application.properties` (Serial Port):**
    Adicione ou configure as seguintes propriedades em `src/main/resources/application.properties`:

    ```properties
    # Configurações da porta serial para o Arduino
    arduino.serial.port=COM4 # Ajuste para a porta COM do seu Arduino (ex: COM3, COM4, /dev/ttyUSB0, /dev/ttyACM0)
    arduino.serial.baudrate=9600 # Deve ser a mesma que no código do Arduino
    ```
2.  **`LedService.java`:**
    Certifique-se de que seu `LedService.java` está enviando os comandos `'1'` e `'2'` para ligar e desligar, conforme discutimos:

    ```java
    // ... dentro de LedService.java
    public String turnOnLed() {
        char commandChar = '1'; // Envia '1' para ligar
        // ... restante do código ...
    }

    public String turnOffLed() {
        char commandChar = '2'; // Envia '2' para desligar
        // ... restante do código ...
    }
    ```
3.  **Construir o Projeto:**
    Abra o terminal na raiz do projeto e execute:
    ```bash
    mvn clean install
    ```
    Ou no IntelliJ IDEA, vá em `Build` -> `Rebuild Project`.

4.  **Executar a Aplicação:**
    No IntelliJ IDEA, clique no botão "Run" (seta verde) na classe `AutomacaoWebApplication`.
    Alternativamente, via terminal:
    ```bash
    mvn spring-boot:run
    ```

### 4. Acessar a Interface Web

Após a aplicação Spring Boot iniciar abra seu navegador e acesse:

`http://localhost:8080`
