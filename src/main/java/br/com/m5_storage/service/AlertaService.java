package br.com.m5_storage.service;

import br.com.m5_storage.dto.alerta.AlertaListagemDTO;
import br.com.m5_storage.entity.alerta.Alerta;
import br.com.m5_storage.exception.IdNaoEncontradoException;
import br.com.m5_storage.repository.AlertaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.m5_storage.entity.setor.Setor;
import java.util.List;

@Service
public class AlertaService {

    private final AlertaRepository alertaRepository;

    public AlertaService(AlertaRepository alertaRepository) {
        this.alertaRepository = alertaRepository;
    }

    // Regra 14: dashboard — todos os alertas ativos
    @Transactional(readOnly = true)
    public List<AlertaListagemDTO> readAlertasAtivos() {
        return alertaRepository.findByResolvidoFalseOrderByDataAlertaDesc()
                .stream().map(this::toDTO).toList();
    }

    // Alertas por recurso
    @Transactional(readOnly = true)
    public List<AlertaListagemDTO> readAlertasByRecurso(Long recursoId) {
        return alertaRepository.findByRecursoIdAndResolvidoFalse(recursoId)
                .stream().map(this::toDTO).toList();
    }

    // Regra 6/20: alertas por setor
    @Transactional(readOnly = true)
    public List<AlertaListagemDTO> readAlertasBySetor(Long setorId) {
        return alertaRepository
                .findByRecurso_Setor_IdAndResolvidoFalseOrderByDataAlertaDesc(setorId)
                .stream().map(this::toDTO).toList();
    }

    // Regra 6: alertas por base
    @Transactional(readOnly = true)
    public List<AlertaListagemDTO> readAlertasByBase(Long baseId) {
        return alertaRepository
                .findByRecurso_Setor_BaseIdAndResolvidoFalseOrderByDataAlertaDesc(baseId)
                .stream().map(this::toDTO).toList();
    }

    // Regra 8: resolver alerta manualmente
    @Transactional
    public AlertaListagemDTO resolverAlerta(Long id) {
        Alerta alerta = alertaRepository.findById(id)
                .orElseThrow(() -> new IdNaoEncontradoException(
                        "Alerta não encontrado com id: " + id
                ));
        alerta.setResolvido(true);
        return toDTO(alertaRepository.save(alerta));
    }

    // ── helpers ──────────────────────────────────────────────

    private AlertaListagemDTO toDTO(Alerta a) {
        Setor setor = a.getRecurso().getSetor();
        return new AlertaListagemDTO(
                a.getId(),
                a.getRecurso().getId(),
                a.getRecurso().getNome(),
                setor.getId(),
                setor.getInfo().getNome(),
                setor.getBase().getId(),
                a.getMensagem(),
                a.getNivel(),
                a.getResolvido(),
                a.getDataAlerta()
        );
    }
}