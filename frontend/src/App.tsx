import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { ThemeProvider } from '@mui/material/styles';
import { CssBaseline } from '@mui/material';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { theme } from './theme';
import Navigation from './components/Navigation';
import DashboardPage from './pages/DashboardPage';
import CuentasPage from './pages/CuentasPage';
import TercerosPage from './pages/TercerosPage';
import TransaccionesPage from './pages/TransaccionesPage';
import SaldosPage from './pages/SaldosPage';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: 1,
      refetchOnWindowFocus: false,
    },
  },
});

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <Router>
          <div style={{ display: 'flex', minHeight: '100vh' }}>
            <Navigation />
            <main style={{ flexGrow: 1, padding: '24px', backgroundColor: '#F8F9FA' }}>
              <Routes>
                <Route path="/" element={<DashboardPage />} />
                <Route path="/cuentas" element={<CuentasPage />} />
                <Route path="/terceros" element={<TercerosPage />} />
                <Route path="/transacciones" element={<TransaccionesPage />} />
                <Route path="/saldos" element={<SaldosPage />} />
              </Routes>
            </main>
          </div>
        </Router>
      </ThemeProvider>
    </QueryClientProvider>
  );
}

export default App; 