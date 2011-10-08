#!/bin/bash

Ver="$(Hg-ident) $(date +%Y-%m-%d)"
exec > res/values/auto-version.xml

cat <<'EOF'
<?xml version="1.0" encoding="utf-8"?>
<!-- Auto-generated file, do not edit -->
<resources xmlns:xliff="urn:oasis:names:tc:xliff:document:1.2">
EOF
echo '   <string name="auto_version">'"$Ver"'</string>'
echo '</resources>'
