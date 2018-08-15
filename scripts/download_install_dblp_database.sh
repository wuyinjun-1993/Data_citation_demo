#!/bin/bash
trap "exit" INT


mkdir -p ../database_dump
wget --no-check-certificate --content-disposition https://github.com/thuwuyinjun/Data_citation_demo/releases/download/v1/DBLP-NSF-Postgresql.sql.zip --directory-prefix=../database_dump/
unzip ../database_dump/DBLP-NSF-Postgresql.sql.zip -d ../database_dump/


dropdb --if-exists dblp1
dropdb --if-exists dblp2
createdb dblp1
createdb dblp2
psql dblp1 < ../database_dump/dblp.sql
psql dblp2 < ../database_dump/dblp.sql
