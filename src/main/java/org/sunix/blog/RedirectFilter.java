package org.sunix.blog;

import io.quarkus.runtime.StartupEvent;
import io.vertx.core.Handler;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Redirect filter for old Jekyll article URLs to new Quarkus Roq post URLs.
 * 
 * Old Jekyll format: /articles/{category}/{year}/{month}/{day}/{slug}.html
 * New Roq format: /posts/{full-post-slug}/
 * 
 * Example:
 * /articles/howto/2026/01/11/feeling-powerful-with-just-a-browser.html
 * -> /posts/feeling-powerful-with-just-a-browser-working-around-a-broken-tennis-booking-system/
 */
@ApplicationScoped
public class RedirectFilter {

    // Pattern to match old Jekyll article URLs
    private static final Pattern JEKYLL_URL_PATTERN = 
        Pattern.compile("^/articles/[^/]+/(\\d{4})/(\\d{2})/(\\d{2})/([^/]+)\\.html$");

    @Inject
    Router router;

    void setupRoutes(@Observes StartupEvent event) {
        // Add redirect route with high priority (runs first)
        router.route().order(-1).handler(new Handler<RoutingContext>() {
            @Override
            public void handle(RoutingContext rc) {
                String path = rc.request().path();
                
                Matcher matcher = JEKYLL_URL_PATTERN.matcher(path);
                if (matcher.matches()) {
                    String year = matcher.group(1);
                    String month = matcher.group(2);
                    String day = matcher.group(3);
                    String slug = matcher.group(4);
                    
                    // Find the matching post directory
                    String newPostUrl = findMatchingPost(year, month, day, slug);
                    
                    if (newPostUrl != null) {
                        // Perform 301 permanent redirect
                        rc.response()
                            .setStatusCode(301)
                            .putHeader("Location", newPostUrl)
                            .end();
                        return;
                    }
                }
                
                // Continue to next handler if not a redirect
                rc.next();
            }
        });
    }

    /**
     * Finds the matching post directory based on the date and slug from the old URL.
     * 
     * @param year The year from the old URL
     * @param month The month from the old URL
     * @param day The day from the old URL
     * @param slug The slug from the old URL
     * @return The new post URL path, or null if not found
     */
    private String findMatchingPost(String year, String month, String day, String slug) {
        try {
            Path postsDir = Paths.get("content/posts");
            if (!Files.exists(postsDir)) {
                return null;
            }

            // Expected directory format: YYYY-MM-DD-slug.md
            String datePrefix = String.format("%s-%s-%s-", year, month, day);
            
            try (Stream<Path> paths = Files.list(postsDir)) {
                Path matchingDir = paths
                    .filter(p -> {
                        String dirName = p.getFileName().toString();
                        // Check if directory starts with the date and contains similar slug words
                        if (!dirName.startsWith(datePrefix) || !Files.isDirectory(p)) {
                            return false;
                        }
                        
                        // Extract the slug part from directory name (remove date prefix and .md suffix)
                        String dirSlug = dirName.substring(datePrefix.length());
                        if (dirSlug.endsWith(".md")) {
                            dirSlug = dirSlug.substring(0, dirSlug.length() - 3);
                        }
                        
                        // Check if the slug from URL matches the beginning of the directory slug
                        // or if they share significant words
                        return dirSlug.startsWith(slug) || slugsMatch(slug, dirSlug);
                    })
                    .findFirst()
                    .orElse(null);
                
                if (matchingDir != null) {
                    // Extract the slug from the directory name
                    String dirName = matchingDir.getFileName().toString();
                    String postSlug = dirName.substring(datePrefix.length());
                    if (postSlug.endsWith(".md")) {
                        postSlug = postSlug.substring(0, postSlug.length() - 3);
                    }
                    
                    // Return the new URL format
                    return "/posts/" + postSlug + "/";
                }
            }
        } catch (IOException e) {
            // Log error but don't fail the request
            System.err.println("Error finding matching post: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Checks if two slugs match by comparing their significant words.
     * 
     * @param slug1 First slug
     * @param slug2 Second slug
     * @return true if slugs match, false otherwise
     */
    private boolean slugsMatch(String slug1, String slug2) {
        // Split slugs into words
        String[] words1 = slug1.toLowerCase().split("-");
        String[] words2 = slug2.toLowerCase().split("-");
        
        // Count matching words (minimum 2 to avoid false positives)
        int matchCount = 0;
        for (String word1 : words1) {
            if (word1.length() <= 2) {
                continue; // Skip very short words
            }
            for (String word2 : words2) {
                if (word1.equals(word2) || 
                    (word1.length() >= 4 && word2.startsWith(word1)) ||
                    (word2.length() >= 4 && word1.startsWith(word2))) {
                    matchCount++;
                    break;
                }
            }
        }
        
        return matchCount >= 2;
    }
}
