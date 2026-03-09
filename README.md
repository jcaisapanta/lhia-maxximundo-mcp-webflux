# Maxximundo - MCP Context Server (lhia-v2-mcp-webflux)

![Java](https://img.shields.io/badge/Java-17+-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.5-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Spring WebFlux](https://img.shields.io/badge/Spring_WebFlux-Reactive-6DB33F?style=for-the-badge)

Este repositorio contiene el **Servidor de Protocolo de Contexto de Modelos (MCP - Model Context Protocol)** (`lhia-v2-mcp-webflux`) para **Maxximundo**. Está desarrollado en un esquema reactivo con Spring WebFlux y provee un canal estándar de herramientas y esquemas contextuales que pueden ser consumidos por Agentes de LangChain o LLMs soportados por MCP.

## 🚀 Funcionalidades Principales

*   **Compuerta MCP (Model Context Protocol)**: Actúa como puente para exponer las APIs legadas y bases de datos internas hacia los Agentes Inteligentes.
*   **Agnosticismo de Protocolo**: Provee un endpoint mediante Transporte sobre HTTP (Streamable) y soporta protocolos STDIO (desactivando la consola para ejecución estándar).
*   **Orquestación de Múltiples Fuentes**: Conecta transversalmente las diferentes APIs de backend en Maxximundo:
    *   API de Base (`back-maxximundo`).
    *   API de Integración y Reglas de Negocio Lhia (`back-lhia-maxximundo`).
*   **Infraestructura Reactiva**: Construido sobre WebFlux, para procesar la emisión de eventos Server-Sent-Events (SSE) usados en la especificación MCP.

## 🛠️ Tecnologías

*   **Java** (Compatible Java 17+)
*   **Spring Boot 3.4.5**
*   **Spring AI Starter MCP Server WebFlux** (Para soportar `mcp-server`)
*   **Lombok**

## ⚙️ Configuración y Puertos

Se levanta bajo una capa asíncrona reactiva Netty/WebFlux y expide la ruta `/mcp` para clientes MCP conectables.

| Componente | Valor de Configuración |
| :--- | :--- |
| **Puerto de Ejecución** | `9002` |
| **Endpoint MCP HTTP** | `/mcp` |
| **Protocolo Seteeado** | `STREAMABLE` |

### Endpoint Dependencias:
1.  **Lhia Core API**: Apunta al puerto local `9001` (`lhia.api.base-url=http://localhost:9001`).
2.  **Lhia Maxximundo API**: Apunta al puerto local `7205` (`maxximundo.api.base-url=http://localhost:7205/maxximundo/maxximundo-api`).

## 💻 Cómo Ejecutar (Local)

1.  Asegúrate tener tus microservicios de backend (`9001` y `7205`) en ejecución.
2.  Clonar el repositorio y ejecutar vía terminal:
    ```bash
    ./mvnw spring-boot:run
    ```
3.  El servidor despachará metadatos y herramientas de protocolo de modelos en `http://localhost:9002/mcp`.

---
*Desarrollado internamente para enriquecer la inteligencia generativa de Maxximundo.*
