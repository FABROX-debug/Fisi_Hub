package com.fisihub.config;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fisihub.model.EspacioTrabajo;
import com.fisihub.model.EstadoProyecto;
import com.fisihub.model.PrioridadProyecto;
import com.fisihub.model.Proyecto;
import com.fisihub.model.Rol;
import com.fisihub.model.RolNombre;
import com.fisihub.model.RolProyecto;
import com.fisihub.model.Tarea;
import com.fisihub.model.EstadoTarea;
import com.fisihub.model.PrioridadTarea;
import com.fisihub.model.Usuario;
import com.fisihub.repository.EspacioTrabajoRepository;
import com.fisihub.repository.ProyectoRepository;
import com.fisihub.repository.RolRepository;
import com.fisihub.repository.TareaRepository;
import com.fisihub.repository.UsuarioRepository;

@Component
@Profile({"dev", "local", "default"})
public class DemoDataInitializer implements ApplicationRunner {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final EspacioTrabajoRepository espacioRepository;
    private final ProyectoRepository proyectoRepository;
    private final TareaRepository tareaRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.demo.enabled:true}")
    private boolean enabled;

    public DemoDataInitializer(
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            EspacioTrabajoRepository espacioRepository,
            ProyectoRepository proyectoRepository,
            TareaRepository tareaRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.espacioRepository = espacioRepository;
        this.proyectoRepository = proyectoRepository;
        this.tareaRepository = tareaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!enabled) {
            return;
        }

        Rol rolMiembro = rolRepository.findByNombre(RolNombre.MIEMBRO)
                .orElseGet(() -> rolRepository.save(new Rol(RolNombre.MIEMBRO)));

        String correo = "demo.fisihub@example.com";
        Usuario demo = usuarioRepository.findByCorreoIgnoreCase(correo)
                .orElseGet(() -> {
                    Usuario usuario = new Usuario(
                            "Fabrizio Huaytalla",
                            correo,
                            passwordEncoder.encode("Demo1234"));
                    usuario.agregarRol(rolMiembro);
                    return usuarioRepository.save(usuario);
                });

        if (espacioRepository.findDistinctByMiembrosUsuarioCorreoIgnoreCaseOrderByCreadoEnDesc(
                correo).isEmpty()) {
            EspacioTrabajo espacio = espacioRepository.save(
                    new EspacioTrabajo(
                            "Espacio Demo FISIHUB",
                            "Datos de prueba para iniciar la aplicacion",
                            "#6D28D9",
                            "folder",
                            demo));
            espacio.agregarMiembro(demo, com.fisihub.model.RolEspacio.LIDER);
            espacio = espacioRepository.save(espacio);

            Proyecto proyecto = proyectoRepository.save(new Proyecto(
                    "Sistema FISIHUB",
                    "Proyecto demo cargado automaticamente",
                    LocalDate.now().minusDays(7),
                    LocalDate.now().plusDays(30),
                    EstadoProyecto.EN_PROCESO,
                    PrioridadProyecto.ALTA,
                    espacio,
                    demo));
            proyecto.agregarMiembro(demo, RolProyecto.LIDER);
            proyecto = proyectoRepository.save(proyecto);

            tareaRepository.save(new Tarea(
                    "Configurar autenticacion",
                    "Tarea de ejemplo para la cuenta demo",
                    proyecto,
                    demo,
                    LocalDate.now().plusDays(3),
                    EstadoTarea.EN_PROCESO,
                    PrioridadTarea.ALTA,
                    demo));
            tareaRepository.save(new Tarea(
                    "Revisar dashboard",
                    "Otra tarea de ejemplo",
                    proyecto,
                    demo,
                    LocalDate.now().plusDays(5),
                    EstadoTarea.PENDIENTE,
                    PrioridadTarea.MEDIA,
                    demo));
        }
    }
}
