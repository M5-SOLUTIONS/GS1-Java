package br.com.m5_storage.entity.recurso;

import br.com.m5_storage.controller.EnergiaController;
import br.com.m5_storage.dto.energia.EnergiaListagemDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class EnergiaAssembler
        implements RepresentationModelAssembler<EnergiaListagemDTO, EntityModel<EnergiaListagemDTO>> {

    @Override
    public EntityModel<EnergiaListagemDTO> toModel(EnergiaListagemDTO dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(EnergiaController.class).buscarPorId(dto.id())).withSelfRel(),
                linkTo(methodOn(EnergiaController.class).listarTodos()).withRel("energias")
        );
    }
}
