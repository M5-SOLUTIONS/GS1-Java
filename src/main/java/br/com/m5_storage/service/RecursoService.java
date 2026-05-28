package br.com.m5_storage.service;

import br.com.m5_storage.dto.recurso.RecursoAtualizarDTO;
import br.com.m5_storage.dto.recurso.RecursoCadastroDTO;
import br.com.m5_storage.dto.recurso.RecursoListagemDTO;
import br.com.m5_storage.entity.alerta.Alerta;
import br.com.m5_storage.entity.recurso.Recurso;
import br.com.m5_storage.entity.recurso.StatusRecurso;
import br.com.m5_storage.entity.setor.Setor;
import br.com.m5_storage.exception.IdNaoEncontradoException;
import br.com.m5_storage.repository.AlertaRepository;
import br.com.m5_storage.repository.MovimentacaoRepository;
import br.com.m5_storage.repository.RecursoRepository;
import br.com.m5_storage.repository.SetorRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RecursoService {

    private final RecursoRepository recursoRepository;
    private final MovimentacaoRepository movimentacaoRepository;
    private final SetorRepository setorRepository;
    private final AlertaRepository alertaRepository;

    public RecursoService(RecursoRepository recursoRepository,
                          MovimentacaoRepository movimentacaoRepository,
                          SetorRepository setorRepository,
                          AlertaRepository alertaRepository) {
        this.recursoRepository = recursoRepository;
        this.movimentacaoRepository = movimentacaoRepository;
        this.setorRepository = setorRepository;
        this.alertaRepository = alertaRepository;
    }

    @Transactional
    public RecursoListagemDTO createRecurso(RecursoCadastroDTO dto) {
        Setor setor = setorRepository.findById(dto.setorId())
                .orElseThrow(() -> new IdNaoEncontradoException(
                        "Setor não encontrado com id: " + dto.setorId()
                ));

        Recurso recurso = Recurso.builder()
                .setor(setor)
                .nome(dto.nome())
                .categoria(dto.categoria())
                .quantidade(dto.quantidade())
                .minimo(dto.minimo())
                .capacidadeMaxima(dto.capacidadeMaxima())
                .critico(dto.critico() != null && dto.critico())
                .status(calcularStatus(dto.quantidade(), dto.minimo()))
                .ultimaAtualizacao(LocalDateTime.now())
                .build();

        return toDTO(recursoRepository.save(recurso));
    }

    @Transactional(readOnly = true)
    public List<RecursoListagemDTO> readAllRecursos() {
        return recursoRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public RecursoListagemDTO readRecursoById(Long id) {
        return toDTO(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<RecursoListagemDTO> readRecursosByStatus(StatusRecurso status) {
        return recursoRepository.findByStatus(status).stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<RecursoListagemDTO> readRecursosBySetor(Long setorId) {
        return recursoRepository.findBySetorId(setorId).stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<RecursoListagemDTO> readRecursosByBase(Long baseId) {
        return recursoRepository.findBySetor_BaseId(baseId).stream().map(this::toDTO).toList();
    }

    @Transactional
    public RecursoListagemDTO updateRecurso(Long id, RecursoAtualizarDTO dto) {
        Recurso recurso = findOrThrow(id);

        recurso.setNome(dto.nome());
        recurso.setCategoria(dto.categoria());
        recurso.setQuantidade(dto.quantidade());
        recurso.setMinimo(dto.minimo());
        recurso.setCapacidadeMaxima(dto.capacidadeMaxima());
        recurso.setCritico(dto.critico() != null && dto.critico());
        recurso.setStatus(calcularStatus(dto.quantidade(), dto.minimo()));
        recurso.setUltimaAtualizacao(LocalDateTime.now());

        recursoRepository.save(recurso);
        sincronizarAlertas(recurso);

        return toDTO(recurso);
    }

    @Transactional
    public void deleteRecurso(Long id) {
        findOrThrow(id);

        if (movimentacaoRepository.existsByRecursoId(id)) {
            throw new DataIntegrityViolationException(
                    "Não é possível remover o recurso pois existem movimentações vinculadas."
            );
        }

        recursoRepository.deleteById(id);
    }

    // ── helpers ──────────────────────────────────────────────

    public Recurso findOrThrow(Long id) {
        return recursoRepository.findById(id)
                .orElseThrow(() -> new IdNaoEncontradoException(
                        "Recurso não encontrado com id: " + id
                ));
    }

    /**
     * Regra 4:
     * quantidade > minimo  → OK
     * quantidade == minimo → ATENCAO
     * quantidade < minimo  → CRITICO
     */
    public StatusRecurso calcularStatus(Double quantidade, Double minimo) {
        if (quantidade > minimo)       return StatusRecurso.OK;
        if (quantidade.equals(minimo)) return StatusRecurso.ATENCAO;
        return StatusRecurso.CRITICO;
    }

    /**
     * Regras 5/7/8: sincroniza alertas após mudança de status.
     *
     * Correção: quando sai de CRITICO para ATENCAO, atualiza o nível
     * do alerta existente em vez de deixá-lo desatualizado.
     * Só resolve definitivamente quando chega em OK (quantidade > minimo).
     */
    public void sincronizarAlertas(Recurso recurso) {
        // Regra 7: só recursos críticos geram alertas
        if (!recurso.getCritico()) return;

        StatusRecurso status = recurso.getStatus();

        if (status == StatusRecurso.CRITICO || status == StatusRecurso.ATENCAO) {

            List<Alerta> alertasAtivos = alertaRepository
                    .findByRecursoIdAndResolvidoFalse(recurso.getId());

            if (alertasAtivos.isEmpty()) {
                // Regra 5: gera novo alerta
                alertaRepository.save(Alerta.builder()
                        .recurso(recurso)
                        .mensagem("Recurso " + recurso.getNome()
                                + " atingiu nível " + status.name()
                                + ". Quantidade: " + recurso.getQuantidade())
                        .nivel(status.name())
                        .resolvido(false)
                        .dataAlerta(LocalDateTime.now())
                        .build());
            } else {
                // Correção erro 3: atualiza o nível do alerta existente
                // (ex: CRITICO → ATENCAO após reabastecimento parcial)
                alertasAtivos.forEach(a -> {
                    a.setNivel(status.name());
                    a.setMensagem("Recurso " + recurso.getNome()
                            + " em nível " + status.name()
                            + ". Quantidade: " + recurso.getQuantidade());
                    alertaRepository.save(a);
                });
            }

        } else if (status == StatusRecurso.OK) {
            // Regra 8: resolve todos os alertas ativos ao voltar ao nível seguro
            alertaRepository.findByRecursoIdAndResolvidoFalse(recurso.getId())
                    .forEach(a -> {
                        a.setResolvido(true);
                        alertaRepository.save(a);
                    });
        }
    }

    public RecursoListagemDTO toDTO(Recurso r) {
        return new RecursoListagemDTO(
                r.getId(),
                r.getNome(),
                r.getCategoria(),
                r.getQuantidade(),
                r.getMinimo(),
                r.getCapacidadeMaxima(),
                r.getCritico(),
                r.getStatus(),
                r.getUltimaAtualizacao(),
                r.getSetor().getId(),
                r.getSetor().getInfo().getNome(),
                r.getSetor().getBase().getId()
        );
    }
}