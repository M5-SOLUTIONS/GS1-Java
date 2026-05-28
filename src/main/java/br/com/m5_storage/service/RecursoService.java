package br.com.m5_storage.service;

import br.com.m5_storage.dto.recurso.*;
import br.com.m5_storage.entity.base.Base;
import br.com.m5_storage.entity.recurso.Recurso;
import br.com.m5_storage.entity.recurso.StatusRecurso;
import br.com.m5_storage.exception.IdNaoEncontradoException;
import br.com.m5_storage.repository.BaseRepository;
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
    private final BaseRepository baseRepository;

    public RecursoService(RecursoRepository recursoRepository,
                          MovimentacaoRepository movimentacaoRepository,
                          BaseRepository baseRepository) {
        this.recursoRepository = recursoRepository;
        this.movimentacaoRepository = movimentacaoRepository;
        this.baseRepository = baseRepository;
    }

    @Transactional
    public RecursoListagemDTO createRecurso(RecursoCadastroDTO dto) {

        Base base = baseRepository.findById(dto.baseId())
                .orElseThrow(() -> new IdNaoEncontradoException(
                        "Base não encontrada com id: " + dto.baseId()
                ));

        Recurso recurso = Recurso.builder()
                .nome(dto.nome())
                .categoria(dto.categoria())
                .quantidade(dto.quantidade())
                .minimo(dto.minimo())
                .capacidadeMaxima(dto.capacidadeMaxima())
                .critico(dto.critico() != null && dto.critico())
                .status(calcularStatus(dto.quantidade(), dto.minimo(), dto.capacidadeMaxima()))
                .ultimaAtualizacao(LocalDateTime.now())
                .base(base)
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

    @Transactional(readOnly = true)
    public List<RecursoListagemDTO> readRecursosByStatus(StatusRecurso status) {
        return recursoRepository.findByStatus(status)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RecursoListagemDTO> readRecursosByBase(Long baseId) {

        return recursoRepository.findByBaseId(baseId)
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
        recurso.setCapacidadeMaxima(dto.capacidadeMaxima());
        recurso.setCritico(dto.critico() != null && dto.critico());
        recurso.setStatus(calcularStatus(dto.quantidade(), dto.minimo(), dto.capacidadeMaxima()));
        recurso.setUltimaAtualizacao(LocalDateTime.now());

        return toDTO(recursoRepository.save(recurso));
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

    public StatusRecurso calcularStatus(Double quantidade, Double minimo, Double capacidadeMaxima) {
        double limiteAtencao = capacidadeMaxima / 3;

        if (quantidade > limiteAtencao) {
            return StatusRecurso.OK;
        }

        if (quantidade > minimo) {
            return StatusRecurso.ATENCAO;
        }

        return StatusRecurso.CRITICO;
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
                r.getBase().getId()
        );
    }
}