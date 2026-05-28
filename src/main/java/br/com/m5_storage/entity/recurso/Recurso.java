package br.com.m5_storage.entity.recurso;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_recursos")
@Inheritance(strategy = InheritanceType.JOINED)
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

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "categoria", nullable = false, length = 50)
    private String categoria;

    @Column(name = "quantidade", nullable = false)
    private Double quantidade;

    @Column(name = "minimo", nullable = false)
    private Double minimo;

    /**
     * Regra 3/4: Apenas recursos críticos geram alertas.
     * true = este recurso pode gerar alertas ao atingir nível mínimo.
     */
    @Column(name = "critico", nullable = false)
    private Boolean critico = false;

    /**
     * Regra 7/8: Status depende da quantidade vs mínimo.
     * OK | ATENCAO | CRITICO
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    private StatusRecurso status;

    @Column(name = "nivel", length = 30)
    private String nivel;

    @Column(name = "ultima_atualizacao")
    private LocalDateTime ultimaAtualizacao;
}