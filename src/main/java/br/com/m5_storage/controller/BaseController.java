package br.com.m5_storage.controller;

import br.com.m5_storage.dto.base.BaseAtualizarDTO;
import br.com.m5_storage.dto.base.BaseCadastroDTO;
import br.com.m5_storage.dto.base.BaseListagemDTO;
import br.com.m5_storage.entity.base.BaseAssembler;
import br.com.m5_storage.service.BaseService;
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

@Tag(name = "Bases")
@RestController
@RequestMapping("/bases")
public class BaseController {

    private final BaseService baseService;
    private final BaseAssembler assembler;

    public BaseController(BaseService baseService, BaseAssembler assembler) {
        this.baseService = baseService;
        this.assembler = assembler;
    }

    @Operation(summary = "Cadastra uma base", responses = {
            @ApiResponse(responseCode = "201", description = "Base cadastrada com sucesso",
                    content = @Content(schema = @Schema(implementation = BaseListagemDTO.class))),
            @ApiResponse(responseCode = "400", description = "Erro ao cadastrar base")
    })
    @PostMapping
    public ResponseEntity<EntityModel<BaseListagemDTO>> criar(
            @RequestBody @Valid BaseCadastroDTO dto) {

        BaseListagemDTO criada = baseService.createBase(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(criada.id())
                .toUri();

        return ResponseEntity.created(location).body(assembler.toModel(criada));
    }

    @Operation(summary = "Lista todas as bases", responses = {
            @ApiResponse(responseCode = "200", description = "Bases encontradas",
                    content = @Content(array = @ArraySchema(
                            schema = @Schema(implementation = BaseListagemDTO.class))))
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<BaseListagemDTO>>> listarTodos() {

        List<EntityModel<BaseListagemDTO>> lista = baseService.readAllBases()
                .stream()
                .map(assembler::toModel)
                .toList();

        CollectionModel<EntityModel<BaseListagemDTO>> collection = CollectionModel.of(lista,
                linkTo(methodOn(BaseController.class).listarTodos()).withSelfRel());

        return ResponseEntity.ok(collection);
    }

    @Operation(summary = "Busca base por id", responses = {
            @ApiResponse(responseCode = "200", description = "Base encontrada",
                    content = @Content(schema = @Schema(implementation = BaseListagemDTO.class))),
            @ApiResponse(responseCode = "404", description = "Base não encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<BaseListagemDTO>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(assembler.toModel(baseService.readBaseById(id)));
    }

    @Operation(summary = "Atualiza uma base", responses = {
            @ApiResponse(responseCode = "200", description = "Base atualizada com sucesso",
                    content = @Content(schema = @Schema(implementation = BaseListagemDTO.class))),
            @ApiResponse(responseCode = "400", description = "Erro ao atualizar base"),
            @ApiResponse(responseCode = "404", description = "Base não encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<BaseListagemDTO>> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid BaseAtualizarDTO dto) {

        return ResponseEntity.ok(assembler.toModel(baseService.updateBase(id, dto)));
    }

    @Operation(summary = "Deleta uma base", responses = {
            @ApiResponse(responseCode = "204", description = "Base deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Base não encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        baseService.deleteBase(id);
        return ResponseEntity.noContent().build();
    }
}