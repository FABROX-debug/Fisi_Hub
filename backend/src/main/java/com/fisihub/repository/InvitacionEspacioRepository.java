package com.fisihub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fisihub.model.EstadoInvitacion;
import com.fisihub.model.InvitacionEspacio;

public interface InvitacionEspacioRepository
        extends JpaRepository<InvitacionEspacio, Long> {

    @EntityGraph(attributePaths = {"espacio", "invitadoPor", "usuario"})
    List<InvitacionEspacio> findByEspacioIdOrderByCreadoEnDesc(Long espacioId);

    @EntityGraph(attributePaths = {"espacio", "invitadoPor", "usuario"})
    List<InvitacionEspacio> findByUsuarioIdOrderByCreadoEnDesc(Long usuarioId);

    @EntityGraph(attributePaths = {"espacio", "invitadoPor", "usuario"})
    List<InvitacionEspacio> findByEspacioIdAndEstado(Long espacioId, EstadoInvitacion estado);

    boolean existsByEspacioIdAndUsuarioIdAndEstado(
            Long espacioId,
            Long usuarioId,
            EstadoInvitacion estado);
}
