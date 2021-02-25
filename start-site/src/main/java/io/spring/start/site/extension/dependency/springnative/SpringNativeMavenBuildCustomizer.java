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

import io.spring.initializr.generator.buildsystem.maven.MavenBuild;
import io.spring.initializr.generator.spring.build.BuildCustomizer;

import org.springframework.core.Ordered;

/**
 * A {@link BuildCustomizer} that configures Spring Native for Maven.
 *
 * @author Stephane Nicoll
 */
class SpringNativeMavenBuildCustomizer implements BuildCustomizer<MavenBuild>, Ordered {

	@Override
	public void customize(MavenBuild build) {
		// AOT plugin
		build.plugins().add("org.springframework.experimental", "spring-aot-maven-plugin",
				(plugin) -> plugin.version("${spring-native.version}")
						.execution("test-generate", (execution) -> execution.goal("test-generate"))
						.execution("generate", (execution) -> execution.goal("generate")));

		// Spring Boot plugin
		build.plugins().add("org.springframework.boot", "spring-boot-maven-plugin",
				(plugin) -> plugin.configuration((configuration) -> configuration.add("image", (image) -> {
					image.add("builder", "paketobuildpacks/builder:tiny");
					image.add("env", (env) -> env.add("BP_BOOT_NATIVE_IMAGE", "true"));
				})));

		if (build.dependencies().has("data-jpa")) {
			configureHibernateEnhancePlugin(build);
		}
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE - 10;
	}

	private void configureHibernateEnhancePlugin(MavenBuild build) {
		build.plugins()
				.add("org.hibernate.orm.tooling", "hibernate-enhance-maven-plugin",
						(plugin) -> plugin.version("${hibernate.version}").execution("enhance",
								(execution) -> execution.goal("enhance").configuration((configuration) -> configuration
										.add("failOnError", "true").add("enableLazyInitialization", "true")
										.add("enableDirtyTracking", "true").add("enableAssociationManagement", "true")
										.add("enableExtendedEnhancement", "false"))));
	}

}
