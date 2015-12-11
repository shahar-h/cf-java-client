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

package org.cloudfoundry.client.spring.v3.packages;

import org.cloudfoundry.client.RequestValidationException;
import org.cloudfoundry.client.spring.AbstractRestTest;
import org.cloudfoundry.client.v2.CloudFoundryException;
import org.cloudfoundry.client.v3.Hash;
import org.cloudfoundry.client.v3.Link;
import org.cloudfoundry.client.v3.PaginatedResponse.Pagination;
import org.cloudfoundry.client.v3.packages.CopyPackageRequest;
import org.cloudfoundry.client.v3.packages.CopyPackageResponse;
import org.cloudfoundry.client.v3.packages.CreatePackageRequest;
import org.cloudfoundry.client.v3.packages.CreatePackageResponse;
import org.cloudfoundry.client.v3.packages.DeletePackageRequest;
import org.cloudfoundry.client.v3.packages.DownloadPackageRequest;
import org.cloudfoundry.client.v3.packages.GetPackageRequest;
import org.cloudfoundry.client.v3.packages.GetPackageResponse;
import org.cloudfoundry.client.v3.packages.ListPackagesRequest;
import org.cloudfoundry.client.v3.packages.ListPackagesResponse;
import org.cloudfoundry.client.v3.packages.StagePackageRequest;
import org.cloudfoundry.client.v3.packages.StagePackageResponse;
import org.cloudfoundry.client.v3.packages.UploadPackageRequest;
import org.cloudfoundry.client.v3.packages.UploadPackageResponse;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import reactor.rx.Streams;

import java.io.IOException;

import static org.cloudfoundry.client.v3.packages.CreatePackageRequest.PackageType.DOCKER;
import static org.cloudfoundry.client.v3.packages.ListPackagesResponse.Resource;
import static org.cloudfoundry.client.v3.packages.ListPackagesResponse.builder;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertEquals;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;

public final class SpringPackagesTest extends AbstractRestTest {

    private final SpringPackages packages = new SpringPackages(this.restTemplate, this.root);

    @Test
    public void copy() {
        mockRequest(new RequestContext()
                .method(POST).path("/v3/apps/test-application-id/packages?source_package_guid=test-source-package-id")
                .status(OK)
                .responsePayload("v3/apps/POST_{id}_packages_response.json"));

        CopyPackageRequest request = CopyPackageRequest.builder()
                .applicationId("test-application-id")
                .sourcePackageId("test-source-package-id")
                .build();

        CopyPackageResponse expected = CopyPackageResponse.builder()
                .id("126e54c4-811d-4f7a-9a34-804130a75ab2")
                .type("docker")
                .hash(Hash.builder()
                        .type("sha1")
                        .build())
                .url("docker://cloudfoundry/runtime-ci")
                .state("READY")
                .createdAt("2015-08-06T00:36:55Z")
                .link("self", Link.builder()
                        .href("/v3/packages/126e54c4-811d-4f7a-9a34-804130a75ab2")
                        .build())
                .link("app", Link.builder()
                        .href("/v3/apps/guid-f8e68f3f-663d-478d-98ff-5d554910fde0")
                        .build())
                .build();

        CopyPackageResponse actual = Streams.wrap(this.packages.copy(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void copyError() {
        mockRequest(new RequestContext()
                .method(POST).path("/v3/apps/test-application-id/packages?source_package_guid=test-source-package-id")
                .errorResponse());

        CopyPackageRequest request = CopyPackageRequest.builder()
                .applicationId("test-application-id")
                .sourcePackageId("test-source-package-id")
                .build();

        Streams.wrap(this.packages.copy(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void copyInvalidRequest() {
        CopyPackageRequest request = CopyPackageRequest.builder()
                .build();

        Streams.wrap(this.packages.copy(request)).next().get();
    }

    @Test
    public void create() {
        mockRequest(new RequestContext()
                .method(POST).path("/v3/apps/test-application-id/packages")
                .requestPayload("v3/apps/POST_{id}_packages_request.json")
                .status(CREATED)
                .responsePayload("v3/apps/POST_{id}_packages_response.json"));

        CreatePackageRequest request = CreatePackageRequest.builder()
                .applicationId("test-application-id")
                .type(DOCKER)
                .url("docker://cloudfoundry/runtime-ci")
                .build();

        CreatePackageResponse expected = CreatePackageResponse.builder()
                .id("126e54c4-811d-4f7a-9a34-804130a75ab2")
                .type("docker")
                .hash(Hash.builder()
                        .type("sha1")
                        .build())
                .url("docker://cloudfoundry/runtime-ci")
                .state("READY")
                .createdAt("2015-08-06T00:36:55Z")
                .link("self", Link.builder()
                        .href("/v3/packages/126e54c4-811d-4f7a-9a34-804130a75ab2")
                        .build())
                .link("app", Link.builder()
                        .href("/v3/apps/guid-f8e68f3f-663d-478d-98ff-5d554910fde0")
                        .build())
                .build();

        CreatePackageResponse actual = Streams.wrap(this.packages.create(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void createError() {
        mockRequest(new RequestContext()
                .method(POST).path("/v3/apps/test-application-id/packages")
                .requestPayload("v3/apps/POST_{id}_packages_request.json")
                .errorResponse());

        CreatePackageRequest request = CreatePackageRequest.builder()
                .applicationId("test-application-id")
                .type(DOCKER)
                .url("docker://cloudfoundry/runtime-ci")
                .build();

        Streams.wrap(this.packages.create(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void createInvalidRequest() {
        CreatePackageRequest request = CreatePackageRequest.builder()
                .build();

        Streams.wrap(this.packages.create(request)).next().get();
    }

    @Test
    public void delete() {
        mockRequest(new RequestContext()
                .method(DELETE).path("/v3/packages/test-id")
                .status(OK));

        DeletePackageRequest request = DeletePackageRequest.builder()
                .id("test-id")
                .build();

        Streams.wrap(this.packages.delete(request)).next().get();

        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void deleteError() {
        mockRequest(new RequestContext()
                .method(DELETE).path("/v3/packages/test-id")
                .errorResponse());

        DeletePackageRequest request = DeletePackageRequest.builder()
                .id("test-id")
                .build();

        Streams.wrap(this.packages.delete(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void deleteInvalidRequest() {
        DeletePackageRequest request = DeletePackageRequest.builder()
                .build();

        Streams.wrap(this.packages.delete(request)).next().get();
    }

    @Test
    public void download() throws IOException {
        mockRequest(new RequestContext()
                .method(GET).path("v3/packages/test-id/download")
                .status(OK)
                .responsePayload("v3/packages/GET_{id}_download_response.bin"));

        DownloadPackageRequest request = DownloadPackageRequest.builder()
                .id("test-id")
                .build();

        assertBytesEqual(new ClassPathResource("v3/packages/GET_{id}_download_response.bin").getInputStream(),
                Streams.wrap(this.packages.download(request)));

        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void downloadError() {
        mockRequest(new RequestContext()
                .method(GET).path("v3/packages/test-id/download")
                .errorResponse());

        DownloadPackageRequest request = DownloadPackageRequest.builder()
                .id("test-id")
                .build();

        Streams.wrap(this.packages.download(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void downloadInvalidRequest() {
        DownloadPackageRequest request = DownloadPackageRequest.builder()
                .build();

        Streams.wrap(this.packages.download(request)).next().get();
    }


    @Test
    public void get() {
        mockRequest(new RequestContext()
                .method(GET).path("/v3/packages/test-id")
                .status(OK)
                .responsePayload("v3/packages/GET_{id}_response.json"));

        GetPackageRequest request = GetPackageRequest.builder()
                .id("test-id")
                .build();

        GetPackageResponse expected = GetPackageResponse.builder()
                .id("guid-9067cc41-b832-4de9-89a2-0987dab65e8e")
                .type("bits")
                .hash(Hash.builder()
                        .type("sha1")
                        .build())
                .state("AWAITING_UPLOAD")
                .createdAt("2015-07-27T22:43:15Z")
                .link("self", Link.builder()
                        .href("/v3/packages/guid-9067cc41-b832-4de9-89a2-0987dab65e8e")
                        .build())
                .link("upload", Link.builder()
                        .href("/v3/packages/guid-9067cc41-b832-4de9-89a2-0987dab65e8e/upload")
                        .method("POST")
                        .build())
                .link("download", Link.builder()
                        .href("/v3/packages/guid-9067cc41-b832-4de9-89a2-0987dab65e8e/download")
                        .method("GET")
                        .build())
                .link("stage", Link.builder()
                        .href("/v3/packages/guid-9067cc41-b832-4de9-89a2-0987dab65e8e/droplets")
                        .method("POST")
                        .build())
                .link("app", Link.builder()
                        .href("/v3/apps/guid-1eb7b328-4769-45c9-8c61-3d2e7b69541a")
                        .build())
                .build();

        GetPackageResponse actual = Streams.wrap(this.packages.get(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void getError() {
        mockRequest(new RequestContext()
                .method(GET).path("/v3/packages/test-id")
                .errorResponse());

        GetPackageRequest request = GetPackageRequest.builder()
                .id("test-id")
                .build();

        Streams.wrap(this.packages.get(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void getInvalidRequest() {
        GetPackageRequest request = GetPackageRequest.builder()
                .build();

        Streams.wrap(this.packages.get(request)).next().get();
    }

    @Test
    public void list() {
        mockRequest(new RequestContext()
                .method(GET).path("/v3/packages")
                .status(OK)
                .responsePayload("v3/packages/GET_response.json"));

        ListPackagesRequest request = ListPackagesRequest.builder()
                .build();

        ListPackagesResponse expected = builder()
                .pagination(Pagination.builder()
                        .totalResults(3)
                        .first(Link.builder()
                                .href("/v3/packages?page=1&per_page=2")
                                .build())
                        .last(Link.builder()
                                .href("/v3/packages?page=2&per_page=2")
                                .build())
                        .next(Link.builder()
                                .href("/v3/packages?page=2&per_page=2")
                                .build())
                        .build())
                .resource(Resource.builder()
                        .id("guid-84ffc554-5d3a-4ea3-bfeb-d796fa82bf7a")
                        .type("bits")
                        .hash(Hash.builder()
                                .type("sha1")
                                .build())
                        .state("AWAITING_UPLOAD")
                        .createdAt("2015-07-27T22:43:15Z")
                        .link("self", Link.builder()
                                .href("/v3/packages/guid-84ffc554-5d3a-4ea3-bfeb-d796fa82bf7a")
                                .build())
                        .link("upload", Link.builder()
                                .href("/v3/packages/guid-84ffc554-5d3a-4ea3-bfeb-d796fa82bf7a/upload")
                                .method("POST")
                                .build())
                        .link("download", Link.builder()
                                .href("/v3/packages/guid-84ffc554-5d3a-4ea3-bfeb-d796fa82bf7a/download")
                                .method("GET")
                                .build())
                        .link("stage", Link.builder()
                                .href("/v3/packages/guid-84ffc554-5d3a-4ea3-bfeb-d796fa82bf7a/droplets")
                                .method("POST")
                                .build())
                        .link("app", Link.builder()
                                .href("/v3/apps/guid-ec3d91b9-a9c7-4fec-a0d1-d2dfe1c7bac4")
                                .build())
                        .build())
                .resource(Resource.builder()
                        .id("guid-caa0f920-0f24-4a80-b7e4-3119758901c3")
                        .type("docker")
                        .hash(Hash.builder()
                                .type("sha1")
                                .build())
                        .url("http://docker-repo/my-image")
                        .state("READY")
                        .createdAt("2015-07-27T22:43:15Z")
                        .link("self", Link.builder()
                                .href("/v3/packages/guid-caa0f920-0f24-4a80-b7e4-3119758901c3")
                                .build())
                        .link("app", Link.builder()
                                .href("/v3/apps/guid-ec3d91b9-a9c7-4fec-a0d1-d2dfe1c7bac4")
                                .build())
                        .build())
                .build();

        ListPackagesResponse actual = Streams.wrap(this.packages.list(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void listError() {
        mockRequest(new RequestContext()
                .method(GET).path("/v3/packages")
                .errorResponse());

        ListPackagesRequest request = ListPackagesRequest.builder()
                .build();

        Streams.wrap(this.packages.list(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void listInvalidRequest() {
        ListPackagesRequest request = ListPackagesRequest.builder()
                .page(-1)
                .build();

        Streams.wrap(this.packages.list(request)).next().get();
    }

    @Test
    public void stage() {
        mockRequest(new RequestContext()
                .method(POST).path("/v3/packages/test-id/droplets")
                .requestPayload("v3/packages/POST_{id}_droplets_request.json")
                .status(CREATED)
                .responsePayload("v3/packages/POST_{id}_droplets_response.json"));

        StagePackageRequest request = StagePackageRequest.builder()
                .buildpack("http://github.com/myorg/awesome-buildpack")
                .environmentVariable("CUSTOM_ENV_VAR", "hello")
                .id("test-id")
                .stack("cflinuxfs2")
                .build();

        StagePackageResponse expected = StagePackageResponse.builder()
                .id("guid-4dc396dd-9fe3-4b96-847e-d0c63768d5f9")
                .state("STAGED")
                .hash(Hash.builder()
                        .type("sha1")
                        .build())
                .buildpack("http://buildpack.git.url.com")
                .error("example error")
                .environmentVariable("cloud", "foundry")
                .createdAt("2015-07-27T22:43:30Z")
                .link("self", Link.builder()
                        .href("/v3/droplets/guid-4dc396dd-9fe3-4b96-847e-d0c63768d5f9")
                        .build())
                .link("package", Link.builder()
                        .href("/v3/packages/guid-1df1d953-ef12-4604-a746-d6e047314c12")
                        .build())
                .link("app", Link.builder()
                        .href("/v3/apps/guid-059d1bf5-1b72-4ad6-b73f-6abe87bc77e8")
                        .build())
                .link("assign_current_droplet", Link.builder()
                        .href("/v3/apps/guid-059d1bf5-1b72-4ad6-b73f-6abe87bc77e8/current_droplet")
                        .method("PUT")
                        .build())
                .build();

        StagePackageResponse actual = Streams.wrap(this.packages.stage(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    public void stageError() {
        mockRequest(new RequestContext()
                .method(POST).path("/v3/packages/test-id/droplets")
                .requestPayload("v3/packages/POST_{id}_droplets_request.json")
                .errorResponse());

        StagePackageRequest request = StagePackageRequest.builder()
                .buildpack("http://github.com/myorg/awesome-buildpack")
                .environmentVariable("CUSTOM_ENV_VAR", "hello")
                .id("test-id")
                .stack("cflinuxfs2")
                .build();

        Streams.wrap(this.packages.stage(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void stageInvalidRequest() {
        StagePackageRequest request = StagePackageRequest.builder()
                .build();

        Streams.wrap(this.packages.stage(request)).next().get();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void upload() throws IOException {
        mockRequest(new RequestContext()
                .method(POST).path("/v3/packages/test-id/upload")
                .requestMatcher(header("Content-Type", startsWith(MULTIPART_FORM_DATA_VALUE)))
                .anyRequestPayload()
                .status(CREATED)
                .responsePayload("v3/packages/POST_{id}_upload_response.json"));

        UploadPackageRequest request = UploadPackageRequest.builder()
                .file(new ClassPathResource("v3/packages/test-file").getFile())
                .id("test-id")
                .build();

        UploadPackageResponse expected = UploadPackageResponse.builder()
                .id("guid-013bf35c-3473-461a-ae53-16f89239fe38")
                .type("bits")
                .hash(Hash.builder()
                        .type("sha1")
                        .build())
                .state("PROCESSING_UPLOAD")
                .createdAt("2015-07-27T22:43:15Z")
                .updatedAt("2015-07-27T22:43:15Z")
                .link("self", Link.builder()
                        .href("/v3/packages/guid-013bf35c-3473-461a-ae53-16f89239fe38")
                        .build())
                .link("upload", Link.builder()
                        .href("/v3/packages/guid-013bf35c-3473-461a-ae53-16f89239fe38/upload")
                        .method("POST")
                        .build())
                .link("download", Link.builder()
                        .href("/v3/packages/guid-013bf35c-3473-461a-ae53-16f89239fe38/download")
                        .method("GET")
                        .build())
                .link("stage", Link.builder()
                        .href("/v3/packages/guid-013bf35c-3473-461a-ae53-16f89239fe38/droplets")
                        .method("POST")
                        .build())
                .link("app", Link.builder()
                        .href("/v3/apps/guid-285002dc-b13e-4851-9e6d-79bb4df6e449")
                        .build())
                .build();

        UploadPackageResponse actual = Streams.wrap(this.packages.upload(request)).next().get();

        assertEquals(expected, actual);
        verify();
    }

    @Test(expected = CloudFoundryException.class)
    @SuppressWarnings("unchecked")
    public void uploadError() throws IOException {
        mockRequest(new RequestContext()
                .method(POST).path("/v3/packages/test-id/upload")
                .requestMatcher(header("Content-Type", startsWith(MULTIPART_FORM_DATA_VALUE)))
                .anyRequestPayload()
                .errorResponse());

        UploadPackageRequest request = UploadPackageRequest.builder()
                .file(new ClassPathResource("v3/packages/test-file").getFile())
                .id("test-id")
                .build();

        Streams.wrap(this.packages.upload(request)).next().get();
    }

    @Test(expected = RequestValidationException.class)
    public void uploadInvalidRequest() {
        UploadPackageRequest request = UploadPackageRequest.builder()
                .build();

        Streams.wrap(this.packages.upload(request)).next().get();
    }

}
