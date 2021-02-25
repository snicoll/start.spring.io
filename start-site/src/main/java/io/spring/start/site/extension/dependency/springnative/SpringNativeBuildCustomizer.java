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

import io.spring.initializr.generator.buildsystem.Build;
import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.buildsystem.MavenRepository;
import io.spring.initializr.generator.spring.build.BuildCustomizer;
import io.spring.initializr.generator.version.VersionProperty;
import io.spring.initializr.generator.version.VersionReference;

/**
 * A general {@link BuildCustomizer} for Spring Native.
 *
 * @author Stephane Nicoll
 */
class SpringNativeBuildCustomizer implements BuildCustomizer<Build> {

	private final MavenRepository pluginRepository;

	SpringNativeBuildCustomizer(MavenRepository pluginRepository) {
		this.pluginRepository = pluginRepository;
	}

	@Override
	public void customize(Build build) {
		Dependency dependency = build.dependencies().get("native");
		String springNativeVersion = dependency.getVersion().getValue();

		// Expose a property
		build.properties().version(VersionProperty.of("spring-native.version"), springNativeVersion);

		// Update dependency to reuse the property
		build.dependencies().add("native",
				Dependency.from(dependency).version(VersionReference.ofProperty("spring-native.version")));

		// Register a plugin repository if necessary
		if (this.pluginRepository != null) {
			build.pluginRepositories().add(this.pluginRepository);
		}

	}

}
