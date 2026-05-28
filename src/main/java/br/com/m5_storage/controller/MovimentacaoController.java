package br.com.m5_storage.controller;

import br.com.m5_storage.dto.movimentacao.MovimentacaoCadastroDTO;
import br.com.m5_storage.dto.movimentacao.MovimentacaoListagemDTO;
import br.com.m5_storage.entity.movimentacao.MovimentacaoAssembler;
import br.com.m5_storage.service.MovimentacaoService;
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
public class MovimentacaoController {

    private final MovimentacaoService movimentacaoService;
    private final MovimentacaoAssembler assembler;

    public MovimentacaoController(MovimentacaoService movimentacaoService,
                                  MovimentacaoAssembler assembler) {
        this.movimentacaoService = movimentacaoService;
        this.assembler = assembler;
    }

    // POST /movimentacoes → 201 Created
    // Regras 1, 2, 3, 4, 5, 8, 9, 10, 15
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

    // GET /movimentacoes/recurso/{recursoId} → 200 OK  (Regra 20: histórico)
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

    // GET /movimentacoes/usuario/{usuarioId} → 200 OK  (Regra 10)
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
}