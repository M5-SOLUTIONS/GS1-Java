package br.com.m5_storage.service;

import br.com.m5_storage.dto.movimentacao.MovimentacaoCadastroDTO;
import br.com.m5_storage.dto.movimentacao.MovimentacaoListagemDTO;
import br.com.m5_storage.entity.alerta.Alerta;
import br.com.m5_storage.entity.movimentacao.Movimentacao;
import br.com.m5_storage.entity.movimentacao.TipoMovimentacao;
import br.com.m5_storage.entity.recurso.Recurso;
import br.com.m5_storage.entity.recurso.StatusRecurso;
import br.com.m5_storage.entity.usuario.Usuario;
import br.com.m5_storage.exception.IdNaoEncontradoException;
import br.com.m5_storage.repository.AlertaRepository;
import br.com.m5_storage.repository.MovimentacaoRepository;
import br.com.m5_storage.repository.RecursoRepository;
import br.com.m5_storage.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MovimentacaoService {

    private final MovimentacaoRepository movimentacaoRepository;
    private final RecursoRepository recursoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AlertaRepository alertaRepository;
    private final RecursoService recursoService;

    public MovimentacaoService(MovimentacaoRepository movimentacaoRepository,
                               RecursoRepository recursoRepository,
                               UsuarioRepository usuarioRepository,
                               AlertaRepository alertaRepository,
                               RecursoService recursoService) {
        this.movimentacaoRepository = movimentacaoRepository;
        this.recursoRepository = recursoRepository;
        this.usuarioRepository = usuarioRepository;
        this.alertaRepository = alertaRepository;
        this.recursoService = recursoService;
    }

    @Transactional
    public MovimentacaoListagemDTO registrarMovimentacao(MovimentacaoCadastroDTO dto) {

        Recurso recurso = recursoRepository.findById(dto.recursoId())
                .orElseThrow(() -> new IdNaoEncontradoException(
                        "Recurso não encontrado com id: " + dto.recursoId()
                ));

        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new IdNaoEncontradoException(
                        "Usuário não encontrado com id: " + dto.usuarioId()
                ));

        if (dto.quantidade() == null || dto.quantidade() <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }

        // ── lógica de estoque ──
        if (dto.tipoMovimentacao() == TipoMovimentacao.CONSUMO) {
            realizarConsumo(recurso, dto.quantidade());
        } else if (dto.tipoMovimentacao() == TipoMovimentacao.REABASTECIMENTO) {
            realizarReabastecimento(recurso, dto.quantidade());
        } else {
            throw new IllegalArgumentException("Tipo de movimentação inválido");
        }

        Movimentacao movimentacao = Movimentacao.builder()
                .recurso(recurso)
                .usuario(usuario)
                .base(recurso.getBase())
                .tipoMovimentacao(dto.tipoMovimentacao())
                .quantidade(dto.quantidade())
                .descricao(dto.descricao())
                .dataMovimentacao(LocalDateTime.now())
                .build();

        return toDTO(movimentacaoRepository.save(movimentacao));
    }

    // Regra 20: histórico por recurso
    @Transactional(readOnly = true)
    public List<MovimentacaoListagemDTO> readMovimentacoesByRecurso(Long recursoId) {
        return movimentacaoRepository
                .findByRecursoIdOrderByDataMovimentacaoDesc(recursoId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // Regra 10: histórico por usuário
    @Transactional(readOnly = true)
    public List<MovimentacaoListagemDTO> readMovimentacoesByUsuario(Long usuarioId) {
        return movimentacaoRepository
                .findByUsuarioIdOrderByDataMovimentacaoDesc(usuarioId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // NOVO: histórico por base (Regra 20 estendida)
    @Transactional(readOnly = true)
    public List<MovimentacaoListagemDTO> readMovimentacoesByBase(Long baseId) {
        return movimentacaoRepository
                .findByRecurso_Base_IdOrderByDataMovimentacaoDesc(baseId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // NOVO: histórico por base + tipo
    @Transactional(readOnly = true)
    public List<MovimentacaoListagemDTO> readMovimentacoesByBaseAndTipo(Long baseId, TipoMovimentacao tipo) {
        return movimentacaoRepository
                .findByRecurso_Base_IdAndTipoMovimentacaoOrderByDataMovimentacaoDesc(baseId, tipo)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // ── lógica de estoque ────────────────────────────────────

    private void realizarConsumo(Recurso recurso, Double quantidade) {
        double novaQuantidade = recurso.getQuantidade() - quantidade;

        if (novaQuantidade < 0) {
            throw new IllegalArgumentException(
                    "Estoque insuficiente. Disponível: " + recurso.getQuantidade()
            );
        }

        recurso.setQuantidade(novaQuantidade);
        atualizarStatusEAlertas(recurso);
    }

    private void realizarReabastecimento(Recurso recurso, Double quantidade) {
        recurso.setQuantidade(recurso.getQuantidade() + quantidade);
        atualizarStatusEAlertas(recurso);
    }

    private void atualizarStatusEAlertas(Recurso recurso) {

        StatusRecurso novoStatus = recursoService.calcularStatus(
                recurso.getQuantidade(),
                recurso.getMinimo(),
                recurso.getCapacidadeMaxima()
        );

        recurso.setStatus(novoStatus);
        recurso.setUltimaAtualizacao(LocalDateTime.now());
        recursoRepository.save(recurso);

        if (!recurso.getCritico()) return;

        if (novoStatus == StatusRecurso.ATENCAO || novoStatus == StatusRecurso.CRITICO) {

            boolean jaTemAlerta = !alertaRepository
                    .findByRecursoIdAndResolvidoFalse(recurso.getId())
                    .isEmpty();

            if (!jaTemAlerta) {
                Alerta alerta = Alerta.builder()
                        .recurso(recurso)
                        .mensagem("Recurso " + recurso.getNome()
                                + " em nível crítico. Quantidade: "
                                + recurso.getQuantidade())
                        .nivel(novoStatus.name())
                        .resolvido(false)
                        .dataAlerta(LocalDateTime.now())
                        .build();

                alertaRepository.save(alerta);
            }

        } else if (novoStatus == StatusRecurso.OK) {

            alertaRepository.findByRecursoIdAndResolvidoFalse(recurso.getId())
                    .forEach(alerta -> {
                        alerta.setResolvido(true);
                        alertaRepository.save(alerta);
                    });
        }
    }

    private MovimentacaoListagemDTO toDTO(Movimentacao m) {
        return new MovimentacaoListagemDTO(
                m.getId(),
                m.getRecurso().getId(),
                m.getRecurso().getNome(),
                m.getUsuario().getId(),
                m.getUsuario().getNome(),
                m.getTipoMovimentacao(),
                m.getQuantidade(),
                m.getDescricao(),
                m.getDataMovimentacao()
        );
    }
}