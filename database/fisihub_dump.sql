--
-- PostgreSQL database dump
--

\restrict 7fBAW43AXNU1fMvR4IxpFUmsCKk5GC9ggYbP72JqCvjh6rEsnubcujXtuHjNhg3

-- Dumped from database version 18.4
-- Dumped by pg_dump version 18.4

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

ALTER TABLE IF EXISTS ONLY public.miembro_proyecto DROP CONSTRAINT IF EXISTS fkskk6a5giwahk4y7avvlxb1564;
ALTER TABLE IF EXISTS ONLY public.espacio_trabajo DROP CONSTRAINT IF EXISTS fkr4cfbqee3onc14wdmih7o6ult;
ALTER TABLE IF EXISTS ONLY public.invitacion_espacio DROP CONSTRAINT IF EXISTS fkpbfqp7hdag94g4axj0ynl7qyb;
ALTER TABLE IF EXISTS ONLY public.proyecto DROP CONSTRAINT IF EXISTS fkp2gthugb7rbtlv3n8k4lmi7mo;
ALTER TABLE IF EXISTS ONLY public.miembro_proyecto DROP CONSTRAINT IF EXISTS fkonrcb6adygm028okkixwb75ea;
ALTER TABLE IF EXISTS ONLY public.espacio_miembro DROP CONSTRAINT IF EXISTS fko14wjal4q3e52bxq5359iho2m;
ALTER TABLE IF EXISTS ONLY public.espacio_miembro DROP CONSTRAINT IF EXISTS fkm66s9c03n332r50ktbr5afkxg;
ALTER TABLE IF EXISTS ONLY public.proyecto DROP CONSTRAINT IF EXISTS fkm148i0xpkkrl1fgo4p8euts90;
ALTER TABLE IF EXISTS ONLY public.historial_actividad DROP CONSTRAINT IF EXISTS fklhxwdnyenkfd9u9imtr9xavjs;
ALTER TABLE IF EXISTS ONLY public.comentario DROP CONSTRAINT IF EXISTS fkkqaouj99djvseplfuaxmvg6tw;
ALTER TABLE IF EXISTS ONLY public.tarea DROP CONSTRAINT IF EXISTS fkgsqbkt38hrb1beqhd50j2x9op;
ALTER TABLE IF EXISTS ONLY public.historial_actividad DROP CONSTRAINT IF EXISTS fkdd9eqg7be0k8cn7ixhpyq19y0;
ALTER TABLE IF EXISTS ONLY public.invitacion_espacio DROP CONSTRAINT IF EXISTS fkcuw51c8t7qv578bl01h7fmftj;
ALTER TABLE IF EXISTS ONLY public.usuario_rol DROP CONSTRAINT IF EXISTS fkbyfgloj439r9wr9smrms9u33r;
ALTER TABLE IF EXISTS ONLY public.tarea DROP CONSTRAINT IF EXISTS fkagdgkbqcdlwe0wgbrs8v7pjhv;
ALTER TABLE IF EXISTS ONLY public.tarea DROP CONSTRAINT IF EXISTS fka1lvp5rhjg8iwvmds9esksvfd;
ALTER TABLE IF EXISTS ONLY public.usuario_rol DROP CONSTRAINT IF EXISTS fk610kvhkwcqk2pxeewur4l7bd1;
ALTER TABLE IF EXISTS ONLY public.notificacion DROP CONSTRAINT IF EXISTS fk5hnclv9lmmc1w4335x04warbm;
ALTER TABLE IF EXISTS ONLY public.invitacion_espacio DROP CONSTRAINT IF EXISTS fk3u0rh99uhbfjpmeuqut6e2cw6;
ALTER TABLE IF EXISTS ONLY public.comentario DROP CONSTRAINT IF EXISTS fk1gcyoyuhavbjdhqr9hbulwa69;
ALTER TABLE IF EXISTS ONLY public.usuario_rol DROP CONSTRAINT IF EXISTS usuario_rol_pkey;
ALTER TABLE IF EXISTS ONLY public.usuario DROP CONSTRAINT IF EXISTS usuario_pkey;
ALTER TABLE IF EXISTS ONLY public.usuario_rol DROP CONSTRAINT IF EXISTS uk_usuario_rol;
ALTER TABLE IF EXISTS ONLY public.notificacion DROP CONSTRAINT IF EXISTS uk_notificacion_usuario_tipo_referencia;
ALTER TABLE IF EXISTS ONLY public.miembro_proyecto DROP CONSTRAINT IF EXISTS uk_miembro_proyecto;
ALTER TABLE IF EXISTS ONLY public.espacio_miembro DROP CONSTRAINT IF EXISTS uk_espacio_miembro;
ALTER TABLE IF EXISTS ONLY public.rol DROP CONSTRAINT IF EXISTS uk43kr6s7bts1wqfv43f7jd87kp;
ALTER TABLE IF EXISTS ONLY public.usuario DROP CONSTRAINT IF EXISTS uk2mlfr087gb1ce55f2j87o74t;
ALTER TABLE IF EXISTS ONLY public.tarea DROP CONSTRAINT IF EXISTS tarea_pkey;
ALTER TABLE IF EXISTS ONLY public.rol DROP CONSTRAINT IF EXISTS rol_pkey;
ALTER TABLE IF EXISTS ONLY public.proyecto DROP CONSTRAINT IF EXISTS proyecto_pkey;
ALTER TABLE IF EXISTS ONLY public.notificacion DROP CONSTRAINT IF EXISTS notificacion_pkey;
ALTER TABLE IF EXISTS ONLY public.miembro_proyecto DROP CONSTRAINT IF EXISTS miembro_proyecto_pkey;
ALTER TABLE IF EXISTS ONLY public.invitacion_espacio DROP CONSTRAINT IF EXISTS invitacion_espacio_pkey;
ALTER TABLE IF EXISTS ONLY public.historial_actividad DROP CONSTRAINT IF EXISTS historial_actividad_pkey;
ALTER TABLE IF EXISTS ONLY public.espacio_trabajo DROP CONSTRAINT IF EXISTS espacio_trabajo_pkey;
ALTER TABLE IF EXISTS ONLY public.espacio_miembro DROP CONSTRAINT IF EXISTS espacio_miembro_pkey;
ALTER TABLE IF EXISTS ONLY public.comentario DROP CONSTRAINT IF EXISTS comentario_pkey;
DROP TABLE IF EXISTS public.usuario_rol;
DROP TABLE IF EXISTS public.usuario;
DROP TABLE IF EXISTS public.tarea;
DROP TABLE IF EXISTS public.rol;
DROP TABLE IF EXISTS public.proyecto;
DROP TABLE IF EXISTS public.notificacion;
DROP TABLE IF EXISTS public.miembro_proyecto;
DROP TABLE IF EXISTS public.invitacion_espacio;
DROP TABLE IF EXISTS public.historial_actividad;
DROP TABLE IF EXISTS public.espacio_trabajo;
DROP TABLE IF EXISTS public.espacio_miembro;
DROP TABLE IF EXISTS public.comentario;
SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: comentario; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.comentario (
    id bigint NOT NULL,
    contenido character varying(2000) NOT NULL,
    creado_en timestamp(6) without time zone NOT NULL,
    autor_id bigint NOT NULL,
    tarea_id bigint NOT NULL
);


--
-- Name: comentario_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.comentario ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.comentario_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: espacio_miembro; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.espacio_miembro (
    id bigint NOT NULL,
    rol character varying(20) NOT NULL,
    espacio_id bigint NOT NULL,
    usuario_id bigint NOT NULL,
    CONSTRAINT espacio_miembro_rol_check CHECK (((rol)::text = ANY ((ARRAY['LIDER'::character varying, 'MIEMBRO'::character varying])::text[])))
);


--
-- Name: espacio_miembro_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.espacio_miembro ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.espacio_miembro_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: espacio_trabajo; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.espacio_trabajo (
    id bigint NOT NULL,
    color character varying(7) NOT NULL,
    creado_en timestamp(6) without time zone NOT NULL,
    descripcion character varying(500),
    icono character varying(40) NOT NULL,
    nombre character varying(120) NOT NULL,
    creado_por_id bigint NOT NULL
);


--
-- Name: espacio_trabajo_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.espacio_trabajo ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.espacio_trabajo_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: historial_actividad; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.historial_actividad (
    id bigint NOT NULL,
    descripcion character varying(500) NOT NULL,
    fecha timestamp(6) without time zone NOT NULL,
    tipo character varying(40) NOT NULL,
    proyecto_id bigint NOT NULL,
    usuario_id bigint NOT NULL,
    CONSTRAINT historial_actividad_tipo_check CHECK (((tipo)::text = ANY ((ARRAY['PROYECTO_CREADO'::character varying, 'TAREA_CREADA'::character varying, 'ESTADO_TAREA_CAMBIADO'::character varying, 'MIEMBRO_AGREGADO'::character varying, 'COMENTARIO_CREADO'::character varying])::text[])))
);


--
-- Name: historial_actividad_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.historial_actividad ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.historial_actividad_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: invitacion_espacio; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.invitacion_espacio (
    id bigint NOT NULL,
    actualizado_en timestamp(6) without time zone NOT NULL,
    creado_en timestamp(6) without time zone NOT NULL,
    estado character varying(20) NOT NULL,
    expira_en timestamp(6) without time zone NOT NULL,
    rol character varying(20) NOT NULL,
    espacio_id bigint NOT NULL,
    invitado_por_id bigint NOT NULL,
    usuario_id bigint NOT NULL,
    CONSTRAINT invitacion_espacio_estado_check CHECK (((estado)::text = ANY ((ARRAY['PENDIENTE'::character varying, 'ACEPTADA'::character varying, 'EXPIRADA'::character varying, 'REVOCADA'::character varying])::text[]))),
    CONSTRAINT invitacion_espacio_rol_check CHECK (((rol)::text = ANY ((ARRAY['LIDER'::character varying, 'MIEMBRO'::character varying])::text[])))
);


--
-- Name: invitacion_espacio_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.invitacion_espacio ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.invitacion_espacio_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: miembro_proyecto; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.miembro_proyecto (
    id bigint NOT NULL,
    rol_en_proyecto character varying(20) NOT NULL,
    proyecto_id bigint NOT NULL,
    usuario_id bigint NOT NULL,
    CONSTRAINT miembro_proyecto_rol_en_proyecto_check CHECK (((rol_en_proyecto)::text = ANY ((ARRAY['LIDER'::character varying, 'MIEMBRO'::character varying])::text[])))
);


--
-- Name: miembro_proyecto_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.miembro_proyecto ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.miembro_proyecto_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: notificacion; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.notificacion (
    id bigint NOT NULL,
    creado_en timestamp(6) without time zone NOT NULL,
    leida boolean NOT NULL,
    mensaje character varying(500) NOT NULL,
    referencia_id bigint NOT NULL,
    tipo character varying(30) NOT NULL,
    usuario_id bigint NOT NULL
);


--
-- Name: notificacion_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.notificacion ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.notificacion_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: proyecto; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.proyecto (
    id bigint NOT NULL,
    creado_en timestamp(6) without time zone NOT NULL,
    descripcion character varying(1000),
    estado character varying(20) NOT NULL,
    fecha_fin date,
    fecha_inicio date,
    nombre character varying(140) NOT NULL,
    porcentaje_avance integer NOT NULL,
    prioridad character varying(20) NOT NULL,
    espacio_id bigint NOT NULL,
    lider_id bigint NOT NULL,
    CONSTRAINT proyecto_estado_check CHECK (((estado)::text = ANY ((ARRAY['PLANIFICADO'::character varying, 'EN_PROCESO'::character varying, 'EN_REVISION'::character varying, 'FINALIZADO'::character varying, 'CANCELADO'::character varying])::text[]))),
    CONSTRAINT proyecto_prioridad_check CHECK (((prioridad)::text = ANY ((ARRAY['BAJA'::character varying, 'MEDIA'::character varying, 'ALTA'::character varying, 'URGENTE'::character varying])::text[])))
);


--
-- Name: proyecto_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.proyecto ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.proyecto_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: rol; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.rol (
    id bigint NOT NULL,
    nombre character varying(20) NOT NULL,
    CONSTRAINT rol_nombre_check CHECK (((nombre)::text = ANY ((ARRAY['ADMIN'::character varying, 'LIDER'::character varying, 'MIEMBRO'::character varying])::text[])))
);


--
-- Name: rol_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.rol ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.rol_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: tarea; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tarea (
    id bigint NOT NULL,
    actualizado_en timestamp(6) without time zone NOT NULL,
    creado_en timestamp(6) without time zone NOT NULL,
    descripcion character varying(2000),
    estado character varying(20) NOT NULL,
    fecha_limite date,
    prioridad character varying(20) NOT NULL,
    titulo character varying(180) NOT NULL,
    creado_por_id bigint NOT NULL,
    proyecto_id bigint NOT NULL,
    responsable_id bigint,
    CONSTRAINT tarea_estado_check CHECK (((estado)::text = ANY ((ARRAY['PENDIENTE'::character varying, 'EN_PROCESO'::character varying, 'EN_REVISION'::character varying, 'COMPLETADA'::character varying, 'BLOQUEADA'::character varying])::text[]))),
    CONSTRAINT tarea_prioridad_check CHECK (((prioridad)::text = ANY ((ARRAY['BAJA'::character varying, 'MEDIA'::character varying, 'ALTA'::character varying, 'URGENTE'::character varying])::text[])))
);


--
-- Name: tarea_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.tarea ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.tarea_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: usuario; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.usuario (
    id bigint NOT NULL,
    activo boolean NOT NULL,
    correo character varying(180) NOT NULL,
    creado_en timestamp(6) without time zone NOT NULL,
    nombre character varying(120) NOT NULL,
    password character varying(100) NOT NULL
);


--
-- Name: usuario_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.usuario ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.usuario_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: usuario_rol; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.usuario_rol (
    id bigint NOT NULL,
    rol_id bigint NOT NULL,
    usuario_id bigint NOT NULL
);


--
-- Name: usuario_rol_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.usuario_rol ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.usuario_rol_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Data for Name: comentario; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.comentario (id, contenido, creado_en, autor_id, tarea_id) FROM stdin;
\.


--
-- Data for Name: espacio_miembro; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.espacio_miembro (id, rol, espacio_id, usuario_id) FROM stdin;
4	LIDER	4	7
5	MIEMBRO	4	5
6	MIEMBRO	4	8
\.


--
-- Data for Name: espacio_trabajo; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.espacio_trabajo (id, color, creado_en, descripcion, icono, nombre, creado_por_id) FROM stdin;
4	#6D28D9	2026-06-12 20:48:37.436581	Espacio con datos de prueba del MVP	folder	Espacio Demo FISIHUB	7
\.


--
-- Data for Name: historial_actividad; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.historial_actividad (id, descripcion, fecha, tipo, proyecto_id, usuario_id) FROM stdin;
1	Usuario Demo FISIHUB creo el proyecto "Proyecto Demo Plataforma Web"	2026-06-12 20:48:37.510729	PROYECTO_CREADO	4	7
2	Usuario Demo FISIHUB creo la tarea "Definir alcance del MVP"	2026-06-12 20:48:37.59622	TAREA_CREADA	4	7
3	Usuario Demo FISIHUB creo la tarea "Validar endpoints REST"	2026-06-12 20:50:34.667559	TAREA_CREADA	4	7
4	Usuario Demo FISIHUB creo la tarea "Preparar presentacion final"	2026-06-12 20:50:34.699915	TAREA_CREADA	4	7
5	Usuario Demo FISIHUB creo la tarea "Resolver incidencia de despliegue"	2026-06-12 20:50:34.734774	TAREA_CREADA	4	7
6	Usuario Demo FISIHUB creo la tarea "Disenar interfaz principal"	2026-06-12 20:50:55.010209	TAREA_CREADA	4	7
7	Usuario Demo FISIHUB agrego a fabriziohuaytalla al proyecto	2026-06-13 01:18:05.170657	MIEMBRO_AGREGADO	4	7
8	Usuario Demo FISIHUB cambio "Disenar interfaz principal" de EN_PROCESO a EN_REVISION	2026-06-13 01:18:19.050984	ESTADO_TAREA_CAMBIADO	4	7
9	Usuario Demo FISIHUB cambio "Disenar interfaz principal" de EN_REVISION a EN_PROCESO	2026-06-13 01:18:33.149856	ESTADO_TAREA_CAMBIADO	4	7
10	Usuario Demo FISIHUB cambio "Validar endpoints REST" de EN_REVISION a PENDIENTE	2026-06-13 01:18:36.100744	ESTADO_TAREA_CAMBIADO	4	7
11	Usuario Demo FISIHUB creo la tarea "Pruebas Funcionales"	2026-06-13 01:29:07.743266	TAREA_CREADA	4	7
12	Usuario Demo FISIHUB cambio "Disenar interfaz principal" de EN_PROCESO a COMPLETADA	2026-06-13 01:29:41.830074	ESTADO_TAREA_CAMBIADO	4	7
13	Usuario Demo FISIHUB cambio "Pruebas Funcionales" de PENDIENTE a COMPLETADA	2026-06-13 01:29:43.618245	ESTADO_TAREA_CAMBIADO	4	7
14	Usuario Demo FISIHUB cambio "Preparar presentacion final" de PENDIENTE a COMPLETADA	2026-06-13 01:29:47.061525	ESTADO_TAREA_CAMBIADO	4	7
15	Usuario Demo FISIHUB cambio "Validar endpoints REST" de PENDIENTE a COMPLETADA	2026-06-13 01:29:49.890615	ESTADO_TAREA_CAMBIADO	4	7
16	Usuario Demo FISIHUB cambio "Resolver incidencia de despliegue" de BLOQUEADA a PENDIENTE	2026-06-13 01:29:57.100902	ESTADO_TAREA_CAMBIADO	4	7
17	Usuario Demo FISIHUB cambio "Resolver incidencia de despliegue" de PENDIENTE a COMPLETADA	2026-06-13 01:29:59.421733	ESTADO_TAREA_CAMBIADO	4	7
18	Usuario Demo FISIHUB cambio "Pruebas Funcionales" de COMPLETADA a PENDIENTE	2026-06-13 01:30:01.799743	ESTADO_TAREA_CAMBIADO	4	7
19	Usuario Demo FISIHUB cambio "Disenar interfaz principal" de COMPLETADA a EN_REVISION	2026-06-23 00:25:36.36294	ESTADO_TAREA_CAMBIADO	4	7
20	Usuario Demo FISIHUB cambio "Pruebas Funcionales" de PENDIENTE a COMPLETADA	2026-06-23 00:29:39.87588	ESTADO_TAREA_CAMBIADO	4	7
21	Usuario Demo FISIHUB cambio "Disenar interfaz principal" de EN_REVISION a COMPLETADA	2026-06-23 00:29:44.267058	ESTADO_TAREA_CAMBIADO	4	7
22	Usuario Demo FISIHUB cambio "Pruebas Funcionales" de COMPLETADA a EN_REVISION	2026-06-23 00:29:46.125919	ESTADO_TAREA_CAMBIADO	4	7
23	Usuario Demo FISIHUB creo la tarea "Modificar Base de datos"	2026-06-23 00:30:28.527399	TAREA_CREADA	4	7
24	Usuario Demo FISIHUB cambio "Modificar Base de datos" de PENDIENTE a EN_PROCESO	2026-06-23 00:30:42.476614	ESTADO_TAREA_CAMBIADO	4	7
25	Usuario Demo FISIHUB cambio "Modificar Base de datos" de EN_PROCESO a PENDIENTE	2026-06-23 00:30:45.870567	ESTADO_TAREA_CAMBIADO	4	7
\.


--
-- Data for Name: invitacion_espacio; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.invitacion_espacio (id, actualizado_en, creado_en, estado, expira_en, rol, espacio_id, invitado_por_id, usuario_id) FROM stdin;
9	2026-06-13 01:17:30.445825	2026-06-13 01:17:14.216277	ACEPTADA	2026-06-20 01:17:14.215632	MIEMBRO	4	7	5
10	2026-06-23 00:32:44.643432	2026-06-23 00:32:22.661675	ACEPTADA	2026-06-30 00:32:22.660472	MIEMBRO	4	7	8
\.


--
-- Data for Name: miembro_proyecto; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.miembro_proyecto (id, rol_en_proyecto, proyecto_id, usuario_id) FROM stdin;
4	LIDER	4	7
5	MIEMBRO	4	5
\.


--
-- Data for Name: notificacion; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.notificacion (id, creado_en, leida, mensaje, referencia_id, tipo, usuario_id) FROM stdin;
6	2026-06-13 01:17:14.269991	t	Usuario Demo FISIHUB te invito al espacio "Espacio Demo FISIHUB" como MIEMBRO	9	INVITACION_ESPACIO	5
8	2026-06-13 01:29:07.750792	t	Te asignaron la tarea "Pruebas Funcionales" en Proyecto Demo Plataforma Web	9	ASIGNACION_TAREA	5
7	2026-06-13 01:18:05.176617	t	Fuiste agregado al proyecto "Proyecto Demo Plataforma Web"	4	MIEMBRO_PROYECTO	5
3	2026-06-12 20:50:34.70492	t	Te asignaron la tarea "Preparar presentacion final" en Proyecto Demo Plataforma Web	6	ASIGNACION_TAREA	7
2	2026-06-12 20:50:34.672373	t	Te asignaron la tarea "Validar endpoints REST" en Proyecto Demo Plataforma Web	5	ASIGNACION_TAREA	7
1	2026-06-12 20:48:37.60249	t	Te asignaron la tarea "Definir alcance del MVP" en Proyecto Demo Plataforma Web	4	ASIGNACION_TAREA	7
4	2026-06-12 20:50:34.738213	t	Te asignaron la tarea "Resolver incidencia de despliegue" en Proyecto Demo Plataforma Web	7	ASIGNACION_TAREA	7
5	2026-06-12 20:50:55.016724	t	Te asignaron la tarea "Disenar interfaz principal" en Proyecto Demo Plataforma Web	8	ASIGNACION_TAREA	7
9	2026-06-23 00:30:28.529799	f	Te asignaron la tarea "Modificar Base de datos" en Proyecto Demo Plataforma Web	10	ASIGNACION_TAREA	5
10	2026-06-23 00:32:22.665525	t	Usuario Demo FISIHUB te invito al espacio "Espacio Demo FISIHUB" como MIEMBRO	10	INVITACION_ESPACIO	8
\.


--
-- Data for Name: proyecto; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.proyecto (id, creado_en, descripcion, estado, fecha_fin, fecha_inicio, nombre, porcentaje_avance, prioridad, espacio_id, lider_id) FROM stdin;
4	2026-06-12 20:48:37.504916	Proyecto completo para probar dashboard, tareas, Kanban y reportes	EN_PROCESO	2026-07-12	2026-06-12	Proyecto Demo Plataforma Web	71	ALTA	4	7
\.


--
-- Data for Name: rol; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.rol (id, nombre) FROM stdin;
1	ADMIN
2	LIDER
3	MIEMBRO
\.


--
-- Data for Name: tarea; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.tarea (id, actualizado_en, creado_en, descripcion, estado, fecha_limite, prioridad, titulo, creado_por_id, proyecto_id, responsable_id) FROM stdin;
4	2026-06-12 20:48:37.584916	2026-06-12 20:48:37.584916	Revisar requisitos y criterios de aceptacion	COMPLETADA	2026-06-14	ALTA	Definir alcance del MVP	7	4	7
6	2026-06-13 01:29:47.054404	2026-06-12 20:50:34.696889	Consolidar resultados del proyecto	COMPLETADA	2026-06-22	URGENTE	Preparar presentacion final	7	4	7
5	2026-06-13 01:29:49.882465	2026-06-12 20:50:34.663501	Probar autenticacion y recursos protegidos	COMPLETADA	2026-06-19	MEDIA	Validar endpoints REST	7	4	7
7	2026-06-13 01:29:59.410828	2026-06-12 20:50:34.728953	Tarea bloqueada para visualizar la columna Kanban	COMPLETADA	2026-06-24	MEDIA	Resolver incidencia de despliegue	7	4	7
8	2026-06-23 00:29:44.262683	2026-06-12 20:50:55.008439	Preparar layout responsive y componentes	COMPLETADA	2026-06-17	ALTA	Disenar interfaz principal	7	4	7
9	2026-06-23 00:29:46.121032	2026-06-13 01:29:07.695009	\N	EN_REVISION	\N	MEDIA	Pruebas Funcionales	7	4	5
10	2026-06-23 00:30:45.866499	2026-06-23 00:30:28.521213	\N	PENDIENTE	2026-06-29	ALTA	Modificar Base de datos	7	4	5
\.


--
-- Data for Name: usuario; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.usuario (id, activo, correo, creado_en, nombre, password) FROM stdin;
1	t	fabrizio@test.com	2026-06-12 11:37:04.56828	Fabrizio Huaytalla	$2a$10$bQ4JDahvi7g.Q3zbXpbRmeaNu6DhQGmK8oaSea.p4HhNQJ4isk4wC
2	t	fabrizio.test.local@example.com	2026-06-12 11:50:02.32678	Fabrizio Huaytalla	$2a$10$fLrKgCzSCyxu81QHpoLrf.5z1eSSiPLZEvbkKzyO4F0UlkakNHl4.
5	t	fraziohuaytalla@gmail.com	2026-06-12 12:17:14.008285	fabriziohuaytalla	$2a$10$QjoFtBUevVHZkoPAsO/F8.98tgSVXNjYu7JRN39BdwReEmaNlsGYq
7	t	demo.fisihub@example.com	2026-06-12 20:48:37.354263	Usuario Demo FISIHUB	$2a$10$YJcK3vk1CmZEsVkRQ969I.JeJBBkPq9BFTee.KOUYXXeDAtE9703m
8	t	miguelkbro@unmsm.edu.pe	2026-06-23 00:28:38.923907	Miguel Solis Cunza	$2a$10$A0PM5OkpRjBm5jya1HqHLexOdryPyEXQPOBQjrtbd4G2F/KVJTzWC
\.


--
-- Data for Name: usuario_rol; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.usuario_rol (id, rol_id, usuario_id) FROM stdin;
1	3	1
2	3	2
5	3	5
7	3	7
8	3	8
\.


--
-- Name: comentario_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.comentario_id_seq', 1, false);


--
-- Name: espacio_miembro_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.espacio_miembro_id_seq', 6, true);


--
-- Name: espacio_trabajo_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.espacio_trabajo_id_seq', 4, true);


--
-- Name: historial_actividad_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.historial_actividad_id_seq', 25, true);


--
-- Name: invitacion_espacio_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.invitacion_espacio_id_seq', 10, true);


--
-- Name: miembro_proyecto_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.miembro_proyecto_id_seq', 5, true);


--
-- Name: notificacion_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.notificacion_id_seq', 10, true);


--
-- Name: proyecto_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.proyecto_id_seq', 4, true);


--
-- Name: rol_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.rol_id_seq', 3, true);


--
-- Name: tarea_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.tarea_id_seq', 10, true);


--
-- Name: usuario_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.usuario_id_seq', 8, true);


--
-- Name: usuario_rol_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.usuario_rol_id_seq', 8, true);


--
-- Name: comentario comentario_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.comentario
    ADD CONSTRAINT comentario_pkey PRIMARY KEY (id);


--
-- Name: espacio_miembro espacio_miembro_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.espacio_miembro
    ADD CONSTRAINT espacio_miembro_pkey PRIMARY KEY (id);


--
-- Name: espacio_trabajo espacio_trabajo_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.espacio_trabajo
    ADD CONSTRAINT espacio_trabajo_pkey PRIMARY KEY (id);


--
-- Name: historial_actividad historial_actividad_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.historial_actividad
    ADD CONSTRAINT historial_actividad_pkey PRIMARY KEY (id);


--
-- Name: invitacion_espacio invitacion_espacio_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.invitacion_espacio
    ADD CONSTRAINT invitacion_espacio_pkey PRIMARY KEY (id);


--
-- Name: miembro_proyecto miembro_proyecto_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.miembro_proyecto
    ADD CONSTRAINT miembro_proyecto_pkey PRIMARY KEY (id);


--
-- Name: notificacion notificacion_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.notificacion
    ADD CONSTRAINT notificacion_pkey PRIMARY KEY (id);


--
-- Name: proyecto proyecto_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.proyecto
    ADD CONSTRAINT proyecto_pkey PRIMARY KEY (id);


--
-- Name: rol rol_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.rol
    ADD CONSTRAINT rol_pkey PRIMARY KEY (id);


--
-- Name: tarea tarea_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tarea
    ADD CONSTRAINT tarea_pkey PRIMARY KEY (id);


--
-- Name: usuario uk2mlfr087gb1ce55f2j87o74t; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.usuario
    ADD CONSTRAINT uk2mlfr087gb1ce55f2j87o74t UNIQUE (correo);


--
-- Name: rol uk43kr6s7bts1wqfv43f7jd87kp; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.rol
    ADD CONSTRAINT uk43kr6s7bts1wqfv43f7jd87kp UNIQUE (nombre);


--
-- Name: espacio_miembro uk_espacio_miembro; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.espacio_miembro
    ADD CONSTRAINT uk_espacio_miembro UNIQUE (espacio_id, usuario_id);


--
-- Name: miembro_proyecto uk_miembro_proyecto; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.miembro_proyecto
    ADD CONSTRAINT uk_miembro_proyecto UNIQUE (proyecto_id, usuario_id);


--
-- Name: notificacion uk_notificacion_usuario_tipo_referencia; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.notificacion
    ADD CONSTRAINT uk_notificacion_usuario_tipo_referencia UNIQUE (usuario_id, tipo, referencia_id);


--
-- Name: usuario_rol uk_usuario_rol; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.usuario_rol
    ADD CONSTRAINT uk_usuario_rol UNIQUE (usuario_id, rol_id);


--
-- Name: usuario usuario_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.usuario
    ADD CONSTRAINT usuario_pkey PRIMARY KEY (id);


--
-- Name: usuario_rol usuario_rol_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.usuario_rol
    ADD CONSTRAINT usuario_rol_pkey PRIMARY KEY (id);


--
-- Name: comentario fk1gcyoyuhavbjdhqr9hbulwa69; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.comentario
    ADD CONSTRAINT fk1gcyoyuhavbjdhqr9hbulwa69 FOREIGN KEY (autor_id) REFERENCES public.usuario(id);


--
-- Name: invitacion_espacio fk3u0rh99uhbfjpmeuqut6e2cw6; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.invitacion_espacio
    ADD CONSTRAINT fk3u0rh99uhbfjpmeuqut6e2cw6 FOREIGN KEY (espacio_id) REFERENCES public.espacio_trabajo(id);


--
-- Name: notificacion fk5hnclv9lmmc1w4335x04warbm; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.notificacion
    ADD CONSTRAINT fk5hnclv9lmmc1w4335x04warbm FOREIGN KEY (usuario_id) REFERENCES public.usuario(id);


--
-- Name: usuario_rol fk610kvhkwcqk2pxeewur4l7bd1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.usuario_rol
    ADD CONSTRAINT fk610kvhkwcqk2pxeewur4l7bd1 FOREIGN KEY (rol_id) REFERENCES public.rol(id);


--
-- Name: tarea fka1lvp5rhjg8iwvmds9esksvfd; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tarea
    ADD CONSTRAINT fka1lvp5rhjg8iwvmds9esksvfd FOREIGN KEY (proyecto_id) REFERENCES public.proyecto(id);


--
-- Name: tarea fkagdgkbqcdlwe0wgbrs8v7pjhv; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tarea
    ADD CONSTRAINT fkagdgkbqcdlwe0wgbrs8v7pjhv FOREIGN KEY (responsable_id) REFERENCES public.usuario(id);


--
-- Name: usuario_rol fkbyfgloj439r9wr9smrms9u33r; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.usuario_rol
    ADD CONSTRAINT fkbyfgloj439r9wr9smrms9u33r FOREIGN KEY (usuario_id) REFERENCES public.usuario(id);


--
-- Name: invitacion_espacio fkcuw51c8t7qv578bl01h7fmftj; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.invitacion_espacio
    ADD CONSTRAINT fkcuw51c8t7qv578bl01h7fmftj FOREIGN KEY (usuario_id) REFERENCES public.usuario(id);


--
-- Name: historial_actividad fkdd9eqg7be0k8cn7ixhpyq19y0; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.historial_actividad
    ADD CONSTRAINT fkdd9eqg7be0k8cn7ixhpyq19y0 FOREIGN KEY (proyecto_id) REFERENCES public.proyecto(id);


--
-- Name: tarea fkgsqbkt38hrb1beqhd50j2x9op; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tarea
    ADD CONSTRAINT fkgsqbkt38hrb1beqhd50j2x9op FOREIGN KEY (creado_por_id) REFERENCES public.usuario(id);


--
-- Name: comentario fkkqaouj99djvseplfuaxmvg6tw; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.comentario
    ADD CONSTRAINT fkkqaouj99djvseplfuaxmvg6tw FOREIGN KEY (tarea_id) REFERENCES public.tarea(id);


--
-- Name: historial_actividad fklhxwdnyenkfd9u9imtr9xavjs; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.historial_actividad
    ADD CONSTRAINT fklhxwdnyenkfd9u9imtr9xavjs FOREIGN KEY (usuario_id) REFERENCES public.usuario(id);


--
-- Name: proyecto fkm148i0xpkkrl1fgo4p8euts90; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.proyecto
    ADD CONSTRAINT fkm148i0xpkkrl1fgo4p8euts90 FOREIGN KEY (lider_id) REFERENCES public.usuario(id);


--
-- Name: espacio_miembro fkm66s9c03n332r50ktbr5afkxg; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.espacio_miembro
    ADD CONSTRAINT fkm66s9c03n332r50ktbr5afkxg FOREIGN KEY (espacio_id) REFERENCES public.espacio_trabajo(id);


--
-- Name: espacio_miembro fko14wjal4q3e52bxq5359iho2m; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.espacio_miembro
    ADD CONSTRAINT fko14wjal4q3e52bxq5359iho2m FOREIGN KEY (usuario_id) REFERENCES public.usuario(id);


--
-- Name: miembro_proyecto fkonrcb6adygm028okkixwb75ea; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.miembro_proyecto
    ADD CONSTRAINT fkonrcb6adygm028okkixwb75ea FOREIGN KEY (usuario_id) REFERENCES public.usuario(id);


--
-- Name: proyecto fkp2gthugb7rbtlv3n8k4lmi7mo; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.proyecto
    ADD CONSTRAINT fkp2gthugb7rbtlv3n8k4lmi7mo FOREIGN KEY (espacio_id) REFERENCES public.espacio_trabajo(id);


--
-- Name: invitacion_espacio fkpbfqp7hdag94g4axj0ynl7qyb; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.invitacion_espacio
    ADD CONSTRAINT fkpbfqp7hdag94g4axj0ynl7qyb FOREIGN KEY (invitado_por_id) REFERENCES public.usuario(id);


--
-- Name: espacio_trabajo fkr4cfbqee3onc14wdmih7o6ult; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.espacio_trabajo
    ADD CONSTRAINT fkr4cfbqee3onc14wdmih7o6ult FOREIGN KEY (creado_por_id) REFERENCES public.usuario(id);


--
-- Name: miembro_proyecto fkskk6a5giwahk4y7avvlxb1564; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.miembro_proyecto
    ADD CONSTRAINT fkskk6a5giwahk4y7avvlxb1564 FOREIGN KEY (proyecto_id) REFERENCES public.proyecto(id);


--
-- PostgreSQL database dump complete
--

\unrestrict 7fBAW43AXNU1fMvR4IxpFUmsCKk5GC9ggYbP72JqCvjh6rEsnubcujXtuHjNhg3
