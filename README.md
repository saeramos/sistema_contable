# Sistema de Contabilidad Integral
Sistema de contabilidad moderno para gestionar transacciones, cuentas, terceros y reportes financieros.

## Estructura del Proyecto

```
contabilidad-sistema/
├── 📁 backend/                    # Backend Spring Boot
│   ├── 📄 Dockerfile             # Configuración Docker para backend
│   ├── 📄 pom.xml                # Dependencias Maven
│   └── 📁 src/main/
│       ├── 📁 java/com/contabilidad/
│       │   ├── 📁 config/        # Configuraciones (Security, OpenAPI)
│       │   ├── 📁 controller/    # Controladores REST API
│       │   ├── 📁 dto/           # Data Transfer Objects
│       │   │   └── 📁 request/   # DTOs para requests
│       │   ├── 📁 model/         # Entidades JPA
│       │   ├── 📁 repository/    # Repositorios de datos
│       │   ├── 📁 service/       # Lógica de negocio
│       │   └── 📁 util/          # Utilidades (encriptación)
│       └── 📁 resources/
│           └── 📄 application.yml # Configuración de la aplicación
│
├── 📁 database/                   # Base de datos
│   └── 📄 init.sql               # Script de inicialización MySQL
│
├── 📁 frontend/                   # Frontend React
│   ├── 📄 Dockerfile             # Configuración Docker para frontend
│   ├── 📄 nginx.conf             # Configuración Nginx
│   ├── 📄 package.json           # Dependencias Node.js
│   ├── 📄 tsconfig.json          # Configuración TypeScript
│   ├── 📁 public/                # Archivos públicos
│   └── 📁 src/
│       ├── 📁 components/        # Componentes reutilizables
│       ├── 📁 pages/             # Páginas de la aplicación
│       ├── 📁 services/          # Servicios API
│       ├── 📄 App.tsx            # Componente principal
│       ├── 📄 index.tsx          # Punto de entrada
│       └── 📄 theme.ts           # Configuración de tema
│
├── 📄 docker-compose.yml         # Orquestación de contenedores
└── 📄 README.md                  # Documentación del proyecto
```



## Funcionalidades

### Transacciones
- Creación y edición de transacciones contables
- Validación automática de balance (Débitos = Créditos)
- Estados: Activa, Anulada, Pendiente
- Búsqueda y filtros avanzados
- Paginación y navegación eficiente

### Terceros
- Registro de clientes, proveedores y empleados
- Múltiples tipos de documento (CC, NIT, CE, etc.)
- Búsqueda por nombre, documento o tipo
- Edición directa desde la tabla
- Contador de transacciones por tercero

### Cuentas Contables
- Plan de cuentas con estructura jerárquica
- Tipos: Activo, Pasivo, Patrimonio, Ingresos, Gastos
- Códigos personalizables
- Activación/desactivación de cuentas
- Búsqueda por código, nombre o tipo

### Dashboard
- Gráficos de transacciones en tiempo real
- Balance mensual de los últimos 6 meses
- Estadísticas de estados de transacciones
- Saldos por tipo de cuenta
- Métricas clave del sistema

### Saldos
- Saldos actuales por cuenta
- Indicadores visuales (positivo/negativo)
- Filtros por código, nombre o tipo
- Estados de cuentas activas/inactivas
- Formato monetario con separadores

## Tecnologías

**Backend**: Java 17, Spring Boot, MySQL  
**Frontend**: React 18, TypeScript, Material-UI  
**Infraestructura**: Docker, Docker Compose, Nginx

## Instalación

1. **Clonar repositorio**
   ```bash
   git clone <repository-url>
   cd contabilidad-sistema
   ```

2. **Ejecutar con Docker**
   ```bash
   docker-compose up --build -d
   ```

3. **Acceder**
   - Frontend: http://localhost
   - Backend: http://localhost:8080
   - API Docs: http://localhost:8080/swagger-ui.html

## Características Principales

- **Interfaz moderna** y responsive
- **Búsqueda en tiempo real** en todas las secciones
- **Paginación** para mejor rendimiento
- **Validaciones automáticas** de datos
- **Gráficos interactivos** en el dashboard
- **Edición completa** de registros
- **Filtros avanzados** por múltiples criterios

---