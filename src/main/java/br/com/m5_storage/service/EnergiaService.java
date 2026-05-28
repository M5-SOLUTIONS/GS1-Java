package br.com.m5_storage.service;

import br.com.m5_storage.dto.energia.EnergiaAtualizarDTO;
import br.com.m5_storage.dto.energia.EnergiaCadastroDTO;
import br.com.m5_storage.dto.energia.EnergiaListagemDTO;
import br.com.m5_storage.entity.base.Base;
import br.com.m5_storage.entity.recurso.Energia;
import br.com.m5_storage.exception.IdNaoEncontradoException;
import br.com.m5_storage.repository.BaseRepository;
import br.com.m5_storage.repository.EnergiaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EnergiaService {

    private final EnergiaRepository energiaRepository;
    private final BaseRepository baseRepository;
    private final RecursoService recursoService;

    public EnergiaService(EnergiaRepository energiaRepository,
                          BaseRepository baseRepository,
                          RecursoService recursoService) {
        this.energiaRepository = energiaRepository;
        this.baseRepository = baseRepository;
        this.recursoService = recursoService;
    }

    @Transactional
    public EnergiaListagemDTO createEnergia(EnergiaCadastroDTO dto) {

        Base base = baseRepository.findById(dto.baseId())
                .orElseThrow(() -> new IdNaoEncontradoException(
                        "Base não encontrada com id: " + dto.baseId()
                ));

        Energia energia = new Energia();

        energia.setNome(dto.nome());
        energia.setCategoria(dto.categoria());
        energia.setQuantidade(dto.quantidade());
        energia.setMinimo(dto.minimo());
        energia.setCritico(dto.critico() != null && dto.critico());
        energia.setTipoEnergia(dto.tipoEnergia());
        energia.setBase(base);

        energia.setStatus(
                recursoService.calcularStatus(dto.quantidade(), dto.minimo())
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

        Base base = baseRepository.findById(dto.baseId())
                .orElseThrow(() -> new IdNaoEncontradoException(
                        "Base não encontrada com id: " + dto.baseId()
                ));

        energia.setNome(dto.nome());
        energia.setCategoria(dto.categoria());
        energia.setQuantidade(dto.quantidade());
        energia.setMinimo(dto.minimo());
        energia.setCritico(dto.critico() != null && dto.critico());
        energia.setTipoEnergia(dto.tipoEnergia());
        energia.setBase(base);

        energia.setStatus(
                recursoService.calcularStatus(dto.quantidade(), dto.minimo())
        );

        energia.setUltimaAtualizacao(LocalDateTime.now());

        return toDTO(energiaRepository.save(energia));
    }

    private Energia findOrThrow(Long id) {
        return energiaRepository.findById(id)
                .orElseThrow(() -> new IdNaoEncontradoException(
                        "Energia não encontrada com id: " + id
                ));
    }

    private EnergiaListagemDTO toDTO(Energia e) {

        Double porcentagem = 0.0;

        if (e.getQuantidade() != null
                && e.getMinimo() != null
                && e.getMinimo() > 0) {

            porcentagem = (e.getQuantidade() / e.getMinimo()) * 100;
        }

        return new EnergiaListagemDTO(
                e.getId(),
                e.getNome(),
                e.getCategoria(),
                e.getQuantidade(),
                e.getMinimo(),
                e.getCritico(),
                e.getStatus(),
                e.getTipoEnergia(),
                porcentagem,
                e.getBase().getId(),
                e.getBase().getNome(),
                e.getUltimaAtualizacao()
        );
    }
}