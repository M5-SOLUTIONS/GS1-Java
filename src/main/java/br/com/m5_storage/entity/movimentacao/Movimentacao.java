package br.com.m5_storage.entity.movimentacao;

import br.com.m5_storage.entity.recurso.Recurso;
import br.com.m5_storage.entity.setor.Setor;
import br.com.m5_storage.entity.usuario.Usuario;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "st_t_movimentacoes")
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

    // Regra 11: usuário obrigatório
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // Regra 10/15: recurso e setor obrigatórios
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "recurso_id", nullable = false)
    private Recurso recurso;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "setor_id", nullable = false)
    private Setor setor;

    // Regra 12: tipo obrigatório
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimentacao", nullable = false, length = 30)
    private TipoMovimentacao tipoMovimentacao;

    // Regra 13: quantidade > 0
    @Column(name = "quantidade", nullable = false)
    private Double quantidade;

    @Column(name = "descricao", length = 255)
    private String descricao;

    @Column(name = "data_movimentacao", nullable = false)
    private LocalDateTime dataMovimentacao;
}