mkdir output

export OLD_CLASSPATH=$CLASSPATH

export CLASSPATH=ABAGAIL-master/ABAGAIL.jar:$OLD_CLASSPATH

java opt.test.RunAll

mv output master_output

mkdir output

export CLASSPATH=ABAGAIL-looping/ABAGAIL-looping.jar:$OLD_CLASSPATH

java opt.test.RunAll

export CLASSPATH=$OLD_CLASSPATH

mv output looping_output