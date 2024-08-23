openapi: 3.0.3
info:
  title: REST Vinos - OpenAPI 3.0
  description: >-
    API que simula una comunidad online para la gestión y recomendación de vinos entre usuarios
  termsOfService: http://swagger.io/terms/
  license:
    name: Apache 2.0
    url: http://www.apache.org/licenses/LICENSE-2.0.html
  version: 1.0.11
externalDocs:
  description: Find out more about Swagger
  url: http://swagger.io
servers:
  - url: http://localhost:8080/RESTVinos/api
    variables: {}
    description: REST Api que simula una red social enfocada en la comunidad de vinos
paths:
  /usuarios:
    get:
      tags:
        - Usuario
      summary: Devuelve una lista con los usuarios registrados en el sistema
      description: Obtiene una lista de los usuarios existentes, pudiendo aplicar un patrón de nombre en esta búsqueda para filtrar la misma.
      operationId: getUsuarios
      parameters:
        - name: nombre
          in: query
          required: false
          schema:
            type: string
        - name: desde
          in: query
          required: false
          schema:
            type: integer
        - name: count
          in: query
          required: false
          schema:
            type: integer
      responses:
        '200':
          description: successful operation
          content:
            application/json:
              schema:
                type: array
                items: 
                  $ref: '#/components/schemas/Usuario'
        '400':
          description: Internal server error
    post:
      tags:
        - Usuario
      summary: Crea un nuevo usuario
      description: Registra el usuario especificando su nombre, email y fecha de nacimiento. No pueden haber dos usuarios con el mismo email y un usuario no puede registrarse si no es mayor de edad.
      operationId: createUsuario
      requestBody:
        content:
          application/json:
            schema: 
              $ref: '#/components/schemas/Usuario'
      responses:
        '201':
          description: Successful operation
          content:
            application/json:
              schema: 
                $ref: '#/components/schemas/Usuario'
        '400':
          description: Internal server error 
        
  /usuarios/{usuario_id}:
    get:
      tags:
        - Usuario
      summary: Devuelve los datos básicos del usuario
      description: Obtiene el nombre, email y fecha de nacimiento del id de usuario especificado.
      operationId: getUsuario
      parameters:
        - name: usuario_id
          in: path
          description: id usuario
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: successful operation
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Usuario'
        '400':
          description: Internal server error
        '404':
          description: User not found
    put:
      tags:
        - Usuario
      summary: Actualiza datos del usuario
      description: Actualiza el email, nombre, fecha de nacimiento o la combinación de ellos del usuario especificado.
      operationId: updateUsuario
      parameters:
        - name: usuario_id
          in: path
          description: id usuario
          required: true
          schema:
            type: integer
      requestBody:
        description: Actualiza usuario existente
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/Usuario'
      responses:
        '200':
          description: successful operation
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Usuario'
        '400':
          description: Invalid id supplied
        '404':
          description: User not found
    delete:
      tags:
        - Usuario
      summary: Elimina un usuario
      description: Elimina el usuario especificado del sistema.
      operationId: deleteUser
      parameters:
        - name: usuario_id
          in: path
          description: id usuario
          required: true
          schema:
            type: integer
      responses:
        '204':
          description: User deleted
        '400':
          description: Internal server error
        '404':
          description: User not found
  /usuarios/{usuario_id}/puntuaciones:
    post:
      tags:
        - Puntuaciones
      summary: Inserta un vino a la lista de vinos del usuario
      description: Inserta un vino especificado con una posible puntuación del 0 al 10 con un decimal a la lista de vinos del usuario especificado.
      operationId: createPuntuacion
      parameters:
        - name: usuario_id
          in: path
          description: id usuario
          required: true
          schema:
            type: integer
      requestBody:
        content:
          application/json:
            schema: 
              $ref: '#/components/schemas/Puntuacion'
      responses:
        '201':
          description: successful operation
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Puntuacion'
        '400':
          description: Internal server error
        '404':
          description: User not found
    get:
      tags:
        - Puntuaciones
      summary: Devuelve lista de vinos con puntuacion
      description: Devuelve la lista de vinos con sus puntuaciones del usuario especificado. Se pueden usar parámetros para filtrar la lista, como el nombre del vino, orden de la fecha de adicion, tipo de vino, origen, año, la uva y/o limitar la cantidad de información mostrada con los parámetros desde y hasta, por ejemplo los X primeros (únicamente parámetro hasta) o los elementos entre X e Y (parámetros desde y hasta).
      operationId: getPuntuaciones
      parameters:
        - name: usuario_id
          in: path
          description: id usuario
          required: true
          schema:
            type: integer
        - name: vino
          in: query
          required: false
          schema:
            type: string
          description: Nombre del vino a buscar
        - name: fechaAdicion
          in: query
          required: false
          schema:
            type: string
          description: Orden de la fecha de adicion (ascendente o descendente)
        - name: tipo
          in: query
          required: false
          schema:
            type: string
          description: Tipo del vino a buscar
        - name: origen
          in: query
          required: false
          schema:
            type: string
          description: Origen del vino a buscar
        - name: ano
          in: query
          required: false
          schema:
            type: string
          description: Año del vino a buscar
        - name: uva
          in: query
          required: false
          schema:
            type: string
          description: Uva del vino a buscar
        - name: desde
          in: query
          required: false
          schema:
            type: string
          description: Desde qué elemento empieza la lista
        - name: count
          in: query
          required: false
          schema:
            type: string
          description: Cuántos elementos debe mostrar cada página
      responses:
        '200':
          description: successful operation
          content:
            application/json:
              schema:
                type: array
                items: 
                  $ref: '#/components/schemas/Puntuacion'
        '400':
          description: Internal server error
        '404':
          description: User not found
    put:
      tags:
        - Puntuaciones
      summary: Actualiza la puntuacion
      description: Actualiza la puntuación en el vino especificado por su id, si este vino no tiene puntuación, se le añade.
      operationId: updatePuntuacion
      parameters:
        - name: usuario_id
          in: path
          description: id usuario
          required: true
          schema:
            type: integer
      requestBody:
        content:
          application/json:
            schema: 
              $ref: '#/components/schemas/Puntuacion'
      responses:
        '200':
          description: successful operation
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Puntuacion'
        '400':
          description: Internal server error
        '404':
          description: User not found
          
  /usuarios/{usuario_id}/puntuaciones/{vino_id}:
    delete:
      tags:
        - Puntuaciones
      summary: Elimina el vino de la lista de vinos del usuario
      description: Elimina la puntuacion de un vino especificado por su id en la lista de vinos del usuario especificado por su id.
      operationId: deletePuntuacion
      parameters:
        - name: usuario_id
          in: path
          description: El id del usuario que va a eliminar el vino
          required: true
          schema:
            type: string
        - name: vino_id
          in: path
          description: El id del vino que se va a eliminar
          required: true
          schema:
            type: string
      responses:
        '204':
          description: Vino eliminado
        '400':
          description: Internal server error
        '404':
          description: User not found
          
  /usuarios/{usuario_id}/seguidores:
    get:
      tags:
        - Seguidores
      summary: Devuelve los seguidores de un usuario
      description: Devuelve una lista con los seguidores del usuario especificado por su id. Esta lista puede ser filtrada por patrón de nombre.
      operationId: getSeguidores
      parameters:
        - name: usuario_id
          in: path
          description: id usuario
          required: true
          schema:
            type: integer
        - name: nombre
          in: query
          required: false
          schema:
            type: string
          description: Nombre del seguidor a buscar
        - name: desde
          in: query
          required: false
          schema:
            type: integer
        - name: count
          in: query
          required: false
          schema:
            type: integer
      responses:
        '200':
          description: successful operation
          content:
            application/json:
              schema:
                type: array
                items: 
                  $ref: '#/components/schemas/Usuario'
        '400':
          description: Internal server error
        '404':
          description: User not found
    post:
      tags:
        - Seguidores
      summary: Añade un seguidor
      description: Añade un seguidor existente a la lista de seguidores del usuario especificado por su id.
      operationId: createSeguidor
      parameters:
        - name: usuario_id
          in: path
          description: usuario que quiere añadir seguidor
          required: true
          schema:
            type: integer
      requestBody:
        content:
          application/json:
            schema: 
              $ref: '#/components/schemas/Usuario'
      responses:
        '201':
          description: successful operation
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Usuario'
        '400':
          description: Internal server error
        '404':
          description: User not found      
  /usuarios/{usuario_id}/seguidores/{seguidor_id}:
    delete:
      tags:
        - Seguidores
      summary: Elimina el seguidor
      description: Elimina el seguidor especificado por su id del sistema
      operationId: deleteSeguidor
      parameters:
        - name: seguidor_id
          in: path
          description: El id del seguidor a eliminar
          required: true
          schema:
            type: integer
        - name: usuario_id
          in: path
          description: id usuario
          required: true
          schema:
            type: integer
      responses:
        '204':
          description: Seguidor eliminado
        '400':
          description: Internal server error
        '404':
          description: User not found
  /usuarios/{usuario_id}/seguidores/{seguidor_id}/puntuaciones:
    get:
      tags:
        - Puntuaciones
      summary: Puntuaciones del seguidor de un usuario
      description:  Devuelve la lista de vinos con sus puntuaciones del seguidor del usuario especificado. Se pueden usar parámetros para filtrar la lista, como el nombre del vino, orden de la fecha de adicion, tipo de vino, origen, año, la uva y/o limitar la cantidad de información mostrada con los parámetros desde y hasta, por ejemplo los X primeros (únicamente parámetro hasta) o los elementos entre X e Y (parámetros desde y hasta).
      operationId: getPuntuacionesSeguidor
      parameters:
        - name: usuario_id
          in: path
          description: id del usuario
          required: true
          schema:
            type: integer
        - name: seguidor_id
          in: path
          description: id del seguidor
          required: true
          schema:
            type: integer
        - name: vino
          in: query
          required: false
          schema:
            type: string
          description: Nombre del vino a buscar
        - name: fechaAdicion
          in: query
          required: false
          schema:
            type: string
          description: Fecha a buscar
        - name: tipo
          in: query
          required: false
          schema:
            type: string
          description: Tipo del vino a buscar
        - name: origen
          in: query
          required: false
          schema:
            type: string
          description: Origen del vino a buscar
        - name: ano
          in: query
          required: false
          schema:
            type: string
          description: Año del vino a buscar
        - name: uva
          in: query
          required: false
          schema:
            type: string
          description: Uva del vino a buscar
        - name: desde
          in: query
          required: false
          schema:
            type: string
          description: Desde qué elemento empieza la lista
        - name: count
          in: query
          required: false
          schema:
            type: string
          description: Cuántos elementos debe mostrar cada página
      responses:
        '200':
          description: successful operation
          content:
            application/json:
              schema:
                type: array
                items: 
                  $ref: '#/components/schemas/Puntuacion'
        '400':
          description: Internal server error
        '404':
          description: User not found
  /usuarios/{usuario_id}/recomendacion:
    get:
      tags:
        - Recomendacion
      summary: Devuelve una recomendacion personalizada para un usuario
      description:  Devuelve los datos personales del usuario, un listado con sus 5 últimos vinos añadidos, un listado con sus 5 vinos con mayor puntuación y otro listado con los 5 vinos con mayor puntuación de todos sus seguidores.
      operationId: getRecomendacion
      parameters:
        - name: usuario_id
          in: path
          description: id del usuario
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: successful operation
          content:
            application/json:
             schema:
              type: object
              properties:
                usuario:
                  $ref: '#/components/schemas/Usuario'
                ultimosVinos:
                  type: array
                  items:
                    $ref: '#/components/schemas/Vino'
                mejoresVinos:
                  type: array
                  items:
                    $ref: '#/components/schemas/Vino'
                mejoresVinosAmigos:
                  type: array
                  items:
                    $ref: '#/components/schemas/Vino'
        '404':
          description: not found
components:
  schemas:
    Usuario:
      type: object
      properties:
        id:
          type: integer
          format: int64
          example: 10
        nombre:
          type: string
          example: pepe
        email:
          type: string
          example: pepe@email.com
        fechaNacimiento:
          type: string
          format: date-time
          example: '2000-01-01'
    Vino:
      type: object
      properties:
        id:
          type: integer
          format: int64
          example: 10
        nombre:
          type: string
          example: Vino A
        fechaAdicion:
          type: string
          format: date-time
          example: '2024-01-01'
        uva:
          type: string
          example: Tempranillo
        tipo:
          type: string
          example: Tinto
        origen:
          type: string
          example: España
        ano:
          type: integer
          format: int64
          example: 2001
    Puntuacion:
      type: object
      properties:
        idVino:
          type: integer
          format: int64
          example: 10
        puntuacion:
          type: number
          example: 9.5
