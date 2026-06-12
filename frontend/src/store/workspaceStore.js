import { create } from 'zustand'
import {
  createEspacio,
  deleteEspacio,
  getEspacios,
  updateEspacio,
} from '../services/espacioService'
import {
  createProyecto,
  deleteProyecto,
  getProyectos,
  updateProyecto,
} from '../services/proyectoService'

const messageFor = (error) =>
  error?.message || 'No se pudo completar la operacion'

const useWorkspaceStore = create((set, get) => ({
  espacios: [],
  proyectos: [],
  loading: false,
  saving: false,
  error: null,

  loadAll: async () => {
    set({ loading: true, error: null })
    try {
      const [espacios, proyectos] = await Promise.all([
        getEspacios(),
        getProyectos(),
      ])
      set({ espacios, proyectos, loading: false })
    } catch (error) {
      set({ loading: false, error: messageFor(error) })
    }
  },

  saveEspacio: async (payload, id) => {
    set({ saving: true, error: null })
    try {
      const espacio = id
        ? await updateEspacio(id, payload)
        : await createEspacio(payload)
      const espacios = id
        ? get().espacios.map((item) => (item.id === id ? espacio : item))
        : [espacio, ...get().espacios]
      set({ espacios, saving: false })
      return espacio
    } catch (error) {
      set({ saving: false, error: messageFor(error) })
      throw error
    }
  },

  removeEspacio: async (id) => {
    set({ saving: true, error: null })
    try {
      await deleteEspacio(id)
      set({
        espacios: get().espacios.filter((item) => item.id !== id),
        proyectos: get().proyectos.filter((item) => item.espacioId !== id),
        saving: false,
      })
    } catch (error) {
      set({ saving: false, error: messageFor(error) })
      throw error
    }
  },

  saveProyecto: async (payload, id) => {
    set({ saving: true, error: null })
    try {
      const proyecto = id
        ? await updateProyecto(id, payload)
        : await createProyecto(payload)
      const proyectos = id
        ? get().proyectos.map((item) => (item.id === id ? proyecto : item))
        : [proyecto, ...get().proyectos]
      set({ proyectos, saving: false })
      return proyecto
    } catch (error) {
      set({ saving: false, error: messageFor(error) })
      throw error
    }
  },

  removeProyecto: async (id) => {
    set({ saving: true, error: null })
    try {
      await deleteProyecto(id)
      set({
        proyectos: get().proyectos.filter((item) => item.id !== id),
        saving: false,
      })
    } catch (error) {
      set({ saving: false, error: messageFor(error) })
      throw error
    }
  },

  clearError: () => set({ error: null }),
}))

export default useWorkspaceStore
