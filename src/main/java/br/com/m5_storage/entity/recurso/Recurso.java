package br.com.m5_storage.entity.recurso;

import br.com.m5_storage.entity.setor.Setor;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_recursos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = "id")
public class Recurso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Regra 9/15: recurso pertence a um setor
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "setor_id", nullable = false)
    private Setor setor;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "categoria", nullable = false, length = 50)
    private String categoria;

    @Column(name = "quantidade", nullable = false)
    private Double quantidade;

    @Column(name = "minimo", nullable = false)
    private Double minimo;

    // Regra 18: capacidade máxima obrigatória
    @Column(name = "capacidade_maxima", nullable = false)
    private Double capacidadeMaxima;

    // Regra 7: apenas recursos críticos geram alertas
    @Column(name = "critico", nullable = false)
    private Boolean critico = false;

    // Regra 17: status obrigatório
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    private StatusRecurso status;

    @Column(name = "ultima_atualizacao")
    private LocalDateTime ultimaAtualizacao;
}