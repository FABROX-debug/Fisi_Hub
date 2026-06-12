import { create } from 'zustand'
import { getProyecto, getProyectos } from '../services/proyectoService'
import {
  createTarea,
  deleteTarea,
  getTareas,
  updateEstadoTarea,
  updateTarea,
} from '../services/tareaService'

const errorMessage = (error) =>
  error?.message || 'No se pudo completar la operacion'

const useTaskStore = create((set, get) => ({
  tareas: [],
  proyectos: [],
  loading: false,
  saving: false,
  error: null,

  loadData: async () => {
    set({ loading: true, error: null })
    try {
      const [tareas, proyectos] = await Promise.all([
        getTareas(),
        getProyectos(),
      ])
      set({ tareas, proyectos, loading: false })
    } catch (error) {
      set({ loading: false, error: errorMessage(error) })
    }
  },

  saveTask: async (payload, id) => {
    set({ saving: true, error: null })
    try {
      const task = id
        ? await updateTarea(id, payload)
        : await createTarea(payload)
      const tareas = id
        ? get().tareas.map((item) => (item.id === id ? task : item))
        : [task, ...get().tareas]
      set({ tareas, saving: false })
      return task
    } catch (error) {
      set({ saving: false, error: errorMessage(error) })
      throw error
    }
  },

  changeStatus: async (id, estado) => {
    set({ saving: true, error: null })
    try {
      const task = await updateEstadoTarea(id, estado)
      set({
        tareas: get().tareas.map((item) => (item.id === id ? task : item)),
      })
      const project = await getProyecto(task.proyectoId)
      set({
        proyectos: get().proyectos.map((item) =>
          item.id === project.id ? project : item),
        saving: false,
      })
      return task
    } catch (error) {
      set({ saving: false, error: errorMessage(error) })
      throw error
    }
  },

  removeTask: async (id) => {
    set({ saving: true, error: null })
    try {
      await deleteTarea(id)
      set({
        tareas: get().tareas.filter((item) => item.id !== id),
        saving: false,
      })
    } catch (error) {
      set({ saving: false, error: errorMessage(error) })
      throw error
    }
  },

  clearError: () => set({ error: null }),
}))

export default useTaskStore
