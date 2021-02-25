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

import java.util.Map.Entry;

import io.spring.initializr.generator.buildsystem.gradle.GradleBuild;
import io.spring.initializr.generator.spring.build.BuildCustomizer;
import io.spring.initializr.generator.version.VersionProperty;

import org.springframework.core.Ordered;

/**
 * A {@link BuildCustomizer} that configures Spring Native for Gradle.
 *
 * @author Stephane Nicoll
 */
class SpringNativeGradleBuildCustomizer implements BuildCustomizer<GradleBuild>, Ordered {

	@Override
	public void customize(GradleBuild build) {
		String springNativeVersion = build.properties().versions(VersionProperty::toCamelCaseFormat)
				.filter((candidate) -> candidate.getKey().equals("springNativeVersion")).map(Entry::getValue)
				.findFirst().orElse(null);

		// AOT plugin
		build.plugins().add("org.springframework.experimental.aot", (plugin) -> plugin.setVersion(springNativeVersion));

		// Spring Boot plugin
		build.tasks().customize("bootBuildImage", (task) -> {
			task.attribute("builder", "'paketobuildpacks/builder:tiny'");
			task.attribute("environment", "['BP_BOOT_NATIVE_IMAGE': 'true']");
		});
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE - 10;
	}

}
