USER="admin@readynamic.com"
PASS="password"
WEBSITE="http://rdqa2.datalogics.com"

TEST_FILE_PATH="/Volumes/qa/Test_files/script-files/"

TEST_EPUB2_TITLE="TESTsign"
TEST_EPUB3_TITLE="TESTmoby"
TEST_PDF_TITLE="TESTwar"

TEST_EPUB2="TheSignOfTheFour.epub"
TEST_EPUB3="TheSignOfTheFour.epub"
TEST_PDF="TheWarOfTheWorlds.pdf"

TEST_STUDENT="testStu1"
TEST_STUDENT_PASS="password"

TEST_PUBLISHER="testPub1"
TEST_PUBLISHER_PASS="password"

# Fetch admin auth token
echo "== Logging in =="
echo "User:      " $USER
echo "Password:  " $PASS
AUTH=`curl -sX POST --header "Content-Type:application/json" -d '{"login" : "'$USER'", "password" : "'$PASS'"}' $WEBSITE/login.json | grep -o '".*"' | sed 's/"//g' | sed 's/auth_token://g'`
echo "Auth token:" $AUTH
echo

# Upload epub 2
echo "Uploading epub 2..."
curl -X POST -F 'book[name]='$TEST_EPUB2_TITLE -F 'book[backing_file]=@'$TEST_FILE_PATH$TEST_EPUB2 -b 'auth_token='$AUTH $WEBSITE/portal/api/books
echo
echo

# Upload epub 3
echo "Uploading epub 3..."
curl -X POST -F 'book[name]='$TEST_EPUB3_TITLE -F 'book[backing_file]=@'$TEST_FILE_PATH$TEST_EPUB3 -b 'auth_token='$AUTH $WEBSITE/portal/api/books
echo
echo

# Upload pdf
echo "Uploading pdf..."
curl -X POST -F 'book[name]='$TEST_PDF_TITLE -F 'book[backing_file]=@'$TEST_FILE_PATH$TEST_PDF -b 'auth_token='$AUTH $WEBSITE/portal/api/books
echo
echo

# create the test student and accept the EULA
echo "create student user"
curl -X POST -H "Content-Type:application/json" -d '{"account_type":"student", "user": {"full_name": "'$TEST_STUDENT'", "email": "'$TEST_STUDENT'@datalogics.com", "password": "'$TEST_STUDENT_PASS'", "password_confirmation": "'$TEST_STUDENT_PASS'"}}' $WEBSITE/users.json;
echo
S_AUTH=`curl -sX POST --header "Content-Type:application/json" -d '{"login" : "'$TEST_STUDENT'@datalogics.com", "password" : "'$TEST_STUDENT_PASS'"}' $WEBSITE/login.json | grep -o '".*"' | sed 's/"//g' | sed 's/auth_token://g'`
echo "Student auth token: "$S_AUTH
curl -X POST --header "Content-Type: application/json" -d '{"accept_eula":true}' -b 'auth_token='$S_AUTH $WEBSITE/eula.json
echo
echo

# create the test publisher and accept the EULA
echo "create publisher user"
curl -X POST -H "Content-Type:application/json" -d '{"account_type":"student", "user": {"full_name": "'$TEST_PUBLISHER'", "email": "'$TEST_PUBLISHER'@datalogics.com", "password": "'$TEST_PUBLISHER_PASS'", "password_confirmation": "'$TEST_PUBLISHER_PASS'"}}' $WEBSITE/users.json;
echo
P_AUTH=`curl -sX POST --header "Content-Type:application/json" -d '{"login" : "'$TEST_STUDENT'@datalogics.com", "password" : "'$TEST_PUBLISHER_PASS'"}' $WEBSITE/login.json | grep -o '".*"' | sed 's/"//g' | sed 's/auth_token://g'`
echo "Publisher auth token: "$P_AUTH
curl -X POST --header "Content-Type: application/json" -d '{"accept_eula":true}' -b 'auth_token='$P_AUTH $WEBSITE/eula.json
echo
echo