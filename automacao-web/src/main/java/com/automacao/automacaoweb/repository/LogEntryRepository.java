
package com.automacao.automacaoweb.repository;

import com.automacao.automacaoweb.model.LogEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository // Marca esta interface como um componente de repositório
public interface LogEntryRepository extends JpaRepository<LogEntry, Long> {
    // Spring Data JPA fornecerá automaticamente métodos como save(), findById(), findAll(), deleteById(), etc.
    // Você pode adicionar métodos de consulta personalizados aqui se precisar, como:
     //List<LogEntry> findByAction(String action);
}