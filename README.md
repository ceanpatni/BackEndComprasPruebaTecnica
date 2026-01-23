# Entregable Prueba
<p align="justify">
A partir de los requerimientos definidos en la prueba LinkTIC, se desarrolló una solución enfocada en la correcta separación de responsabilidades, la claridad en la arquitectura y la aplicación de buenas prácticas de desarrollo. Aunque se plantea una arquitectura basada en microservicios, se optó por una implementación con separación lógica dentro de un único backend, lo cual permite simplificar la ejecución y mantener la consistencia del flujo de compra; del mismo modo, se incorporó un frontend en Angular para validar la integración entre componentes. Con lo anterior, la entrega incluye backend, frontend, pruebas, documentación y containerización, presentando una solución funcional, clara y alineada con los objetivos planteados.
</p>

### ANÁLISIS FUNCIONAL DEL PROYECTO

#### Objetivo

Implementar un sistema de compras basado en dos microservicios lógicos:

Productos

Inventario


### DECISIÓN ARQUITECTÓNICA CLAVE
¿Dónde vive el endpoint de compra?

InventarioService

#### Justificación técnica:

El inventario es el dueño de la cantidad

La compra modifica stock

Se evita acoplar lógica de negocio en Productos

Se cumple Single Responsibility Principle

Se mantiene consistencia transaccional

### DIAGRAMA FUNCIONAL – FLUJO DE COMPRA
![img.png](diagrama_funcional.png)

### DIAGRAMA TÉCNICO FRONT → BACK → DB
![img_2.png](diagrama_tecnico.png)

### ESTÁNDAR DTO – JSON:API
Request Compra
{
"data": {
"type": "compra",
"attributes": {
"productoId": 1,
"cantidad": 2
}
}
}

### Response Compra Exitosa
{
"data": {
"type": "compra",
"id": "10",
"attributes": {
"productoId": 1,
"cantidad": 2,
"total": 20000
}
}
}


### MODELO LÓGICO DE DATOS

#### Entidades:
##### Microservicio

Producto

##### Microservicio
Inventario

Compra

Auditoria



### Autenticacion Backend Compras - Seguridad JWT + RSA

Este proyecto implementa un sistema de compras con **autenticación y autorización** mediante **JWT (JSON Web Token)** y **Spring Security**. Se utilizó **RSA** para firmar y validar los tokens, asegurando que solo usuarios autenticados puedan acceder a ciertas rutas.


#### 1. Seguridad con JWT + RSA

Se implementó la autenticación utilizando **JWT firmado con claves RSA**, con las siguientes características:

- **Generación de token:**  
  Cuando un usuario inicia sesión con su `username` y `password`, se valida contra la base de datos. Si es correcto, se genera un JWT firmado con la **clave privada RSA** (`private_key.pem`) que contiene los datos del usuario y su rol.

- **Validación de token:**  
  Cada solicitud a rutas protegidas incluye el token en el encabezado `Authorization: Bearer <token>`. Spring Security valida el token utilizando la **clave pública RSA** (`public_key.pem`) para asegurar que no haya sido modificado.

- **Roles y autorizaciones:**  
  Dependiendo del `role` del usuario (`ROLE_ADMIN`, `ROLE_USER`, etc.), se permite o deniega el acceso a ciertos endpoints.


## DOCKERIZACION
## Dockerizacion backend Compras

## Dockerizacion backend productos

### Versionamiento GIT
#### Versionamiento Backend Compras

lo primero que vamos a realizar es clonar el repositorio
comparto la url del repositorio https://github.com/ceanpatni/BackEndComprasPruebaTecnica.git
para bajar los cambios al local
1) git clone --single-branch -b developAndres https://github.com/ceanpatni/BackEndComprasPruebaTecnica.git
#### Versionamiento Backend Productos
para bajar los cambios al local
git clone --single-branch -b developAndres https://github.com/ceanpatni/BackEndProductosPruebaTecnica.git

#### Versionamiento Frontend
para bajar en local
1) git clone --single-branch -b developFrontAndres https://github.com/ceanpatni/FrontEndComprasPruebaTecnica.git

