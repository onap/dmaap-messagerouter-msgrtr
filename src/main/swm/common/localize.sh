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
FINAL_CONFIG_FILE=${ROOT_DIR}/etc/cambriaApi.properties
TEMPLATE_CONFIG_FILE=${ROOT_DIR}/etc/cambriaApi_template.properties
BACKUP1_CONFIG_FILE=${ROOT_DIR}/etc/cambriaApi.properties.bk.1
BACKUP2_CONFIG_FILE=${ROOT_DIR}/etc/cambriaApi.properties.bk.2

echo "Localizing the Cambria API Server configuration"

if [ -z "${ELASTICSEARCH_NODES}" ]; then
	echo "ERROR: ELASTICSEARCH_NODES must be set"; exit 1
fi

if [ -z "${ZOOKEEPER_ENSEMBLE}" ]; then
	echo "ERROR: ZOOKEEPER_ENSEMBLE must be set"; exit 2
fi

if [ -z "${ZOOKEEPER_CLIENT_PORT}" ]; then
	ZOOKEEPER_CLIENT_PORT=2181
fi

if [ -z "${CAMBRIA_SERVICE_PORT}" ]; then
	CAMBRIA_SERVICE_PORT=3904
fi

if [ -z "${CAMBRIA_BROKER_TYPE}" ]; then
	CAMBRIA_BROKER_TYPE=kafka
fi

if [ -z "${KAFKA_PORT}" ]; then
	KAFKA_PORT=9092
fi

if [ -z "${KAFKA_BROKER_LIST}" ]; then
	KAFKA_BROKER_LIST=localhost:${KAFKA_PORT}
fi

#------------------------------------------------------------------------
#- MAKE A BACKUP OF PREVIOUS BACKUP FILE, IF EXISTS
#------------------------------------------------------------------------
if [ -f ${BACKUP1_CONFIG_FILE} ]; then
    cp -f ${BACKUP1_CONFIG_FILE} ${BACKUP2_CONFIG_FILE} || {
        echo "ERROR: Could not copy ${BACKUP1_CONFIG_FILE} to ${BACKUP2_CONFIG_FILE}"
        exit 5
    }
fi
 
#------------------------------------------------------------------------
#- MAKE A BACKUP OF CURRENT FILE, IF EXISTS
#------------------------------------------------------------------------
if [ -f ${FINAL_CONFIG_FILE} ]; then
    cp -f ${FINAL_CONFIG_FILE} ${BACKUP1_CONFIG_FILE} || {
        echo "ERROR: Could not copy ${FINAL_CONFIG_FILE} to ${BACKUP1_CONFIG_FILE}"
        exit 6
    }
fi

CAMBRIA_ZOOKEEPER_NODES=`echo ${ZOOKEEPER_ENSEMBLE} | sed -e "s/ /:${ZOOKEEPER_CLIENT_PORT},/g" | sed -e "s/$/:${ZOOKEEPER_CLIENT_PORT}/g"`

#------------------------------------------------------------------------
#- PROCESS THE TEMPLATE
#------------------------------------------------------------------------
sed -e 's/${CAMBRIA_SERVICE_PORT}/'${CAMBRIA_SERVICE_PORT}'/g' \
    -e 's/${CAMBRIA_BROKER_TYPE}/'${CAMBRIA_BROKER_TYPE}'/g' \
    -e 's/${KAFKA_BROKER_LIST}/'${KAFKA_BROKER_LIST}'/g' \
    -e 's/${CAMBRIA_ZOOKEEPER_NODES}/'${CAMBRIA_ZOOKEEPER_NODES}'/g' ${TEMPLATE_CONFIG_FILE} > ${FINAL_CONFIG_FILE} || {
	    echo "ERROR: Could not process template file ${TEMPLATE_CONFIG_FILE} into ${FINAL_CONFIG_FILE}"
	    exit 7
	}

FINAL_LOG4J_FILE=${ROOT_DIR}/etc/log4j2.xml
TEMPLATE_LOG4J_FILE=${ROOT_DIR}/etc/log4j2_template.xml
BACKUP1_LOG4J_FILE=${ROOT_DIR}/etc/log4j2.xml.bk.1
BACKUP2_LOG4J_FILE=${ROOT_DIR}/etc/log4j2.xml.bk.2

if [ -z "${CAMBRIA_LOG_DIR}" ]; then
	CAMBRIA_LOG_DIR=${ROOT_DIR}/logs
fi

if [ -z "${CAMBRIA_LOG_THRESHOLD}" ]; then
	CAMBRIA_LOG_THRESHOLD="INFO"
fi

#------------------------------------------------------------------------
#- MAKE A BACKUP OF PREVIOUS BACKUP FILE, IF EXISTS
#------------------------------------------------------------------------
if [ -f ${BACKUP1_LOG4J_FILE} ]; then
    cp -f ${BACKUP1_LOG4J_FILE} ${BACKUP2_LOG4J_FILE} || {
        echo "ERROR: Could not copy ${BACKUP1_LOG4J_FILE} to ${BACKUP2_LOG4J_FILE}"
        exit 8
    }
fi
 
#------------------------------------------------------------------------
#- MAKE A BACKUP OF CURRENT FILE, IF EXISTS
#------------------------------------------------------------------------
if [ -f ${FINAL_LOG4J_FILE} ]; then
    cp -f ${FINAL_LOG4J_FILE} ${BACKUP1_LOG4J_FILE} || {
        echo "ERROR: Could not copy ${FINAL_LOG4J_FILE} to ${BACKUP1_LOG4J_FILE}"
        exit 9
    }
fi

#------------------------------------------------------------------------
#- PROCESS THE TEMPLATE
#------------------------------------------------------------------------
sed -e 's/${CAMBRIA_LOG_THRESHOLD}/'${CAMBRIA_LOG_THRESHOLD}'/g' \
    -e 's,${CAMBRIA_LOG_DIR},'${CAMBRIA_LOG_DIR}',g' ${TEMPLATE_LOG4J_FILE} > ${FINAL_LOG4J_FILE} || {
	    echo "ERROR: Could not process template file ${TEMPLATE_LOG4J_FILE} into ${FINAL_LOG4J_FILE}"
	    exit 10
	}

FINAL_LOGSTASH_FILE=${ROOT_DIR}/etc/messages.conf
TEMPLATE_LOGSTASH_FILE=${ROOT_DIR}/etc/logstash_cambria_template.conf
BACKUP1_LOGSTASH_FILE=${ROOT_DIR}/etc/messages.conf.bk.1
BACKUP2_LOGSTASH_FILE=${ROOT_DIR}/etc/messages.conf.bk.2
#------------------------------------------------------------------------
#- MAKE A BACKUP OF PREVIOUS BACKUP FILE, IF EXISTS
#------------------------------------------------------------------------
if [ -f ${BACKUP1_LOGSTASH_FILE} ]; then
    cp -f ${BACKUP1_LOGSTASH_FILE} ${BACKUP2_LOGSTASH_FILE} || {
        echo "ERROR: Could not copy ${BACKUP1_LOGSTASH_FILE} to ${BACKUP2_LOGSTASH_FILE}"
        exit 11
    }
fi
 
#------------------------------------------------------------------------
#- MAKE A BACKUP OF CURRENT FILE, IF EXISTS
#------------------------------------------------------------------------
if [ -f ${FINAL_LOGSTASH_FILE} ]; then
    cp -f ${FINAL_LOGSTASH_FILE} ${BACKUP1_LOGSTASH_FILE} || {
        echo "ERROR: Could not copy ${FINAL_LOGSTASH_FILE} to ${BACKUP1_LOGSTASH_FILE}"
        exit 12
    }
fi

#------------------------------------------------------------------------
#- PROCESS THE TEMPLATE
#------------------------------------------------------------------------
sed -e 's,${CAMBRIA_SERVER_LOG},'${CAMBRIA_LOG_DIR}/cambria.log',g' \
    -e 's/${ELASTICSEARCH_NODES}/'${ELASTICSEARCH_NODES}'/g' ${TEMPLATE_LOGSTASH_FILE} > ${FINAL_LOGSTASH_FILE} || {
	    echo "ERROR: Could not process template file ${TEMPLATE_LOGSTASH_FILE} into ${FINAL_LOGSTASH_FILE}"
	    exit 13
	}

#------------------------------------------------------------------------
#- CLEAN EXIT
#------------------------------------------------------------------------
echo "Localized Successfully."
exit 0
