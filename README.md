# 🥋 OssFlow: Segundo Cerebro para BJJ

**OssFlow** es una aplicación diseñada para mapear el conocimiento técnico del Brazilian Jiu-Jitsu. Utiliza un modelo de **grafo relacional** para conectar posiciones (nodos) con técnicas (transiciones/finalizaciones), permitiendo a los practicantes estructurar su aprendizaje de forma lógica.

---

## 🏗️ Arquitectura: Hexagonal (Ports & Adapters)

El proyecto sigue los principios de **Arquitectura Hexagonal** para mantener el núcleo de negocio (el Jiu-Jitsu) aislado de la tecnología:

*   **Dominio (`domain`)**: Modelos puros (`Technique`, `Position`) y Enums de lógica de negocio.
*   **Aplicación (`application`)**: Puertos de entrada/salida y servicios que orquestan los casos de uso.
*   **Infraestructura (`infra`)**: Adaptadores para la persistencia (JPA/H2) y la capa web (REST Controllers).

---

## 🛠️ Stack Tecnológico

| Tecnología | Versión | Propósito |
| :--- | :--- | :--- |
| **Java** | 25 | Lenguaje principal |
| **Spring Boot** | 4.0.5 | Framework base y gestión de Beans |
| **Hibernate** | 7.2.7 | ORM para persistencia |
| **MapStruct** | 1.6.3 | Mapeo de objetos entre capas (Compile-time) |
| **Lombok** | 1.18.44 | Reducción de Boilerplate (Builders/Data) |
| **H2 Database** | In-memory | Base de datos de desarrollo |

---

## 🚀 Configuración y Ejecución

### 1. Compilación (Importante)
Debido al uso de **MapStruct** y **Lombok**, es fundamental ejecutar el procesador de anotaciones de Maven para generar las implementaciones de los mappers:

```bash
mvn clean compile
```

# 🚀 Guía de la Aplicación

## 2. Ejecutar la Aplicación
```bash
mvn spring-boot:run
```

La API estará disponible en `http://localhost:8080`.

---

## 🧪 Estrategia de Pruebas (TDD)

El desarrollo se guía por **Test-Driven Development**. Contamos con pruebas unitarias que validan la lógica de enlace entre nodos sin necesidad de levantar el contexto completo de Spring.

### Ejecutar todos los tests
```bash
mvn test
```

---

## 🔌 API Reference

### 📍 Posiciones (Positions)

#### `GET /api/v1/positions`
Lista todas las posiciones.

**Query Params:**
- `?name=Guardia` - Filtro por nombre parcial e insensible a mayúsculas

#### `POST /api/v1/positions`
Crea una nueva posición base.

**Ejemplo de creación:**
```json
{
  "name": "Guardia Cerrada",
  "type": "BOTTOM"
}
```

---

### 🥋 Técnicas (Techniques)

#### `POST /api/v1/techniques`
Registra una técnica vinculándola a una posición.

> ⚠️ **Importante:** Requiere un `startPositionId` válido. Si la posición no existe, el sistema lanzará una excepción `IllegalArgumentException`.

**Ejemplo de creación:**
```json
{
  "name": "Triángulo",
  "category": "SUBMISSION",
  "description": "Estrangulación aislando un brazo y el cuello.",
  "youtubeUrl": "https://youtube.com/watch?v=example",
  "minimumBelt": "WHITE",
  "modality": "BOTH",
  "startPositionId": 1
}
```

---

## 🛣️ Roadmap

- [ ] Integración de GraphQL para consultas de grafos complejas
- [ ] Soporte para Position Final (para encadenar técnicas)
- [ ] Sistema de filtrado por niveles de cinturón
- [ ] Frontend visualizador de grafos con D3.js