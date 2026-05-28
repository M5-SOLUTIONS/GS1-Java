package br.com.m5_storage.entity.recurso;

import br.com.m5_storage.controller.MedicamentoController;
import br.com.m5_storage.dto.medicamento.MedicamentoListagemDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class MedicamentoAssembler
        implements RepresentationModelAssembler<MedicamentoListagemDTO, EntityModel<MedicamentoListagemDTO>> {

    @Override
    public EntityModel<MedicamentoListagemDTO> toModel(MedicamentoListagemDTO dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(MedicamentoController.class).buscarPorId(dto.id())).withSelfRel(),
                linkTo(methodOn(MedicamentoController.class).listarTodos()).withRel("medicamentos"),
                linkTo(methodOn(MedicamentoController.class).listarVencidos()).withRel("vencidos")
        );
    }
}
