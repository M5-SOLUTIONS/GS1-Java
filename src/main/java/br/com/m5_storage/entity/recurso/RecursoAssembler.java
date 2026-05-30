package br.com.m5_storage.entity.recurso;

import br.com.m5_storage.controller.RecursoController;
import br.com.m5_storage.dto.recurso.RecursoListagemDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class RecursoAssembler
        implements RepresentationModelAssembler<RecursoListagemDTO, EntityModel<RecursoListagemDTO>> {

    @Override
    public EntityModel<RecursoListagemDTO> toModel(RecursoListagemDTO dto) {
        return EntityModel.of(dto,
                // GET /{id} — sem usuarioId
                linkTo(methodOn(RecursoController.class)
                        .buscarPorId(dto.id())).withSelfRel(),

                // GET / — sem usuarioId
                linkTo(methodOn(RecursoController.class)
                        .listarTodos()).withRel("recursos"),

                // GET /setor/{setorId} — sem usuarioId
                linkTo(methodOn(RecursoController.class)
                        .listarPorSetor(dto.setorId())).withRel("recursos-do-setor"),

                // GET /status/{status} — sem usuarioId
                linkTo(methodOn(RecursoController.class)
                        .listarPorStatus(dto.status())).withRel("por-status")
        );
    }
}