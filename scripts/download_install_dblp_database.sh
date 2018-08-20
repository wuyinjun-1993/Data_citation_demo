#!/bin/bash
trap "exit" INT


mkdir -p ../database_dump
wget --no-check-certificate --content-disposition https://github.com/thuwuyinjun/Data_citation_demo/releases/download/v1/DBLP-NSF-Postgresql.sql.zip --directory-prefix=../database_dump/
unzip ../database_dump/DBLP-NSF-Postgresql.sql.zip -d ../database_dump/


dropdb --if-exists dblp1 -U $1
dropdb --if-exists dblp2 -U $1
createdb dblp1 -U $1
createdb dblp2 -U $1
psql dblp1 < ../database_dump/dblp.sql -U $1
psql dblp2 < ../database_dump/dblp.sql -U $1
