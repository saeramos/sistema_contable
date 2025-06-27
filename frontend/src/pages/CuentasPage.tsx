import React, { useState, useMemo } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { cuentasAPI, CuentaContable } from '../services/api';
import {
  Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, IconButton, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, Typography, Switch, TablePagination, InputAdornment, Card, CardContent, Grid, Chip
} from '@mui/material';
import { Add, Delete, Edit, ToggleOn, ToggleOff, Search } from '@mui/icons-material';
import { useForm } from 'react-hook-form';

const CuentasPage: React.FC = () => {
  const queryClient = useQueryClient();
  const { data: cuentas, isLoading } = useQuery({
    queryKey: ['cuentas'],
    queryFn: cuentasAPI.getAll
  });

  const [open, setOpen] = useState(false);
  const [editCuenta, setEditCuenta] = useState<CuentaContable | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);

  const mutationCreate = useMutation({
    mutationFn: cuentasAPI.create,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['cuentas'] })
  });
  const mutationUpdate = useMutation({
    mutationFn: ({ id, data }: { id: number, data: any }) => cuentasAPI.update(id, data),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['cuentas'] })
  });
  const mutationDelete = useMutation({
    mutationFn: cuentasAPI.delete,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['cuentas'] })
  });
  const mutationActivar = useMutation({
    mutationFn: cuentasAPI.activar,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['cuentas'] })
  });
  const mutationDesactivar = useMutation({
    mutationFn: cuentasAPI.desactivar,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['cuentas'] })
  });

  const { register, handleSubmit, reset } = useForm<Omit<CuentaContable, 'id' | 'saldo' | 'colorSaldo'>>();

  // Filtrar cuentas por término de búsqueda
  const filteredCuentas = useMemo(() => {
    if (!cuentas) return [];
    return cuentas.filter((c: CuentaContable) => 
      c.nombre.toLowerCase().includes(searchTerm.toLowerCase()) ||
      c.codigo.toLowerCase().includes(searchTerm.toLowerCase()) ||
      c.tipo.toLowerCase().includes(searchTerm.toLowerCase())
    );
  }, [cuentas, searchTerm]);

  // Paginación
  const paginatedCuentas = useMemo(() => {
    const start = page * rowsPerPage;
    return filteredCuentas.slice(start, start + rowsPerPage);
  }, [filteredCuentas, page, rowsPerPage]);

  const handleOpen = (cuenta?: CuentaContable) => {
    setEditCuenta(cuenta || null);
    if (cuenta) {
      reset({
        codigo: cuenta.codigo,
        nombre: cuenta.nombre,
        tipo: cuenta.tipo,
        permiteSaldoNegativo: cuenta.permiteSaldoNegativo,
        activo: cuenta.activo
      });
    } else {
      reset({ codigo: '', nombre: '', tipo: 'ACTIVO', permiteSaldoNegativo: false, activo: true });
    }
    setOpen(true);
  };
  const handleClose = () => setOpen(false);

  const onSubmit = (data: any) => {
    if (editCuenta) {
      mutationUpdate.mutate({ id: editCuenta.id, data });
    } else {
      mutationCreate.mutate(data);
    }
    setOpen(false);
  };

  const getTipoColor = (tipo: string) => {
    switch (tipo) {
      case 'ACTIVO': return '#10B981';
      case 'PASIVO': return '#EF4444';
      case 'PATRIMONIO': return '#8B5CF6';
      case 'INGRESO': return '#06B6D4';
      case 'GASTO': return '#F59E0B';
      default: return '#64748B';
    }
  };

  return (
    <Box sx={{ p: { xs: 1, md: 3 } }}>
      <Typography variant="h4" gutterBottom sx={{ fontWeight: 600, color: '#1E293B', mb: 3 }}>
        Cuentas Contables
      </Typography>
      
      {/* Barra de búsqueda */}
      <Card sx={{ mb: 3, background: 'linear-gradient(145deg, #F1F5F9 0%, #E2E8F0 100%)', borderRadius: 3 }}>
        <CardContent>
          <Grid container spacing={2} alignItems="center">
            <Grid item xs={12} md={8}>
              <TextField
                fullWidth
                placeholder="Buscar cuentas por código, nombre o tipo..."
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
            <Grid item xs={12} md={4}>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <Typography variant="body2" sx={{ color: '#64748B' }}>
                  {filteredCuentas.length} cuentas encontradas
                </Typography>
                <Button 
                  variant="contained" 
                  startIcon={<Add />} 
                  onClick={() => handleOpen()}
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
                  Nueva Cuenta
                </Button>
              </Box>
            </Grid>
          </Grid>
        </CardContent>
      </Card>

      {/* Tabla */}
      <Card sx={{ background: 'white', border: '1px solid #E2E8F0', borderRadius: 3, overflow: 'hidden' }}>
        <TableContainer>
          <Table>
            <TableHead>
              <TableRow sx={{ background: 'linear-gradient(135deg, #F8FAFC 0%, #F1F5F9 100%)' }}>
                <TableCell sx={{ color: '#1E293B', fontWeight: 700 }}>Código</TableCell>
                <TableCell sx={{ color: '#1E293B', fontWeight: 700 }}>Nombre</TableCell>
                <TableCell sx={{ color: '#1E293B', fontWeight: 700 }}>Tipo</TableCell>
                <TableCell sx={{ color: '#1E293B', fontWeight: 700 }}>Permite Saldo Negativo</TableCell>
                <TableCell sx={{ color: '#1E293B', fontWeight: 700 }}>Activa</TableCell>
                <TableCell align="right" sx={{ color: '#1E293B', fontWeight: 700 }}>Acciones</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {isLoading ? (
                <TableRow><TableCell colSpan={6}>Cargando...</TableCell></TableRow>
              ) : (
                paginatedCuentas.map((c: CuentaContable) => (
                  <TableRow key={c.id} sx={{ '&:hover': { background: 'rgba(99, 102, 241, 0.05)' } }}>
                    <TableCell sx={{ color: '#334155', fontWeight: 600 }}>{c.codigo}</TableCell>
                    <TableCell sx={{ color: '#334155' }}>{c.nombre}</TableCell>
                    <TableCell>
                      <Chip 
                        label={c.tipo} 
                        size="small"
                        sx={{ 
                          background: getTipoColor(c.tipo),
                          color: 'white',
                          fontWeight: 600
                        }}
                      />
                    </TableCell>
                    <TableCell sx={{ color: '#334155' }}>{c.permiteSaldoNegativo ? 'Sí' : 'No'}</TableCell>
                    <TableCell>
                      <Switch 
                        checked={c.activo} 
                        onChange={() => c.activo ? mutationDesactivar.mutate(c.id) : mutationActivar.mutate(c.id)}
                        color="primary"
                      />
                    </TableCell>
                    <TableCell align="right">
                      <Box sx={{ display: 'flex', gap: 1, justifyContent: 'flex-end' }}>
                        <IconButton 
                          onClick={() => handleOpen(c)}
                          sx={{ 
                            background: 'rgba(139, 92, 246, 0.1)',
                            '&:hover': { background: 'rgba(139, 92, 246, 0.2)' }
                          }}
                        >
                          <Edit />
                        </IconButton>
                        <IconButton 
                          color="error" 
                          onClick={() => mutationDelete.mutate(c.id)}
                          sx={{ 
                            background: 'rgba(239, 68, 68, 0.1)',
                            '&:hover': { background: 'rgba(239, 68, 68, 0.2)' }
                          }}
                        >
                          <Delete />
                        </IconButton>
                      </Box>
                    </TableCell>
                  </TableRow>
                ))
              )}
            </TableBody>
          </Table>
        </TableContainer>
        <TablePagination
          component="div"
          count={filteredCuentas.length}
          page={page}
          onPageChange={(e, newPage) => setPage(newPage)}
          rowsPerPage={rowsPerPage}
          onRowsPerPageChange={e => { setRowsPerPage(parseInt(e.target.value, 10)); setPage(0); }}
          rowsPerPageOptions={[5, 10, 25, 50]}
        />
      </Card>

      {/* Modal */}
      <Dialog open={open} onClose={handleClose} maxWidth="sm" fullWidth>
        <DialogTitle sx={{ background: 'linear-gradient(135deg, #F8FAFC 0%, #F1F5F9 100%)' }}>
          {editCuenta ? 'Editar Cuenta' : 'Nueva Cuenta'}
        </DialogTitle>
        <form onSubmit={handleSubmit(onSubmit)}>
          <DialogContent sx={{ display: 'flex', flexDirection: 'column', gap: 2, minWidth: 350, pt: 3 }}>
            <TextField label="Código" {...register('codigo', { required: true })} fullWidth />
            <TextField label="Nombre" {...register('nombre', { required: true })} fullWidth />
            <TextField label="Tipo" {...register('tipo', { required: true })} fullWidth />
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
              <Typography>Permite Saldo Negativo</Typography>
              <Switch {...register('permiteSaldoNegativo')} />
            </Box>
          </DialogContent>
          <DialogActions sx={{ p: 3 }}>
            <Button onClick={handleClose}>Cancelar</Button>
            <Button type="submit" variant="contained">Guardar</Button>
          </DialogActions>
        </form>
      </Dialog>
    </Box>
  );
};

export default CuentasPage; 