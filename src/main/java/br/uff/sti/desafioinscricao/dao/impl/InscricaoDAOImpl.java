package br.uff.sti.desafioinscricao.dao.impl;

import br.uff.sti.desafioinscricao.dao.InscricaoDAO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class InscricaoDAOImpl implements InscricaoDAO {

    private final JdbcTemplate jdbcTemplate;

    public InscricaoDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void inscrever(String matricula, long idTurma) {
        jdbcTemplate.update("INSERT INTO inscricao VALUES (?, ?)", matricula, idTurma);
    }

    @Override
    public void desinscrever(String matricula, long idTurma) {
        jdbcTemplate.update("DELETE FROM inscricao WHERE matricula_aluno = ? AND ID_TURMA = ?", matricula, idTurma);
    }

    @Override
    public boolean estaInscrito(String matricula, long idTurma) {
        return Optional.ofNullable(jdbcTemplate.queryForObject(
                "SELECT count(*) FROM inscricao WHERE matricula_aluno = ? AND id_turma = ?",
                new Object[]{matricula, idTurma},
                Long.class
        )).orElse(0L) > 0;
    }
}
