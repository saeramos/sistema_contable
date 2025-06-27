import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import {
  Drawer,
  List,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Box,
  Typography,
  Divider,
  IconButton,
  Tooltip,
  Avatar,
} from '@mui/material';
import {
  Dashboard,
  People,
  AccountBalance,
  Receipt,
  Assessment,
  Menu as MenuIcon,
  ChevronLeft,
  Security,
  Settings,
} from '@mui/icons-material';
import { motion } from 'framer-motion';

const drawerWidth = 280;

const menuItems = [
  { text: 'Dashboard', icon: <Dashboard />, path: '/' },
  { text: 'Terceros', icon: <People />, path: '/terceros' },
  { text: 'Cuentas Contables', icon: <AccountBalance />, path: '/cuentas' },
  { text: 'Transacciones', icon: <Receipt />, path: '/transacciones' },
  { text: 'Saldos', icon: <Assessment />, path: '/saldos' },
];

const Navigation: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [open, setOpen] = useState(true);

  const handleDrawerToggle = () => {
    setOpen(!open);
  };

  const handleNavigation = (path: string) => {
    navigate(path);
  };

  return (
    <>
      <Drawer
        variant="permanent"
        sx={{
          width: open ? drawerWidth : 70,
          flexShrink: 0,
          '& .MuiDrawer-paper': {
            width: open ? drawerWidth : 70,
            boxSizing: 'border-box',
            background: '#374151',
            color: 'white',
            transition: 'width 0.3s ease-in-out',
            overflowX: 'hidden',
            border: 'none',
            boxShadow: '4px 0 20px rgba(0,0,0,0.1)',
            borderRight: '1px solid #6B7280',
          },
        }}
      >
        <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', p: 2 }}>
          {open && (
            <motion.div
              initial={{ opacity: 0, x: -20 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ duration: 0.3 }}
            >
              <Typography variant="h6" sx={{ fontWeight: 600, color: 'white' }}>
                Sistema Contable
              </Typography>
            </motion.div>
          )}
          <IconButton
            onClick={handleDrawerToggle}
            sx={{ color: 'white', ml: open ? 'auto' : 0 }}
          >
            {open ? <ChevronLeft /> : <MenuIcon />}
          </IconButton>
        </Box>

        <Divider sx={{ borderColor: '#6B7280' }} />

        <List sx={{ pt: 1 }}>
          {menuItems.map((item) => {
            const isActive = location.pathname === item.path;
            return (
              <ListItem key={item.text} disablePadding sx={{ mb: 1 }}>
                <ListItemButton
                  onClick={() => handleNavigation(item.path)}
                  sx={{
                    mx: 1,
                    borderRadius: 2,
                    backgroundColor: isActive ? 'rgba(255,255,255,0.15)' : 'transparent',
                    '&:hover': {
                      backgroundColor: 'rgba(255,255,255,0.1)',
                    },
                    transition: 'all 0.2s ease-in-out',
                    minHeight: 48,
                  }}
                >
                  <ListItemIcon
                    sx={{
                      color: isActive ? 'white' : 'rgba(255,255,255,0.8)',
                      minWidth: 40,
                    }}
                  >
                    {item.icon}
                  </ListItemIcon>
                  {open && (
                    <motion.div
                      initial={{ opacity: 0, x: -10 }}
                      animate={{ opacity: 1, x: 0 }}
                      transition={{ duration: 0.3 }}
                    >
                      <ListItemText
                        primary={item.text}
                        sx={{
                          '& .MuiListItemText-primary': {
                            fontWeight: isActive ? 600 : 400,
                            fontSize: '0.95rem',
                          },
                        }}
                      />
                    </motion.div>
                  )}
                </ListItemButton>
              </ListItem>
            );
          })}
        </List>

        <Box sx={{ mt: 'auto', p: 2 }}>
          <Divider sx={{ borderColor: '#6B7280', mb: 2 }} />
          
          {open && (
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.3 }}
            >
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                <Avatar
                  sx={{
                    width: 40,
                    height: 40,
                    bgcolor: 'rgba(255,255,255,0.2)',
                    mr: 2,
                  }}
                >
                  <Security />
                </Avatar>
                <Box>
                  <Typography variant="body2" sx={{ fontWeight: 600, color: 'white' }}>
                    Sistema Seguro
                  </Typography>
                  <Typography variant="caption" sx={{ color: 'rgba(255,255,255,0.7)' }}>
                    Protegido y Cifrado
                  </Typography>
                </Box>
              </Box>
            </motion.div>
          )}

          <Tooltip title={open ? '' : 'Configuración'} placement="right">
            <ListItemButton
              sx={{
                borderRadius: 2,
                '&:hover': {
                  backgroundColor: 'rgba(255,255,255,0.1)',
                },
                minHeight: 48,
              }}
            >
              <ListItemIcon
                sx={{
                  color: 'rgba(255,255,255,0.8)',
                  minWidth: 40,
                }}
              >
                <Settings />
              </ListItemIcon>
              {open && (
                <motion.div
                  initial={{ opacity: 0, x: -10 }}
                  animate={{ opacity: 1, x: 0 }}
                  transition={{ duration: 0.3 }}
                >
                  <ListItemText
                    primary="Configuración"
                    sx={{
                      '& .MuiListItemText-primary': {
                        fontSize: '0.9rem',
                      },
                    }}
                  />
                </motion.div>
              )}
            </ListItemButton>
          </Tooltip>
        </Box>
      </Drawer>
    </>
  );
};

export default Navigation; 