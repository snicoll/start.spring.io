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

import io.spring.initializr.generator.version.Version;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.start.site.extension.AbstractExtensionTests;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link SpringNativeProjectGenerationConfiguration}.
 *
 * @author Stephane Nicoll
 */
class SpringNativeProjectGenerationConfigurationTests extends AbstractExtensionTests {

	private final String springNativeVersion;

	SpringNativeProjectGenerationConfigurationTests(@Autowired InitializrMetadataProvider metadataProvider) {
		this.springNativeVersion = determineSpringNativeVersion(metadataProvider.get());
	}

	private static String determineSpringNativeVersion(InitializrMetadata metadata) {
		return metadata.getDependencies().get("native")
				.resolve(Version.parse(metadata.getBootVersions().getDefault().getId())).getVersion();
	}

	@Test
	void gradleBuildWithoutNativeDoesNotConfigureNativeSupport() {
		assertThat(gradleBuild(createProjectRequest("web"))).doesNotContain("springNativeVersion");
	}

	@Test
	void mavenBuildWithoutNativeDoesNotConfigureNativeSupport() {
		assertThat(mavenPom(createProjectRequest("web"))).doesNotContain("spring-native.version");
	}

	@Test
	void gradleBuildSetProperty() {
		assertThat(gradleBuild(createProjectRequest("native")))
				.contains("'springNativeVersion', \"" + this.springNativeVersion + "\")");
	}

	@Test
	void mavenBuildSetProperty() {
		assertThat(mavenPom(createProjectRequest("native"))).hasProperty("spring-native.version",
				this.springNativeVersion);
	}

	@Test
	void gradleBuildReusePropertyForSpringNativeDependency() {
		assertThat(gradleBuild(createProjectRequest("native")))
				.contains("implementation \"org.springframework.experimental:spring-native:${springNativeVersion}\"");
	}

	@Test
	void mavenBuildReusePropertyForSpringNativeDependency() {
		assertThat(mavenPom(createProjectRequest("native"))).hasDependency("org.springframework.experimental",
				"spring-native", "${spring-native.version}");
	}

	@Test
	void gradleBuildConfigureAotPlugin() {
		assertThat(gradleBuild(createProjectRequest("native"))).hasPlugin("org.springframework.experimental.aot",
				this.springNativeVersion);
	}

	@Test
	void mavenBuildConfigureAotPlugin() {
		assertThat(mavenPom(createProjectRequest("native"))).lines().containsSequence(
		// @formatter:off
				"			<plugin>",
				"				<groupId>org.springframework.experimental</groupId>",
				"				<artifactId>spring-aot-maven-plugin</artifactId>",
				"				<version>${spring-native.version}</version>",
				"				<executions>",
				"					<execution>",
				"						<id>test-generate</id>",
				"						<goals>",
				"							<goal>test-generate</goal>",
				"						</goals>",
				"					</execution>",
				"					<execution>",
				"						<id>generate</id>",
				"						<goals>",
				"							<goal>generate</goal>",
				"						</goals>",
				"					</execution>",
				"				</executions>",
				"			</plugin>");
		// @formatter:on
	}

	@Test
	void gradleBuildConfigureSpringBootPlugin() {
		assertThat(gradleBuild(createProjectRequest("native"))).lines().containsSequence("bootBuildImage {",
				"	builder = 'paketobuildpacks/builder:tiny'", "	environment = ['BP_BOOT_NATIVE_IMAGE': 'true']",
				"}");
	}

	@Test
	void mavenBuildConfigureSpringBootPlugin() {
		assertThat(mavenPom(createProjectRequest("native"))).lines().containsSequence(
		// @formatter:off
				"			<plugin>",
				"				<groupId>org.springframework.boot</groupId>",
				"				<artifactId>spring-boot-maven-plugin</artifactId>",
				"				<configuration>",
				"					<image>",
				"						<builder>paketobuildpacks/builder:tiny</builder>",
				"						<env>",
				"							<BP_BOOT_NATIVE_IMAGE>true</BP_BOOT_NATIVE_IMAGE>",
				"						</env>",
				"					</image>",
				"				</configuration>",
				"			</plugin>");
		// @formatter:on
	}

	@Test
	void mavenBuildWithoutJavaDoesNotConfigureHibernateEnhancePlugin() {
		assertThat(mavenPom(createProjectRequest("native"))).doesNotContain("hibernate-enhance-maven-plugin");
	}

	@Test
	void mavenBuildWithJavaConfigureHibernateEnhancePlugin() {
		assertThat(mavenPom(createProjectRequest("native", "data-jpa"))).lines().containsSequence(
		// @formatter:off
				"			<plugin>",
				"				<groupId>org.hibernate.orm.tooling</groupId>",
				"				<artifactId>hibernate-enhance-maven-plugin</artifactId>",
				"				<version>${hibernate.version}</version>",
				"				<executions>",
				"					<execution>",
				"						<id>enhance</id>",
				"						<goals>",
				"							<goal>enhance</goal>",
				"						</goals>",
				"						<configuration>",
				"							<failOnError>true</failOnError>",
				"							<enableLazyInitialization>true</enableLazyInitialization>",
				"							<enableDirtyTracking>true</enableDirtyTracking>",
				"							<enableAssociationManagement>true</enableAssociationManagement>",
				"							<enableExtendedEnhancement>false</enableExtendedEnhancement>",
				"						</configuration>",
				"					</execution>",
				"				</executions>",
				"			</plugin>");
		// @formatter:on
	}

}
