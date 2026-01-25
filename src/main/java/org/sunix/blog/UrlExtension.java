package org.sunix.blog;

import io.quarkus.qute.TemplateExtension;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Qute Template Extension for URL manipulation and encoding.
 * Provides methods to properly encode URLs for use in query parameters
 * and construct full URLs with proper slash handling.
 */
public class UrlExtension {

    /**
     * URL-encodes a string for safe use in query parameters.
     * Namespace method for URL encoding.
     * 
     * @param value The value to encode
     * @return The URL-encoded string
     */
    @TemplateExtension(namespace = "url")
    public static String encode(Object value) {
        if (value == null) {
            return "";
        }
        return URLEncoder.encode(value.toString(), StandardCharsets.UTF_8);
    }

    /**
     * Constructs a full URL by concatenating base URL and path,
     * ensuring there's exactly one slash between them.
     * Static namespace method for URL construction.
     * 
     * @param baseUrl The base URL object
     * @param path The path
     * @return The properly constructed full URL
     */
    @TemplateExtension(namespace = "url")
    public static String fullUrl(Object baseUrl, Object path) {
        String baseUrlStr = baseUrl != null ? baseUrl.toString() : "";
        String pathStr = path != null ? path.toString() : "";
        
        // Remove trailing slash from baseUrl
        baseUrlStr = baseUrlStr.replaceAll("/+$", "");
        
        // Ensure path starts with a slash
        if (!pathStr.isEmpty() && !pathStr.startsWith("/")) {
            pathStr = "/" + pathStr;
        }
        
        return baseUrlStr + pathStr;
    }
}
