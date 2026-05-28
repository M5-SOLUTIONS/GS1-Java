package br.com.m5_storage.controller;

import br.com.m5_storage.dto.medicamento.MedicamentoAtualizarDTO;
import br.com.m5_storage.dto.medicamento.MedicamentoCadastroDTO;
import br.com.m5_storage.dto.medicamento.MedicamentoListagemDTO;
import br.com.m5_storage.entity.recurso.MedicamentoAssembler;
import br.com.m5_storage.service.MedicamentoService;
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
@RequestMapping("/medicamentos")
public class MedicamentoController {

    private final MedicamentoService medicamentoService;
    private final MedicamentoAssembler assembler;

    public MedicamentoController(MedicamentoService medicamentoService,
                                 MedicamentoAssembler assembler) {
        this.medicamentoService = medicamentoService;
        this.assembler = assembler;
    }

    // POST /medicamentos → 201 Created
    @PostMapping
    public ResponseEntity<EntityModel<MedicamentoListagemDTO>> criar(
            @RequestBody @Valid MedicamentoCadastroDTO dto) {

        MedicamentoListagemDTO criado = medicamentoService.createMedicamento(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(criado.id())
                .toUri();

        return ResponseEntity.created(location).body(assembler.toModel(criado));
    }

    // GET /medicamentos → 200 OK
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<MedicamentoListagemDTO>>> listarTodos() {
        List<EntityModel<MedicamentoListagemDTO>> lista = medicamentoService.readAllMedicamentos()
                .stream()
                .map(assembler::toModel)
                .toList();

        CollectionModel<EntityModel<MedicamentoListagemDTO>> collection = CollectionModel.of(lista,
                linkTo(methodOn(MedicamentoController.class).listarTodos()).withSelfRel());

        return ResponseEntity.ok(collection);
    }

    // GET /medicamentos/{id} → 200 OK
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<MedicamentoListagemDTO>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(assembler.toModel(medicamentoService.readMedicamentoById(id)));
    }

    // GET /medicamentos/vencidos → 200 OK  (Regra 11)
    @GetMapping("/vencidos")
    public ResponseEntity<CollectionModel<EntityModel<MedicamentoListagemDTO>>> listarVencidos() {
        List<EntityModel<MedicamentoListagemDTO>> lista = medicamentoService.readMedicamentosVencidos()
                .stream()
                .map(assembler::toModel)
                .toList();

        CollectionModel<EntityModel<MedicamentoListagemDTO>> collection = CollectionModel.of(lista,
                linkTo(methodOn(MedicamentoController.class).listarVencidos()).withSelfRel(),
                linkTo(methodOn(MedicamentoController.class).listarTodos()).withRel("todos"));

        return ResponseEntity.ok(collection);
    }

    // GET /medicamentos/a-vencer?dias=30 → 200 OK  (Regra 11)
    @GetMapping("/a-vencer")
    public ResponseEntity<CollectionModel<EntityModel<MedicamentoListagemDTO>>> listarAVencer(
            @RequestParam(defaultValue = "30") int dias) {

        List<EntityModel<MedicamentoListagemDTO>> lista = medicamentoService.readMedicamentosAVencer(dias)
                .stream()
                .map(assembler::toModel)
                .toList();

        CollectionModel<EntityModel<MedicamentoListagemDTO>> collection = CollectionModel.of(lista,
                linkTo(methodOn(MedicamentoController.class).listarAVencer(dias)).withSelfRel(),
                linkTo(methodOn(MedicamentoController.class).listarTodos()).withRel("todos"));

        return ResponseEntity.ok(collection);
    }

    // PUT /medicamentos/{id} → 200 OK
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<MedicamentoListagemDTO>> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid MedicamentoAtualizarDTO dto) {

        return ResponseEntity.ok(assembler.toModel(medicamentoService.updateMedicamento(id, dto)));
    }
}