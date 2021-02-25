/*
 * Copyright 2012-2021 the original author or authors.
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

package io.spring.start.site.extension.dependency.springnative;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import io.spring.initializr.generator.buildsystem.Build;
import io.spring.initializr.generator.buildsystem.maven.MavenBuild;
import io.spring.initializr.generator.io.template.MustacheTemplateRenderer;
import io.spring.initializr.generator.io.text.MustacheSection;
import io.spring.initializr.generator.spring.documentation.HelpDocument;
import io.spring.initializr.generator.spring.documentation.HelpDocumentCustomizer;
import io.spring.initializr.generator.version.VersionProperty;

/**
 * Provide additional information when Spring Native is selected.
 *
 * @author Stephane Nicoll
 */
class SpringNativeHelpDocumentCustomizer implements HelpDocumentCustomizer {

	private final MustacheTemplateRenderer templateRenderer;

	private final Build build;

	SpringNativeHelpDocumentCustomizer(MustacheTemplateRenderer templateRenderer, Build build) {
		this.templateRenderer = templateRenderer;
		this.build = build;
	}

	@Override
	public void customize(HelpDocument document) {
		String springNativeVersion = this.build.properties().versions(VersionProperty::toCamelCaseFormat)
				.filter((candidate) -> candidate.getKey().equals("springNativeVersion")).map(Entry::getValue)
				.findFirst().orElse("current");
		Map<String, Object> model = new HashMap<>();
		model.put("version", springNativeVersion);
		model.put("buildImageCommand",
				(this.build instanceof MavenBuild) ? "./mvnw spring-boot:build-image" : "./gradlew bootBuildImage");
		MustacheSection mainSection = new MustacheSection(this.templateRenderer, "spring-native", model);
		document.addSection(mainSection);
	}

}
