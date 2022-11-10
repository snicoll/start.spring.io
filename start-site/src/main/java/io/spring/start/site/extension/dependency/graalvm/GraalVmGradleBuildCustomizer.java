/*
 * Copyright 2012-2022 the original author or authors.
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

package io.spring.start.site.extension.dependency.graalvm;

import java.util.function.Supplier;

import io.spring.initializr.generator.buildsystem.gradle.GradleBuild;
import io.spring.initializr.generator.spring.build.BuildCustomizer;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.generator.version.VersionReference;

/**
 * {@link BuildCustomizer} abstraction for Gradle projects using GraalVM.
 *
 * @author Stephane Nicoll
 */
class GraalVmGradleBuildCustomizer implements BuildCustomizer<GradleBuild> {

	private final String nbtVersion;

	private final Supplier<VersionReference> hibernateVersion;

	protected GraalVmGradleBuildCustomizer(Version platformVersion, Supplier<VersionReference> hibernateVersion) {
		this.nbtVersion = NativeBuildtoolsVersionResolver.resolve(platformVersion);
		this.hibernateVersion = hibernateVersion;
	}

	@Override
	public final void customize(GradleBuild build) {
		if (this.nbtVersion != null) {
			build.plugins().add("org.graalvm.buildtools.native", (plugin) -> plugin.setVersion(this.nbtVersion));
		}
		// Spring Boot plugin
		customizeSpringBootPlugin(build);

		if (build.dependencies().has("data-jpa")) {
			build.plugins().add("org.hibernate.orm",
					(plugin) -> plugin.setVersion(this.hibernateVersion.get().toString()));
			configureHibernateEnhancePlugin(build);
		}
	}

	protected void customizeSpringBootPlugin(GradleBuild build) {

	}

	private void configureHibernateEnhancePlugin(GradleBuild build) {
		build.tasks().customize("hibernate", (task) -> task.nested("enhancement", (enhancement) -> {
			enhancement.attribute("lazyInitialization", "true");
			enhancement.attribute("dirtyTracking", "true");
			enhancement.attribute("associationManagement", "true");
		}));
	}

}
