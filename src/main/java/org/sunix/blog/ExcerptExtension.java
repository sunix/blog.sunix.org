package org.sunix.blog;

import io.quarkus.qute.TemplateExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;

/**
 * Qute Template Extension to extract excerpt from blog posts.
 * This extension adds an excerpt() method to blog post objects
 * that extracts and formats content before the <!-- more --> marker.
 */
@TemplateExtension
public class ExcerptExtension {

    private static final Pattern MORE_MARKER = Pattern.compile("<!--\\s*more\\s*-->", Pattern.CASE_INSENSITIVE);
    private static final Pattern HTML_TAG_PATTERN = Pattern.compile("<[^>]+>");
    private static final int MAX_EXCERPT_LENGTH = 500;

    /**
     * Extracts the excerpt from a blog post.
     * 
     * @param post The post object (will have url property)
     * @return The excerpt text, or empty string if no excerpt is found
     */
    public static String excerpt(Object post) {
        try {
            // Try to get the URL from the post object
            String url = getUrl(post);
            if (url == null || url.isEmpty()) {
                return "";
            }

            // Convert URL to file path
            // URLs are like "/2024/07/09/email-migration/" 
            // Files are in content/posts/YYYY-MM-DD-slug/index.md
            Path sourcePath = urlToFilePath(url);
            if (sourcePath == null || !Files.exists(sourcePath)) {
                return "";
            }

            // Read the markdown content
            String content = Files.readString(sourcePath);
            
            // Extract content before <!-- more --> marker
            String excerptMarkdown = extractBeforeMoreMarker(content);
            if (excerptMarkdown.isEmpty()) {
                return "";
            }

            // Convert markdown to HTML
            String html = markdownToHtml(excerptMarkdown);
            
            // Strip HTML tags to get plain text
            String plainText = stripHtmlTags(html);
            
            // Trim and truncate if needed
            plainText = plainText.trim();
            if (plainText.length() > MAX_EXCERPT_LENGTH) {
                plainText = plainText.substring(0, MAX_EXCERPT_LENGTH);
                // Find last space to avoid cutting words
                int lastSpace = plainText.lastIndexOf(' ');
                if (lastSpace > 0) {
                    plainText = plainText.substring(0, lastSpace);
                }
                plainText += "...";
            }
            
            return plainText;
        } catch (Exception e) {
            // Log error and return empty string
            System.err.println("Error reading excerpt: " + e.getMessage());
            return "";
        }
    }

    /**
     * Gets the URL from a post object using reflection.
     */
    private static String getUrl(Object post) {
        try {
            java.lang.reflect.Method method = post.getClass().getMethod("url");
            Object result = method.invoke(post);
            return result != null ? result.toString() : "";
        } catch (Exception e) {
            System.err.println("Error getting URL from post: " + e.getMessage());
            return "";
        }
    }

    /**
     * Converts a post URL to the file path.
     */
    private static Path urlToFilePath(String url) {
        try {
            // Remove leading and trailing slashes
            url = url.replaceAll("^/+|/+$", "");
            
            // Try to find the file in content/posts
            Path contentDir = Paths.get("content/posts");
            if (!Files.exists(contentDir)) {
                return null;
            }

            // Look for directories that match the URL pattern
            // The directory name format is YYYY-MM-DD-slug
            String[] parts = url.split("/");
            if (parts.length < 4) {
                return null;
            }
            
            // URL format: year/month/day/slug
            String year = parts[0];
            String month = parts[1];
            String day = parts[2];
            String slug = parts[3];
            
            // Directory format: YYYY-MM-DD-slug
            String dirPrefix = year + "-" + month + "-" + day + "-" + slug;
            
            // Find matching directory
            try (var stream = Files.list(contentDir)) {
                var matchingPath = stream
                    .filter(p -> Files.isDirectory(p) && p.getFileName().toString().startsWith(dirPrefix))
                    .findFirst();
                
                if (matchingPath.isPresent()) {
                    Path indexMd = matchingPath.get().resolve("index.md");
                    if (Files.exists(indexMd)) {
                        return indexMd;
                    }
                }
            }
            
            return null;
        } catch (Exception e) {
            System.err.println("Error converting URL to file path: " + e.getMessage());
            return null;
        }
    }

    /**
     * Extracts content before the <!-- more --> marker.
     */
    private static String extractBeforeMoreMarker(String content) {
        // Remove YAML frontmatter first
        content = removeFrontmatter(content);
        
        Matcher matcher = MORE_MARKER.matcher(content);
        if (matcher.find()) {
            return content.substring(0, matcher.start()).trim();
        }
        return "";
    }

    /**
     * Removes YAML frontmatter from the content.
     */
    private static String removeFrontmatter(String content) {
        if (content.startsWith("---")) {
            int endOfFrontmatter = content.indexOf("---", 3);
            if (endOfFrontmatter > 0) {
                return content.substring(endOfFrontmatter + 3).trim();
            }
        }
        return content;
    }

    /**
     * Converts markdown to HTML using Flexmark.
     */
    private static String markdownToHtml(String markdown) {
        MutableDataSet options = new MutableDataSet();
        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();
        
        Node document = parser.parse(markdown);
        return renderer.render(document);
    }

    /**
     * Strips HTML tags from the text.
     */
    private static String stripHtmlTags(String html) {
        // Replace common HTML entities
        String text = html.replace("&nbsp;", " ")
                         .replace("&lt;", "<")
                         .replace("&gt;", ">")
                         .replace("&amp;", "&")
                         .replace("&quot;", "\"");
        
        // Remove HTML tags
        text = HTML_TAG_PATTERN.matcher(text).replaceAll("");
        
        // Normalize whitespace
        text = text.replaceAll("\\s+", " ");
        
        return text.trim();
    }
}
