import React, { useState, useMemo } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { tercerosAPI, Tercero } from '../services/api';
import {
  Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, IconButton, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, Typography, TablePagination, InputAdornment, Card, CardContent, Grid
} from '@mui/material';
import { Add, Delete, Edit, Search } from '@mui/icons-material';
import { useForm } from 'react-hook-form';

const TercerosPage: React.FC = () => {
  const queryClient = useQueryClient();
  const { data: terceros, isLoading } = useQuery({
    queryKey: ['terceros'],
    queryFn: tercerosAPI.getAll
  });

  const [open, setOpen] = useState(false);
  const [editTercero, setEditTercero] = useState<Tercero | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);

  const mutationCreate = useMutation({
    mutationFn: tercerosAPI.create,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['terceros'] })
  });
  const mutationUpdate = useMutation({
    mutationFn: ({ id, data }: { id: number, data: any }) => tercerosAPI.update(id, data),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['terceros'] })
  });
  const mutationDelete = useMutation({
    mutationFn: tercerosAPI.delete,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['terceros'] })
  });

  const { register, handleSubmit, reset } = useForm<Omit<Tercero, 'id' | 'totalTransacciones'>>();

  // Filtrar terceros por término de búsqueda
  const filteredTerceros = useMemo(() => {
    if (!terceros) return [];
    return terceros.filter((t: Tercero) => 
      t.nombre.toLowerCase().includes(searchTerm.toLowerCase()) ||
      t.numeroDocumento.toLowerCase().includes(searchTerm.toLowerCase()) ||
      t.tipoDocumento.toLowerCase().includes(searchTerm.toLowerCase())
    );
  }, [terceros, searchTerm]);

  // Paginación
  const paginatedTerceros = useMemo(() => {
    const start = page * rowsPerPage;
    return filteredTerceros.slice(start, start + rowsPerPage);
  }, [filteredTerceros, page, rowsPerPage]);

  const handleOpen = (tercero?: Tercero) => {
    setEditTercero(tercero || null);
    if (tercero) {
      reset({
        nombre: tercero.nombre,
        tipoDocumento: tercero.tipoDocumento,
        numeroDocumento: tercero.numeroDocumento
      });
    } else {
      reset({ nombre: '', tipoDocumento: '', numeroDocumento: '' });
    }
    setOpen(true);
  };
  const handleClose = () => setOpen(false);

  const onSubmit = (data: any) => {
    if (editTercero) {
      mutationUpdate.mutate({ id: editTercero.id, data });
    } else {
      mutationCreate.mutate(data);
    }
    setOpen(false);
  };

  return (
    <Box sx={{ p: { xs: 1, md: 3 } }}>
      <Typography variant="h4" gutterBottom sx={{ fontWeight: 600, color: '#1E293B', mb: 3 }}>
        Terceros
      </Typography>
      
      {/* Barra de búsqueda */}
      <Card sx={{ mb: 3, background: 'linear-gradient(145deg, #F1F5F9 0%, #E2E8F0 100%)', borderRadius: 3 }}>
        <CardContent>
          <Grid container spacing={2} alignItems="center">
            <Grid item xs={12} md={8}>
              <TextField
                fullWidth
                placeholder="Buscar terceros por nombre, documento o tipo..."
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
                  {filteredTerceros.length} terceros encontrados
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
                  Nuevo Tercero
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
                <TableCell sx={{ color: '#1E293B', fontWeight: 700 }}>Nombre</TableCell>
                <TableCell sx={{ color: '#1E293B', fontWeight: 700 }}>Tipo Documento</TableCell>
                <TableCell sx={{ color: '#1E293B', fontWeight: 700 }}>Número Documento</TableCell>
                <TableCell sx={{ color: '#1E293B', fontWeight: 700 }}>Transacciones</TableCell>
                <TableCell align="right" sx={{ color: '#1E293B', fontWeight: 700 }}>Acciones</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {isLoading ? (
                <TableRow><TableCell colSpan={5}>Cargando...</TableCell></TableRow>
              ) : (
                paginatedTerceros.map((t: Tercero) => (
                  <TableRow key={t.id} sx={{ '&:hover': { background: 'rgba(99, 102, 241, 0.05)' } }}>
                    <TableCell sx={{ color: '#334155' }}>{t.nombre}</TableCell>
                    <TableCell sx={{ color: '#334155' }}>{t.tipoDocumento}</TableCell>
                    <TableCell sx={{ color: '#334155' }}>{t.numeroDocumento}</TableCell>
                    <TableCell sx={{ color: '#334155' }}>{t.totalTransacciones ?? '-'}</TableCell>
                    <TableCell align="right">
                      <Box sx={{ display: 'flex', gap: 1, justifyContent: 'flex-end' }}>
                        <IconButton 
                          onClick={() => handleOpen(t)}
                          sx={{ 
                            background: 'rgba(139, 92, 246, 0.1)',
                            '&:hover': { background: 'rgba(139, 92, 246, 0.2)' }
                          }}
                        >
                          <Edit />
                        </IconButton>
                        <IconButton 
                          color="error" 
                          onClick={() => mutationDelete.mutate(t.id)}
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
          count={filteredTerceros.length}
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
          {editTercero ? 'Editar Tercero' : 'Nuevo Tercero'}
        </DialogTitle>
        <form onSubmit={handleSubmit(onSubmit)}>
          <DialogContent sx={{ display: 'flex', flexDirection: 'column', gap: 2, minWidth: 350, pt: 3 }}>
            <TextField label="Nombre" {...register('nombre', { required: true })} fullWidth />
            <TextField label="Tipo Documento" {...register('tipoDocumento', { required: true })} fullWidth />
            <TextField label="Número Documento" {...register('numeroDocumento', { required: true })} fullWidth />
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

export default TercerosPage; 