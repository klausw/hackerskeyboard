#!/bin/bash

Res=res/
Alt=donottranslate-altchars.xml
Map=donottranslate-keymap.xml

Out=assets/kbd/
mkdir -p "$Out"

for Dir in res/values res/values-*
do
  [ -f $Dir/$Map ] || continue # -o -f $Dir/$Alt ] || continue
  Args="$Res/values/$Alt"
  [ -f $Dir/$Alt ] && Args="$Args $Dir/$Alt"
  Args="$Args $Res/values/$Map"
  [ -f $Dir/$Map ] && Args="$Args $Dir/$Map"
  if [ -n "$CONVERT_MAPS" ]; then
    Loc=$(echo "$Dir" | sed 's/res.values-*//; s/\/$//; s/^$/en/')
    perl CheckMap.pl -c $Args > "$Out/map-full-$Loc.txt"
  else
    echo >&2 -n "$Dir: "
    perl CheckMap.pl $Args
  fi
done
