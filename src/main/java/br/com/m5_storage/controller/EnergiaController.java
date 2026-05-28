package br.com.m5_storage.controller;

import br.com.m5_storage.dto.energia.EnergiaAtualizarDTO;
import br.com.m5_storage.dto.energia.EnergiaCadastroDTO;
import br.com.m5_storage.dto.energia.EnergiaListagemDTO;
import br.com.m5_storage.entity.recurso.EnergiaAssembler;
import br.com.m5_storage.service.EnergiaService;
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
@RequestMapping("/energias")
public class EnergiaController {

    private final EnergiaService energiaService;
    private final EnergiaAssembler assembler;

    public EnergiaController(EnergiaService energiaService, EnergiaAssembler assembler) {
        this.energiaService = energiaService;
        this.assembler = assembler;
    }

    // POST /energias → 201 Created
    @PostMapping
    public ResponseEntity<EntityModel<EnergiaListagemDTO>> criar(
            @RequestBody @Valid EnergiaCadastroDTO dto) {

        EnergiaListagemDTO criado = energiaService.createEnergia(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(criado.id())
                .toUri();

        return ResponseEntity.created(location).body(assembler.toModel(criado));
    }

    // GET /energias → 200 OK
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<EnergiaListagemDTO>>> listarTodos() {
        List<EntityModel<EnergiaListagemDTO>> lista = energiaService.readAllEnergias()
                .stream()
                .map(assembler::toModel)
                .toList();

        CollectionModel<EntityModel<EnergiaListagemDTO>> collection = CollectionModel.of(lista,
                linkTo(methodOn(EnergiaController.class).listarTodos()).withSelfRel());

        return ResponseEntity.ok(collection);
    }

    // GET /energias/{id} → 200 OK
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<EnergiaListagemDTO>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(assembler.toModel(energiaService.readEnergiaById(id)));
    }

    // PUT /energias/{id} → 200 OK
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<EnergiaListagemDTO>> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid EnergiaAtualizarDTO dto) {

        return ResponseEntity.ok(assembler.toModel(energiaService.updateEnergia(id, dto)));
    }
}