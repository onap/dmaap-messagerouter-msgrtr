#!/bin/sh
#*******************************************************************************
#  ============LICENSE_START=======================================================
#  org.onap.dmaap
#  ================================================================================
#  Copyright © 2017 AT&T Intellectual Property. All rights reserved.
#  ================================================================================
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#        http://www.apache.org/licenses/LICENSE-2.0
#  
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
#  ============LICENSE_END=========================================================
#
#  ECOMP is a trademark and service mark of AT&T Intellectual Property.
#  
#*******************************************************************************

# switched this from CAMBRIA_API_HOME, which should be declared in the env.
# harmless to overwrite it here, but it's confusing to do so.
BASE_DIR=`dirname "$0"`/..

# use JAVA_HOME if provided
if [ -n "${CAMBRIA_JAVA_HOME}" ]; then
    JAVA=${CAMBRIA_JAVA_HOME}/bin/java
elif [ -n "${JAVA_HOME}" ]; then
    JAVA=${JAVA_HOME}/bin/java
else
    JAVA=java
fi

# use the logs dir set in environment, or the installation's logs dir if not set
if [ -z "$CAMBRIA_LOGS_HOME" ]; then
	CAMBRIA_LOGS_HOME=$BASE_DIR/logs
fi

mkdir -p ${CAMBRIA_LOGS_HOME}
# run java. The classpath is the etc dir for config files, and the lib dir
# for all the jars.
#
# don't pipe stdout/stderr to /dev/null here - some diagnostic info is available only there.
# also don't assume the run is in the background. the caller should take care of that.
#
$JAVA -cp ${BASE_DIR}/etc:${BASE_DIR}/lib/* com.att.nsa.cambria.CambriaApiServer $* >${CAMBRIA_LOGS_HOME}/console.log 2>&1
