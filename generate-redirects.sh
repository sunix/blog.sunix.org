#!/bin/bash
#
# Generate HTML redirect files for old Jekyll article URLs
# This script should be run after the Roq site has been generated
#

set -euo pipefail

echo "🔄 Generating redirects for old Jekyll URLs..."

# Compile and run the redirect generator
export JAVA_HOME=${JAVA_HOME:-$(dirname $(dirname $(readlink -f $(which java))))}
export PATH=$JAVA_HOME/bin:$PATH

# Run the RedirectGenerator class
java -cp "target/classes:$(find target/quarkus-app/lib -name '*.jar' | tr '\n' ':')" \
    org.sunix.blog.RedirectGenerator

echo "✅ Redirect generation complete!"
