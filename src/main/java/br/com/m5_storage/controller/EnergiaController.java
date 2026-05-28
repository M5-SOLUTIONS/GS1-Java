package br.com.m5_storage.controller;

import br.com.m5_storage.dto.energia.EnergiaAtualizarDTO;
import br.com.m5_storage.dto.energia.EnergiaCadastroDTO;
import br.com.m5_storage.dto.energia.EnergiaListagemDTO;
import br.com.m5_storage.entity.recurso.EnergiaAssembler;
import br.com.m5_storage.service.EnergiaService;
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
@RequestMapping("/energias")
@Tag(name = "Energias", description = "Gerenciamento de recursos de energia (Solar, Nuclear, Bateria...)")
public class EnergiaController {

    private final EnergiaService energiaService;
    private final EnergiaAssembler assembler;

    public EnergiaController(EnergiaService energiaService, EnergiaAssembler assembler) {
        this.energiaService = energiaService;
        this.assembler = assembler;
    }

    @Operation(summary = "Cadastra um recurso de energia", responses = {
            @ApiResponse(responseCode = "201", description = "Energia cadastrada com sucesso",
                    content = @Content(schema = @Schema(implementation = EnergiaListagemDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
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

    @Operation(summary = "Lista todos os recursos de energia", responses = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = EnergiaListagemDTO.class)))
    })
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

    @Operation(summary = "Busca um recurso de energia pelo ID", responses = {
            @ApiResponse(responseCode = "200", description = "Energia encontrada",
                    content = @Content(schema = @Schema(implementation = EnergiaListagemDTO.class))),
            @ApiResponse(responseCode = "404", description = "Energia não encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<EnergiaListagemDTO>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(assembler.toModel(energiaService.readEnergiaById(id)));
    }

    @Operation(summary = "Atualiza um recurso de energia", responses = {
            @ApiResponse(responseCode = "200", description = "Energia atualizada com sucesso",
                    content = @Content(schema = @Schema(implementation = EnergiaListagemDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Energia não encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<EnergiaListagemDTO>> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid EnergiaAtualizarDTO dto) {

        return ResponseEntity.ok(assembler.toModel(energiaService.updateEnergia(id, dto)));
    }
}