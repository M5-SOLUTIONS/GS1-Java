package br.com.m5_storage.controller;

import br.com.m5_storage.dto.alerta.AlertaListagemDTO;
import br.com.m5_storage.entity.alerta.AlertaAssembler;
import br.com.m5_storage.service.AlertaService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/alertas")
public class AlertaController {

    private final AlertaService alertaService;
    private final AlertaAssembler assembler;

    public AlertaController(AlertaService alertaService, AlertaAssembler assembler) {
        this.alertaService = alertaService;
        this.assembler = assembler;
    }

    // GET /alertas → 200 OK  (Regra 16: dashboard)
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<AlertaListagemDTO>>> listarAtivos() {
        List<EntityModel<AlertaListagemDTO>> lista = alertaService.readAlertasAtivos()
                .stream()
                .map(assembler::toModel)
                .toList();

        CollectionModel<EntityModel<AlertaListagemDTO>> collection = CollectionModel.of(lista,
                linkTo(methodOn(AlertaController.class).listarAtivos()).withSelfRel());

        return ResponseEntity.ok(collection);
    }

    // GET /alertas/recurso/{recursoId} → 200 OK  (Regras 3, 4)
    @GetMapping("/recurso/{recursoId}")
    public ResponseEntity<CollectionModel<EntityModel<AlertaListagemDTO>>> listarPorRecurso(
            @PathVariable Long recursoId) {

        List<EntityModel<AlertaListagemDTO>> lista = alertaService.readAlertasByRecurso(recursoId)
                .stream()
                .map(assembler::toModel)
                .toList();

        CollectionModel<EntityModel<AlertaListagemDTO>> collection = CollectionModel.of(lista,
                linkTo(methodOn(AlertaController.class).listarPorRecurso(recursoId)).withSelfRel(),
                linkTo(methodOn(AlertaController.class).listarAtivos()).withRel("todos-ativos"));

        return ResponseEntity.ok(collection);
    }

    // PATCH /alertas/{id}/resolver → 200 OK  (Regra 5)
    @PatchMapping("/{id}/resolver")
    public ResponseEntity<EntityModel<AlertaListagemDTO>> resolverAlerta(@PathVariable Long id) {
        return ResponseEntity.ok(assembler.toModel(alertaService.resolverAlerta(id)));
    }
}