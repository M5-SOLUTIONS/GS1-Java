package br.com.m5_storage.service;

import br.com.m5_storage.dto.energia.EnergiaAtualizarDTO;
import br.com.m5_storage.dto.energia.EnergiaCadastroDTO;
import br.com.m5_storage.dto.energia.EnergiaListagemDTO;
import br.com.m5_storage.entity.recurso.Energia;
import br.com.m5_storage.exception.IdNaoEncontradoException;
import br.com.m5_storage.repository.EnergiaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EnergiaService {

    private final EnergiaRepository energiaRepository;
    private final RecursoService recursoService;

    public EnergiaService(EnergiaRepository energiaRepository,
                          RecursoService recursoService) {
        this.energiaRepository = energiaRepository;
        this.recursoService = recursoService;
    }

    @Transactional
    public EnergiaListagemDTO createEnergia(EnergiaCadastroDTO dto) {

        Energia energia = new Energia();

        energia.setNome(dto.nome());
        energia.setCategoria(dto.categoria());
        energia.setQuantidade(dto.quantidade());
        energia.setMinimo(dto.minimo());
        energia.setCapacidadeMaxima(dto.capacidadeMaxima());
        energia.setCritico(dto.critico() != null && dto.critico());
        energia.setTipoEnergia(dto.tipoEnergia());

        energia.setStatus(
                recursoService.calcularStatus(
                        dto.quantidade(),
                        dto.minimo(),
                        dto.capacidadeMaxima()
                )
        );

        energia.setUltimaAtualizacao(LocalDateTime.now());

        return toDTO(energiaRepository.save(energia));
    }

    @Transactional(readOnly = true)
    public List<EnergiaListagemDTO> readAllEnergias() {
        return energiaRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public EnergiaListagemDTO readEnergiaById(Long id) {
        return toDTO(findOrThrow(id));
    }

    @Transactional
    public EnergiaListagemDTO updateEnergia(Long id, EnergiaAtualizarDTO dto) {

        Energia energia = findOrThrow(id);

        energia.setNome(dto.nome());
        energia.setCategoria(dto.categoria());
        energia.setQuantidade(dto.quantidade());
        energia.setMinimo(dto.minimo());
        energia.setCapacidadeMaxima(dto.capacidadeMaxima());
        energia.setCritico(dto.critico() != null && dto.critico());
        energia.setTipoEnergia(dto.tipoEnergia());

        energia.setStatus(
                recursoService.calcularStatus(
                        dto.quantidade(),
                        dto.minimo(),
                        dto.capacidadeMaxima()
                )
        );

        energia.setUltimaAtualizacao(LocalDateTime.now());

        return toDTO(energiaRepository.save(energia));
    }

    // ── helpers ──────────────────────────────────────────────

    private Energia findOrThrow(Long id) {
        return energiaRepository.findById(id)
                .orElseThrow(() -> new IdNaoEncontradoException(
                        "Energia não encontrada com id: " + id
                ));
    }

    private EnergiaListagemDTO toDTO(Energia e) {
        return new EnergiaListagemDTO(
                e.getId(),
                e.getNome(),
                e.getCategoria(),
                e.getQuantidade(),
                e.getMinimo(),
                e.getCapacidadeMaxima(),
                e.getCritico(),
                e.getStatus(),
                e.getTipoEnergia(),
                e.getPorcentagem(),
                e.getUltimaAtualizacao()
        );
    }
}