package br.com.m5_storage.controller;

import br.com.m5_storage.dto.recurso.RecursoAtualizarDTO;
import br.com.m5_storage.dto.recurso.RecursoCadastroDTO;
import br.com.m5_storage.dto.recurso.RecursoListagemDTO;
import br.com.m5_storage.entity.recurso.RecursoAssembler;
import br.com.m5_storage.entity.recurso.StatusRecurso;
import br.com.m5_storage.service.RecursoService;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/recursos")
@Tag(name = "Recursos", description = "Gerenciamento de recursos genéricos do estoque")
public class RecursoController {

    private final RecursoService recursoService;
    private final RecursoAssembler assembler;

    public RecursoController(RecursoService recursoService, RecursoAssembler assembler) {
        this.recursoService = recursoService;
        this.assembler = assembler;
    }

    @Operation(summary = "Cadastra um recurso", responses = {
            @ApiResponse(responseCode = "201", description = "Recurso cadastrado com sucesso",
                    content = @Content(schema = @Schema(implementation = RecursoListagemDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping
    public ResponseEntity<EntityModel<RecursoListagemDTO>> criar(
            @RequestBody @Valid RecursoCadastroDTO dto) {

        RecursoListagemDTO criado = recursoService.createRecurso(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(criado.id())
                .toUri();

        return ResponseEntity.created(location).body(assembler.toModel(criado));
    }

    @Operation(summary = "Lista todos os recursos", responses = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = RecursoListagemDTO.class)))
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<RecursoListagemDTO>>> listarTodos() {
        List<EntityModel<RecursoListagemDTO>> lista = recursoService.readAllRecursos()
                .stream()
                .map(assembler::toModel)
                .toList();

        CollectionModel<EntityModel<RecursoListagemDTO>> collection = CollectionModel.of(lista,
                linkTo(methodOn(RecursoController.class).listarTodos()).withSelfRel());

        return ResponseEntity.ok(collection);
    }

    @Operation(summary = "Busca um recurso pelo ID", responses = {
            @ApiResponse(responseCode = "200", description = "Recurso encontrado",
                    content = @Content(schema = @Schema(implementation = RecursoListagemDTO.class))),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<RecursoListagemDTO>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(assembler.toModel(recursoService.readRecursoById(id)));
    }

    @Operation(summary = "Lista recursos por status (dashboard)", responses = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = RecursoListagemDTO.class)))
    })
    @GetMapping("/status/{status}")
    public ResponseEntity<CollectionModel<EntityModel<RecursoListagemDTO>>> listarPorStatus(
            @PathVariable StatusRecurso status) {

        List<EntityModel<RecursoListagemDTO>> lista = recursoService.readRecursosByStatus(status)
                .stream()
                .map(assembler::toModel)
                .toList();

        CollectionModel<EntityModel<RecursoListagemDTO>> collection = CollectionModel.of(lista,
                linkTo(methodOn(RecursoController.class).listarPorStatus(status)).withSelfRel(),
                linkTo(methodOn(RecursoController.class).listarTodos()).withRel("todos"));

        return ResponseEntity.ok(collection);
    }

    @Operation(summary = "Atualiza um recurso", responses = {
            @ApiResponse(responseCode = "200", description = "Recurso atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = RecursoListagemDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<RecursoListagemDTO>> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid RecursoAtualizarDTO dto) {

        return ResponseEntity.ok(assembler.toModel(recursoService.updateRecurso(id, dto)));
    }

    @Operation(summary = "Remove um recurso", responses = {
            @ApiResponse(responseCode = "204", description = "Recurso removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado"),
            @ApiResponse(responseCode = "409", description = "Recurso possui movimentações vinculadas")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        recursoService.deleteRecurso(id);
        return ResponseEntity.noContent().build();
    }
}