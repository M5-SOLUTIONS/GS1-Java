package br.com.m5_storage.service;

import br.com.m5_storage.dto.alerta.AlertaListagemDTO;
import br.com.m5_storage.entity.alerta.Alerta;
import br.com.m5_storage.exception.IdNaoEncontradoException;
import br.com.m5_storage.repository.AlertaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AlertaService {

    private final AlertaRepository alertaRepository;

    public AlertaService(AlertaRepository alertaRepository) {
        this.alertaRepository = alertaRepository;
    }

    @Transactional(readOnly = true)
    public List<AlertaListagemDTO> readAlertasAtivos(Long baseId) {

        return alertaRepository
                .findByRecursoBaseIdAndResolvidoFalseOrderByDataAlertaDesc(baseId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AlertaListagemDTO> readAlertasByRecurso(
            Long baseId,
            Long recursoId
    ) {

        return alertaRepository
                .findByRecursoIdAndRecursoBaseIdAndResolvidoFalse(
                        recursoId,
                        baseId
                )
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional
    public AlertaListagemDTO resolverAlerta(Long id) {

        Alerta alerta = alertaRepository.findById(id)
                .orElseThrow(() -> new IdNaoEncontradoException(
                        "Alerta não encontrado com id: " + id
                ));

        alerta.setResolvido(true);

        return toDTO(alertaRepository.save(alerta));
    }

    private AlertaListagemDTO toDTO(Alerta a) {

        return new AlertaListagemDTO(
                a.getId(),
                a.getRecurso().getId(),
                a.getRecurso().getNome(),
                a.getRecurso().getBase().getId(),
                a.getRecurso().getBase().getNome(),
                a.getMensagem(),
                a.getNivel(),
                a.getResolvido(),
                a.getDataAlerta()
        );
    }
}