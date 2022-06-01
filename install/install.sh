#! /bin/bash

/bin/cp -r template-engine-jar-with-dependencies.jar /usr/bin/template-engine-jar-with-dependencies.jar
/bin/cp -r template-engine.sh /usr/bin
/bin/cp -r template-engine.sh /usr/bin/template-engine
/bin/cp -r alias-bash.sh /etc/profile.d
chmod +x /usr/bin/template-engine.sh
chmod +x /usr/bin/template-engine

source /etc/profile.d/alias-bash.sh