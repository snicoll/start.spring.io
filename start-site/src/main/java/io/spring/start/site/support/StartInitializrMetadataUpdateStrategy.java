/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.start.site.support;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.initializr.metadata.DefaultMetadataElement;
import io.spring.initializr.web.support.DefaultInitializrMetadataUpdateStrategy;
import io.spring.initializr.web.support.InitializrMetadataUpdateStrategy;

import org.springframework.web.client.RestTemplate;

/**
 * A custom {@link InitializrMetadataUpdateStrategy} for start.spring.io that filters
 * certain versions that are still available on spring.io but that we don't want users to
 * chose to start a brand new project.
 *
 * @author Stephane Nicoll
 */
public class StartInitializrMetadataUpdateStrategy extends DefaultInitializrMetadataUpdateStrategy {

	public StartInitializrMetadataUpdateStrategy(RestTemplate restTemplate, ObjectMapper objectMapper) {
		super(restTemplate, objectMapper);
	}

	@Override
	protected List<DefaultMetadataElement> fetchSpringBootVersions(String url) {
		List<DefaultMetadataElement> versions = new ArrayList<>();
		versions.add(new DefaultMetadataElement("2.4.0-SNAPSHOT", "2.4.0 (SNAPSHOT)", false));
		versions.addAll(super.fetchSpringBootVersions(url));
		return versions;
	}

}
