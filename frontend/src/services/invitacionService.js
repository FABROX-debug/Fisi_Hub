import { apiRequest } from './apiClient'

/** Obtiene las invitaciones de un espacio (para líderes/admins). */
export const getWorkspaceInvitations = (workspaceId) =>
  apiRequest(`/api/espacios/${workspaceId}/invitaciones`)

/** Crea una invitación in-app para un usuario registrado. */
export const createWorkspaceInvitation = (workspaceId, payload) =>
  apiRequest(`/api/espacios/${workspaceId}/invitaciones`, {
    method: 'POST',
    body: payload,
  })

/** Obtiene los usuarios activos que aún no son miembros del espacio. */
export const getAvailableUsers = (workspaceId) =>
  apiRequest(`/api/espacios/${workspaceId}/usuarios-disponibles`)

/** Acepta una invitación pendiente (el propio usuario invitado). */
export const acceptInvitation = (invitationId) =>
  apiRequest(`/api/invitaciones/${invitationId}/aceptar`, { method: 'POST' })

/** Rechaza una invitación pendiente (el propio usuario invitado). */
export const rejectInvitation = (invitationId) =>
  apiRequest(`/api/invitaciones/${invitationId}/rechazar`, { method: 'POST' })

/** Reenvía una invitación (líderes/admins del espacio). */
export const resendInvitation = (invitationId) =>
  apiRequest(`/api/invitaciones/${invitationId}/reenviar`, { method: 'POST' })

/** Revoca una invitación (líderes/admins del espacio). */
export const revokeInvitation = (invitationId) =>
  apiRequest(`/api/invitaciones/${invitationId}`, { method: 'DELETE' })
