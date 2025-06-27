import { createTheme } from '@mui/material/styles';

export const theme = createTheme({
  palette: {
    primary: {
      main: '#6366F1',
      light: '#818CF8',
      dark: '#4F46E5',
      contrastText: '#FFFFFF',
    },
    secondary: {
      main: '#EC4899',
      light: '#F472B6',
      dark: '#DB2777',
      contrastText: '#FFFFFF',
    },
    success: {
      main: '#10B981',
      light: '#34D399',
      dark: '#059669',
      contrastText: '#FFFFFF',
    },
    warning: {
      main: '#F59E0B',
      light: '#FBBF24',
      dark: '#D97706',
      contrastText: '#FFFFFF',
    },
    error: {
      main: '#EF4444',
      light: '#F87171',
      dark: '#DC2626',
      contrastText: '#FFFFFF',
    },
    info: {
      main: '#06B6D4',
      light: '#22D3EE',
      dark: '#0891B2',
      contrastText: '#FFFFFF',
    },
    background: {
      default: '#F8FAFC',
      paper: '#FFFFFF',
    },
    text: {
      primary: '#1E293B',
      secondary: '#64748B',
    },
    divider: '#E2E8F0',
  },
  typography: {
    fontFamily: '"Inter", "Roboto", "Helvetica", "Arial", sans-serif',
    h1: {
      fontWeight: 700,
      color: '#1E293B',
    },
    h2: {
      fontWeight: 600,
      color: '#1E293B',
    },
    h3: {
      fontWeight: 600,
      color: '#1E293B',
    },
    h4: {
      fontWeight: 600,
      color: '#1E293B',
    },
    h5: {
      fontWeight: 600,
      color: '#1E293B',
    },
    h6: {
      fontWeight: 600,
      color: '#1E293B',
    },
    body1: {
      color: '#334155',
    },
    body2: {
      color: '#64748B',
    },
  },
  shape: {
    borderRadius: 12,
  },
  components: {
    MuiButton: {
      styleOverrides: {
        root: {
          textTransform: 'none',
          borderRadius: 8,
          fontWeight: 600,
          boxShadow: '0 4px 12px rgba(99, 102, 241, 0.3)',
          '&:hover': {
            boxShadow: '0 8px 24px rgba(99, 102, 241, 0.4)',
            transform: 'translateY(-2px)',
          },
          transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
        },
        contained: {
          '&:hover': {
            transform: 'translateY(-2px) scale(1.02)',
          },
        },
        outlined: {
          border: '2px solid #6366F1',
          color: '#6366F1',
          '&:hover': {
            background: 'rgba(99, 102, 241, 0.1)',
            borderColor: '#818CF8',
          },
        },
        text: {
          color: '#1E293B',
          '&:hover': {
            background: 'rgba(30, 41, 59, 0.1)',
          },
        },
      },
    },
    MuiCard: {
      styleOverrides: {
        root: {
          borderRadius: 16,
          background: '#FFFFFF',
          boxShadow: '0 4px 12px rgba(0, 0, 0, 0.08)',
          border: '1px solid #E2E8F0',
          '&:hover': {
            boxShadow: '0 8px 24px rgba(0, 0, 0, 0.12)',
            transform: 'translateY(-2px)',
          },
          transition: 'all 0.3s ease-in-out',
        },
      },
    },
    MuiCardContent: {
      styleOverrides: {
        root: {
          padding: '24px',
          '&:last-child': {
            paddingBottom: '24px',
          },
          '&.dashboard-card': {
            background: 'linear-gradient(135deg, #F1F5F9 0%, #E2E8F0 100%)',
            border: '1px solid #CBD5E1',
            borderRadius: '12px',
          },
          '&.transaction-card': {
            background: 'linear-gradient(135deg, #DBEAFE 0%, #BFDBFE 100%)',
            border: '1px solid #3B82F6',
            borderRadius: '12px',
          },
          '&.account-card': {
            background: 'linear-gradient(135deg, #D1FAE5 0%, #A7F3D0 100%)',
            border: '1px solid #10B981',
            borderRadius: '12px',
          },
          '&.third-party-card': {
            background: 'linear-gradient(135deg, #FED7AA 0%, #FDBA74 100%)',
            border: '1px solid #F59E0B',
            borderRadius: '12px',
          },
          '&.balance-card': {
            background: 'linear-gradient(135deg, #E0E7FF 0%, #C7D2FE 100%)',
            border: '1px solid #6366F1',
            borderRadius: '12px',
          },
          '&.stats-card': {
            background: 'linear-gradient(135deg, #F1F5F9 0%, #E2E8F0 100%)',
            border: '1px solid #94A3B8',
            borderRadius: '12px',
          },
          '&.success-card': {
            background: 'linear-gradient(135deg, #D1FAE5 0%, #A7F3D0 100%)',
            border: '1px solid #10B981',
            borderRadius: '12px',
          },
          '&.warning-card': {
            background: 'linear-gradient(135deg, #FEF3C7 0%, #FDE68A 100%)',
            border: '1px solid #F59E0B',
            borderRadius: '12px',
          },
          '&.error-card': {
            background: 'linear-gradient(135deg, #FEE2E2 0%, #FECACA 100%)',
            border: '1px solid #EF4444',
            borderRadius: '12px',
          },
          '&.info-card': {
            background: 'linear-gradient(135deg, #E0F2FE 0%, #BAE6FD 100%)',
            border: '1px solid #06B6D4',
            borderRadius: '12px',
          },
        },
      },
    },
    MuiPaper: {
      styleOverrides: {
        root: {
          borderRadius: 12,
          background: '#FFFFFF',
          boxShadow: '0 2px 8px rgba(0, 0, 0, 0.06)',
          border: '1px solid #E2E8F0',
        },
      },
    },
    MuiChip: {
      styleOverrides: {
        root: {
          borderRadius: 20,
          fontWeight: 600,
          color: '#1E293B',
          '&:hover': {
            transform: 'scale(1.05)',
          },
        },
        label: {
          color: '#1E293B',
        },
      },
    },
    MuiTextField: {
      styleOverrides: {
        root: {
          '& .MuiOutlinedInput-root': {
            borderRadius: 8,
            background: '#FFFFFF',
            border: '1px solid #CBD5E1',
            '&:hover .MuiOutlinedInput-notchedOutline': {
              borderColor: '#818CF8',
            },
            '&.Mui-focused .MuiOutlinedInput-notchedOutline': {
              borderColor: '#6366F1',
              borderWidth: '2px',
            },
            '& input': {
              color: '#1E293B',
            },
            '& .MuiInputLabel-root': {
              color: '#64748B',
              '&.Mui-focused': {
                color: '#6366F1',
              },
            },
            '& .MuiInputBase-input': {
              color: '#1E293B',
            },
          },
        },
      },
    },
    MuiSelect: {
      styleOverrides: {
        root: {
          borderRadius: 8,
          background: '#FFFFFF',
          border: '1px solid #CBD5E1',
          color: '#1E293B',
          '& .MuiSelect-select': {
            color: '#1E293B',
          },
        },
        icon: {
          color: '#64748B',
        },
      },
    },
    MuiTableHead: {
      styleOverrides: {
        root: {
          background: '#F8FAFC',
          '& .MuiTableCell-head': {
            fontWeight: 700,
            color: '#1E293B',
            borderBottom: '2px solid #E2E8F0',
          },
        },
      },
    },
    MuiTableCell: {
      styleOverrides: {
        root: {
          borderBottom: '1px solid #E2E8F0',
          color: '#334155',
        },
      },
    },
    MuiTableBody: {
      styleOverrides: {
        root: {
          '& .MuiTableRow-root:hover': {
            background: 'rgba(99, 102, 241, 0.05)',
          },
        },
      },
    },
    MuiLinearProgress: {
      styleOverrides: {
        root: {
          borderRadius: 4,
          backgroundColor: '#F1F5F9',
          height: 8,
        },
        bar: {
          borderRadius: 4,
          background: 'linear-gradient(90deg, #6366F1 0%, #EC4899 100%)',
        },
      },
    },
    MuiFab: {
      styleOverrides: {
        root: {
          background: 'linear-gradient(135deg, #6366F1 0%, #EC4899 100%)',
          boxShadow: '0 8px 24px rgba(99, 102, 241, 0.4)',
          '&:hover': {
            boxShadow: '0 12px 32px rgba(99, 102, 241, 0.6)',
            transform: 'scale(1.1)',
          },
          transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
        },
      },
    },
    MuiIconButton: {
      styleOverrides: {
        root: {
          color: '#64748B',
          '&:hover': {
            backgroundColor: 'rgba(99, 102, 241, 0.1)',
            transform: 'scale(1.1)',
          },
          transition: 'all 0.2s ease-in-out',
        },
      },
    },
    MuiAppBar: {
      styleOverrides: {
        root: {
          background: '#FFFFFF',
          borderBottom: '1px solid #E2E8F0',
          color: '#1E293B',
        },
      },
    },
    MuiDrawer: {
      styleOverrides: {
        paper: {
          background: '#FFFFFF',
          borderRight: '1px solid #E2E8F0',
        },
      },
    },
    MuiListItem: {
      styleOverrides: {
        root: {
          color: '#FFFFFF',
          fontWeight: 500,
          '&:hover': {
            background: 'rgba(255, 255, 255, 0.1)',
            borderRadius: 8,
          },
          '&.Mui-selected': {
            background: 'rgba(255, 255, 255, 0.2)',
            borderRadius: 8,
            borderLeft: '4px solid #FFFFFF',
            color: '#FFFFFF',
            fontWeight: 600,
          },
        },
      },
    },
    MuiListItemText: {
      styleOverrides: {
        primary: {
          color: '#FFFFFF',
          fontWeight: 500,
        },
        secondary: {
          color: '#E2E8F0',
        },
      },
    },
    MuiListItemIcon: {
      styleOverrides: {
        root: {
          color: '#E2E8F0',
          '&.Mui-selected': {
            color: '#FFFFFF',
          },
        },
      },
    },
    MuiDialog: {
      styleOverrides: {
        paper: {
          background: '#FFFFFF',
          color: '#1E293B',
        },
      },
    },
    MuiDialogTitle: {
      styleOverrides: {
        root: {
          color: '#1E293B',
        },
      },
    },
    MuiDialogContent: {
      styleOverrides: {
        root: {
          color: '#334155',
        },
      },
    },
    MuiDialogActions: {
      styleOverrides: {
        root: {
          color: '#334155',
        },
      },
    },
    MuiMenu: {
      styleOverrides: {
        paper: {
          background: '#FFFFFF',
          border: '1px solid #E2E8F0',
        },
      },
    },
    MuiMenuItem: {
      styleOverrides: {
        root: {
          color: '#334155',
          '&:hover': {
            background: 'rgba(99, 102, 241, 0.05)',
          },
          '&.Mui-selected': {
            background: 'rgba(99, 102, 241, 0.1)',
            color: '#1E293B',
          },
        },
      },
    },
    MuiTooltip: {
      styleOverrides: {
        tooltip: {
          background: '#1E293B',
          color: '#FFFFFF',
          border: '1px solid #E2E8F0',
        },
      },
    },
    MuiSnackbar: {
      styleOverrides: {
        root: {
          '& .MuiSnackbarContent-root': {
            background: '#FFFFFF',
            color: '#1E293B',
            border: '1px solid #E2E8F0',
          },
        },
      },
    },
  },
});

export const contabilidadColors = {
  debe: {
    main: '#EF4444',
    light: '#F87171',
    dark: '#DC2626',
    contrastText: '#FFFFFF',
  },
  haber: {
    main: '#10B981',
    light: '#34D399',
    dark: '#059669',
    contrastText: '#FFFFFF',
  },
}; 