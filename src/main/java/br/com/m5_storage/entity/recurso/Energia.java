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

    @Transient
    public Double getPorcentagem() {
        if (getCapacidadeMaxima() == null || getCapacidadeMaxima() == 0) return null;
        return (getQuantidade() / getCapacidadeMaxima()) * 100;
    }
}
