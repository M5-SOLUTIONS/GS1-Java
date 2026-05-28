package br.com.m5_storage.controller;

import br.com.m5_storage.dto.movimentacao.MovimentacaoCadastroDTO;
import br.com.m5_storage.dto.movimentacao.MovimentacaoListagemDTO;
import br.com.m5_storage.entity.movimentacao.MovimentacaoAssembler;
import br.com.m5_storage.service.MovimentacaoService;
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
@RequestMapping("/movimentacoes")
@Tag(name = "Movimentações", description = "Registro de consumo e reabastecimento de recursos")
public class MovimentacaoController {

    private final MovimentacaoService movimentacaoService;
    private final MovimentacaoAssembler assembler;

    public MovimentacaoController(MovimentacaoService movimentacaoService,
                                  MovimentacaoAssembler assembler) {
        this.movimentacaoService = movimentacaoService;
        this.assembler = assembler;
    }

    @Operation(summary = "Registra uma movimentação (consumo ou reabastecimento)", responses = {
            @ApiResponse(responseCode = "201", description = "Movimentação registrada com sucesso",
                    content = @Content(schema = @Schema(implementation = MovimentacaoListagemDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou estoque insuficiente"),
            @ApiResponse(responseCode = "404", description = "Recurso ou usuário não encontrado")
    })
    @PostMapping
    public ResponseEntity<EntityModel<MovimentacaoListagemDTO>> registrar(
            @RequestBody @Valid MovimentacaoCadastroDTO dto) {

        MovimentacaoListagemDTO criado = movimentacaoService.registrarMovimentacao(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/recurso/{id}")
                .buildAndExpand(criado.recursoId())
                .toUri();

        return ResponseEntity.created(location).body(assembler.toModel(criado));
    }

    @Operation(summary = "Lista o histórico de movimentações de um recurso", responses = {
            @ApiResponse(responseCode = "200", description = "Histórico retornado com sucesso",
                    content = @Content(schema = @Schema(implementation = MovimentacaoListagemDTO.class))),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado")
    })
    @GetMapping("/recurso/{recursoId}")
    public ResponseEntity<CollectionModel<EntityModel<MovimentacaoListagemDTO>>> listarPorRecurso(
            @PathVariable Long recursoId) {

        List<EntityModel<MovimentacaoListagemDTO>> lista =
                movimentacaoService.readMovimentacoesByRecurso(recursoId)
                        .stream()
                        .map(assembler::toModel)
                        .toList();

        CollectionModel<EntityModel<MovimentacaoListagemDTO>> collection = CollectionModel.of(lista,
                linkTo(methodOn(MovimentacaoController.class).listarPorRecurso(recursoId)).withSelfRel());

        return ResponseEntity.ok(collection);
    }

    @Operation(summary = "Lista o histórico de movimentações de um usuário", responses = {
            @ApiResponse(responseCode = "200", description = "Histórico retornado com sucesso",
                    content = @Content(schema = @Schema(implementation = MovimentacaoListagemDTO.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<CollectionModel<EntityModel<MovimentacaoListagemDTO>>> listarPorUsuario(
            @PathVariable Long usuarioId) {

        List<EntityModel<MovimentacaoListagemDTO>> lista =
                movimentacaoService.readMovimentacoesByUsuario(usuarioId)
                        .stream()
                        .map(assembler::toModel)
                        .toList();

        CollectionModel<EntityModel<MovimentacaoListagemDTO>> collection = CollectionModel.of(lista,
                linkTo(methodOn(MovimentacaoController.class).listarPorUsuario(usuarioId)).withSelfRel());

        return ResponseEntity.ok(collection);
    }

    @Operation(summary = "Lista o histórico de movimentações de uma base")
    @GetMapping("/base/{baseId}")
    public ResponseEntity<CollectionModel<EntityModel<MovimentacaoListagemDTO>>> listarPorBase(
            @PathVariable Long baseId) {

        List<EntityModel<MovimentacaoListagemDTO>> lista =
                movimentacaoService.readMovimentacoesByBase(baseId)
                        .stream()
                        .map(assembler::toModel)
                        .toList();

        CollectionModel<EntityModel<MovimentacaoListagemDTO>> collection =
                CollectionModel.of(
                        lista,
                        linkTo(methodOn(MovimentacaoController.class).listarPorBase(baseId)).withSelfRel()
                );

        return ResponseEntity.ok(collection);
    }

    @Operation(summary = "Lista movimentações por base e tipo")
    @GetMapping("/base/{baseId}/tipo/{tipo}")
    public ResponseEntity<CollectionModel<EntityModel<MovimentacaoListagemDTO>>> listarPorBaseETipo(
            @PathVariable Long baseId,
            @PathVariable br.com.m5_storage.entity.movimentacao.TipoMovimentacao tipo) {

        List<EntityModel<MovimentacaoListagemDTO>> lista =
                movimentacaoService.readMovimentacoesByBaseAndTipo(baseId, tipo)
                        .stream()
                        .map(assembler::toModel)
                        .toList();

        CollectionModel<EntityModel<MovimentacaoListagemDTO>> collection =
                CollectionModel.of(
                        lista,
                        linkTo(methodOn(MovimentacaoController.class).listarPorBaseETipo(baseId, tipo)).withSelfRel()
                );

        return ResponseEntity.ok(collection);
    }
}