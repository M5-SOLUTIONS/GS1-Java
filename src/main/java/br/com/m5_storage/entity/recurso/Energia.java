package br.com.m5_storage.entity.recurso;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "t_recurso_energia")
@PrimaryKeyJoinColumn(name = "id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Energia extends Recurso {

    @Column(name = "tipo_energia", length = 50)
    private String tipoEnergia;

    /**
     * Porcentagem do nível atual em relação ao mínimo de segurança.
     * Calculado, não armazenado no banco.
     *
     * Exemplo: quantidade=80, minimo=100 → 80%
     */
    @Transient
    public Double getPorcentagem() {
        if (getMinimo() == null || getMinimo() == 0) return null;
        return (getQuantidade() / getMinimo()) * 100;
    }
}
