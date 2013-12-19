#!/bin/bash

OUT=res/values/auto-version.xml

if grep -q custom $OUT; then
  exit 0
fi

# Use the fancy id generator if present, fall back to plain "hg id" if not.
Id="$(Hg-ident)" 2>/dev/null
[ -z "$Id" ] && Id="$(hg id)"

Ver="$Id $(date +%Y-%m-%d)"

# Create the auto-version file with this version string
exec > $OUT

cat <<'EOF'
<?xml version="1.0" encoding="utf-8"?>
<!-- Auto-generated file, do not edit -->
<resources xmlns:xliff="urn:oasis:names:tc:xliff:document:1.2">
EOF
echo '   <string name="auto_version">'"$Ver"'</string>'
echo '</resources>'
