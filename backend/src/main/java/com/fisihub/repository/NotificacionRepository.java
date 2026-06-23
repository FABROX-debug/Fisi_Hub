package com.fisihub.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fisihub.model.Notificacion;
import com.fisihub.model.TipoNotificacion;

public interface NotificacionRepository
        extends JpaRepository<Notificacion, Long> {

    @EntityGraph(attributePaths = {"usuario"})
    List<Notificacion> findByUsuarioCorreoIgnoreCaseOrderByCreadoEnDesc(
            String correo);

    @EntityGraph(attributePaths = {"usuario"})
    Optional<Notificacion> findByIdAndUsuarioCorreoIgnoreCase(
            Long id,
            String correo);

    boolean existsByUsuarioIdAndTipoAndReferenciaId(
            Long usuarioId,
            TipoNotificacion tipo,
            Long referenciaId);

    void deleteByUsuarioIdAndTipoAndReferenciaId(
            Long usuarioId,
            TipoNotificacion tipo,
            Long referenciaId);

    List<Notificacion> findByUsuarioCorreoIgnoreCaseAndLeidaFalse(
            String correo);
}
