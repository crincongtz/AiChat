# Configuración de OpenAI API

Para que la aplicación funcione correctamente, necesitas configurar tu API key de OpenAI.

## Pasos para configurar la API key:

1. **Obtén tu API key de OpenAI:**
   - Ve a [https://platform.openai.com/api-keys](https://platform.openai.com/api-keys)
   - Inicia sesión o crea una cuenta
   - Crea una nueva API key

2. **Configura la API key en el proyecto:**
   - Abre el archivo `local.properties` en la raíz del proyecto
   - Agrega la siguiente línea al final del archivo:
   ```
   OPENAI_API_KEY=tu-api-key-aqui
   ```
   - Reemplaza `tu-api-key-aqui` con tu API key real de OpenAI

3. **Sincroniza el proyecto:**
   - En Android Studio, haz clic en "Sync Now" o sincroniza manualmente
   - Compila el proyecto

## Ejemplo de local.properties:

```properties
sdk.dir=/Users/tu-usuario/Library/Android/sdk
OPENAI_API_KEY=sk-proj-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
```

## Importante:

- **NO** compartas tu API key públicamente
- **NO** hagas commit del archivo `local.properties` al repositorio
- El archivo `local.properties` está en `.gitignore` por defecto

## Modelos disponibles:

Por defecto, la app usa el modelo `gpt-3.5-turbo`, pero puedes cambiarlo en `ChatRepository.kt` a otros modelos como:
- `gpt-4`
- `gpt-4-turbo-preview`
- Otros modelos disponibles en la documentación de OpenAI

## Costos:

Ten en cuenta que usar la API de OpenAI tiene costos asociados. Revisa los precios en:
[https://openai.com/pricing](https://openai.com/pricing)
