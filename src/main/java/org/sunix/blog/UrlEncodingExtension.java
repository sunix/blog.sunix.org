package org.sunix.blog;

import io.quarkus.qute.TemplateExtension;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@TemplateExtension
public class UrlEncodingExtension {

    public static String encodeForHash(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }
}
