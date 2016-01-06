/*
 * Copyright 2013-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cloudfoundry.client.v3.applications;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.ToString;
import org.cloudfoundry.client.v3.Hash;
import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.PaginatedResponse;
import org.cloudfoundry.client.v3.packages.Package;

import java.util.List;
import java.util.Map;

/**
 * The response payload for the List Application Packages operation
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class ListApplicationPackagesResponse extends PaginatedResponse<ListApplicationPackagesResponse.Resource> {

    @Builder
    ListApplicationPackagesResponse(@JsonProperty("pagination") Pagination pagination,
                                    @JsonProperty("resources") @Singular List<ListApplicationPackagesResponse.Resource> resources) {
        super(pagination, resources);
    }

    /**
     * The Resource response payload for the List Application Packages operation
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static final class Resource extends Package {

        @Builder
        Resource(@JsonProperty("created_at") String createdAt,
                 @JsonProperty("error") String error,
                 @JsonProperty("hash") Hash hash,
                 @JsonProperty("guid") String id,
                 @JsonProperty("_links") @Singular Map<String, Link> links,
                 @JsonProperty("state") String state,
                 @JsonProperty("type") String type,
                 @JsonProperty("updated_at") String updatedAt,
                 @JsonProperty("url") String url) {
            super(createdAt, error, hash, id, links, state, type, updatedAt, url);
        }

    }

}