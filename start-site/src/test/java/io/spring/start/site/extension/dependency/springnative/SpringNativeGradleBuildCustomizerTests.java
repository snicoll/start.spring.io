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

import java.util.function.Supplier;

import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuild;
import io.spring.initializr.generator.version.VersionReference;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * Tests for {@link SpringNativeGradleBuildCustomizer}.
 *
 * @author Stephane Nicoll
 */
class SpringNativeGradleBuildCustomizerTests {

	@Test
	void gradleBuildWithJpaConfigureHibernateEnhancePlugin() {
		SpringNativeGradleBuildCustomizer customizer = new SpringNativeGradleBuildCustomizer(
				() -> VersionReference.ofValue("1.0.0"));
		GradleBuild build = new GradleBuild();
		build.dependencies().add("data-jpa", Dependency.withCoordinates("org.hibernate", "hibernate"));
		customizer.customize(build);
		assertThat(build.plugins().has("org.hibernate.orm")).isTrue();
		assertThat(build.getSettings().getPluginMappings()).singleElement().satisfies((pluginMapping) -> {
			assertThat(pluginMapping.getId()).isEqualTo("org.hibernate.orm");
			assertThat(pluginMapping.getDependency()).satisfies((dependency) -> {
				assertThat(dependency.getGroupId()).isEqualTo("org.hibernate");
				assertThat(dependency.getArtifactId()).isEqualTo("hibernate-gradle-plugin");
				assertThat(dependency.getVersion().isProperty()).isFalse();
				assertThat(dependency.getVersion().getValue()).isEqualTo("1.0.0");
			});
		});
		assertThat(build.tasks().has("hibernate")).isTrue();
	}

	@Test
	void gradleBuildWithoutJpaDoesNotConfigureHibernateEnhancePlugin() {
		SpringNativeGradleBuildCustomizer customizer = new SpringNativeGradleBuildCustomizer(
				() -> VersionReference.ofValue("1.0.0"));
		GradleBuild build = new GradleBuild();
		customizer.customize(build);
		assertThat(build.plugins().has("org.hibernate.orm")).isFalse();
		assertThat(build.getSettings().getPluginMappings()).isEmpty();
		assertThat(build.tasks().has("hibernate")).isFalse();
	}

	@Test
	@SuppressWarnings("unchecked")
	void gradleBuildWithoutJpaDoesNotRequireHibernateVersion() {
		Supplier<VersionReference> hibernateVersionSupplier = mock(Supplier.class);
		SpringNativeGradleBuildCustomizer customizer = new SpringNativeGradleBuildCustomizer(hibernateVersionSupplier);
		GradleBuild build = new GradleBuild();
		customizer.customize(build);
		verifyNoInteractions(hibernateVersionSupplier);
	}

}
