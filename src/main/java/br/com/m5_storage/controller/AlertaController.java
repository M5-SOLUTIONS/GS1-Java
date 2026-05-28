package br.com.m5_storage.controller;

import br.com.m5_storage.dto.alerta.AlertaListagemDTO;
import br.com.m5_storage.entity.alerta.AlertaAssembler;
import br.com.m5_storage.service.AlertaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/alertas")
@Tag(name = "Alertas", description = "Gerenciamento de alertas de recursos")
public class AlertaController {

    private final AlertaService alertaService;
    private final AlertaAssembler assembler;

    public AlertaController(AlertaService alertaService, AlertaAssembler assembler) {
        this.alertaService = alertaService;
        this.assembler = assembler;
    }

    @Operation(summary = "Lista todos os alertas ativos (dashboard)", responses = {
            @ApiResponse(responseCode = "200", description = "Alertas ativos retornados com sucesso",
                    content = @Content(schema = @Schema(implementation = AlertaListagemDTO.class)))
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<AlertaListagemDTO>>> listarAtivos() {

        List<EntityModel<AlertaListagemDTO>> lista = alertaService.readAlertasAtivos()
                .stream()
                .map(assembler::toModel)
                .toList();

        CollectionModel<EntityModel<AlertaListagemDTO>> collection = CollectionModel.of(
                lista,
                linkTo(methodOn(AlertaController.class).listarAtivos()).withSelfRel()
        );

        return ResponseEntity.ok(collection);
    }

    @Operation(summary = "Lista alertas por recurso", responses = {
            @ApiResponse(responseCode = "200", description = "Alertas do recurso retornados com sucesso",
                    content = @Content(schema = @Schema(implementation = AlertaListagemDTO.class))
                    ),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado")
    })
    @GetMapping("/recurso/{recursoId}")
    public ResponseEntity<CollectionModel<EntityModel<AlertaListagemDTO>>> listarPorRecurso(
            @PathVariable Long recursoId) {

        List<EntityModel<AlertaListagemDTO>> lista = alertaService.readAlertasByRecurso(recursoId)
                .stream()
                .map(assembler::toModel)
                .toList();

        CollectionModel<EntityModel<AlertaListagemDTO>> collection = CollectionModel.of(
                lista,
                linkTo(methodOn(AlertaController.class).listarPorRecurso(recursoId)).withSelfRel(),
                linkTo(methodOn(AlertaController.class).listarAtivos()).withRel("todos-ativos")
        );

        return ResponseEntity.ok(collection);
    }

    @Operation(summary = "Resolve um alerta", responses = {
            @ApiResponse(responseCode = "200", description = "Alerta resolvido com sucesso",
                    content = @Content(schema = @Schema(implementation = AlertaListagemDTO.class))),
            @ApiResponse(responseCode = "404", description = "Alerta não encontrado")
    })
    @PatchMapping("/{id}/resolver")
    public ResponseEntity<EntityModel<AlertaListagemDTO>> resolverAlerta(@PathVariable Long id) {

        return ResponseEntity.ok(
                assembler.toModel(alertaService.resolverAlerta(id))
        );
    }
}