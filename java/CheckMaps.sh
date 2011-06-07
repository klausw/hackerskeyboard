#!/bin/bash

Res=res/
Alt=donottranslate-altchars.xml
Map=donottranslate-keymap.xml

for Dir in res/values-*
do
  [ -f $Dir/$Map -o -f $Dir/$Alt ] || continue
  Args="$Res/values/$Alt"
  [ -f $Dir/$Alt ] && Args="$Args $Dir/$Alt"
  Args="$Args $Res/values/$Map"
  [ -f $Dir/$Map ] && Args="$Args $Dir/$Map"
  echo -n "$Dir: "
  perl CheckMap.pl $Args
done
