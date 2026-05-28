package br.com.m5_storage.entity.recurso;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "t_recurso_medicamento")
@PrimaryKeyJoinColumn(name = "id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Medicamento extends Recurso {

    @Column(name = "validade")
    private LocalDate validade;

}