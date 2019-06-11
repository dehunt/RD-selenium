USER="admin@readynamic.com"
PASS="password"
WEBSITE="http://rdqa2.datalogics.com"

TEST_STUDENT="testStudent_1" #$IT
TEST_STUDENT_PASS="password"

# Fetch admin auth token
echo "== Logging in =="
echo "User:      " $USER
echo "Password:  " $PASS
AUTH=`curl -sSX POST --header "Content-Type:application/json" -d '{"login" : "'$USER'", "password" : "'$PASS'"}' $WEBSITE/login.json | grep -o '".*"' | sed 's/"//g' | sed 's/auth_token://g'`
echo "Auth token:" $AUTH
echo

# create the test student and accept the EULA
# accept the EULA broken in recent versions
echo "create student user"
outStu=`curl -sSX POST -H "Content-Type:application/json" -d '{"account_type":"student", "user": {"full_name": "'$TEST_STUDENT'", "email": "'$TEST_STUDENT'@datalogics.com", "password": "'$TEST_STUDENT_PASS'", "password_confirmation": "'$TEST_STUDENT_PASS'"}}' -b 'auth_token='$AUTH $WEBSITE/users.json;`
echo $outStu
idStu=`echo $outStu | sed -e 's/.*id":\(.*\),"full_name.*/\1/'`
echo "id: "$idStu
# curl -sSX POST -H "Content-Type:application/json" -b 'auth_token='$AUTH $WEBSITE/admin/users/$idStu/confirm_email.json;
# S_AUTH=`curl -sX POST --header "Content-Type:application/json" -d '{"login" : "'$TEST_STUDENT'@datalogics.com", "password" : "'$TEST_STUDENT_PASS'"}' $WEBSITE/login.json | grep -o '".*"' | sed 's/"//g' | sed 's/auth_token://g'`
echo "Student auth token: "$S_AUTH
# curl -sSX POST --header "Content-Type: application/json" -d '{"accept_eula":true}' -b 'auth_token='$S_AUTH $WEBSITE/eula.json
# echo
# echo