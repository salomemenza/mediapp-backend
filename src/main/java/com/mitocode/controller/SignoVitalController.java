package com.mitocode.controller;

import com.mitocode.dto.EspecialidadDTO;
import com.mitocode.dto.SignoVitalDTO;
import com.mitocode.exception.ModeloNotFoundException;
import com.mitocode.model.Especialidad;
import com.mitocode.model.SignoVital;
import com.mitocode.service.ISignoVitalService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/signos-vitales")
public class SignoVitalController {
    @Autowired
    private ModelMapper mapper;

    @Autowired
    private ISignoVitalService service;

    @GetMapping
    //@RequestMapping(value = "/" , method = RequestMethod.GET)
    public ResponseEntity<List<SignoVitalDTO>> listar() throws Exception {
        List<SignoVitalDTO> lista = service.listar().stream().map(p -> mapper.map(p, SignoVitalDTO.class)).collect(Collectors.toList());

        return new ResponseEntity<>(lista, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SignoVitalDTO> listarPorId(@PathVariable("id") Integer id) throws Exception {
        SignoVital obj = service.listarPorId(id);

        if(obj == null) {
            throw new ModeloNotFoundException("ID NO ENCONTRADO " + id);
        }

        SignoVitalDTO dto = mapper.map(obj, SignoVitalDTO.class);

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Void> registrar(@Valid @RequestBody SignoVitalDTO dto) throws Exception {
        SignoVital p = mapper.map(dto, SignoVital.class);
        SignoVital obj = service.registrar(p);

        //localhost:8080/pacientes/5
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getIdSignoVital()).toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping
    public ResponseEntity<SignoVitalDTO> modificar(@Valid @RequestBody SignoVitalDTO dto) throws Exception {
        SignoVital obj = service.listarPorId(dto.getIdSignoVital());

        if(obj == null) {
            throw new ModeloNotFoundException("ID NO ENCONTRADO " + dto.getIdSignoVital());
        }

        SignoVital p = mapper.map(dto, SignoVital.class);
        SignoVital pac = service.modificar(p);
        SignoVitalDTO dtoResponse = mapper.map(pac, SignoVitalDTO.class);
        return new ResponseEntity<>(dtoResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Integer id) throws Exception {
        SignoVital obj = service.listarPorId(id);

        if(obj == null) {
            throw new ModeloNotFoundException("ID NO ENCONTRADO " + id);
        }
        service.eliminar(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
