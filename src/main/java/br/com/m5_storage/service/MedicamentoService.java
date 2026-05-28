package br.com.m5_storage.service;

import br.com.m5_storage.dto.medicamento.MedicamentoAtualizarDTO;
import br.com.m5_storage.dto.medicamento.MedicamentoCadastroDTO;
import br.com.m5_storage.dto.medicamento.MedicamentoListagemDTO;
import br.com.m5_storage.entity.recurso.Medicamento;
import br.com.m5_storage.exception.IdNaoEncontradoException;
import br.com.m5_storage.repository.MedicamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MedicamentoService {

    private final MedicamentoRepository medicamentoRepository;
    private final RecursoService recursoService;

    public MedicamentoService(MedicamentoRepository medicamentoRepository,
                              RecursoService recursoService) {
        this.medicamentoRepository = medicamentoRepository;
        this.recursoService = recursoService;
    }

    @Transactional
    public MedicamentoListagemDTO createMedicamento(MedicamentoCadastroDTO dto) {
        Medicamento medicamento = new Medicamento();
        medicamento.setNome(dto.nome());
        medicamento.setCategoria(dto.categoria());
        medicamento.setQuantidade(dto.quantidade());
        medicamento.setMinimo(dto.minimo());
        medicamento.setCritico(dto.critico() != null && dto.critico());
        medicamento.setValidade(dto.validade());
        medicamento.setStatus(recursoService.calcularStatus(dto.quantidade(), dto.minimo()));
        medicamento.setUltimaAtualizacao(LocalDateTime.now());

        return toDTO(medicamentoRepository.save(medicamento));
    }

    @Transactional(readOnly = true)
    public List<MedicamentoListagemDTO> readAllMedicamentos() {
        return medicamentoRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public MedicamentoListagemDTO readMedicamentoById(Long id) {
        return toDTO(findOrThrow(id));
    }

    // Regra 11: medicamentos com validade já vencida
    @Transactional(readOnly = true)
    public List<MedicamentoListagemDTO> readMedicamentosVencidos() {
        return medicamentoRepository
                .findByValidadeBeforeOrderByValidadeAsc(LocalDate.now())
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // Regra 11: medicamentos que vencem nos próximos N dias
    @Transactional(readOnly = true)
    public List<MedicamentoListagemDTO> readMedicamentosAVencer(int dias) {
        return medicamentoRepository
                .findByValidadeBeforeOrderByValidadeAsc(LocalDate.now().plusDays(dias))
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional
    public MedicamentoListagemDTO updateMedicamento(Long id, MedicamentoAtualizarDTO dto) {
        Medicamento medicamento = findOrThrow(id);

        medicamento.setNome(dto.nome());
        medicamento.setCategoria(dto.categoria());
        medicamento.setQuantidade(dto.quantidade());
        medicamento.setMinimo(dto.minimo());
        medicamento.setCritico(dto.critico() != null && dto.critico());
        medicamento.setValidade(dto.validade());
        medicamento.setStatus(recursoService.calcularStatus(dto.quantidade(), dto.minimo()));
        medicamento.setUltimaAtualizacao(LocalDateTime.now());

        return toDTO(medicamentoRepository.save(medicamento));
    }

    // ── helpers ──────────────────────────────────────────────

    private Medicamento findOrThrow(Long id) {
        return medicamentoRepository.findById(id)
                .orElseThrow(() -> new IdNaoEncontradoException(
                        "Medicamento não encontrado com id: " + id
                ));
    }

    private MedicamentoListagemDTO toDTO(Medicamento m) {
        return new MedicamentoListagemDTO(
                m.getId(), m.getNome(), m.getCategoria(),
                m.getQuantidade(), m.getMinimo(), m.getCritico(),
                m.getStatus(), m.getValidade(), m.getUltimaAtualizacao()
        );
    }
}