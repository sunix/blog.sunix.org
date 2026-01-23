package org.sunix.blog;

import io.quarkus.qute.TemplateExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.node.Node;

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
    private static final int MIN_MATCHING_WORDS = 2;  // Reduced from 3 to 2 to handle cases like "why-we-estimate"
    
    // Common stop words to exclude from matching
    private static final java.util.Set<String> STOP_WORDS = java.util.Set.of(
        "the", "and", "with", "for", "from", "are", "was", "were", "been",
        "have", "has", "had", "but", "not", "you", "all", "can", "her",
        "him", "his", "how", "its", "our", "out", "she", "who", "boy",
        "did", "get", "may", "now", "old", "run", "too", "any", "day",
        "guy", "kid", "let", "say", "use"
    );

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
            // Silently fail and return empty string
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
            final String finalUrl = url; // Make final for use in lambda
            
            // Try to find the file in content/posts
            Path contentDir = Paths.get("content/posts");
            if (!Files.exists(contentDir)) {
                return null;
            }

            // URL format is typically: posts/long-slug-name
            // Directory format is: YYYY-MM-DD-shorter-slug.md
            String[] parts = finalUrl.split("/");
            if (parts.length < 2) {
                return null;
            }
            
            // Get the slug (last part of URL)
            String slug = parts[parts.length - 1];
            
            // Find matching directory - use fuzzy matching
            // Extract key words from slug (remove common words)
            String[] slugWords = slug.toLowerCase()
                .replaceAll("[^a-z0-9-]", "")
                .split("-");
            
            try (var stream = Files.list(contentDir)) {
                var matchingPath = stream
                    .filter(p -> {
                        String dirName = p.getFileName().toString().toLowerCase();
                        if (!Files.isDirectory(p) || !dirName.endsWith(".md")) {
                            return false;
                        }
                        
                        // Remove date prefix and .md suffix
                        dirName = dirName.replaceAll("^\\d{4}-\\d{2}-\\d{2}-", "").replace(".md", "");
                        
                        // Count matching words (must have at least MIN_MATCHING_WORDS)
                        int matchCount = 0;
                        for (String word : slugWords) {
                            // Skip short or stop words
                            if (word.length() <= 2 || STOP_WORDS.contains(word)) {
                                continue;
                            }
                            // Check if word is in directory name OR if directory contains a word that starts with this word
                            // (e.g., "estimate" in dir matches "estimation" in slug via prefix matching)
                            if (dirName.contains(word)) {
                                matchCount++;
                            } else if (word.length() >= 4) {
                                // Check if any word in the directory starts with this slug word or vice versa
                                String[] dirWords = dirName.split("-");
                                for (String dirWord : dirWords) {
                                    if (dirWord.length() >= 4 && (dirWord.startsWith(word) || word.startsWith(dirWord))) {
                                        matchCount++;
                                        break;
                                    }
                                }
                            }
                        }
                        
                        return matchCount >= Math.min(MIN_MATCHING_WORDS, slugWords.length / 2);
                    })
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
     * Converts markdown to HTML using CommonMark.
     */
    private static String markdownToHtml(String markdown) {
        Parser parser = Parser.builder().build();
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        
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
