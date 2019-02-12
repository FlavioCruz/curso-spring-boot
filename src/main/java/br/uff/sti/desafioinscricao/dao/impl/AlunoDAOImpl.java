package br.uff.sti.desafioinscricao.dao.impl;

import br.uff.sti.desafioinscricao.dao.AlunoDAO;
import br.uff.sti.desafioinscricao.model.AnoSemestre;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AlunoDAOImpl implements AlunoDAO {

    private final JdbcTemplate jdbcTemplate;

    public AlunoDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public long getCargaHorariaTotal(String matricula, AnoSemestre anoSemestre) {
        try{
            StringBuilder query = new StringBuilder("SELECT SUM(t.carga_horaria) FROM inscricao as i, turma as t ")
                                            .append("WHERE i.id_turma = t.id                                     ")
                                            .append("AND i.matricula_aluno = ? AND t.ano_semestre = ?            ");

            return Optional.ofNullable(jdbcTemplate.queryForObject(query.toString(),
                    new Object[]{matricula, anoSemestre.intValue()}, Long.class)).orElse(0L);
        }catch (EmptyResultDataAccessException e){
            return 0;
        }
    }

    /**
     * Atente-se que queryForObject retornará nulo se não achar aluno nenhum neste caso. tente usar optional.
     * @param matricula
     * @return
     */
    @Override
    public boolean existeAluno(String matricula) {

        StringBuilder query = new StringBuilder("SELECT COUNT(*) FROM ALUNO ")
                                        .append("WHERE matricula = ?        ");

        return Optional.ofNullable(jdbcTemplate.queryForObject(query.toString(),
                new Object[]{matricula}, Integer.class)).orElse(0 ) > 0;
    }
}
