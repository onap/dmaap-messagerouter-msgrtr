/*******************************************************************************
 *  ============LICENSE_START===================================================
 *  org.onap.dmaap
 *  ============================================================================
 *  Copyright © 2019 Nokia Intellectual Property. All rights reserved.
 *  ============================================================================
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  ============LICENSE_END=====================================================
 ******************************************************************************/
package org.onap.dmaap.dmf.mr.service.impl;

import com.google.common.base.Preconditions;
import java.util.Date;
import org.apache.http.HttpStatus;
import org.onap.dmaap.dmf.mr.beans.DMaaPContext;
import org.onap.dmaap.dmf.mr.exception.DMaaPErrorMessages;
import org.onap.dmaap.dmf.mr.exception.DMaaPResponseCode;
import org.onap.dmaap.dmf.mr.exception.ErrorResponse;
import org.onap.dmaap.dmf.mr.utils.Utils;

class ErrorResponseProvider {

    private String clientId;
    private String topicName;
    private String consumerGroup;
    private String remoteHost;
    private DMaaPErrorMessages errorMessages;

    private ErrorResponseProvider() {

    }

    ErrorResponse getIpBlacklistedError(String remoteAddr, DMaaPContext ctx) {
        return new ErrorResponse(HttpStatus.SC_FORBIDDEN,
            DMaaPResponseCode.ACCESS_NOT_PERMITTED.getResponseCode(),
            "Source address [" + remoteAddr + "] is blacklisted. Please contact the cluster management team.",
            null, Utils.getFormattedDate(new Date()), topicName, Utils.getUserApiKey(ctx.getRequest()),
            remoteHost, null, null);
    }

    ErrorResponse getTopicNotFoundError() {
        return new ErrorResponse(HttpStatus.SC_NOT_FOUND,
            DMaaPResponseCode.RESOURCE_NOT_FOUND.getResponseCode(),
            errorMessages.getTopicNotExist() + "-[" + topicName + "]", null, Utils.getFormattedDate(new Date()),
            topicName, null, null, consumerGroup + "/" + clientId, remoteHost);
    }

    ErrorResponse getAafAuthorizationError(String permission) {
        return new ErrorResponse(HttpStatus.SC_UNAUTHORIZED,
            DMaaPResponseCode.ACCESS_NOT_PERMITTED.getResponseCode(),
            errorMessages.getNotPermitted1() + " read " + errorMessages.getNotPermitted2() + topicName + " on "
                + permission,
            null, Utils.getFormattedDate(new Date()), topicName, null, null, consumerGroup + "/" + clientId,
            remoteHost);
    }

    ErrorResponse getServiceUnavailableError(String msg) {
        return new ErrorResponse(HttpStatus.SC_SERVICE_UNAVAILABLE,
            DMaaPResponseCode.SERVER_UNAVAILABLE.getResponseCode(),
            errorMessages.getServerUnav() + msg, null, Utils.getFormattedDate(new Date()), topicName,
            null, null, consumerGroup + "-" + clientId, remoteHost);
    }

    ErrorResponse getConcurrentModificationError() {
        return new ErrorResponse(HttpStatus.SC_CONFLICT,
            DMaaPResponseCode.TOO_MANY_REQUESTS.getResponseCode(),
            "Couldn't respond to client, possible of consumer requests from more than one server. Please contact MR team if you see this issue occurs continously", null,
            Utils.getFormattedDate(new Date()), topicName, null, null, clientId, remoteHost);
    }

    ErrorResponse getGenericError(String msg) {
        return new ErrorResponse(HttpStatus.SC_SERVICE_UNAVAILABLE,
            DMaaPResponseCode.SERVER_UNAVAILABLE.getResponseCode(),
            "Couldn't respond to client, closing cambria consumer" + msg, null,
            Utils.getFormattedDate(new Date()), topicName, null, null, clientId, remoteHost);
    }

    public static class Builder {

        private String clientId;
        private String topicName;
        private String consumerGroup;
        private String remoteHost;
        DMaaPErrorMessages errorMessages;

        Builder withErrorMessages(DMaaPErrorMessages errorMessages) {
            this.errorMessages = errorMessages;
            return this;
        }

        Builder withTopic(String topic) {
            this.topicName = topic;
            return this;
        }

        Builder withClient(String client) {
            this.clientId = client;
            return this;
        }

        Builder withConsumerGroup(String consumerGroup) {
            this.consumerGroup = consumerGroup;
            return this;
        }

        Builder withRemoteHost(String remoteHost) {
            this.remoteHost = remoteHost;
            return this;
        }

        public ErrorResponseProvider build() {
            Preconditions.checkArgument(errorMessages!=null);
            ErrorResponseProvider errRespProvider = new ErrorResponseProvider();
            errRespProvider.errorMessages = this.errorMessages;
            errRespProvider.clientId = this.clientId;
            errRespProvider.consumerGroup = this.consumerGroup;
            errRespProvider.topicName = this.topicName;
            errRespProvider.remoteHost = this.remoteHost;
            return errRespProvider;
        }
    }
}
