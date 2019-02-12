package br.uff.sti.desafioinscricao;

import br.uff.sti.desafioinscricao.dao.AlunoDAO;
import br.uff.sti.desafioinscricao.dao.InscricaoDAO;
import br.uff.sti.desafioinscricao.model.AnoSemestre;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

//@Disabled
@DisplayName("Grupo-02 fazendo as regras de obter carga horária de um aluno em um ano-semestre.")
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class Grupo02Test {

    @Autowired
    private AlunoDAO alunoDAO;

    @Autowired
    private InscricaoDAO inscricaoDAO;

    /**
     * Teste para dar algumas dicas sobre o optional que pode ser usado nesta etapa dos exercícios.
     */
    @Nested
    class quando_usando_Optional{

        @Test
        public void quando_usa_optional_com_valor_preenchido_entao_retorna_ele(){
            final Optional<Long> optNumero = Optional.of(1L);

            assertEquals(1L, optNumero.get().longValue());
        }

        @Test
        public void quando_usa_optional_vazio_entao_gera_excecao(){
            final Optional<Long> optNumero = Optional.empty();

            assertThrows(NoSuchElementException.class, ()->{
                optNumero.get();//vai gerar uma exceção;
            });
        }

        @Test
        public void quando_usa_optional_com_valor_possivelmente_vazio_entao_ok(){
            final Optional<Long> optNumero = Optional.ofNullable(null);

            Assertions.assertFalse(optNumero.isPresent());
        }

        @Test
        public void quando_usa_optional_com_valor_nao_nulo_entao_posso_mapealo(){
            final Optional<Long> optNumero = Optional.of(3L);

            final Optional<String> optTexto = optNumero.map((l)->l.toString());

            assertEquals("3", optTexto.get());
        }

        @Test
        public void quando_usa_optional_vazio_entao_posso_usar_valor_padrao(){
            final Optional<Long> optNumero = Optional.empty();

            final String texto = optNumero.map((l)->"3").orElse("0");

            assertEquals("0", texto);//veja, foi considerado o valor 'orElse' e não o valor mapeado 3.
        }
    }

    @Nested
    class quando_aluno{

        private final String MATRICULA = "1111";
        private final AnoSemestre ANO_SEMESTRE = new AnoSemestre(20182);

        @Test
        public void aluno_existe(){
            boolean alunoExiste = alunoDAO.existeAluno(MATRICULA);

            assertEquals(true, alunoExiste);
        }

        @Test
        public void nao_possui_inscricao_em_20182_entao_nao_possui_carga_horaria(){

            long cargaHoraria = alunoDAO.getCargaHorariaTotal(MATRICULA, ANO_SEMESTRE);

            assertEquals(0, cargaHoraria);
        }

        @Nested
        class possui_uma_turma{

            private final long TURMA_ID = 21L; //turma M1 MAT0001 em 20182

            /**
             * Faz a inscrição para testar a quantidade de carga horária
             */
            @BeforeEach
            public void init(){
                inscricaoDAO.inscrever(MATRICULA, TURMA_ID);
            }

            @AfterEach
            public void destroy(){
                inscricaoDAO.desinscrever(MATRICULA, TURMA_ID);
            }

            @Test
            public void entao_conta_a_carga_horaria_desta_turma(){

                final long cargaHorariaTotal = alunoDAO.getCargaHorariaTotal(MATRICULA, ANO_SEMESTRE);

                assertEquals(60, cargaHorariaTotal);
            }

            @Nested
            class pussui_mais_uma_turma_no_mesmo_semestre{

                private final long TURMA_B_ID = 20L;
                /**
                 * Faz a inscrição para testar a quantidade de carga horária
                 */
                @BeforeEach
                public void init(){
                    inscricaoDAO.inscrever(MATRICULA, TURMA_B_ID);
                }

                @AfterEach
                public void destroy(){
                    inscricaoDAO.desinscrever(MATRICULA, TURMA_B_ID);
                }

                @Test
                public void entao_conta_a_carga_horaria_das_2_turmas(){
                    final long cargaHorariaTotal = alunoDAO.getCargaHorariaTotal(MATRICULA, ANO_SEMESTRE);

                    assertEquals(60+30, cargaHorariaTotal);
                }
            }

            @Nested
            class pussui_mais_uma_turma_em_semestres_diferentes{

                private final long TURMA_C = 9L;
                /**
                 * Faz a inscrição para testar a quantidade de carga horária
                 */
                @BeforeEach
                public void init(){
                    inscricaoDAO.inscrever(MATRICULA, TURMA_C);
                }

                @AfterEach
                public void destroy(){
                    inscricaoDAO.desinscrever(MATRICULA, TURMA_C);
                }

                @Test
                public void entao_nao_conta_a_carga_horaria_da_turma_do_ano_semestre_diferente(){
                    final long cargaHorariaTotal = alunoDAO.getCargaHorariaTotal(MATRICULA, ANO_SEMESTRE);

                    assertEquals(60, cargaHorariaTotal);
                }

                @Test
                public void entao_nao_conta_a_carga_horaria_da_outra_turma_de_ano_semestre_diferente(){
                    final long cargaHorariaTotal = alunoDAO.getCargaHorariaTotal(MATRICULA, new AnoSemestre(20181));

                    assertEquals(20, cargaHorariaTotal);
                }
            }
        }
    }
}
