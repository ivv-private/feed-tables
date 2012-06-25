#!/bin/bash

WAR=war.appengine.tmp
[[ -d $WAR ]] || mkdir $WAR
rm -fr $WAR/*

( cd $WAR; jar -xf ../feed-tables.war )

(appcfg.sh update $WAR;  rm -fr "C:/Documents and Settings/ignatyvj/appcfg*")
# dev_appserver.sh -p 80 --disable_update_check $WAR

