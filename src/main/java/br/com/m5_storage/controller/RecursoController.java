package br.com.m5_storage.controller;

import br.com.m5_storage.dto.recurso.RecursoAtualizarDTO;
import br.com.m5_storage.dto.recurso.RecursoCadastroDTO;
import br.com.m5_storage.dto.recurso.RecursoListagemDTO;
import br.com.m5_storage.entity.recurso.RecursoAssembler;
import br.com.m5_storage.entity.recurso.StatusRecurso;
import br.com.m5_storage.service.RecursoService;
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
public class RecursoController {

    private final RecursoService recursoService;
    private final RecursoAssembler assembler;

    public RecursoController(RecursoService recursoService, RecursoAssembler assembler) {
        this.recursoService = recursoService;
        this.assembler = assembler;
    }

    // POST /recursos → 201 Created
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

    // GET /recursos → 200 OK
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

    // GET /recursos/{id} → 200 OK
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<RecursoListagemDTO>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(assembler.toModel(recursoService.readRecursoById(id)));
    }

    // GET /recursos/status/{status} → 200 OK  (Regra 16: dashboard)
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

    // PUT /recursos/{id} → 200 OK
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<RecursoListagemDTO>> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid RecursoAtualizarDTO dto) {

        return ResponseEntity.ok(assembler.toModel(recursoService.updateRecurso(id, dto)));
    }

    // DELETE /recursos/{id} → 204 No Content
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        recursoService.deleteRecurso(id);
        return ResponseEntity.noContent().build();
    }
}