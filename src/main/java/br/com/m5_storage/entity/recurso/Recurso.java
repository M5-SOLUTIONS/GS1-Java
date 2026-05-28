package br.com.m5_storage.entity.recurso;

import br.com.m5_storage.entity.base.Base;
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

    @Column(name = "critico", nullable = false)
    private Boolean critico = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    private StatusRecurso status;

    @Column(name = "nivel", length = 30)
    private String nivel;

    @Column(name = "ultima_atualizacao")
    private LocalDateTime ultimaAtualizacao;

    @ManyToOne
    @JoinColumn(name = "base_id", nullable = false)
    private Base base;
}