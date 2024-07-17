package com.challenge.forohub.domain.respuesta;

import com.challenge.forohub.helper.ResponseMessage;
import com.challenge.forohub.helper.Type;
import com.challenge.forohub.infra.security.usuario.Usuario;
import com.challenge.forohub.domain.topico.TopicoRespository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class RespuestaService {

    private final RespuestaRepository respuestaRepository;
    private final TopicoRespository topicoRespository;

    public RespuestaService(RespuestaRepository respuestaRepository,TopicoRespository topicoRespository) {
        this.respuestaRepository = respuestaRepository;
        this.topicoRespository = topicoRespository;
    }

    @Transactional
    public DatosDetalleRespuesta registrarRespuesta(DatosRegistroRespuesta datos) {

        if(datos.idTopico() == null){
            throw new ValidationException("El id del topico es obligatorio y debe ser diferente de nulo");
        }

        if(!topicoRespository.existsById(datos.idTopico())){
            throw new EntityNotFoundException("No existe ningun topico con ese id");
        }

        if(respuestaRepository.existsByMensaje(datos.mensaje())){
            throw new DataIntegrityViolationException("Ya existe una respuesta con este mensaje");
        }

        if(respuestaRepository.existsBySolucion(datos.solucion())){
            throw new DataIntegrityViolationException("Solucion duplicada");
        }

        var topico = topicoRespository.getReferenceById(datos.idTopico());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var usuario = (Usuario) authentication.getPrincipal();

        var respuesta = new Respuesta(datos,topico,usuario);

        respuestaRepository.save(respuesta);

        return new DatosDetalleRespuesta(
                respuesta.getId(),
                respuesta.getMensaje(),
                respuesta.getSolucion(),
                respuesta.getAutor().getNombre(),
                respuesta.getTopico().getTitulo(),
                respuesta.getFechaCreacion(),
                respuesta.getStatus()
        );
    }

    public ResponseMessage deleteRespuesta(Long id) {
        if(id ==  null){
            throw new ValidationException("El id de la respuesta debe ser diferente de nulo");
        }

        if (!respuestaRepository.existsById(id)){
            throw new EntityNotFoundException("El id de referencia a la respuesta no existe");
        }

        var respuesta = respuestaRepository.getReferenceById(id);
        respuestaRepository.delete(respuesta);

        return new ResponseMessage(Type.SUCCESS, "La respuesta se elimino correctamente");
    }

    public DatosDetalleRespuesta updateRespuesta(DatosActualizacionRespuesta datos) {

        if(datos.idRespuesta() == null){
            throw new ValidationException("El id de la respuesta debe ser diferente de nulo");
        }

        if(!respuestaRepository.existsById(datos.idRespuesta())){
            throw new EntityNotFoundException("el id de referencia a la respuesta no existe");
        }

        var respuesta = respuestaRepository.getReferenceById(datos.idRespuesta());

        respuesta.actualizarDatos(datos);

        return new DatosDetalleRespuesta(
                respuesta.getId(),
                respuesta.getMensaje(),
                respuesta.getSolucion(),
                respuesta.getAutor().getNombre(),
                respuesta.getTopico().getTitulo(),
                respuesta.getFechaCreacion(),
                respuesta.getStatus()
        );
    }
}
