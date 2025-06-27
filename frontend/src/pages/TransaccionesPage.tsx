import React, { useState, useMemo, useEffect } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { transaccionesAPI, cuentasAPI, tercerosAPI, Transaccion, CuentaContable, Tercero } from '../services/api';
import {
  Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, IconButton, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, Typography, MenuItem, Select, InputLabel, FormControl, Chip, Card, CardContent, Grid, Alert, Divider, Fab, Tooltip, InputAdornment, Badge, Avatar, TablePagination
} from '@mui/material';
import { 
  Add, Visibility, Search, FilterList, AccountBalance, Receipt, 
  TrendingUp, TrendingDown, CheckCircle, Error, Warning, CalendarToday, Person, Delete, Edit, MoreVert, Add as AddIcon, Remove as RemoveIcon
} from '@mui/icons-material';
import { useForm, useFieldArray, Controller } from 'react-hook-form';
import { motion, AnimatePresence } from 'framer-motion';
import { format, parseISO } from 'date-fns';
import { es } from 'date-fns/locale';
import toast from 'react-hot-toast';
import AutoCompleteField from '../components/AutoCompleteField';
import { contabilidadColors } from '../theme';

// Crear motion components
const MotionTableRow = motion(TableRow);

interface TransaccionFormData {
  fecha: string;
  descripcion: string;
  terceroId?: Tercero | null;
  partidas: Array<{
    cuentaContableId: CuentaContable | null;
    tipo: 'DEBE' | 'HABER';
    monto: number;
    descripcion: string;
  }>;
}

const TransaccionesPage: React.FC = () => {
  const queryClient = useQueryClient();
  const [open, setOpen] = useState(false);
  const [detalle, setDetalle] = useState<Transaccion | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [filterStatus, setFilterStatus] = useState('TODAS');
  const [selectedTransaccion, setSelectedTransaccion] = useState<Transaccion | null>(null);
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [editMode, setEditMode] = useState(false);

  // Queries
  const { data: transacciones, isLoading } = useQuery({
    queryKey: ['transacciones'],
    queryFn: transaccionesAPI.getAll
  });
  const { data: cuentas } = useQuery({
    queryKey: ['cuentas'],
    queryFn: cuentasAPI.getActivas
  });
  const { data: terceros } = useQuery({
    queryKey: ['terceros'],
    queryFn: tercerosAPI.getAll
  });

  // Mutations
  const mutationCreate = useMutation({
    mutationFn: transaccionesAPI.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['transacciones'] });
      toast.success('Transacción creada exitosamente');
      setOpen(false);
      reset();
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Error al crear la transacción');
    }
  });

  const mutationAnular = useMutation({
    mutationFn: (id: number) => transaccionesAPI.anular(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['transacciones'] });
      toast.success('Transacción anulada exitosamente');
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Error al anular la transacción');
    }
  });

  const mutationReactivar = useMutation({
    mutationFn: (id: number) => transaccionesAPI.reactivar(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['transacciones'] });
      toast.success('Transacción reactivada exitosamente');
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Error al reactivar la transacción');
    }
  });

  const mutationPendiente = useMutation({
    mutationFn: (id: number) => transaccionesAPI.marcarPendiente(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['transacciones'] });
      toast.success('Transacción marcada como pendiente');
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Error al cambiar el estado');
    }
  });

  const mutationUpdate = useMutation({
    mutationFn: ({ id, data }: { id: number, data: any }) => transaccionesAPI.update(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['transacciones'] });
      toast.success('Transacción actualizada exitosamente');
      setOpen(false);
      setEditMode(false);
      setSelectedTransaccion(null);
      reset();
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Error al actualizar la transacción');
    }
  });

  // Form setup
  const { control, register, handleSubmit, reset, watch, setValue, formState: { errors } } = useForm<TransaccionFormData>({
    defaultValues: {
      fecha: format(new Date(), 'yyyy-MM-dd'),
      descripcion: '',
      terceroId: undefined,
      partidas: [
        { cuentaContableId: null, tipo: 'DEBE', monto: 0, descripcion: '' },
        { cuentaContableId: null, tipo: 'HABER', monto: 0, descripcion: '' }
      ]
    }
  });

  const { fields, append, remove } = useFieldArray({ control, name: 'partidas' });
  const watchedPartidas = watch('partidas');

  // Computed values
  const totalDebe = useMemo(() => 
    watchedPartidas?.reduce((sum: number, partida: any) => {
      const monto = Number(partida.monto) || 0;
      return sum + (partida.tipo === 'DEBE' ? monto : 0);
    }, 0) || 0, 
    [JSON.stringify(watchedPartidas)]
  );
  
  const totalHaber = useMemo(() => 
    watchedPartidas?.reduce((sum: number, partida: any) => {
      const monto = Number(partida.monto) || 0;
      return sum + (partida.tipo === 'HABER' ? monto : 0);
    }, 0) || 0, 
    [JSON.stringify(watchedPartidas)]
  );

  const isBalanced = Math.abs(totalDebe - totalHaber) < 0.01;

  // Filtered transactions
  const filteredTransacciones = useMemo(() => {
    if (!transacciones) return [];
    return transacciones.filter((t: Transaccion) => {
      const matchesSearch = t.descripcion?.toLowerCase().includes(searchTerm.toLowerCase()) ||
                           t.terceroNombre?.toLowerCase().includes(searchTerm.toLowerCase()) ||
                           t.id?.toString().includes(searchTerm);
      
      const matchesStatus = filterStatus === 'TODAS' || t.estado === filterStatus;
      
      return matchesSearch && matchesStatus;
    });
  }, [transacciones, searchTerm, filterStatus]);

  const paginatedTransacciones = useMemo(() => {
    const start = page * rowsPerPage;
    return filteredTransacciones.slice(start, start + rowsPerPage);
  }, [filteredTransacciones, page, rowsPerPage]);

  // Handlers
  const handleOpen = () => {
    reset({
      fecha: format(new Date(), 'yyyy-MM-dd'),
      descripcion: '',
      terceroId: undefined,
      partidas: [
        { cuentaContableId: null, tipo: 'DEBE', monto: 0, descripcion: '' },
        { cuentaContableId: null, tipo: 'HABER', monto: 0, descripcion: '' }
      ]
    });
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
    setEditMode(false);
    setSelectedTransaccion(null);
    reset();
  };

  const onSubmit = (data: TransaccionFormData) => {
    if (!isBalanced) {
      toast.error('La transacción debe estar balanceada (Débitos = Créditos)');
      return;
    }
    const partidasValidas = data.partidas.every(p => p.cuentaContableId && p.monto > 0);
    if (!partidasValidas) {
      toast.error('Todas las partidas deben tener cuenta contable y monto válido');
      return;
    }
    const transaccionData = {
      fecha: data.fecha,
      descripcion: data.descripcion,
      terceroId: data.terceroId ? data.terceroId.id : null,
      partidas: data.partidas.map(p => ({
        cuentaContableId: p.cuentaContableId ? p.cuentaContableId.id : null,
        tipo: p.tipo,
        valor: Number(p.monto),
        descripcion: p.descripcion || ''
      }))
    };
    if (editMode && selectedTransaccion) {
      mutationUpdate.mutate({ id: selectedTransaccion.id, data: transaccionData });
    } else {
      mutationCreate.mutate(transaccionData);
    }
  };

  const addPartida = () => {
    append({ cuentaContableId: null, tipo: 'DEBE', monto: 0, descripcion: '' });
  };

  const removePartida = (index: number) => {
    if (fields.length > 2) {
      remove(index);
    } else {
      toast.error('Debe mantener al menos 2 partidas');
    }
  };

  const handleEdit = (transaccion: Transaccion) => {
    setEditMode(true);
    setOpen(true);
    // Pre-cargar datos en el formulario
    reset({
      fecha: transaccion.fecha,
      descripcion: transaccion.descripcion,
      terceroId: terceros?.find(t => t.id === transaccion.terceroId) || null,
      partidas: transaccion.partidas.map((p: any) => ({
        cuentaContableId: cuentas?.find(c => c.id === p.cuentaContableId) || null,
        tipo: p.tipo,
        monto: p.valor,
        descripcion: p.descripcion || ''
      }))
    });
    setSelectedTransaccion(transaccion);
  };

  const handleAnular = (transaccion: Transaccion) => {
    if (transaccion.estado === 'ANULADA') {
      toast.error('La transacción ya está anulada');
      return;
    }
    
    if (window.confirm(`¿Está seguro de anular la transacción ${transaccion.id}?`)) {
      mutationAnular.mutate(transaccion.id);
    }
  };

  return (
    <Box sx={{ p: { xs: 1, md: 3 } }}>
      <motion.div initial={{ opacity: 0, y: -20 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.5 }}>
        <Typography variant="h3" gutterBottom sx={{ fontWeight: 700, background: 'linear-gradient(135deg, #6366F1 0%, #EC4899 100%)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent', backgroundClip: 'text' }}>
          Transacciones Contables
        </Typography>
        <Typography variant="body1" sx={{ color: 'text.secondary', mb: 2 }}>
          Gestiona las transacciones y partidas contables del sistema
        </Typography>
      </motion.div>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" sx={{ fontWeight: 600, color: '#1E293B' }}>
          Transacciones
        </Typography>
        <Button
          variant="contained"
          startIcon={<Add />}
          onClick={handleOpen}
          sx={{
            background: 'linear-gradient(135deg, #6366F1 0%, #8B5CF6 100%)',
            color: 'white',
            fontWeight: 600,
            px: 3,
            py: 1.5,
            borderRadius: 2,
            boxShadow: '0 4px 14px rgba(99, 102, 241, 0.3)',
            '&:hover': {
              background: 'linear-gradient(135deg, #5855EB 0%, #7C3AED 100%)',
              boxShadow: '0 6px 20px rgba(99, 102, 241, 0.4)',
              transform: 'translateY(-2px)',
            },
            transition: 'all 0.3s ease',
          }}
        >
          Nueva Transacción
        </Button>
      </Box>
      <Card sx={{ background: 'linear-gradient(145deg, #F1F5F9 0%, #E2E8F0 100%)', boxShadow: '0 8px 32px rgba(99,102,241,0.10)', borderRadius: 5, mt: 2, mb: 3 }}>
        <CardContent>
          <Grid container spacing={2} alignItems="center">
            <Grid item xs={12} md={6}>
              <TextField
                fullWidth
                placeholder="Buscar transacciones..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <Search />
                    </InputAdornment>
                  ),
                }}
                variant="filled"
                sx={{ input: { color: '#1E293B' }, background: 'rgba(255,255,255,0.8)', borderRadius: 2 }}
              />
            </Grid>
            <Grid item xs={12} md={3}>
              <FormControl fullWidth>
                <InputLabel sx={{ color: '#64748B' }}>Estado</InputLabel>
                <Select
                  value={filterStatus}
                  onChange={(e) => setFilterStatus(e.target.value)}
                  label="Estado"
                  sx={{ color: '#1E293B', background: 'rgba(255,255,255,0.8)', borderRadius: 2 }}
                >
                  <MenuItem value="TODAS">Todas</MenuItem>
                  <MenuItem value="ACTIVA">Activas</MenuItem>
                  <MenuItem value="ANULADA">Anuladas</MenuItem>
                  <MenuItem value="PENDIENTE">Pendientes</MenuItem>
                </Select>
              </FormControl>
            </Grid>
            <Grid item xs={12} md={3}>
              <Typography variant="body2" sx={{ color: '#64748B', textAlign: 'center' }}>
                {filteredTransacciones.length} transacciones
              </Typography>
            </Grid>
          </Grid>
        </CardContent>
      </Card>
      <Card sx={{ background: 'linear-gradient(145deg, #F1F5F9 0%, #E2E8F0 100%)', boxShadow: '0 8px 32px rgba(99,102,241,0.10)', borderRadius: 5 }}>
        <CardContent>
          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell sx={{ color: '#1E293B', fontWeight: 700 }}>ID</TableCell>
                  <TableCell sx={{ color: '#1E293B', fontWeight: 700 }}>Fecha</TableCell>
                  <TableCell sx={{ color: '#1E293B', fontWeight: 700 }}>Tercero</TableCell>
                  <TableCell sx={{ color: '#1E293B', fontWeight: 700 }}>Descripción</TableCell>
                  <TableCell sx={{ color: '#1E293B', fontWeight: 700 }}>Estado</TableCell>
                  <TableCell align="right" sx={{ color: '#1E293B', fontWeight: 700 }}>Acciones</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {isLoading ? (
                  <TableRow><TableCell colSpan={6}>Cargando...</TableCell></TableRow>
                ) : (
                  paginatedTransacciones.map((t: Transaccion) => (
                    <motion.tr key={t.id} initial={{ opacity: 0, y: 10 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.3 }}>
                      <TableCell sx={{ color: '#334155' }}>{t.id}</TableCell>
                      <TableCell sx={{ color: '#334155' }}>{t.fecha}</TableCell>
                      <TableCell sx={{ color: '#334155' }}>{t.terceroNombre || '-'}</TableCell>
                      <TableCell sx={{ color: '#334155' }}>{t.descripcion}</TableCell>
                      <TableCell>
                        <Chip label={t.estado} sx={{ background: t.estado === 'ACTIVA' ? 'linear-gradient(90deg, #10B981 0%, #06B6D4 100%)' : t.estado === 'ANULADA' ? 'linear-gradient(90deg, #EF4444 0%, #F59E0B 100%)' : 'linear-gradient(90deg, #F59E0B 0%, #6366F1 100%)', color: 'white', fontWeight: 600 }} />
                      </TableCell>
                      <TableCell align="right">
                        <Box sx={{ display: 'flex', gap: 1, justifyContent: 'flex-end' }}>
                          <Tooltip title="Ver detalle">
                            <IconButton 
                              color="primary" 
                              onClick={() => setDetalle(t)}
                              sx={{ 
                                background: 'rgba(99, 102, 241, 0.1)',
                                '&:hover': { background: 'rgba(99, 102, 241, 0.2)' }
                              }}
                            >
                              <Visibility />
                            </IconButton>
                          </Tooltip>
                          <Tooltip title="Editar transacción">
                            <IconButton 
                              color="secondary" 
                              onClick={() => handleEdit(t)}
                              sx={{ 
                                background: 'rgba(139, 92, 246, 0.1)',
                                '&:hover': { background: 'rgba(139, 92, 246, 0.2)' }
                              }}
                            >
                              <Edit />
                            </IconButton>
                          </Tooltip>
                          <Tooltip title="Anular transacción">
                            <IconButton 
                              color="error" 
                              onClick={() => handleAnular(t)}
                              disabled={t.estado === 'ANULADA'}
                              sx={{ 
                                background: 'rgba(239, 68, 68, 0.1)',
                                '&:hover': { background: 'rgba(239, 68, 68, 0.2)' },
                                '&:disabled': { background: 'rgba(156, 163, 175, 0.1)' }
                              }}
                            >
                              <Delete />
                            </IconButton>
                          </Tooltip>
                        </Box>
                      </TableCell>
                    </motion.tr>
                  ))
                )}
              </TableBody>
            </Table>
          </TableContainer>
          <TablePagination
            component="div"
            count={filteredTransacciones.length}
            page={page}
            onPageChange={(e, newPage) => setPage(newPage)}
            rowsPerPage={rowsPerPage}
            onRowsPerPageChange={e => { setRowsPerPage(parseInt(e.target.value, 10)); setPage(0); }}
            rowsPerPageOptions={[5, 10, 25, 50]}
          />
        </CardContent>
      </Card>

      {/* Create Transaction Dialog */}
      <Dialog open={open} onClose={handleClose} maxWidth="lg" fullWidth>
        <DialogTitle>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
            <AccountBalance sx={{ color: 'primary.main' }} />
            <Typography variant="h6">{editMode ? 'Editar Transacción Contable' : 'Nueva Transacción Contable'}</Typography>
          </Box>
        </DialogTitle>
        
        <form onSubmit={handleSubmit(onSubmit)}>
          <DialogContent sx={{ p: 3 }}>
            <Grid container spacing={3}>
              <Grid item xs={12} md={6}>
                <TextField
                  fullWidth
                  label="Fecha"
                  type="date"
                  {...register('fecha', { required: 'La fecha es requerida' })}
                  InputLabelProps={{ shrink: true }}
                  error={!!errors.fecha}
                  helperText={errors.fecha?.message}
                />
              </Grid>
              <Grid item xs={12}>
                <AutoCompleteField
                  label="Tercero (Opcional)"
                  options={terceros || []}
                  getOptionLabel={(option: Tercero) => option ? `${option.nombre} - ${option.numeroDocumento}` : ''}
                  value={watch('terceroId') || null}
                  onChange={(value) => setValue('terceroId', value)}
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Descripción"
                  multiline
                  rows={3}
                  {...register('descripcion', { required: 'La descripción es requerida' })}
                  error={!!errors.descripcion}
                  helperText={errors.descripcion?.message}
                />
              </Grid>
            </Grid>

            <Divider sx={{ my: 3 }} />

            <Typography variant="h6" gutterBottom>Partidas Contables</Typography>
            
            {fields.map((field, index) => (
              <Card key={field.id} sx={{ mb: 2, p: 2 }}>
                <Grid container spacing={2} alignItems="center">
                  <Grid item xs={12} md={4}>
                    <AutoCompleteField
                      label="Cuenta Contable"
                      options={cuentas || []}
                      getOptionLabel={(option: CuentaContable) => option ? `${option.codigo} - ${option.nombre}` : ''}
                      value={watchedPartidas[index]?.cuentaContableId || null}
                      onChange={(value) => setValue(`partidas.${index}.cuentaContableId`, value)}
                    />
                  </Grid>
                  <Grid item xs={12} md={2}>
                    <FormControl fullWidth>
                      <InputLabel>Tipo</InputLabel>
                      <Select
                        value={watchedPartidas[index]?.tipo}
                        onChange={(e) => setValue(`partidas.${index}.tipo`, e.target.value as 'DEBE' | 'HABER')}
                        label="Tipo"
                      >
                        <MenuItem value="DEBE">
                          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                            <TrendingUp sx={{ color: contabilidadColors.debe.main }} />
                            Débito
                          </Box>
                        </MenuItem>
                        <MenuItem value="HABER">
                          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                            <TrendingDown sx={{ color: contabilidadColors.haber.main }} />
                            Crédito
                          </Box>
                        </MenuItem>
                      </Select>
                    </FormControl>
                  </Grid>
                  <Grid item xs={12} md={3}>
                    <TextField
                      fullWidth
                      label="Monto"
                      type="number"
                      {...register(`partidas.${index}.monto`, { 
                        required: 'El monto es requerido',
                        min: { value: 0.01, message: 'El monto debe ser mayor a 0' }
                      })}
                      error={!!errors.partidas?.[index]?.monto}
                      helperText={errors.partidas?.[index]?.monto?.message}
                    />
                  </Grid>
                  <Grid item xs={12} md={2}>
                    <TextField
                      fullWidth
                      label="Descripción"
                      {...register(`partidas.${index}.descripcion`)}
                      placeholder="Opcional"
                    />
                  </Grid>
                  <Grid item xs={12} md={1}>
                    <IconButton 
                      onClick={() => removePartida(index)}
                      color="error"
                      disabled={fields.length <= 2}
                    >
                      <Delete />
                    </IconButton>
                  </Grid>
                </Grid>
              </Card>
            ))}

            <Button onClick={addPartida} sx={{ mb: 2 }}>
              + Agregar Partida
            </Button>

            {/* Balance Summary */}
            <Card sx={{ p: 2, backgroundColor: isBalanced ? 'success.50' : 'error.50' }}>
              <Grid container spacing={2} alignItems="center">
                <Grid item xs={4}>
                  <Typography variant="body2" color="text.secondary">
                    Total Débitos:
                  </Typography>
                  <Typography variant="h6" sx={{ color: contabilidadColors.debe.main, fontWeight: 700 }}>
                    ${totalDebe.toLocaleString('es-CO')}
                  </Typography>
                </Grid>
                <Grid item xs={4}>
                  <Typography variant="body2" color="text.secondary">
                    Total Créditos:
                  </Typography>
                  <Typography variant="h6" sx={{ color: contabilidadColors.haber.main, fontWeight: 700 }}>
                    ${totalHaber.toLocaleString('es-CO')}
                  </Typography>
                </Grid>
                <Grid item xs={4}>
                  <Typography variant="body2" color="text.secondary">
                    Diferencia:
                  </Typography>
                  <Typography 
                    variant="h6" 
                    color={isBalanced ? contabilidadColors.haber.main : contabilidadColors.debe.main}
                    sx={{ fontWeight: 700 }}
                  >
                    ${Math.abs(totalDebe - totalHaber).toLocaleString('es-CO')}
                  </Typography>
                </Grid>
              </Grid>
              {!isBalanced && (
                <Alert severity="error" sx={{ mt: 2 }}>
                  La transacción debe estar balanceada (Débitos = Créditos)
                </Alert>
              )}
            </Card>
          </DialogContent>
          <DialogActions>
            <Button onClick={handleClose}>Cancelar</Button>
            <Button 
              type="submit" 
              variant="contained"
              disabled={!isBalanced || mutationCreate.isPending}
            >
              {mutationCreate.isPending ? 'Guardando...' : 'Guardar Transacción'}
            </Button>
          </DialogActions>
        </form>
      </Dialog>

      {/* Transaction Detail Dialog */}
      <Dialog open={!!detalle} onClose={() => setDetalle(null)} maxWidth="md" fullWidth>
        <DialogTitle>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
            <Receipt sx={{ color: 'primary.main' }} />
            <Typography variant="h6">Detalle de Transacción</Typography>
          </Box>
        </DialogTitle>
        <DialogContent>
          {detalle && (
            <Box>
              <Grid container spacing={3}>
                <Grid item xs={12} md={6}>
                  <Typography variant="subtitle2" color="text.secondary">Fecha</Typography>
                  <Typography variant="body1" sx={{ fontWeight: 600, mb: 2 }}>
                    {format(new Date(detalle.fecha), 'dd/MM/yyyy', { locale: es })}
                  </Typography>
                </Grid>
                <Grid item xs={12} md={6}>
                  <Typography variant="subtitle2" color="text.secondary">Monto Total</Typography>
                  <Typography variant="h6" sx={{ fontWeight: 600, mb: 2 }}>
                    ${detalle.totalDebitos?.toLocaleString('es-CO')}
                  </Typography>
                </Grid>
                <Grid item xs={12}>
                  <Typography variant="subtitle2" color="text.secondary">Descripción</Typography>
                  <Typography variant="body1" sx={{ mb: 3 }}>
                    {detalle.descripcion}
                  </Typography>
                </Grid>
              </Grid>

              <Divider sx={{ my: 3 }} />

              <Typography variant="h6" gutterBottom>Partidas Contables</Typography>
              <TableContainer component={Paper} variant="outlined">
                <Table size="small">
                  <TableHead>
                    <TableRow>
                      <TableCell><strong>Cuenta</strong></TableCell>
                      <TableCell><strong>Tipo</strong></TableCell>
                      <TableCell align="right"><strong>Monto</strong></TableCell>
                      <TableCell><strong>Descripción</strong></TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {detalle.partidas?.map((partida, index) => (
                      <TableRow key={index}>
                        <TableCell>
                          <Typography variant="body2" sx={{ fontWeight: 600 }}>
                            {partida.cuentaContableCodigo} - {partida.cuentaContableNombre}
                          </Typography>
                        </TableCell>
                        <TableCell>
                          <Chip
                            label={partida.tipo}
                            sx={{ 
                              backgroundColor: partida.tipo === 'DEBE' ? contabilidadColors.debe.main : contabilidadColors.haber.main,
                              color: 'white',
                              fontWeight: 600
                            }}
                            size="small"
                          />
                        </TableCell>
                        <TableCell align="right">
                          <Typography variant="body2" sx={{ fontWeight: 600 }}>
                            ${partida.valor?.toLocaleString('es-CO')}
                          </Typography>
                        </TableCell>
                        <TableCell>
                          <Typography variant="body2" color="text.secondary">
                            {partida.descripcion || '-'}
                          </Typography>
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
            </Box>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDetalle(null)}>Cerrar</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default TransaccionesPage; 