USER="admin@readynamic.com"
PASS="password"
WEBSITE="http://rdqa2.datalogics.com"

TEST_FILE_PATH="/Volumes/qa/Test_Files/script-files-rd/"

# Fetch admin auth token
echo "== Logging in =="
echo "User:      " $USER
echo "Password:  " $PASS
AUTH=`curl -sSX POST --header "Content-Type:application/json" -d '{"login" : "'$USER'", "password" : "'$PASS'"}' $WEBSITE/login.json | grep -o '".*"' | sed 's/"//g' | sed 's/auth_token://g'`
echo "Auth token:" $AUTH
echo

for file in $TEST_FILE_PATH*
do
	fileName=$(basename $file)
	fileName=$(echo $fileName | sed 's/\.[^.]*$//')
	fileName=$fileName"_"$(date +%b%d-%H%M)
	echo "Uploading: "$fileName
	outEpub=`curl -sSX POST -F 'book[name]='$fileName -F 'book[backing_file]=@'$file -b 'auth_token='$AUTH $WEBSITE/portal/api/books`
	echo $outEpub
	idEpub=`echo $outEpub | sed -e 's/.*id":\(.*\),"name.*/\1/'`
	idEpubAnnot=`echo $outEpub | sed -e 's/.*default_annotation_set_id":\(.*\),"publisher_metadata.*/\1/'`
	echo "id: "$idEpub
	echo "default annotation set: "$idEpubAnnot
	echo
done
