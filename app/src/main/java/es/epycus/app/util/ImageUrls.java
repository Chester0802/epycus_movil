package es.epycus.app.util;

import es.epycus.app.BuildConfig;

/** Utilidades para resolver URLs de imágenes que el backend devuelve como rutas relativas. */
public final class ImageUrls {

    private ImageUrls() {}

    /**
     * Convierte una ruta relativa del servidor (p. ej. {@code /img/personajes/...}) en una
     * URL absoluta usando {@code API_BASE_URL}. Si ya es absoluta (http/https) la devuelve igual.
     * Sin esto, Glide interpreta una ruta que empieza por "/" como un archivo local y falla
     * con FileNotFoundException.
     */
    public static String absolute(String path) {
        if (path == null || path.isEmpty()) {
            return path;
        }
        if (path.startsWith("http://") || path.startsWith("https://")) {
            return path;
        }
        String base = BuildConfig.API_BASE_URL;
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return base + path;
    }
}
