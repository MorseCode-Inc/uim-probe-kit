#!/bin/bash

absdir() {
	if [ ! -d "$1" ]
	then
		echo "$1 must be a directory."
		exit 1
	fi
	cd "$1" >/dev/null 2>&1
	pwd
	cd - >/dev/null 2>&1
	
}

JARF="$1"
if [ -z "$1" ]
then
	JARF="mc-uimpfwk.jar"
fi

if [ -n "${JARF%%*.jar}" ]
then
	JARF="$JARF.jar"
fi

cd "${0%/*}"/build/classes
jar cvf ../../$JARF * >/dev/null
echo $(absdir ../..)/$JARF


exit
