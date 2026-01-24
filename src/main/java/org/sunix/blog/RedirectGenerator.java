package org.sunix.blog;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * Utility to generate HTML redirect files for old Jekyll article URLs.
 * 
 * This is run after the Roq build to create redirect HTML files that use
 * meta refresh to redirect from old Jekyll URLs to new Roq URLs.
 * 
 * Usage: java org.sunix.blog.RedirectGenerator
 */
public class RedirectGenerator {

    private static final String REDIRECT_HTML_TEMPLATE = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="utf-8">
            <title>Redirecting...</title>
            <meta http-equiv="refresh" content="0; url=%s">
            <link rel="canonical" href="%s">
        </head>
        <body>
            <p>This page has moved to <a href="%s">%s</a>.</p>
            <script>window.location.replace("%s");</script>
        </body>
        </html>
        """;

    public static void main(String[] args) {
        try {
            System.out.println("🔄 Generating redirects for old Jekyll URLs...");
            
            Path postsDir = Paths.get("content/posts");
            if (!Files.exists(postsDir)) {
                System.out.println("⚠️  Posts directory not found at: " + postsDir.toAbsolutePath());
                return;
            }

            Path outputDir = Paths.get("target/roq");
            if (!Files.exists(outputDir)) {
                System.out.println("⚠️  Output directory not found at: " + outputDir.toAbsolutePath());
                System.out.println("    Please run 'mvn quarkus:build' first");
                return;
            }

            int redirectCount = 0;
            
            // Iterate through all post directories
            try (Stream<Path> paths = Files.list(postsDir)) {
                for (Path postDir : paths.toList()) {
                    if (!Files.isDirectory(postDir)) {
                        continue;
                    }
                    
                    String dirName = postDir.getFileName().toString();
                    
                    // Parse the directory name: YYYY-MM-DD-slug.md
                    if (!dirName.matches("\\d{4}-\\d{2}-\\d{2}-.+\\.md")) {
                        continue;
                    }
                    
                    // Extract date parts
                    String[] parts = dirName.split("-", 4);
                    if (parts.length < 4) {
                        continue;
                    }
                    
                    String year = parts[0];
                    String month = parts[1];
                    String day = parts[2];
                    String slugWithExt = parts[3];
                    String slug = slugWithExt.endsWith(".md") 
                        ? slugWithExt.substring(0, slugWithExt.length() - 3) 
                        : slugWithExt;
                    
                    // Extract just the first part of the slug (before any long additions)
                    // For matching Jekyll's shorter slugs
                    String shortSlug = extractShortSlug(slug);
                    
                    // New URL format
                    String newUrl = "/posts/" + slug + "/";
                    
                    // Old Jekyll URL format: /articles/{category}/{year}/{month}/{day}/{slug}.html
                    // We'll create redirects for common category patterns
                    String[] categories = {"howto", "articles", "tutorial", "blog", "tech"};
                    
                    for (String category : categories) {
                        String oldPath = String.format("articles/%s/%s/%s/%s/%s.html", 
                            category, year, month, day, shortSlug);
                        
                        createRedirectFile(outputDir, oldPath, newUrl);
                        redirectCount++;
                    }
                }
            }
            
            System.out.println("✅ Generated " + redirectCount + " redirect files");
            
        } catch (Exception e) {
            System.err.println("❌ Error generating redirects: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Extracts a short slug from the full slug by taking significant words.
     * This helps match Jekyll's shorter slug format.
     * 
     * For example: "feeling-powerful-with-just-a-browser-working-around-a-broken-tennis-booking-system"
     * becomes: "feeling-powerful-with-just-a-browser"
     */
    private static String extractShortSlug(String fullSlug) {
        // Split by hyphens and take first few significant words
        String[] words = fullSlug.split("-");
        StringBuilder shortSlug = new StringBuilder();
        int wordCount = 0;
        int maxWords = 6; // Take first 6 words to match typical Jekyll slugs
        
        for (String word : words) {
            // Skip very short words (articles, prepositions)
            if (word.length() > 2 && wordCount < maxWords) {
                if (shortSlug.length() > 0) {
                    shortSlug.append("-");
                }
                shortSlug.append(word);
                wordCount++;
            } else if (word.length() <= 2 && wordCount < maxWords) {
                // Include short words but don't count them toward the limit
                if (shortSlug.length() > 0) {
                    shortSlug.append("-");
                }
                shortSlug.append(word);
            }
        }
        
        return shortSlug.toString();
    }

    /**
     * Creates a redirect HTML file at the specified path.
     */
    private static void createRedirectFile(Path outputDir, String oldPath, String newUrl) throws IOException {
        Path redirectFile = outputDir.resolve(oldPath);
        
        // Create parent directories if they don't exist
        Files.createDirectories(redirectFile.getParent());
        
        // Generate the HTML redirect content
        String html = String.format(REDIRECT_HTML_TEMPLATE, 
            newUrl, newUrl, newUrl, newUrl, newUrl);
        
        // Write the redirect file
        Files.writeString(redirectFile, html);
        
        System.out.println("  → " + oldPath + " → " + newUrl);
    }
}
