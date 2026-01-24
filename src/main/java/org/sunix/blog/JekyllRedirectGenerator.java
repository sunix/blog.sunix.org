package org.sunix.blog;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Generates redirect HTML pages for old Jekyll URLs.
 * 
 * This utility creates static HTML redirect files based on the posts
 * in the content/posts directory. The redirects use JavaScript for
 * instant client-side redirection.
 * 
 * Usage: java org.sunix.blog.JekyllRedirectGenerator
 */
public class JekyllRedirectGenerator {

    // Configuration constants
    private static final String POSTS_DIR = "content/posts";
    private static final String OUTPUT_DIR = "target/roq";
    private static final int MAX_SIGNIFICANT_WORDS_IN_SLUG = 6;

    private static final String REDIRECT_HTML_TEMPLATE = """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="utf-8">
            <title>Redirecting...</title>
            <meta http-equiv="refresh" content="0; url=%s">
            <link rel="canonical" href="%s">
            <script>
                // Instant redirect using JavaScript
                window.location.replace("%s");
            </script>
        </head>
        <body>
            <p>This page has moved to <a href="%s">%s</a>.</p>
            <p>If you are not redirected automatically, please click the link above.</p>
        </body>
        </html>
        """;

    /**
     * Post information extracted from directory names
     */
    private static class PostInfo {
        String year;
        String month;
        String day;
        String slug;
        String fullSlug;
        
        PostInfo(String year, String month, String day, String slug, String fullSlug) {
            this.year = year;
            this.month = month;
            this.day = day;
            this.slug = slug;
            this.fullSlug = fullSlug;
        }
    }

    public static void main(String[] args) {
        try {
            new JekyllRedirectGenerator().generateRedirects();
        } catch (Exception e) {
            System.err.println("❌ Error generating redirects: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Main method to generate all redirect files.
     */
    public void generateRedirects() throws IOException {
        System.out.println("🔄 Generating Jekyll→Roq redirects...");
        
        Path postsDir = Paths.get(POSTS_DIR);
        if (!Files.exists(postsDir)) {
            System.out.println("⚠️  Posts directory not found, skipping redirect generation");
            return;
        }

        Path outputDir = Paths.get(OUTPUT_DIR);
        if (!Files.exists(outputDir)) {
            System.out.println("⚠️  Roq output directory not found, skipping redirect generation");
            return;
        }

        List<PostInfo> posts = scanPosts(postsDir);
        int redirectCount = 0;

        for (PostInfo post : posts) {
            redirectCount += generateRedirectsForPost(outputDir, post);
        }

        System.out.println("✅ Generated " + redirectCount + " redirect files for " + posts.size() + " posts");
    }

    /**
     * Scans the posts directory and extracts post information.
     */
    private List<PostInfo> scanPosts(Path postsDir) throws IOException {
        List<PostInfo> posts = new ArrayList<>();
        
        try (Stream<Path> paths = Files.list(postsDir)) {
            for (Path postDir : paths.toList()) {
                if (!Files.isDirectory(postDir)) {
                    continue;
                }
                
                String dirName = postDir.getFileName().toString();
                
                // Parse directory name: YYYY-MM-DD-slug or YYYY-MM-DD-slug.md
                if (!dirName.matches("\\d{4}-\\d{2}-\\d{2}-.+")) {
                    continue;
                }
                
                String[] parts = dirName.split("-", 4);
                if (parts.length < 4) {
                    continue;
                }
                
                String year = parts[0];
                String month = parts[1];
                String day = parts[2];
                String fullSlug = parts[3];
                
                // Remove .md extension if present (for backward compatibility)
                if (fullSlug.endsWith(".md")) {
                    fullSlug = fullSlug.substring(0, fullSlug.length() - 3);
                }
                
                // Extract short slug for old Jekyll URLs
                String shortSlug = extractShortSlug(fullSlug);
                
                posts.add(new PostInfo(year, month, day, shortSlug, fullSlug));
            }
        }
        
        return posts;
    }

    /**
     * Generates redirect files for a single post.
     * Creates redirects for multiple category paths that Jekyll might have used.
     */
    private int generateRedirectsForPost(Path outputDir, PostInfo post) throws IOException {
        String newUrl = "/posts/" + post.fullSlug + "/";
        
        // Old Jekyll URL patterns used different category names
        String[] categories = {"howto", "articles", "tutorial", "blog", "tech", "post"};
        
        int count = 0;
        for (String category : categories) {
            String oldPath = String.format("articles/%s/%s/%s/%s/%s.html",
                category, post.year, post.month, post.day, post.slug);
            
            createRedirectFile(outputDir, oldPath, newUrl);
            count++;
        }
        
        return count;
    }

    /**
     * Extracts a shortened slug matching Jekyll's typical URL patterns.
     * Takes the first MAX_SIGNIFICANT_WORDS_IN_SLUG significant words from the full slug.
     */
    private String extractShortSlug(String fullSlug) {
        String[] words = fullSlug.split("-");
        StringBuilder result = new StringBuilder();
        int significantWords = 0;
        
        for (String word : words) {
            if (significantWords >= MAX_SIGNIFICANT_WORDS_IN_SLUG) {
                break;
            }
            
            if (result.length() > 0) {
                result.append("-");
            }
            result.append(word);
            
            // Count words longer than 2 characters as "significant"
            if (word.length() > 2) {
                significantWords++;
            }
        }
        
        return result.toString();
    }

    /**
     * Creates a redirect HTML file with JavaScript-based redirection.
     */
    private void createRedirectFile(Path outputDir, String oldPath, String newUrl) throws IOException {
        Path redirectFile = outputDir.resolve(oldPath);
        
        // Create parent directories
        Files.createDirectories(redirectFile.getParent());
        
        // Generate HTML with meta refresh and JavaScript redirect
        String html = String.format(REDIRECT_HTML_TEMPLATE,
            newUrl, newUrl, newUrl, newUrl, newUrl);
        
        Files.writeString(redirectFile, html);
        
        System.out.println("  → /" + oldPath + " → " + newUrl);
    }
}
