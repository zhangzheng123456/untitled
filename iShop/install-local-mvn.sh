#!/usr/bin/env bash
mvn install:install-file -DgroupId=com.bizvane.sun -DartifactId=client_app -Dversion=1.0 -Dfile=lib/client_app-1.0.jar -Dpackaging=jar -DgeneratePom=true