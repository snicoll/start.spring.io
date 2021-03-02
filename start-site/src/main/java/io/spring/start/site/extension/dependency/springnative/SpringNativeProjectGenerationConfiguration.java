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

import java.util.Map;
import java.util.function.Supplier;

import io.spring.initializr.generator.buildsystem.Build;
import io.spring.initializr.generator.buildsystem.MavenRepository;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuildSystem;
import io.spring.initializr.generator.buildsystem.maven.MavenBuildSystem;
import io.spring.initializr.generator.condition.ConditionalOnBuildSystem;
import io.spring.initializr.generator.condition.ConditionalOnRequestedDependency;
import io.spring.initializr.generator.io.template.MustacheTemplateRenderer;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.ProjectGenerationConfiguration;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.generator.version.VersionReference;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.Repository;
import io.spring.initializr.versionresolver.DependencyManagementVersionResolver;

import org.springframework.context.annotation.Bean;

/**
 * {@link ProjectGenerationConfiguration} for generation of projects that depend on Spring
 * Native.
 *
 * @author Stephane Nicoll
 */
@ProjectGenerationConfiguration
@ConditionalOnRequestedDependency("native")
class SpringNativeProjectGenerationConfiguration {

	@Bean
	SpringNativeHelpDocumentCustomizer springNativeHelpDocumentCustomizer(MustacheTemplateRenderer templateRenderer,
			Build build) {
		return new SpringNativeHelpDocumentCustomizer(templateRenderer, build);
	}

	@Bean
	SpringNativeBuildCustomizer springNativeBuildCustomizer(InitializrMetadata metadata,
			ProjectDescription description) {
		return new SpringNativeBuildCustomizer(
				determineNativeMavenRepository(metadata, description.getPlatformVersion()));
	}

	@Bean
	@ConditionalOnBuildSystem(MavenBuildSystem.ID)
	SpringNativeMavenBuildCustomizer springNativeMavenBuildCustomizer() {
		return new SpringNativeMavenBuildCustomizer();
	}

	@Bean
	@ConditionalOnBuildSystem(GradleBuildSystem.ID)
	SpringNativeGradleBuildCustomizer springNativeGradleBuildCustomizer(ProjectDescription description,
			DependencyManagementVersionResolver versionResolver) {
		return new SpringNativeGradleBuildCustomizer(
				determineHibernateVersion(versionResolver, description.getPlatformVersion()));
	}

	private static Supplier<VersionReference> determineHibernateVersion(
			DependencyManagementVersionResolver versionResolver, Version springBootVersion) {
		return () -> {
			Map<String, String> resolve = versionResolver.resolve("org.springframework.boot",
					"spring-boot-dependencies", springBootVersion.toString());
			String hibernateVersion = resolve.get("org.hibernate:hibernate-core");
			if (hibernateVersion == null) {
				throw new IllegalStateException(
						"Failed to determine Hibernate version for Spring Boot " + springBootVersion);
			}
			return VersionReference.ofValue(hibernateVersion);
		};
	}

	private static MavenRepository determineNativeMavenRepository(InitializrMetadata metadata,
			Version platformVersion) {
		Dependency dependency = metadata.getDependencies().get("native");
		if (dependency == null) {
			throw new IllegalStateException("No metadata found for dependency with id 'native'");
		}
		String repositoryId = dependency.resolve(platformVersion).getRepository();
		if (repositoryId != null) {
			Repository repository = metadata.getConfiguration().getEnv().getRepositories().get(repositoryId);
			return MavenRepository.withIdAndUrl(repositoryId, repository.getUrl().toExternalForm())
					.name(repository.getName()).snapshotsEnabled(repository.isSnapshotsEnabled()).build();
		}
		return null;
	}

}
