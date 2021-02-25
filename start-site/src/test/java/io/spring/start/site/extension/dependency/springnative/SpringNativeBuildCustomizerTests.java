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

import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.buildsystem.MavenRepository;
import io.spring.initializr.generator.buildsystem.maven.MavenBuild;
import io.spring.initializr.generator.version.VersionProperty;
import io.spring.initializr.generator.version.VersionReference;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link SpringNativeBuildCustomizer}.
 *
 * @author Stephane Nicoll
 */
class SpringNativeBuildCustomizerTests {

	@Test
	void versionPropertyIsSetOnDependency() {
		MavenBuild build = new MavenBuild();
		build.dependencies().add("native",
				Dependency.withCoordinates("com.example", "native").version(VersionReference.ofValue("2.0.0")));
		new SpringNativeBuildCustomizer(null).customize(build);
		assertThat(build.properties().versions(VersionProperty::toStandardFormat)).singleElement()
				.satisfies((entry) -> {
					assertThat(entry.getKey()).isEqualTo("spring-native.version");
					assertThat(entry.getValue()).isEqualTo("2.0.0");
				});
		assertThat(build.dependencies().get("native").getVersion())
				.isEqualTo(VersionReference.ofProperty("spring-native.version"));
	}

	@Test
	void pluginRepositoryIsRegisteredWhenMavenRepositoryIsSet() {
		MavenBuild build = new MavenBuild();
		build.dependencies().add("native",
				Dependency.withCoordinates("com.example", "native").version(VersionReference.ofValue("2.0.0")));
		MavenRepository testRepository = MavenRepository.withIdAndUrl("test", "https://repo.example.com")
				.name("Test repo").build();
		new SpringNativeBuildCustomizer(testRepository).customize(build);
		assertThat(build.pluginRepositories().isEmpty()).isFalse();
		assertThat(build.pluginRepositories().ids()).containsOnly("test");
		assertThat(build.pluginRepositories().get("test")).satisfies((repository) -> {
			assertThat(repository.getName()).isEqualTo("Test repo");
			assertThat(repository.getUrl()).isEqualTo("https://repo.example.com");
		});
	}

	@Test
	void pluginRepositoryIsNotRegisteredWhenMavenRepositoryIsNull() {
		MavenBuild build = new MavenBuild();
		build.dependencies().add("native",
				Dependency.withCoordinates("com.example", "native").version(VersionReference.ofValue("2.0.0")));
		new SpringNativeBuildCustomizer(null).customize(build);
		assertThat(build.pluginRepositories().isEmpty()).isTrue();
	}

}
