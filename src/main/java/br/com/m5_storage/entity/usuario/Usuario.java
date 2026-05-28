package br.com.m5_storage.entity.usuario;

import br.com.m5_storage.entity.base.Base;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "t_usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = "id")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "senha", nullable = false, length = 100)
    private String senha;

    // 🔥 NOVO: vínculo obrigatório com Base
    @ManyToOne
    @JoinColumn(name = "base_id", nullable = false)
    private Base base;
}