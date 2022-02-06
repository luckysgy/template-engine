#! /bin/bash

/bin/cp -r easy-deploy-jar-with-dependencies.jar /usr/bin/easy-deploy-jar-with-dependencies.jar
/bin/cp -r easy-deploy.sh /usr/bin
/bin/cp -r easy-deploy.sh /usr/bin/easy-deploy
/bin/cp -r alias-bash.sh /etc/profile.d
chmod +x /usr/bin/easy-deploy.sh
chmod +x /usr/bin/easy-deploy

source /etc/profile.d/alias-bash.sh