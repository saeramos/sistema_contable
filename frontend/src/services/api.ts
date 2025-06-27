import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

export const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor
api.interceptors.request.use(
  (config) => {
    console.log('API Request:', config.method?.toUpperCase(), config.url);
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor
api.interceptors.response.use(
  (response) => {
    console.log('API Response:', response.status, response.config.url);
    return response;
  },
  (error) => {
    console.error('API Error:', error.response?.status, error.response?.data);
    return Promise.reject(error);
  }
);

// Types
export interface Tercero {
  id: number;
  nombre: string;
  tipoDocumento: string;
  numeroDocumento: string;
  totalTransacciones: number;
}

export interface CuentaContable {
  id: number;
  codigo: string;
  nombre: string;
  tipo: 'ACTIVO' | 'PASIVO' | 'PATRIMONIO' | 'INGRESO' | 'GASTO';
  permiteSaldoNegativo: boolean;
  activo: boolean;
  saldo?: number;
  colorSaldo?: string;
}

export interface PartidaContable {
  id: number;
  cuentaContableId: number;
  cuentaContableCodigo: string;
  cuentaContableNombre: string;
  tipo: 'DEBE' | 'HABER';
  valor: number;
  descripcion?: string;
}

export interface Transaccion {
  id: number;
  fecha: string;
  descripcion: string;
  estado: 'ACTIVA' | 'ANULADA' | 'PENDIENTE';
  terceroId?: number;
  terceroNombre?: string;
  terceroDocumento?: string;
  partidas: PartidaContable[];
  totalDebitos: number;
  totalCreditos: number;
  balanceado: boolean;
}

// Terceros API
export const tercerosAPI = {
  getAll: () => api.get<Tercero[]>('/terceros').then(res => res.data),
  getById: (id: number) => api.get<Tercero>(`/terceros/${id}`).then(res => res.data),
  create: (data: Omit<Tercero, 'id' | 'totalTransacciones'>) => 
    api.post<Tercero>('/terceros', data).then(res => res.data),
  update: (id: number, data: Omit<Tercero, 'id' | 'totalTransacciones'>) => 
    api.put<Tercero>(`/terceros/${id}`, data).then(res => res.data),
  delete: (id: number) => api.delete(`/terceros/${id}`),
  search: (nombre: string) => api.get<Tercero[]>(`/terceros/search?nombre=${nombre}`).then(res => res.data),
};

// Cuentas API
export const cuentasAPI = {
  getAll: () => api.get<CuentaContable[]>('/cuentas').then(res => res.data),
  getActivas: () => api.get<CuentaContable[]>('/cuentas/activas').then(res => res.data),
  getById: (id: number) => api.get<CuentaContable>(`/cuentas/${id}`).then(res => res.data),
  create: (data: Omit<CuentaContable, 'id' | 'saldo' | 'colorSaldo'>) => 
    api.post<CuentaContable>('/cuentas', data).then(res => res.data),
  update: (id: number, data: Omit<CuentaContable, 'id' | 'saldo' | 'colorSaldo'>) => 
    api.put<CuentaContable>(`/cuentas/${id}`, data).then(res => res.data),
  delete: (id: number) => api.delete(`/cuentas/${id}`),
  activar: (id: number) => api.put<CuentaContable>(`/cuentas/${id}/activar`).then(res => res.data),
  desactivar: (id: number) => api.put<CuentaContable>(`/cuentas/${id}/desactivar`).then(res => res.data),
  search: (nombre: string) => api.get<CuentaContable[]>(`/cuentas/search?nombre=${nombre}`).then(res => res.data),
  searchActivas: (nombre: string) => api.get<CuentaContable[]>(`/cuentas/search/activas?nombre=${nombre}`).then(res => res.data),
};

// Transacciones API
export const transaccionesAPI = {
  getAll: () => api.get<Transaccion[]>('/transacciones').then(res => res.data),
  getById: (id: number) => api.get<Transaccion>(`/transacciones/${id}`).then(res => res.data),
  getByIdWithPartidas: (id: number) => api.get<Transaccion>(`/transacciones/${id}/detalle`).then(res => res.data),
  create: (data: any) => api.post<Transaccion>('/transacciones', data).then(res => res.data),
  update: (id: number, data: any) => api.put<Transaccion>(`/transacciones/${id}`, data).then(res => res.data),
  getByTercero: (terceroId: number) => api.get<Transaccion[]>(`/transacciones/tercero/${terceroId}`).then(res => res.data),
  getByFecha: (fecha: string) => api.get<Transaccion[]>(`/transacciones/fecha/${fecha}`).then(res => res.data),
  getByRangoFechas: (fechaInicio: string, fechaFin: string) => 
    api.get<Transaccion[]>(`/transacciones/rango-fechas?fechaInicio=${fechaInicio}&fechaFin=${fechaFin}`).then(res => res.data),
  getWithFilters: (filters: any) => api.get<Transaccion[]>('/transacciones/filtros', { params: filters }).then(res => res.data),
  search: (descripcion: string) => api.get<Transaccion[]>(`/transacciones/search?descripcion=${descripcion}`).then(res => res.data),
  getByEstado: (estado: string) => api.get<Transaccion[]>(`/transacciones/estado/${estado}`).then(res => res.data),
  cambiarEstado: (id: number, nuevoEstado: string) => api.put<Transaccion>(`/transacciones/${id}/estado?nuevoEstado=${nuevoEstado}`).then(res => res.data),
  anular: (id: number) => api.put<Transaccion>(`/transacciones/${id}/anular`).then(res => res.data),
  reactivar: (id: number) => api.put<Transaccion>(`/transacciones/${id}/reactivar`).then(res => res.data),
  marcarPendiente: (id: number) => api.put<Transaccion>(`/transacciones/${id}/pendiente`).then(res => res.data),
};

// Saldos API
export const saldosAPI = {
  getAll: () => api.get<CuentaContable[]>('/saldos').then(res => res.data),
  getByCuenta: (cuentaId: number) => api.get<CuentaContable>(`/saldos/${cuentaId}`).then(res => res.data),
  getValorByCuenta: (cuentaId: number) => api.get<number>(`/saldos/${cuentaId}/valor`).then(res => res.data),
  getByCuentaHastaFecha: (cuentaId: number, fecha: string) => 
    api.get<number>(`/saldos/${cuentaId}/hasta-fecha?fecha=${fecha}`).then(res => res.data),
  permiteSaldoNegativo: (cuentaId: number) => api.get<boolean>(`/saldos/${cuentaId}/validar-saldo-negativo`).then(res => res.data),
  isCuentaActiva: (cuentaId: number) => api.get<boolean>(`/saldos/${cuentaId}/validar-activa`).then(res => res.data),
};

export default api; 