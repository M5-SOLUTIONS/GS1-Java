package br.com.m5_storage.entity.movimentacao;

import br.com.m5_storage.entity.base.Base;
import br.com.m5_storage.entity.recurso.Recurso;
import br.com.m5_storage.entity.usuario.Usuario;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_movimentacoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = "id")
public class Movimentacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "recurso_id", nullable = false)
    private Recurso recurso;

    /**
     * Regra 9: CONSUMO ou REABASTECIMENTO.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimentacao", nullable = false, length = 30)
    private TipoMovimentacao tipoMovimentacao;

    /**
     * Regra 1/15: Quantidade obrigatória e maior que zero.
     */
    @Column(name = "quantidade", nullable = false)
    private Double quantidade;

    @ManyToOne(optional = false)
    @JoinColumn(name = "base_id", nullable = false)
    private Base base;

    @Column(name = "descricao", length = 255)
    private String descricao;

    @Column(name = "data_movimentacao", nullable = false)
    private LocalDateTime dataMovimentacao;
}
