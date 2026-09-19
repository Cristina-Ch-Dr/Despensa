-- Script para popular la base de datos con datos de ejemplo
-- Este script se ejecutará automáticamente al iniciar la aplicación

-- Limpiar tablas (comentar si no quieres perder datos)
-- DELETE FROM lista_producto;
-- DELETE FROM usuario_grupo;
-- DELETE FROM usuario_amigo;
-- DELETE FROM lista;
-- DELETE FROM producto;
-- DELETE FROM grupo;
-- DELETE FROM usuario;

-- Insertar Usuarios
INSERT INTO usuario (usuario_id, nombre, apellidos, email, password, fecha_creacion) VALUES
(1, 'Juan', 'Pérez García', 'juan.perez@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36DxJiFm', NOW()),
(2, 'María', 'López Rodríguez', 'maria.lopez@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36DxJiFm', NOW()),
(3, 'Carlos', 'Martínez Ruiz', 'carlos.martinez@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36DxJiFm', NOW()),
(4, 'Ana', 'González Díaz', 'ana.gonzalez@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36DxJiFm', NOW());

-- Insertar Grupos
INSERT INTO grupo (grupo_id, nombre, descripcion, fecha_creacion) VALUES
(1, 'Casa', 'Grupo para la compra de la casa', NOW()),
(2, 'Oficina', 'Grupo para la compra de la oficina', NOW()),
(3, 'Viaje', 'Grupo para el viaje de verano', NOW());

-- Insertar Productos
INSERT INTO producto (producto_id, nombre, descripcion, precio, categoria, fecha_creacion) VALUES
(1, 'Leche', 'Leche desnatada 1L', 0.95, 'Lácteos', NOW()),
(2, 'Pan integral', 'Pan integral de 600g', 1.50, 'Panadería', NOW()),
(3, 'Tomates', 'Tomates frescos de rama', 2.30, 'Verduras', NOW()),
(4, 'Pollo', 'Pechuga de pollo 500g', 4.50, 'Carnes', NOW()),
(5, 'Arroz', 'Arroz integral 1kg', 2.10, 'Legumbres', NOW()),
(6, 'Aceite de oliva', 'Aceite de oliva virgen extra 500ml', 5.90, 'Aceites', NOW()),
(7, 'Papel higiénico', 'Rollo de papel higiénico pack 12', 3.20, 'Limpieza', NOW()),
(8, 'Detergente', 'Detergente para ropa 1,5L', 2.80, 'Limpieza', NOW()),
(9, 'Manzanas', 'Manzanas rojas por kg', 1.80, 'Frutas', NOW()),
(10, 'Queso', 'Queso manchego 250g', 3.50, 'Lácteos', NOW());

-- Insertar Listas
INSERT INTO lista (lista_id, nombre, descripcion, grupo_id, fecha_creacion) VALUES
(1, 'Compra semanal', 'Productos para la semana', 1, NOW()),
(2, 'Desayuno', 'Productos para el desayuno', 1, NOW()),
(3, 'Limpieza', 'Productos de limpieza', 1, NOW()),
(4, 'Material de oficina', 'Útiles y material para la oficina', 2, NOW()),
(5, 'Comida de viaje', 'Snacks y bebidas para el viaje', 3, NOW());

-- Insertar relaciones Usuario-Grupo (usuario_grupo)
INSERT INTO usuario_grupo (usuario_id, grupo_id) VALUES
(1, 1), (1, 2), (1, 3),
(2, 1), (2, 2),
(3, 1),
(4, 3);

-- Insertar relaciones Usuario-Amigo (usuario_amigo)
INSERT INTO usuario_amigo (usuario_id, amigo_id) VALUES
(1, 2), (1, 3),
(2, 1), (2, 3),
(3, 1), (3, 2),
(4, 1);

-- Insertar relaciones Lista-Producto (lista_producto)
INSERT INTO lista_producto (lista_id, producto_id) VALUES
-- Lista 1: Compra semanal
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5),
-- Lista 2: Desayuno
(2, 1), (2, 2), (2, 9), (2, 10),
-- Lista 3: Limpieza
(3, 7), (3, 8),
-- Lista 4: Material de oficina
(4, 8),
-- Lista 5: Comida de viaje
(5, 2), (5, 9), (5, 10), (5, 6);
