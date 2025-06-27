import React from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Box,
  Grid,
  Card,
  CardContent,
  Typography,
  Paper,
  Chip,
  Avatar,
  LinearProgress,
  IconButton,
  Tooltip,
  Button,
  Divider,
} from '@mui/material';
import {
  TrendingUp,
  TrendingDown,
  AccountBalance,
  People,
  Receipt,
  Assessment,
  Visibility,
  VisibilityOff,
  Refresh,
  ArrowForward,
  MonetizationOn,
  Warning,
  CheckCircle,
  Add,
} from '@mui/icons-material';
import {
  LineChart,
  Line,
  AreaChart,
  Area,
  BarChart,
  Bar,
  PieChart,
  Pie,
  Cell,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip as RechartsTooltip,
  Legend,
  ResponsiveContainer,
} from 'recharts';
import { motion } from 'framer-motion';
import { useQuery } from '@tanstack/react-query';
import { api } from '../services/api';
import toast from 'react-hot-toast';

// Datos de ejemplo para los gráficos
const monthlyData = [
  { month: 'Ene', ingresos: 45000, gastos: 32000, balance: 13000 },
  { month: 'Feb', ingresos: 52000, gastos: 38000, balance: 14000 },
  { month: 'Mar', ingresos: 48000, gastos: 35000, balance: 13000 },
  { month: 'Abr', ingresos: 61000, gastos: 42000, balance: 19000 },
  { month: 'May', ingresos: 55000, gastos: 39000, balance: 16000 },
  { month: 'Jun', ingresos: 67000, gastos: 45000, balance: 22000 },
];

const accountTypeData = [
  { name: 'Activos', value: 45, color: '#4caf50' },
  { name: 'Pasivos', value: 30, color: '#f44336' },
  { name: 'Patrimonio', value: 25, color: '#2196f3' },
];

const transactionData = [
  { day: 'Lun', transacciones: 12, monto: 25000 },
  { day: 'Mar', transacciones: 18, monto: 32000 },
  { day: 'Mie', transacciones: 15, monto: 28000 },
  { day: 'Jue', transacciones: 22, monto: 45000 },
  { day: 'Vie', transacciones: 19, monto: 38000 },
  { day: 'Sab', transacciones: 8, monto: 15000 },
  { day: 'Dom', transacciones: 5, monto: 8000 },
];

const StatCard: React.FC<{
  title: string;
  value: string | number;
  icon: React.ReactNode;
  color: string;
  trend?: number;
  subtitle?: string;
  onClick?: () => void;
  clickable?: boolean;
  cardType?: string;
}> = ({ title, value, icon, color, trend, subtitle, onClick, clickable = false, cardType = 'dashboard-card' }) => (
  <motion.div
    initial={{ opacity: 0, y: 20 }}
    animate={{ opacity: 1, y: 0 }}
    transition={{ duration: 0.5 }}
  >
    <Card
      onClick={onClick}
      sx={{
        height: '100%',
        cursor: clickable ? 'pointer' : 'default',
        '&:hover': {
          transform: clickable ? 'translateY(-4px)' : 'none',
          boxShadow: clickable ? '0 8px 25px rgba(0, 0, 0, 0.3)' : 'none',
        },
        transition: 'all 0.3s ease-in-out',
        position: 'relative',
        overflow: 'hidden',
      }}
    >
      {clickable && (
        <Box
          sx={{
            position: 'absolute',
            top: 8,
            right: 8,
            opacity: 0.6,
            transition: 'opacity 0.3s',
            zIndex: 1,
          }}
        >
          <ArrowForward fontSize="small" />
        </Box>
      )}
      <CardContent className={cardType}>
        <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', mb: 2 }}>
          <Avatar
            sx={{
              bgcolor: color,
              width: 56,
              height: 56,
            }}
          >
            {icon}
          </Avatar>
          {trend !== undefined && (
            <Box sx={{ display: 'flex', alignItems: 'center' }}>
              {trend > 0 ? (
                <TrendingUp sx={{ color: 'success.main', fontSize: 20 }} />
              ) : (
                <TrendingDown sx={{ color: 'error.main', fontSize: 20 }} />
              )}
              <Typography
                variant="caption"
                sx={{
                  color: trend > 0 ? 'success.main' : 'error.main',
                  fontWeight: 600,
                  ml: 0.5,
                }}
              >
                {Math.abs(trend)}%
              </Typography>
            </Box>
          )}
        </Box>
        <Typography variant="h4" sx={{ fontWeight: 700, mb: 1, color: 'text.primary' }}>
          {value}
        </Typography>
        <Typography variant="body2" sx={{ color: 'text.secondary', mb: 1 }}>
          {title}
        </Typography>
        {subtitle && (
          <Typography variant="caption" sx={{ color: 'text.secondary' }}>
            {subtitle}
          </Typography>
        )}
      </CardContent>
    </Card>
  </motion.div>
);

const DashboardPage: React.FC = () => {
  const navigate = useNavigate();
  const [showSensitiveData, setShowSensitiveData] = React.useState(false);

  const { data: dashboardData, isLoading, refetch } = useQuery({
    queryKey: ['dashboard'],
    queryFn: async () => {
      try {
        const [tercerosRes, cuentasRes, transaccionesRes, saldosRes] = await Promise.all([
          api.get('/terceros'),
          api.get('/cuentas'),
          api.get('/transacciones'),
          api.get('/saldos'),
        ]);

        const transacciones = transaccionesRes.data;
        const saldos = saldosRes.data;

        // Calcular estadísticas de transacciones por estado
        const transaccionesActivas = transacciones.filter((t: any) => t.estado === 'ACTIVA').length;
        const transaccionesAnuladas = transacciones.filter((t: any) => t.estado === 'ANULADA').length;
        const transaccionesPendientes = transacciones.filter((t: any) => t.estado === 'PENDIENTE').length;

        // Calcular totales por tipo de cuenta
        const totalActivos = saldos.filter((s: any) => s.tipo === 'ACTIVO').reduce((sum: number, s: any) => sum + (s.saldo || 0), 0);
        const totalPasivos = saldos.filter((s: any) => s.tipo === 'PASIVO').reduce((sum: number, s: any) => sum + (s.saldo || 0), 0);
        const totalPatrimonio = saldos.filter((s: any) => s.tipo === 'PATRIMONIO').reduce((sum: number, s: any) => sum + (s.saldo || 0), 0);
        const totalIngresos = saldos.filter((s: any) => s.tipo === 'INGRESO').reduce((sum: number, s: any) => sum + (s.saldo || 0), 0);
        const totalGastos = saldos.filter((s: any) => s.tipo === 'GASTO').reduce((sum: number, s: any) => sum + (s.saldo || 0), 0);

        // Calcular porcentajes para el gráfico de tipos de cuenta
        const totalGeneral = totalActivos + totalPasivos + totalPatrimonio;
        const porcentajeActivos = totalGeneral > 0 ? (totalActivos / totalGeneral) * 100 : 0;
        const porcentajePasivos = totalGeneral > 0 ? (totalPasivos / totalGeneral) * 100 : 0;
        const porcentajePatrimonio = totalGeneral > 0 ? (totalPatrimonio / totalGeneral) * 100 : 0;

        // Calcular datos mensuales de transacciones (últimos 6 meses)
        const monthsToShow = 6;
        const monthlyData = [];
        const currentDate = new Date();
        for (let i = monthsToShow - 1; i >= 0; i--) {
          const date = new Date(currentDate.getFullYear(), currentDate.getMonth() - i, 1);
          const monthName = date.toLocaleDateString('es-ES', { month: 'short' });
          const monthTransactions = transacciones.filter((t: any) => {
            const transDate = new Date(t.fecha);
            return transDate.getMonth() === date.getMonth() && 
                   transDate.getFullYear() === date.getFullYear() &&
                   t.estado === 'ACTIVA';
          });
          const ingresos = monthTransactions
            .filter((t: any) => t.partidas.some((p: any) => p.tipo === 'HABER' && ['4100', '4200'].includes(p.cuentaContableCodigo)))
            .reduce((sum: number, t: any) => sum + t.totalDebitos, 0);
          const gastos = monthTransactions
            .filter((t: any) => t.partidas.some((p: any) => p.tipo === 'DEBE' && ['5100', '5200', '5300'].includes(p.cuentaContableCodigo)))
            .reduce((sum: number, t: any) => sum + t.totalDebitos, 0);
          monthlyData.push({
            month: monthName,
            ingresos: ingresos / 1000000, // Convertir a millones
            gastos: gastos / 1000000,
            balance: (ingresos - gastos) / 1000000
          });
        }

        return {
          terceros: tercerosRes.data.length,
          cuentas: cuentasRes.data.length,
          transacciones: transacciones.length,
          transaccionesActivas,
          transaccionesAnuladas,
          transaccionesPendientes,
          saldos,
          totalActivos,
          totalPasivos,
          totalPatrimonio,
          totalIngresos,
          totalGastos,
          porcentajeActivos,
          porcentajePasivos,
          porcentajePatrimonio,
          monthlyData,
          balanceGeneral: totalActivos - totalPasivos
        };
      } catch (error) {
        console.error('Error loading dashboard data:', error);
        toast.error('Error al cargar los datos del dashboard');
        return {
          terceros: 0,
          cuentas: 0,
          transacciones: 0,
          transaccionesActivas: 0,
          transaccionesAnuladas: 0,
          transaccionesPendientes: 0,
          saldos: [],
          totalActivos: 0,
          totalPasivos: 0,
          totalPatrimonio: 0,
          totalIngresos: 0,
          totalGastos: 0,
          porcentajeActivos: 0,
          porcentajePasivos: 0,
          porcentajePatrimonio: 0,
          monthlyData: [],
          balanceGeneral: 0
        };
      }
    },
  });

  const handleRefresh = () => {
    refetch();
    toast.success('Datos actualizados');
  };

  const totalBalance = dashboardData?.saldos?.reduce((sum: number, saldo: any) => sum + saldo.saldo, 0) || 0;
  const activeAccounts = dashboardData?.saldos?.filter((saldo: any) => saldo.activo).length || 0;
  const inactiveAccounts = (dashboardData?.cuentas || 0) - activeAccounts;

  // Handlers para navegación
  const handleNavigateToTerceros = () => {
    navigate('/terceros');
    toast.success('Navegando a Terceros');
  };

  const handleNavigateToCuentas = () => {
    navigate('/cuentas');
    toast.success('Navegando a Cuentas Contables');
  };

  const handleNavigateToTransacciones = () => {
    navigate('/transacciones');
    toast.success('Navegando a Transacciones');
  };

  const handleNavigateToSaldos = () => {
    navigate('/saldos');
    toast.success('Navegando a Saldos');
  };

  return (
    <Box sx={{ p: 3 }}>
      {/* Header */}
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 4 }}>
        <Box>
          <Typography variant="h4" sx={{ fontWeight: 700, mb: 1 }}>
            Dashboard Contable
          </Typography>
          <Typography variant="body1" sx={{ color: 'text.secondary' }}>
            Resumen general del sistema contable - {new Date().toLocaleDateString('es-CO')}
          </Typography>
        </Box>
        <Box sx={{ display: 'flex', gap: 1 }}>
          <Tooltip title="Mostrar/Ocultar datos sensibles">
            <IconButton
              onClick={() => setShowSensitiveData(!showSensitiveData)}
              sx={{ bgcolor: 'background.paper' }}
            >
              {showSensitiveData ? <VisibilityOff /> : <Visibility />}
            </IconButton>
          </Tooltip>
          <Tooltip title="Actualizar datos">
            <IconButton
              onClick={handleRefresh}
              disabled={isLoading}
              sx={{ bgcolor: 'background.paper' }}
            >
              <Refresh />
            </IconButton>
          </Tooltip>
        </Box>
      </Box>

      {/* Statistics Cards */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} sm={6} md={3}>
          <StatCard
            title="Total Terceros"
            value={dashboardData?.terceros || 0}
            icon={<People />}
            color="#1976d2"
            subtitle="Personas registradas"
            onClick={handleNavigateToTerceros}
            clickable={true}
            cardType="third-party-card"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <StatCard
            title="Cuentas Activas"
            value={dashboardData?.cuentas || 0}
            icon={<AccountBalance />}
            color="#4caf50"
            subtitle="Cuentas contables"
            onClick={handleNavigateToCuentas}
            clickable={true}
            cardType="account-card"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <StatCard
            title="Transacciones"
            value={dashboardData?.transacciones || 0}
            icon={<Receipt />}
            color="#ff9800"
            subtitle={`${dashboardData?.transaccionesActivas || 0} activas, ${dashboardData?.transaccionesAnuladas || 0} anuladas`}
            onClick={handleNavigateToTransacciones}
            clickable={true}
            cardType="transaction-card"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <StatCard
            title="Balance General"
            value={showSensitiveData ? `$${(dashboardData?.balanceGeneral || 0).toLocaleString('es-CO')}` : '***'}
            icon={<MonetizationOn />}
            color="#9c27b0"
            subtitle="Activos - Pasivos"
            onClick={handleNavigateToSaldos}
            clickable={true}
            cardType="balance-card"
          />
        </Grid>
      </Grid>

      {/* Charts Section */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        {/* Monthly Balance Chart */}
        <Grid item xs={12} lg={8}>
          <Card sx={{ height: 400 }}>
            <CardContent className="balance-card">
              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
                <Typography variant="h6" sx={{ fontWeight: 600 }}>
                  Balance Mensual
                </Typography>
                <Button
                  size="small"
                  onClick={handleNavigateToTransacciones}
                  endIcon={<ArrowForward />}
                >
                  Ver Transacciones
                </Button>
              </Box>
              <ResponsiveContainer width="100%" height={300}>
                <AreaChart data={dashboardData?.monthlyData || []}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="month" />
                  <YAxis />
                  <RechartsTooltip 
                    formatter={(value: any) => [`$${value.toLocaleString('es-CO')}M`, '']}
                    labelFormatter={(label) => `Mes: ${label}`}
                  />
                  <Legend />
                  <Area
                    type="monotone"
                    dataKey="ingresos"
                    stackId="1"
                    stroke="#4caf50"
                    fill="#4caf50"
                    fillOpacity={0.6}
                    name="Ingresos"
                  />
                  <Area
                    type="monotone"
                    dataKey="gastos"
                    stackId="1"
                    stroke="#f44336"
                    fill="#f44336"
                    fillOpacity={0.6}
                    name="Gastos"
                  />
                </AreaChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>
        </Grid>

        {/* Account Types Pie Chart */}
        <Grid item xs={12} lg={4}>
          <Card sx={{ height: 400 }}>
            <CardContent className="account-card">
              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
                <Typography variant="h6" sx={{ fontWeight: 600 }}>
                  Tipos de Cuenta
                </Typography>
                <Button
                  size="small"
                  onClick={handleNavigateToCuentas}
                  endIcon={<ArrowForward />}
                >
                  Ver Cuentas
                </Button>
              </Box>
              <ResponsiveContainer width="100%" height={300}>
                <PieChart>
                  <Pie
                    data={[
                      { name: 'Activos', value: dashboardData?.porcentajeActivos || 0, color: '#4caf50' },
                      { name: 'Pasivos', value: dashboardData?.porcentajePasivos || 0, color: '#f44336' },
                      { name: 'Patrimonio', value: dashboardData?.porcentajePatrimonio || 0, color: '#2196f3' }
                    ]}
                    cx="50%"
                    cy="50%"
                    labelLine={false}
                    label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                    outerRadius={80}
                    fill="#8884d8"
                    dataKey="value"
                  >
                    {[
                      { name: 'Activos', value: dashboardData?.porcentajeActivos || 0, color: '#4caf50' },
                      { name: 'Pasivos', value: dashboardData?.porcentajePasivos || 0, color: '#f44336' },
                      { name: 'Patrimonio', value: dashboardData?.porcentajePatrimonio || 0, color: '#2196f3' }
                    ].map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={entry.color} />
                    ))}
                  </Pie>
                  <RechartsTooltip />
                </PieChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Quick Actions and Alerts */}
      <Grid container spacing={3}>
        {/* Quick Actions */}
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent className="info-card">
              <Typography variant="h6" sx={{ fontWeight: 600, mb: 3 }}>
                Acciones Rápidas
              </Typography>
              <Grid container spacing={2}>
                <Grid item xs={6}>
                  <Button
                    fullWidth
                    variant="outlined"
                    startIcon={<Add />}
                    onClick={handleNavigateToTransacciones}
                    sx={{ height: 60 }}
                  >
                    Nueva Transacción
                  </Button>
                </Grid>
                <Grid item xs={6}>
                  <Button
                    fullWidth
                    variant="outlined"
                    startIcon={<People />}
                    onClick={handleNavigateToTerceros}
                    sx={{ height: 60 }}
                  >
                    Agregar Tercero
                  </Button>
                </Grid>
                <Grid item xs={6}>
                  <Button
                    fullWidth
                    variant="outlined"
                    startIcon={<AccountBalance />}
                    onClick={handleNavigateToCuentas}
                    sx={{ height: 60 }}
                  >
                    Nueva Cuenta
                  </Button>
                </Grid>
                <Grid item xs={6}>
                  <Button
                    fullWidth
                    variant="outlined"
                    startIcon={<Assessment />}
                    onClick={handleNavigateToSaldos}
                    sx={{ height: 60 }}
                  >
                    Ver Saldos
                  </Button>
                </Grid>
              </Grid>
            </CardContent>
          </Card>
        </Grid>

        {/* System Status */}
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent className="success-card">
              <Typography variant="h6" sx={{ fontWeight: 600, mb: 3 }}>
                Estado del Sistema
              </Typography>
              <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                  <CheckCircle sx={{ color: 'success.main' }} />
                  <Typography variant="body2">Base de datos conectada</Typography>
                </Box>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                  <CheckCircle sx={{ color: 'success.main' }} />
                  <Typography variant="body2">API funcionando correctamente</Typography>
                </Box>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                  <CheckCircle sx={{ color: 'success.main' }} />
                  <Typography variant="body2">Sistema operativo</Typography>
                </Box>
                {inactiveAccounts > 0 && (
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                    <Warning sx={{ color: 'warning.main' }} />
                    <Typography variant="body2">
                      {inactiveAccounts} cuenta(s) inactiva(s)
                    </Typography>
                  </Box>
                )}
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Recent Activity */}
      <Card sx={{ mt: 3 }}>
        <CardContent>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
            <Typography variant="h6" sx={{ fontWeight: 600 }}>
              Actividad Reciente
            </Typography>
            <Button
              size="small"
              onClick={handleNavigateToTransacciones}
              endIcon={<ArrowForward />}
            >
              Ver Todo
            </Button>
          </Box>
          <ResponsiveContainer width="100%" height={200}>
            <BarChart data={transactionData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="day" />
              <YAxis />
              <RechartsTooltip 
                formatter={(value: any) => [`${value}`, '']}
                labelFormatter={(label) => `Día: ${label}`}
              />
              <Bar dataKey="transacciones" fill="#1976d2" name="Transacciones" />
            </BarChart>
          </ResponsiveContainer>
        </CardContent>
      </Card>
    </Box>
  );
};

export default DashboardPage; 