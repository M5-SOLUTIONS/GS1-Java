package br.com.m5_storage.controller;

import br.com.m5_storage.dto.usuario.UsuarioAtualizarDTO;
import br.com.m5_storage.dto.usuario.UsuarioCadastroDTO;
import br.com.m5_storage.dto.usuario.UsuarioListagemDTO;
import br.com.m5_storage.entity.usuario.UsuarioAssembler;
import br.com.m5_storage.service.UsuarioService;
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
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioAssembler assembler;

    public UsuarioController(UsuarioService usuarioService, UsuarioAssembler assembler) {
        this.usuarioService = usuarioService;
        this.assembler = assembler;
    }

    // POST /usuarios → 201 Created
    @PostMapping
    public ResponseEntity<EntityModel<UsuarioListagemDTO>> criar(
            @RequestBody @Valid UsuarioCadastroDTO dto) {

        UsuarioListagemDTO criado = usuarioService.createUsuario(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(criado.id())
                .toUri();

        return ResponseEntity.created(location).body(assembler.toModel(criado));
    }

    // GET /usuarios → 200 OK
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<UsuarioListagemDTO>>> listarTodos() {
        List<EntityModel<UsuarioListagemDTO>> lista = usuarioService.readAllUsuarios()
                .stream()
                .map(assembler::toModel)
                .toList();

        CollectionModel<EntityModel<UsuarioListagemDTO>> collection = CollectionModel.of(lista,
                linkTo(methodOn(UsuarioController.class).listarTodos()).withSelfRel());

        return ResponseEntity.ok(collection);
    }

    // GET /usuarios/{id} → 200 OK
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UsuarioListagemDTO>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(assembler.toModel(usuarioService.readUsuarioById(id)));
    }

    // PUT /usuarios/{id} → 200 OK
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<UsuarioListagemDTO>> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid UsuarioAtualizarDTO dto) {

        return ResponseEntity.ok(assembler.toModel(usuarioService.updateUsuario(id, dto)));
    }

    // DELETE /usuarios/{id} → 204 No Content
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        usuarioService.deleteUsuario(id);
        return ResponseEntity.noContent().build();
    }
}