#!/bin/bash
trap "exit" INT


mkdir -p ../database_dump
wget http://www.guidetopharmacology.org/DATA/public_iuphardb_v2018.3.zip --directory-prefix=../database_dump/
unzip ../database_dump/public_iuphardb_v2018.3.zip -d ../database_dump/


dropdb --if-exists iuphar1
dropdb --if-exists iuphar2
createdb iuphar1
createdb iuphar2
psql iuphar1 < ../database_dump/public_iuphardb_v2018.3.dmp
psql iuphar2 < ../database_dump/public_iuphardb_v2018.3.dmp
