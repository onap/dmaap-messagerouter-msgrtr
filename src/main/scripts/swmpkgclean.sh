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

# SWM can only store a finite amount of packages in its repository, so this script deletes the oldest package.
# This script is run by Jenkins after the build is finished (post SWM upload).

SWM_COMPONENT="com.att.nsa:msgrtr"

SWM_PKGS=`/opt/app/swm/aftswmcli/bin/swmcli "component pkglist -c $SWM_COMPONENT -df -dh -dj -sui"`
SWM_PKGS_COUNT=`echo "$SWM_PKGS" | wc -l`
SWM_PKGS_OLDEST=`echo "$SWM_PKGS" | head -1`
SWM_PKGS_MAX_COUNT=2

if [ $SWM_PKGS_COUNT > $SWM_PKGS_MAX_COUNT ]
then
	SWM_PKG_OLDEST_VERSION=`echo $SWM_PKGS_OLDEST | awk '{print $2}'`

	# Delete the oldest package for this component from the SWM repository
	/opt/app/swm/aftswmcli/bin/swmcli "component pkgdelete -c $SWM_COMPONENT:$SWM_PKG_OLDEST_VERSION"
else
	echo "No need to clean up SWM, package count ($SWM_PKGS_COUNT) is below threshold ($SWM_PKGS_MAX_COUNT)"
fi
