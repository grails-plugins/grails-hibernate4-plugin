#!/bin/bash
# exports a patch from grails-hibernate-plugin and applies it to this plug
#
# by default it will import the last commit (HEAD~1)
#
# you can pass a git range as first parameter
# there is "--cherry-pick" option for cherry picking a single commit
#
# if the patching fails, you can do :
# patch -f --merge -p1 < .git/rebase-apply/patch
# git status -s |grep "^ M"
# - edit the merged files
# - do "git add" for the resolved files (do not commit files, just stage them to index)
# - continue merging with "git am --continue" (will commit changes)
RANGE="$1"
if [ "$RANGE" == "--cherry-pick" ]; then
    shift
    if [ -n "$1" ]; then
        RANGE="${1}~..${1}"
    fi
elif [ -z "$RANGE" ]; then
    RANGE="HEAD~1"
fi
(cd ../grails-hibernate-plugin; git format-patch -k --stdout -U5 -p --no-signature "$RANGE") | git am -k --ignore-space-change --ignore-whitespace --keep-cr -C2
