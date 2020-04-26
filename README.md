# morph-dict-editor
http://morph.boriselec.com/

docker run -d --name morph-dict-editor-db -e MYSQL_ROOT_PASSWORD=pass boriselec/morph-dict-editor-db
docker run -d --name morph-dict-editor -p 80:8080 -e MORPH_FILE_ROOT=/tmp/ -e MORPH_DB_URL=jdbc:mariadb://172.17.0.2:3306/morph -e MORPH_DB_USERNAME=root -e MORPH_DB_PASSWORD=pass -e MORPH_FILELOADER_PERIOD_MINUTES=60 -e MORPH_DBLOADER_PERIOD_MINUTES=5 -e MORPH_REPO_PERIOD_MINUTES=1 boriselec/morph-dict-editor
