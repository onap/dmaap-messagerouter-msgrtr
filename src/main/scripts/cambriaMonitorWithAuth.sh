#!/bin/bash
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

#
#	act as a simple cambria consumer, requires wget
#
#	usage:
#		cambriaMonitor <broker> <topic> <group> <id> <timeout>
#

while :
do
	DATE=`date`
	SIGNATURE=`echo -n "$DATE" | openssl sha1 -hmac $CAMBRIA_APISECRET -binary | openssl base64`

	wget -q --header "X-CambriaAuth: $CAMBRIA_APIKEY:$SIGNATURE" --header "X-CambriaDate: $DATE" -O - $1/events/$2/$3/$4?timeout=$5\&pretty=1
	if [ $? -ne 0 ]
	then
		sleep 10
	fi
	echo
done

