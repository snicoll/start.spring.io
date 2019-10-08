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

package io.spring.start.site.extension.dependency.geode;

import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.spring.documentation.HelpDocument;
import io.spring.initializr.generator.spring.documentation.HelpDocumentCustomizer;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadata;

/**
 * {@link HelpDocumentCustomizer} that adds a reference to Pivotal Cloud Cache.
 *
 * @author Stephane Nicoll
 */
public class GeodeHelpDocumentCustomizer implements HelpDocumentCustomizer {

	private final InitializrMetadata metadata;

	private final ProjectDescription description;

	public GeodeHelpDocumentCustomizer(InitializrMetadata metadata, ProjectDescription description) {
		this.metadata = metadata;
		this.description = description;
	}

	@Override
	public void customize(HelpDocument document) {
		Dependency geode = this.metadata.getDependencies().get("geode").resolve(this.description.getPlatformVersion());
		String referenceUrl = String.format(
				"https://docs.spring.io/spring-boot-data-geode-build/%s/reference/html5/#geode-gemfire-switch",
				geode.getVersion());
		document.addSection((writer) -> {
			writer.println("## Using Pivotal Cloud Cache");
			writer.println();
			writer.println("Something really smart here with a [link to the example](" + referenceUrl + ")");
		});
	}

}
