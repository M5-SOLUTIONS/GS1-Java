package br.com.m5_storage.controller;

import br.com.m5_storage.dto.medicamento.MedicamentoAtualizarDTO;
import br.com.m5_storage.dto.medicamento.MedicamentoCadastroDTO;
import br.com.m5_storage.dto.medicamento.MedicamentoListagemDTO;
import br.com.m5_storage.entity.recurso.MedicamentoAssembler;
import br.com.m5_storage.service.MedicamentoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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

@Tag(name = "Medicamentos")
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

    @Operation(summary = "Cadastra um medicamento", responses = {
            @ApiResponse(responseCode = "201", description = "Medicamento cadastrado com sucesso",
                    content = @Content(schema = @Schema(implementation = MedicamentoListagemDTO.class))),
            @ApiResponse(responseCode = "400", description = "Erro ao cadastrar medicamento"),
            @ApiResponse(responseCode = "404", description = "Base não encontrada")
    })
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

    @Operation(summary = "Lista todos os medicamentos", responses = {
            @ApiResponse(responseCode = "200", description = "Medicamentos encontrados",
                    content = @Content(array = @ArraySchema(
                            schema = @Schema(implementation = MedicamentoListagemDTO.class))))
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<MedicamentoListagemDTO>>> listarTodos() {

        List<EntityModel<MedicamentoListagemDTO>> lista = medicamentoService.readAllMedicamentos()
                .stream()
                .map(assembler::toModel)
                .toList();

        CollectionModel<EntityModel<MedicamentoListagemDTO>> collection = CollectionModel.of(
                lista,
                linkTo(methodOn(MedicamentoController.class).listarTodos()).withSelfRel(),
                linkTo(methodOn(MedicamentoController.class).listarVencidos()).withRel("vencidos"),
                linkTo(methodOn(MedicamentoController.class).listarAVencer(30)).withRel("a-vencer")
        );

        return ResponseEntity.ok(collection);
    }

    @Operation(summary = "Busca um medicamento pelo id", responses = {
            @ApiResponse(responseCode = "200", description = "Medicamento encontrado",
                    content = @Content(schema = @Schema(implementation = MedicamentoListagemDTO.class))),
            @ApiResponse(responseCode = "404", description = "Medicamento não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<MedicamentoListagemDTO>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(
                assembler.toModel(medicamentoService.readMedicamentoById(id))
        );
    }

    @Operation(summary = "Lista medicamentos vencidos", responses = {
            @ApiResponse(responseCode = "200", description = "Medicamentos vencidos encontrados",
                    content = @Content(array = @ArraySchema(
                            schema = @Schema(implementation = MedicamentoListagemDTO.class))))
    })
    @GetMapping("/vencidos")
    public ResponseEntity<CollectionModel<EntityModel<MedicamentoListagemDTO>>> listarVencidos() {

        List<EntityModel<MedicamentoListagemDTO>> lista =
                medicamentoService.readMedicamentosVencidos()
                        .stream()
                        .map(assembler::toModel)
                        .toList();

        CollectionModel<EntityModel<MedicamentoListagemDTO>> collection = CollectionModel.of(
                lista,
                linkTo(methodOn(MedicamentoController.class).listarVencidos()).withSelfRel(),
                linkTo(methodOn(MedicamentoController.class).listarTodos()).withRel("todos"),
                linkTo(methodOn(MedicamentoController.class).listarAVencer(30)).withRel("a-vencer")
        );

        return ResponseEntity.ok(collection);
    }

    @Operation(summary = "Lista medicamentos próximos do vencimento", responses = {
            @ApiResponse(responseCode = "200", description = "Medicamentos encontrados",
                    content = @Content(array = @ArraySchema(
                            schema = @Schema(implementation = MedicamentoListagemDTO.class))))
    })
    @GetMapping("/a-vencer")
    public ResponseEntity<CollectionModel<EntityModel<MedicamentoListagemDTO>>> listarAVencer(
            @RequestParam(defaultValue = "30") int dias) {

        List<EntityModel<MedicamentoListagemDTO>> lista =
                medicamentoService.readMedicamentosAVencer(dias)
                        .stream()
                        .map(assembler::toModel)
                        .toList();

        CollectionModel<EntityModel<MedicamentoListagemDTO>> collection = CollectionModel.of(
                lista,
                linkTo(methodOn(MedicamentoController.class).listarAVencer(dias)).withSelfRel(),
                linkTo(methodOn(MedicamentoController.class).listarTodos()).withRel("todos"),
                linkTo(methodOn(MedicamentoController.class).listarVencidos()).withRel("vencidos")
        );

        return ResponseEntity.ok(collection);
    }

    @Operation(summary = "Atualiza um medicamento", responses = {
            @ApiResponse(responseCode = "200", description = "Medicamento atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = MedicamentoListagemDTO.class))),
            @ApiResponse(responseCode = "400", description = "Erro ao atualizar medicamento"),
            @ApiResponse(responseCode = "404", description = "Medicamento ou base não encontrados")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<MedicamentoListagemDTO>> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid MedicamentoAtualizarDTO dto) {

        return ResponseEntity.ok(
                assembler.toModel(medicamentoService.updateMedicamento(id, dto))
        );
    }
}