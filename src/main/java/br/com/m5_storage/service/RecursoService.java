package br.com.m5_storage.service;

import br.com.m5_storage.dto.recurso.RecursoAtualizarDTO;
import br.com.m5_storage.dto.recurso.RecursoCadastroDTO;
import br.com.m5_storage.dto.recurso.RecursoListagemDTO;
import br.com.m5_storage.entity.recurso.Recurso;
import br.com.m5_storage.entity.recurso.StatusRecurso;
import br.com.m5_storage.exception.IdNaoEncontradoException;
import br.com.m5_storage.repository.MovimentacaoRepository;
import br.com.m5_storage.repository.RecursoRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RecursoService {

    private final RecursoRepository recursoRepository;
    private final MovimentacaoRepository movimentacaoRepository;

    public RecursoService(RecursoRepository recursoRepository,
                          MovimentacaoRepository movimentacaoRepository) {
        this.recursoRepository = recursoRepository;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    @Transactional
    public RecursoListagemDTO createRecurso(RecursoCadastroDTO dto) {
        Recurso recurso = Recurso.builder()
                .nome(dto.nome())
                .categoria(dto.categoria())
                .quantidade(dto.quantidade())
                .minimo(dto.minimo())
                .critico(dto.critico() != null && dto.critico())
                .status(calcularStatus(dto.quantidade(), dto.minimo()))
                .ultimaAtualizacao(LocalDateTime.now())
                .build();

        return toDTO(recursoRepository.save(recurso));
    }

    @Transactional(readOnly = true)
    public List<RecursoListagemDTO> readAllRecursos() {
        return recursoRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public RecursoListagemDTO readRecursoById(Long id) {
        return toDTO(findOrThrow(id));
    }

    // Regra 16: dashboard filtra por status
    @Transactional(readOnly = true)
    public List<RecursoListagemDTO> readRecursosByStatus(StatusRecurso status) {
        return recursoRepository.findByStatus(status)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional
    public RecursoListagemDTO updateRecurso(Long id, RecursoAtualizarDTO dto) {
        Recurso recurso = findOrThrow(id);

        recurso.setNome(dto.nome());
        recurso.setCategoria(dto.categoria());
        recurso.setQuantidade(dto.quantidade());
        recurso.setMinimo(dto.minimo());
        recurso.setCritico(dto.critico() != null && dto.critico());
        // Regra 8: recalcula status
        recurso.setStatus(calcularStatus(dto.quantidade(), dto.minimo()));
        recurso.setUltimaAtualizacao(LocalDateTime.now());

        return toDTO(recursoRepository.save(recurso));
    }

    @Transactional
    public void deleteRecurso(Long id) {
        findOrThrow(id);

        // Regra 19: impede remoção se existirem movimentações vinculadas
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
     * Regra 8: reutilizado por EnergiaService e MedicamentoService.
     * quantidade > minimo  → OK
     * quantidade == minimo → ATENCAO
     * quantidade < minimo  → CRITICO
     */
    public StatusRecurso calcularStatus(Double quantidade, Double minimo) {
        if (quantidade > minimo)        return StatusRecurso.OK;
        if (quantidade.equals(minimo))  return StatusRecurso.ATENCAO;
        return StatusRecurso.CRITICO;
    }

    public RecursoListagemDTO toDTO(Recurso r) {
        return new RecursoListagemDTO(
                r.getId(), r.getNome(), r.getCategoria(),
                r.getQuantidade(), r.getMinimo(), r.getCritico(),
                r.getStatus(), r.getUltimaAtualizacao()
        );
    }
}