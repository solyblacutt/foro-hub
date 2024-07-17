package com.challenge.forohub.controllers;

import com.challenge.forohub.infra.security.AuteticacionService;
import com.challenge.forohub.infra.security.DatosJWT;
import com.challenge.forohub.infra.security.TokenService;
import com.challenge.forohub.infra.security.usuario.DatosAutenticacionUsuario;
import com.challenge.forohub.infra.security.usuario.DatosRegistroUsuario;
import com.challenge.forohub.infra.security.usuario.Usuario;
import com.challenge.forohub.helper.ResponseMessage;
import com.challenge.forohub.helper.Type;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping
public class AutenticacionController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final AuteticacionService auteticacionService;

    @Autowired
    public AutenticacionController(
            AuthenticationManager authenticationManager,
            TokenService tokenService,
            AuteticacionService auteticacionService
    ){
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.auteticacionService = auteticacionService;
    }

    @PostMapping("/login")
    public ResponseEntity<DatosJWT> login(@RequestBody @Valid DatosAutenticacionUsuario datos){
        Authentication authentication = new UsernamePasswordAuthenticationToken(datos.email(),datos.clave());
        var usuarioAuthenticated = authenticationManager.authenticate(authentication);
        var jwt = tokenService.generarToken((Usuario) usuarioAuthenticated.getPrincipal());
        return ResponseEntity.ok(new DatosJWT(jwt));
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseMessage> registrarUsuario(@RequestBody @Valid DatosRegistroUsuario datos, UriComponentsBuilder uriComponentsBuilder){
        var usuario = auteticacionService.registrarUsuario(datos);
        URI url = uriComponentsBuilder.path("/usuario/{id}").buildAndExpand(usuario.getId()).toUri();
        return ResponseEntity.created(url).body(new ResponseMessage(Type.SUCCESS,"Nuevo usuario creado"));
    }
}
