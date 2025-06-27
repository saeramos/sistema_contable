import React, { useState, useMemo } from 'react';
import { useQuery } from '@tanstack/react-query';
import { saldosAPI, CuentaContable } from '../services/api';
import {
  Box, Typography, Card, CardContent, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TablePagination, InputAdornment, TextField, Grid, Chip
} from '@mui/material';
import { Search, TrendingUp, TrendingDown } from '@mui/icons-material';

const SaldosPage: React.FC = () => {
  const { data: saldos, isLoading } = useQuery({
    queryKey: ['saldos'],
    queryFn: saldosAPI.getAll
  });

  const [searchTerm, setSearchTerm] = useState('');
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);

  // Filtrar saldos por término de búsqueda
  const filteredSaldos = useMemo(() => {
    if (!saldos) return [];
    return saldos.filter((s: CuentaContable) => 
      s.nombre.toLowerCase().includes(searchTerm.toLowerCase()) ||
      s.codigo.toLowerCase().includes(searchTerm.toLowerCase()) ||
      s.tipo.toLowerCase().includes(searchTerm.toLowerCase())
    );
  }, [saldos, searchTerm]);

  // Paginación
  const paginatedSaldos = useMemo(() => {
    const start = page * rowsPerPage;
    return filteredSaldos.slice(start, start + rowsPerPage);
  }, [filteredSaldos, page, rowsPerPage]);

  const getSaldoColor = (saldo: number) => {
    if (saldo > 0) return '#10B981';
    if (saldo < 0) return '#EF4444';
    return '#64748B';
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
        Saldos de Cuentas
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
              <Typography variant="body2" sx={{ color: '#64748B', textAlign: 'center' }}>
                {filteredSaldos.length} cuentas encontradas
              </Typography>
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
                <TableCell sx={{ color: '#1E293B', fontWeight: 700 }}>Saldo</TableCell>
                <TableCell sx={{ color: '#1E293B', fontWeight: 700 }}>Estado</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {isLoading ? (
                <TableRow><TableCell colSpan={5}>Cargando...</TableCell></TableRow>
              ) : (
                paginatedSaldos.map((s: CuentaContable) => (
                  <TableRow key={s.id} sx={{ '&:hover': { background: 'rgba(99, 102, 241, 0.05)' } }}>
                    <TableCell sx={{ color: '#334155', fontWeight: 600 }}>{s.codigo}</TableCell>
                    <TableCell sx={{ color: '#334155' }}>{s.nombre}</TableCell>
                    <TableCell>
                      <Chip 
                        label={s.tipo} 
                        size="small"
                        sx={{ 
                          background: getTipoColor(s.tipo),
                          color: 'white',
                          fontWeight: 600
                        }}
                      />
                    </TableCell>
                    <TableCell>
                      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                        {s.saldo && s.saldo > 0 ? (
                          <TrendingUp sx={{ color: '#10B981', fontSize: 16 }} />
                        ) : s.saldo && s.saldo < 0 ? (
                          <TrendingDown sx={{ color: '#EF4444', fontSize: 16 }} />
                        ) : null}
                        <Typography 
                          sx={{ 
                            color: getSaldoColor(s.saldo || 0),
                            fontWeight: 600,
                            fontSize: '1.1rem'
                          }}
                        >
                          ${(s.saldo || 0).toLocaleString('es-CO', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
                        </Typography>
                      </Box>
                    </TableCell>
                    <TableCell>
                      <Chip 
                        label={s.activo ? 'Activa' : 'Inactiva'} 
                        size="small"
                        sx={{ 
                          background: s.activo ? '#10B981' : '#EF4444',
                          color: 'white',
                          fontWeight: 600
                        }}
                      />
                    </TableCell>
                  </TableRow>
                ))
              )}
            </TableBody>
          </Table>
        </TableContainer>
        <TablePagination
          component="div"
          count={filteredSaldos.length}
          page={page}
          onPageChange={(e, newPage) => setPage(newPage)}
          rowsPerPage={rowsPerPage}
          onRowsPerPageChange={e => { setRowsPerPage(parseInt(e.target.value, 10)); setPage(0); }}
          rowsPerPageOptions={[5, 10, 25, 50]}
        />
      </Card>
    </Box>
  );
};

export default SaldosPage; 